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
package io.pzstorm.capsid.setup.task;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Input;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.TaskAction;

import com.google.common.collect.ImmutableMap;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.property.CapsidProperty;
import io.pzstorm.capsid.setup.LocalProperties;

/**
 * This task will initialize game directory by asking for user input.
 */
public class setGameDirectoryTask extends DefaultTask implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		// execute task only if properties file doesn't exist
		File propertiesFile = LocalProperties.get().getFile(project);
		onlyIf(t -> !propertiesFile.exists());
	}

	@TaskAction
	void execute() throws IOException {

		// declare locally to resolve only once
		Project gradleProject = getProject();
		ExtraPropertiesExtension ext = gradleProject.getExtensions().getExtraProperties();

		// make sure the properties file exists
		File propertiesFile = LocalProperties.get().getFile(gradleProject);
		if (!propertiesFile.createNewFile()) {
			throw new IOException(String.format("Unable to create %s file", propertiesFile.getName()));
		}
		Map<CapsidProperty<?>, String> PROPERTIES_INPUT_MAP = ImmutableMap.of(
				LocalProperties.GAME_DIR, "Enter path to game installation directory:"
		);
		org.apache.tools.ant.Project antProject = gradleProject.getAnt().getAntProject();
		Input inputTask = (Input) antProject.createTask("input");
		for (Map.Entry<CapsidProperty<?>, String> entry : PROPERTIES_INPUT_MAP.entrySet())
		{
			CapsidProperty<?> property = entry.getKey();

			inputTask.setAddproperty(property.name);
			inputTask.setMessage(entry.getValue());
			inputTask.execute();

			// transfer properties from ant to gradle
			ext.set(property.name, antProject.getProperty(property.name).trim());
		}
		// write local properties to file
		LocalProperties.get().writeToFile(gradleProject);
	}
}
