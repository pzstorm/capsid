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
package io.pzstorm.capsid.setup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Input;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidTask;

class InitLocalProperties extends CapsidTask {

	@TaskAction
	void execute() throws IOException {

		Map<LocalProperties, String> PROPERTIES_INPUT_MAP = new HashMap<>();
		PROPERTIES_INPUT_MAP.put(LocalProperties.GAME_DIR,
				"Enter path to game installation directory"
		);
		PROPERTIES_INPUT_MAP.put(LocalProperties.IDEA_HOME,
				"Enter path to IntelliJ IDEA installation directory"
		);
		org.apache.tools.ant.Project antProject = getProject().getAnt().getAntProject();
		Input inputTask = (Input) antProject.createTask("input");
		for (LocalProperties property : LocalProperties.values())
		{
			inputTask.setAddproperty(property.data.name);
			inputTask.setMessage(PROPERTIES_INPUT_MAP.get(property));
			inputTask.execute();
		}
		File localPropertiesFile = LocalProperties.getFile(getProject());
		if (!localPropertiesFile.createNewFile())
		{
			String format = "Unable to create %s file";
			throw new IOException(String.format(format, localPropertiesFile.getName()));
		}
	}
}
