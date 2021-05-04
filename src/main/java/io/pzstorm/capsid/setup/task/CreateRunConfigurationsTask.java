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

import java.io.IOException;

import javax.xml.transform.TransformerException;

import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.setup.xml.GradleRunConfig;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.setup.xml.LaunchRunConfig;

/**
 * This task will create useful IDEA run configurations.
 *
 * @see LaunchRunConfig
 */
public class CreateRunConfigurationsTask extends DefaultTask implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		dependsOn(project.getTasks().getByName(SetupTasks.SET_GAME_DIRECTORY.name));
	}

	@TaskAction
	void execute() throws IOException, TransformerException {

		Project project = getProject();

		LaunchRunConfig.RUN_ZOMBOID.configure(project).writeToFile();
		LaunchRunConfig.RUN_ZOMBOID_LOCAL.configure(project).writeToFile();

		LaunchRunConfig.DEBUG_ZOMBOID.configure(project).writeToFile();
		LaunchRunConfig.DEBUG_ZOMBOID_LOCAL.configure(project).writeToFile();

		GradleRunConfig.SETUP_WORKSPACE.configure(project).writeToFile();
		GradleRunConfig.INITIALIZE_MOD.configure(project).writeToFile();
	}
}
