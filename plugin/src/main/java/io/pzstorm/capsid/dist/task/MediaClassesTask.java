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
package io.pzstorm.capsid.dist.task;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.dist.DistributionUtils;

/**
 * This task assembles mod Lua classes with directory hierarchy ready for distribution.
 */
public class MediaClassesTask extends Copy implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet media = java.getSourceSets().getByName("media");
		File module = project.file("media");
		if (!module.exists()) {
			//noinspection ResultOfMethodCallIgnored
			module.mkdirs();
		}
		Map<Path, String> map = DistributionUtils.getPathsRelativeToModule(module, media.getJava());

		from(media.getJava().getSrcDirs());
		into(ProjectProperty.MEDIA_CLASSES_DIR.get(project));

		eachFile(fcd -> {
			String path = map.get(Paths.get(fcd.getPath()));
			if (path == null) {
				throw new GradleException("Unable to relativize copy path '" + fcd.getPath() + '\'');
			}
			fcd.setRelativePath(fcd.getRelativePath().prepend(path));
		});
		setIncludeEmptyDirs(false);
	}
}
