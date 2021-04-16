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

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidPluginExtension;
import io.pzstorm.capsid.CapsidTask;

/**
 * This task creates source and resource directories for {@code media} module.
 * Resource directories can be excluded with {@code excludeResourceDirs} plugin configuration.
 *
 * @see CapsidPluginExtension#excludeResourceDirs(String...)
 */
@SuppressWarnings("WeakerAccess")
public class CreateModStructureTask extends DefaultTask implements CapsidTask {

	@TaskAction
	void execute() {

		Convention convention = getProject().getConvention();
		JavaPluginConvention javaPlugin = convention.getPlugin(JavaPluginConvention.class);
		SourceSet media = javaPlugin.getSourceSets().getByName("media");

		for (File srcDir : media.getJava().getSrcDirs())
		{
			if (!srcDir.exists() && !srcDir.mkdirs())
			{
				String msg = "Unable to create mod structure for source dir '%s'";
				throw new GradleException(String.format(msg, srcDir.toPath()));
			}
		}
		for (File resDir : media.getResources().getSrcDirs())
		{
			if (!resDir.exists() && !resDir.mkdirs())
			{
				String msg = "Unable to create mod structure for resource dir '%s'";
				throw new GradleException(String.format(msg, resDir.toPath()));
			}
		}
	}
}
