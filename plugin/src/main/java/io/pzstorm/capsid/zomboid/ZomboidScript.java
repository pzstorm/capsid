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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.TaskContainer;

import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.UnixPath;

public class ZomboidScript {

	public static void configure(Project project) {

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();

		// directory containing Project Zomboid classes
		File classesDir = new File(project.getBuildDir(), "classes/zomboid").getAbsoluteFile();
		ext.set("zomboidClassesDir",classesDir);

		// directory containing Project Zomboid sources
		File sourcesDir = new File(project.getBuildDir(), "generated/sources/zomboid").getAbsoluteFile();
		ext.set("zomboidSourcesDir", sourcesDir);

		ConfigurationContainer configurations = project.getConfigurations();
		configurations.getByName("runtimeOnly").extendsFrom(configurations.create("zomboidRuntimeOnly"));
		configurations.getByName("implementation").extendsFrom(configurations.create("zomboidImplementation"));

		DependencyHandler dependencies = project.getDependencies();
		UnixPath gameDirProperty = LocalProperties.GAME_DIR.findProperty(project);
		Path gameDir = Objects.requireNonNull(gameDirProperty).convert().toAbsolutePath();

		// Project Zomboid libraries
		ConfigurableFileTree zomboidLibraries = project.fileTree(gameDir.toFile(), tree -> tree.include("*.jar"));
		dependencies.add("zomboidRuntimeOnly", zomboidLibraries);

		// Project Zomboid assets
		ConfigurableFileCollection zomboidAssets = project.files(gameDir.resolve("media"));
		dependencies.add("zomboidImplementation", zomboidAssets);

		// Project Zomboid classes
		String modPzVersion = ModProperties.MOD_PZ_VERSION.findProperty(project);
		if (modPzVersion != null)
		{
			Path jarPath = Paths.get("lib", String.format("zomboid-%s.jar", modPzVersion));
			dependencies.add("zomboidRuntimeOnly", project.files(jarPath));
		}
		TaskContainer tasks = project.getTasks();
		tasks.getByName("classes").dependsOn(tasks.getByName("zomboidClasses"));
	}
}
