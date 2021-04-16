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

import java.util.Objects;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.property.CapsidProperty;

/**
 * This task prints mod metadata information.
 */
public class ShowModMetadataTask extends DefaultTask implements CapsidTask {

	/**
	 * Project properties are used to print info to console.
	 *
	 * @see #getDisplayProperty(CapsidProperty)
	 */
	private ExtraPropertiesExtension ext;

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);
		onlyIf(t -> ProjectProperty.MOD_INFO_FILE.get(project).exists());
	}

	@TaskAction
	void execute() {

		Project project = getProject();
		ext = project.getExtensions().getExtraProperties();
		Logger logger = CapsidPlugin.LOGGER;

		logger.lifecycle("This is a mod for Project Zomboid " + getDisplayProperty(ModProperties.MOD_PZ_VERSION));
		logger.lifecycle("------------------------------------------------");
		logger.lifecycle("Name: " + getDisplayProperty(ModProperties.MOD_NAME));
		logger.lifecycle("Description: " + getDisplayProperty(ModProperties.MOD_DESCRIPTION));
		logger.lifecycle("URL: " + getDisplayProperty(ModProperties.MOD_URL));
		logger.lifecycle("ID: " + getDisplayProperty(ModProperties.MOD_ID));
		logger.lifecycle("Version: " + getDisplayProperty(ModProperties.MOD_VERSION));
	}

	/**
	 * Returns display form for use in console output.
	 *
	 * @param property property to return display form
	 */
	private String getDisplayProperty(CapsidProperty<?> property) {

		String output = ext.has(property.name) ? (String) ext.get(property.name) : "";
		return Objects.requireNonNull(output).isEmpty() ? "<not specified>" : output;
	}
}
