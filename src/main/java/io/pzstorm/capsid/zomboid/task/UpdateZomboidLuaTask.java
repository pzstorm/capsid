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
package io.pzstorm.capsid.zomboid.task;

import java.util.Objects;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.property.VersionProperties;
import io.pzstorm.capsid.util.SemanticVersion;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import io.pzstorm.capsid.zomboid.ZomboidUtils;

/**
 * This task runs {@code ZomboidDoc} to update compiled Lua library.
 */
public class UpdateZomboidLuaTask extends DefaultTask implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		SemanticVersion lastZomboidDocVer = VersionProperties.LAST_ZDOC_VERSION.findProperty(project);
		int compareResult = new SemanticVersion.Comparator().compare(
				ZomboidUtils.getZomboidDocVersion(project), Objects.requireNonNull(lastZomboidDocVer)
		);
		// skip task if semantic version could not be resolved
		onlyIf(t -> !lastZomboidDocVer.equals(new SemanticVersion("0.0.0")) && compareResult != 0);

		TaskContainer tasks = project.getTasks();
		dependsOn(tasks.getByName(ZomboidTasks.ZOMBOID_VERSION.name));

		// ZomboidDoc version has changed
		if (compareResult != 0) {
			dependsOn(tasks.getByName(ZomboidTasks.ANNOTATE_ZOMBOID_LUA.name),
					tasks.getByName(ZomboidTasks.COMPILE_ZOMBOID_LUA.name));
		}
	}
}
