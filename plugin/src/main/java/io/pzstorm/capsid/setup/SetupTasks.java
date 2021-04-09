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

import org.gradle.api.Project;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.setup.task.CreateLaunchConfigsTask;
import io.pzstorm.capsid.setup.task.CreateModSearchScopesTask;
import io.pzstorm.capsid.setup.task.InitLocalPropertiesTask;

/**
 * Tasks that help setup modding work environment.
 */
public enum SetupTasks {

	INIT_LOCAL_PROPERTIES(InitLocalPropertiesTask.class, "initLocalProperties",
			"Initialize local project properties."
	),
	CREATE_LAUNCH_CONFIGS(CreateLaunchConfigsTask.class, "createLaunchRunConfigs",
			"Create game launch run configurations."
	),
	CREATE_MOD_SEARCH_SCOPES(CreateModSearchScopesTask.class, "createModSearchScopes",
			"Create IDEA search scopes for mod files."
	);
	public final String name, description;
	final Class<? extends CapsidTask> type;

	SetupTasks(Class<? extends CapsidTask> type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	/**
	 * Configure and register this task for the given {@code Project}.
	 * @param project {@code Project} register this task.
	 */
	public void register(Project project) {
		project.getTasks().register(name, type, t -> t.configure("build setup", description, project));
	}
}
