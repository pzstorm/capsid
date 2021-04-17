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

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.Configurations;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import io.pzstorm.capsid.zomboid.ZomboidUtils;

public class ZomboidJavaExec extends JavaExec implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		setMain("io.cocolabs.pz.zdoc.Main");
		classpath(Configurations.ZOMBOID_DOC.resolve(project));

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

		// write semantic version to file
		Path zomboidVersionFile = ZomboidUtils.getZomboidVersionFile(project).toPath();
		try (Writer writer = Files.newBufferedWriter(zomboidVersionFile, StandardCharsets.UTF_8)) {
			writer.write(ZomboidUtils.getZomboidDocVersion(project).toString());
		}
	}
}
