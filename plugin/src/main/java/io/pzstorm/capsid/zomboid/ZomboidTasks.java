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
package io.pzstorm.capsid.zomboid;

import org.gradle.api.Project;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.zomboid.task.*;

public enum ZomboidTasks {

	ZOMBOID_CLASSES(ZomboidClassesTask.class, "zomboidClasses",
			"zomboid", "Assembles Project Zomboid classes."
	),
	DECOMPILE_ZOMBOID(DecompileZomboidTask.class, "decompileZomboid",
			"zomboid", "Decompile Project Zomboid classes."
	),
	ZOMBOID_JAR(ZomboidJarTask.class, "zomboidJar",
			"zomboid", "Assembles a jar archive containing game classes."
	),
	ZOMBOID_SOURCES_JAR(ZomboidSourcesJarTask.class, "zomboidSourcesJar",
			"zomboid", "Assembles a jar containing decompiled game sources."
	),
	ZOMBOID_LUA_JAR(ZomboidLuaJarTask.class, "zomboidLuaJar",
			"zomboid", "Assembles a jar containing compiled Lua classes"
	),
	ZOMBOID_VERSION(ZomboidVersionTask.class, "zomboidVersion",
			"zomboid", "Save and print Project Zomboid game version."
	),
	ANNOTATE_ZOMBOID_LUA(AnnotateZomboidLuaTask.class, "annotateZomboidLua",
			"zomboid", "Annotate vanilla Lua with EmmyLua."
	);
	public final String name, group, description;
	final Class<? extends CapsidTask> type;

	ZomboidTasks(Class<? extends CapsidTask> type, String name, String group, String description) {
		this.type = type;
		this.name = name;
		this.group = group;
		this.description = description;
	}

	/**
	 * Configure and register this task for the given {@code Project}.
	 * @param project {@code Project} register this task.
	 */
	public void register(Project project) {
		project.getTasks().register(name, type, t -> t.configure(group, description, project));
	}
}
