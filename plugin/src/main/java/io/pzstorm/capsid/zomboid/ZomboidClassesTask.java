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

import java.util.Objects;

import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.Sync;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.setup.LocalProperties;

/**
 * This task will sync {@code zomboidClassesDir} with game install directory.
 */
@NonNullApi
public class ZomboidClassesTask extends Sync implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {

		setGroup(group);
		setDescription(description);

		setIncludeEmptyDirs(false);
		from(LocalProperties.GAME_DIR.findProperty(project));
		include("**/*.class", "stdlib.lbc");

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		into(Objects.requireNonNull(ext.get("zomboidClassesDir")));
	}
}
