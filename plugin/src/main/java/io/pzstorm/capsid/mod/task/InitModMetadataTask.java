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
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.apache.tools.ant.taskdefs.Input;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.mod.ModTasks;
import io.pzstorm.capsid.property.CapsidProperty;

/**
 * This task initializes mod metadata information.
 */
public class InitModMetadataTask extends DefaultTask implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		onlyIf(t -> !ProjectProperty.MOD_INFO_FILE.get(project).exists());
		finalizedBy(project.getTasks().getByName(ModTasks.SAVE_MOD_METADATA.name));
	}

	@TaskAction
	void execute() throws IOException {

		Project gradleProject = getProject();
		ExtraPropertiesExtension ext = gradleProject.getExtensions().getExtraProperties();

		// make sure the properties file exists
		File modInfoFile = ModProperties.get().getFile(gradleProject);
		if (!modInfoFile.exists() && !modInfoFile.createNewFile()) {
			throw new IOException(String.format("Unable to create %s file", modInfoFile.getName()));
		}
		org.apache.tools.ant.Project antProject = gradleProject.getAnt().getAntProject();
		Input inputTask = (Input) antProject.createTask("input");
		Map<CapsidProperty<?>, String> PROPERTIES_INPUT_MAP = ImmutableMap.of(
				ModProperties.MOD_NAME, "Enter mod name:",
				ModProperties.MOD_DESCRIPTION, "\nEnter mod description:",
				ModProperties.MOD_URL, "\nEnter github repository URL:"
		);
		for (Map.Entry<CapsidProperty<?>, String> entry : PROPERTIES_INPUT_MAP.entrySet())
		{
			CapsidProperty<?> property = entry.getKey();

			inputTask.setAddproperty(property.name);
			inputTask.setMessage(entry.getValue());
			inputTask.execute();

			// transfer properties from ant to gradle
			String antProperty = antProject.getProperty(property.name).trim();
			if (!Strings.isNullOrEmpty(antProperty)) {
				ext.set(property.name, antProperty);
			}
		}
	}
}
