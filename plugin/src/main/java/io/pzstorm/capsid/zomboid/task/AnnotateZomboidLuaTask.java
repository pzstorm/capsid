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

import java.nio.file.Paths;
import java.util.Objects;

import org.gradle.api.NonNullApi;
import org.gradle.api.Project;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.UnixPath;

/**
 * This task will annotate vanilla Lua with {@code EmmyLua}.
 */
@NonNullApi
public class AnnotateZomboidLuaTask extends ZomboidJavaExec implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {

		UnixPath gameDir = Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(project));
		UnixPath zDocLuaDir = UnixPath.get(ProjectProperty.ZDOC_LUA_DIR.get(project));

		args("annotate", "-i", Paths.get(gameDir.toString(), "media/lua").toString(),
				"-o", Paths.get(zDocLuaDir.toString(), "media/lua").toString()
		);
		super.configure(group, description, project);
	}
}
