/*
 * Storm Capsid - Project Zomboid mod development framework for Gradle.
 * Copyright (C) 2021 Matthew Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.pzstorm.capsid.dist.task;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.Exec;

import com.google.common.base.Strings;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidPluginExtension;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.dist.GenerateChangelogOptions;
import io.pzstorm.capsid.util.Utils;

/**
 * This task generates a changelog using {@code github-changelog-generator}.
 */
public class GenerateChangelogTask extends Exec implements CapsidTask {

	private static final String TOKEN_ENV_VAR_NAME = "CHANGELOG_GITHUB_TOKEN";
	private static final String TOKEN_PROPERTY_NAME = "gcl.token";

	/**
	 * Create {@code Gemfile} needed to generate changelog with Ruby if it does not exist.
	 *
	 * @param project {@code Project} to create the file for.
	 *
	 * @throws GradleException if an I/O exception occurred while creating {@code Gemfile}.
	 */
	private static void createGemfile(Project project) {

		File gemFile = new File(project.getProjectDir(), "Gemfile");
		if (gemFile.exists()) {
			return;
		}
		try {
			if (!gemFile.createNewFile()) {
				throw new GradleException("Unable to create Gemfile in root directory");
			}
			try (Writer writer = Files.newBufferedWriter(gemFile.toPath(), StandardCharsets.UTF_8)) {
				writer.write(Utils.readResourceAsTextFromStream(CapsidPlugin.class, "Gemfile"));
			}
		}
		catch (IOException e) {
			throw new GradleException("I/O error occurred while creating Gemfile in root directory", e);
		}
	}

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		ExtensionContainer extensions = project.getExtensions();
		ExtraPropertiesExtension ext = extensions.getExtraProperties();
		CapsidPluginExtension capsidExt = extensions.getByType(CapsidPluginExtension.class);

		// cannot generate changelog without knowing where to look
		boolean hasDefinedRepo = ext.has("repo.owner") && ext.has("repo.name");
		onlyIf(it -> hasDefinedRepo);

		Map<GenerateChangelogOptions, Object> optionsMap = capsidExt.generateChangelogOptions;
		if (hasDefinedRepo)
		{
			optionsMap.putIfAbsent(GenerateChangelogOptions.USER, ext.get("repo.owner"));
			optionsMap.putIfAbsent(GenerateChangelogOptions.PROJECT, ext.get("repo.name"));
			optionsMap.putIfAbsent(GenerateChangelogOptions.ISSUES_WITHOUT_LABELS, "false");
		}
		else CapsidPlugin.LOGGER.warn("WARN: Repository owner and name not specified");

		// first check for token in environment variables
		String token = System.getenv(TOKEN_ENV_VAR_NAME);
		if (Strings.isNullOrEmpty(token))
		{
			// next check for token in project properties
			if (ext.has(TOKEN_PROPERTY_NAME)) {
				token = (String) ext.get(TOKEN_PROPERTY_NAME);
			}
			// don't pass token as null
			else token = "";
		}
		optionsMap.put(GenerateChangelogOptions.TOKEN, new String[]{ token });

		List<String> command = new ArrayList<>(
				Arrays.asList("bundle", "exec", "github_changelog_generator")
		);
		for (Map.Entry<GenerateChangelogOptions, Object> entry : optionsMap.entrySet())
		{
			String[] sValue;
			Object oValue = entry.getValue();
			if (oValue instanceof String[]) {
				sValue = (String[]) oValue;
			}
			else if (oValue instanceof String) {
				sValue = new String[]{ (String) oValue };
			}
			else sValue = new String[]{ oValue.toString() };

			command.add(entry.getKey().formatOption(sValue));
			command.addAll(Arrays.asList(sValue));
		}
		// windows platforms needs extra command tokens to work
		if (System.getProperty("os.name").startsWith("Windows")) {
			command.addAll(0, Arrays.asList("cmd", "/c"));
		}
		commandLine(command);

		// create Gemfile in root directory if one doesn't exist
		createGemfile(project);

		doFirst(task -> CapsidPlugin.LOGGER.lifecycle(String.format(
				"Generating changelog for %s/%s", ext.get("repo.owner"), ext.get("repo.name"))));
	}
}
