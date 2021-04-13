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
package io.pzstorm.capsid.mod;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.mod.task.*;

public enum ModTasks {

	CREATE_MOD_STRUCTURE(CreateModStructureTask.class, true,
			"createModStructure", "Create default mod directory structure."
	),
	SAVE_MOD_METADATA(SaveModMetadataTask.class, true,
			"saveModMetadata", "Save mod metadata to file."
	),
	LOAD_MOD_METADATA(LoadModMetadataTask.class, false,
			"loadModMetadata", "Load mod metadata information."
	),
	INIT_MOD_METADATA(InitModMetadataTask.class, false,
			"initModMetadata", "Initialize mod metadata information."
	),
	SHOW_MOD_METADATA(ShowModMetadataTask.class, false,
			"showModMetadata", "Print mod metadata information."
	),
	APPLY_MOD_TEMPLATE(ApplyModTemplateTask.class, true,
			"applyModTemplate", "Apply Project Zomboid mod template."
	);
	public final String name, description;
	public final boolean register;
	final Class<? extends CapsidTask> type;

	ModTasks(Class<? extends CapsidTask> type, boolean register, String name, String description) {
		this.type = type;
		this.name = name;
		this.register = register;
		this.description = description;
	}

	/**
	 * Configure and create or register this task for the given {@code Project}.
	 *
	 * @param project {@code Project} register this task.
	 */
	public void createOrRegister(Project project) {

		TaskContainer tasks = project.getTasks();
		if (register) {
			tasks.register(name, type, t -> t.configure("mod", description, project));
		}
		else tasks.create(name, type, t -> t.configure("mod", description, project));
	}
}
