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
package io.pzstorm.capsid.zomboid.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.Configurations;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.SemanticVersion;
import io.pzstorm.capsid.util.UnixPath;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

/**
 * This task will annotate vanilla Lua with {@code EmmyLua}.
 */
@NonNullApi
public class AnnotateZomboidLuaTask extends JavaExec implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {

		setGroup(group);
		setDescription(description);

		setMain("io.cocolabs.pz.zdoc.Main");
		classpath(Configurations.ZOMBOID_DOC.resolve(project));

		Path gameDir = Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(project)).convert();
		Path zDocLuaDir = ProjectProperty.ZDOC_LUA_DIR.get(project).toPath();

		args("annotate", "-i", UnixPath.get(Paths.get(gameDir.toString(), "media/lua")).toString(),
				"-o", UnixPath.get(Paths.get(zDocLuaDir.toString(), "media/lua")).toString()
		);
		dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
	}

	/**
	 * @throws IOException if an I/O error occurred while handling version file.
	 * @throws GradleException if unable to find dependency or dependency has unexpected name.
	 * @throws InvalidUserDataException if constructed semantic version is malformed.
	 */
	@TaskAction
	void execute() throws IOException {

		Project project = getProject();

		File versionFile = ProjectProperty.ZDOC_VERSION_FILE.get(project);
		if (!versionFile.exists() && !versionFile.createNewFile()) {
			throw new IOException("Unable to create 'zdoc.version' file");
		}
		// find ZomboidDoc dependency file from configuration
		File dependency = Configurations.ZOMBOID_DOC.resolve(project).getFiles().stream()
				.filter(f -> f.getName().startsWith("pz-zdoc")).findFirst().orElseThrow(
						() -> new GradleException("Unable to find ZomboidDoc dependency"));

		// get and validate dependency name
		String dependencyName = dependency.getName();

		String pattern = "pz-zdoc-(\\d+\\.\\d+\\.\\d+(-.*)?)\\.jar";
		Matcher matcher = Pattern.compile(pattern).matcher(dependencyName);
		if (!matcher.find()) {
			throw new GradleException("Unexpected ZomboidDoc dependency name: " + dependencyName);
		}
		// write semantic version to file
		try (Writer writer = new FileWriter(versionFile)) {
			writer.write(new SemanticVersion(matcher.group(1)).toString());
		}
	}
}
