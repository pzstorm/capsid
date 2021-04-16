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
package io.pzstorm.capsid.mod.task;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

/**
 * This task will save mod metadata to file.
 */
public class SaveModMetadataTask extends DefaultTask implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);
		dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_VERSION.name));
	}

	@TaskAction
	void execute() throws IOException {

		Project project = getProject();

		// ensure that mod.info file exists before writing to it
		File metadataFile = ModProperties.get().getFile(project);
		if (!metadataFile.exists())
		{
			// make sure directory structure is prepared
			//noinspection ResultOfMethodCallIgnored
			metadataFile.getParentFile().mkdirs();

			if (!metadataFile.createNewFile()) {
				throw new IOException("Unable to create mod.info file '" + metadataFile.getPath() + '\'');
			}
		}
		// save mod properties to mod.info file
		ModProperties.get().writeToFile(project);
	}
}
