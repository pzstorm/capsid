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
package io.pzstorm.capsid.dist;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.dist.task.GenerateChangelogTask;
import io.pzstorm.capsid.dist.task.MediaClassesTask;
import io.pzstorm.capsid.dist.task.ProcessResourcesTask;

public enum DistributionTasks {

	GENERATE_CHANGELOG(GenerateChangelogTask.class,
			"generateChangelog", "Generate a project changelog."
	),
	MEDIA_CLASSES(MediaClassesTask.class,
			"mediaClasses", "Assembles mod Lua classes.", true
	),
	PROCESS_RESOURCES(ProcessResourcesTask.class,
			"processMediaResources", "Process mod resources.", true
	);
	public final String name, description;
	private final Class<? extends CapsidTask> type;
	private final boolean overwrite;

	DistributionTasks(Class<? extends CapsidTask> type, String name, String description, boolean overwrite) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.overwrite = overwrite;
	}

	DistributionTasks(Class<? extends CapsidTask> type, String name, String description) {
		this(type, name, description, false);
	}

	/**
	 * Configure and register this task for the given {@code Project}.
	 *
	 * @param project {@code Project} register this task.
	 */
	public void register(Project project) {

		TaskContainer tasks = project.getTasks();
		if (overwrite) {
			tasks.replace(name, type).configure("distribution", description, project);
		}
		else tasks.register(name, type, t -> t.configure("distribution", description, project));
	}
}
