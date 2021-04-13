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

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import io.pzstorm.capsid.mod.ModProperties;

public enum Dependencies {

	/**
	 * Libraries used by Project Zomboid during runtime.
	 */
	ZOMBOID_LIBRARIES("zomboidRuntimeOnly", project -> new Object[]{
			project.fileTree(CapsidPlugin.getGameDirProperty(project), t -> t.include("*.jar"))
	}),

	/**
	 * Project Zomboid assets in {@code media} directory.
	 */
	ZOMBOID_ASSETS("zomboidImplementation", project -> new Object[]{
			project.files(new File(CapsidPlugin.getGameDirProperty(project), "media"))
	}),

	/**
	 * Project Zomboid Java classes used during runtime.
	 */
	ZOMBOID_CLASSES("zomboidRuntimeOnly", project -> {
		String modPzVersion = ModProperties.MOD_PZ_VERSION.findProperty(project);
		if (modPzVersion != null) {
			return new Object[]{ project.files(Paths.get("lib", String.format("zomboid-%s.jar", modPzVersion))) };
		}
		else return new Object[0];
	}),

	/**
	 * Lua library compiler for Project Zomboid.
	 *
	 * @see <a href="https://search.maven.org/artifact/io.github.cocolabs/pz-zdoc">
	 * 		Artifact on Central Maven</a>
	 */
	ZOMBOID_DOC("zomboidDoc", project -> new Object[]{
			"io.github.cocolabs:pz-zdoc:3.+",
			project.files(ProjectProperty.ZOMBOID_CLASSES_DIR.get(project))
	}),

	/**
	 * Lua library compiled with ZomboidDoc.
	 */
	LUA_LIBRARY("compileOnly", project -> {
		String modPzVersion = ModProperties.MOD_PZ_VERSION.findProperty(project);
		if (modPzVersion != null) {
			return new Object[]{ project.files(String.format("lib/zdoc-lua-%s.jar", modPzVersion)) };
		}
		else return new Object[0];
	});

	final String configuration;
	private final DependencyResolver resolver;

	Dependencies(String configuration, DependencyResolver resolver) {
		this.configuration = configuration;
		this.resolver = resolver;
	}

	/**
	 * Register dependencies for {@code Project} with the given {@code DependencyHandler}.
	 *
	 * @param project {@code Project} to register the dependencies for.
	 * @param dependencies handler used to register dependencies.
	 * @return {@code Set} of registered dependencies empty {@code Set} if none registered.
	 */
	Set<Dependency> register(Project project, DependencyHandler dependencies) {

		Set<Dependency> result = new HashSet<>();
		Object[] dependencyNotations = resolver.resolveDependencies(project);
		for (Object notation : dependencyNotations) {
			result.add(dependencies.add(configuration, notation));
		}
		return result;
	}
}
