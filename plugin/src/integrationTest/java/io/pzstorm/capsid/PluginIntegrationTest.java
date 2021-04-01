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
package io.pzstorm.capsid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import io.pzstorm.capsid.util.UnixPath;

@Tag("integration")
public abstract class PluginIntegrationTest {

	private static final File PARENT_TEMP_DIR = new File("build/tmp/integrationTest");
	private static final Set<String> TEMP_DIR_NAMES = new HashSet<>();

	private Project project;
	private Plugin<Project> plugin;

	@BeforeAll
	static void cleanTemporaryDirectories() throws IOException {

		if (PARENT_TEMP_DIR.exists())
		{
			Set<Path> dirsToClean = Files.list(PARENT_TEMP_DIR.toPath()).filter(p ->
					p.toFile().isDirectory() && !TEMP_DIR_NAMES.contains(p.getFileName().toString()))
					.collect(Collectors.toSet());

			for (Path path : dirsToClean)
			{
				MoreFiles.deleteRecursively(path, RecursiveDeleteOption.ALLOW_INSECURE);
				Assertions.assertFalse(path.toFile().exists());
			}
		}
	}

	@BeforeEach
	@SuppressWarnings("unchecked")
	void createProjectAndApplyPlugin() throws IOException {

		File projectDir = generateProjectDirectory();
		Assertions.assertTrue(projectDir.mkdirs());
		TEMP_DIR_NAMES.add(projectDir.getName());

		File localProperties = new File(projectDir, "local.properties");
		Assertions.assertTrue(localProperties.createNewFile());

		File gameDir = new File(projectDir, "gameDir");
		Files.createDirectory(gameDir.toPath());

		File gameMediaDir = new File(gameDir, "media");
		Files.createDirectory(gameMediaDir.toPath());

		for (String dir : new String[] {"lua", "maps", "models"}) {
			Files.createDirectories(new File(gameMediaDir, dir).toPath());
		}
		File ideaHome = new File(projectDir, "ideaHome");
		Files.createFile(ideaHome.toPath());

		try (Writer writer = new FileWriter(localProperties)) {
			writer.write(String.join("\n",
					// property values with backslashes are considered malformed
					"gameDir=" + UnixPath.get(gameDir).toString(),
					"ideaHome=" + UnixPath.get(ideaHome).toString()
			));
		}
		project = ProjectBuilder.builder().withProjectDir(projectDir).build();
		plugin = project.getPlugins().apply("io.pzstorm.capsid");
	}

	private File generateProjectDirectory() {

		// generate a directory name that doesn't exist yet
		File result = getRandomProjectDirectory();
		while (result.exists()) {
			result = getRandomProjectDirectory();
		}
		return result;
	}

	private File getRandomProjectDirectory() {
		return new File(PARENT_TEMP_DIR, "test" + new Random().nextInt(1000));
	}

	protected Project getProject() {
		return project;
	}

	protected Plugin<Project> getPlugin() {
		return plugin;
	}
}
