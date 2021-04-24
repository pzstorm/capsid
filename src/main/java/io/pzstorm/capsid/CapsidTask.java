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
package io.pzstorm.capsid;

import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * Standard contract for all plugin task implementations.
 */
@NonNullApi
public interface CapsidTask extends Task {

	/**
	 * Apply the configuration to this task.
	 *
	 * @param group task group which this task belongs to.
	 * @param description description for this task.
	 * @param project {@code Project} configuring the task.
	 */
	default void configure(String group, String description, Project project) {

		setGroup(group);
		setDescription(description);
	}
}
