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

import org.gradle.api.tasks.TaskContainer;

import io.pzstorm.capsid.CapsidTask;

/**
 * Tasks that help setup modding work environment.
 */
public enum SetupTasks {

	INIT_LOCAL_PROPERTIES(InitLocalProperties.class, "initLocalProperties",
			"build setup", "Initialize local project properties."
	);
	final String name, group, description;
	final Class<? extends CapsidTask> type;

	SetupTasks(Class<? extends CapsidTask> type, String name, String group, String description) {
		this.type = type;
		this.name = name;
		this.group = group;
		this.description = description;
	}

	/**
	 * Configure and register this task in the given {@code TaskContainer}.
	 * @param tasks {@code TaskContainer} to register this task to.
	 */
	public void register(TaskContainer tasks) {
		tasks.register(name, type, t -> t.configure(group, description));
	}
}
