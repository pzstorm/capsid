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

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.jetbrains.annotations.Nullable;

import io.pzstorm.capsid.mod.ModProperties;

public enum Dependencies {

	/**
	 * Libraries used by Project Zomboid during runtime.
	 */
	ZOMBOID_LIBRARIES("zomboidRuntimeOnly", project ->
			project.fileTree(CapsidPlugin.getGameDirProperty(project), t -> t.include("*.jar"))
	),

	/**
	 * Project Zomboid assets in {@code media} directory.
	 */
	ZOMBOID_ASSETS("zomboidImplementation", project ->
			project.files(new File(CapsidPlugin.getGameDirProperty(project), "media"))
	),

	/**
	 * Project Zomboid Java classes used during runtime.
	 */
	ZOMBOID_CLASSES("zomboidRuntimeOnly", project -> {
		String modPzVersion = ModProperties.MOD_PZ_VERSION.findProperty(project);
		if (modPzVersion != null) {
			return project.files(Paths.get("lib", String.format("zomboid-%s.jar", modPzVersion)));
		}
		else return null;
	}),

	/**
	 * Lua library compiler for Project Zomboid.
	 *
	 * @see <a href="https://search.maven.org/artifact/io.github.cocolabs/pz-zdoc">
	 *     Artifact on Central Maven</a>
	 */
	ZOMBOID_DOC("zomboidDoc", project -> "io.github.cocolabs:pz-zdoc:3.+"),

	/**
	 * Lua library compiled with ZomboidDoc.
	 */
	LUA_LIBRARY("compileOnly", project -> {
		String modPzVersion = ModProperties.MOD_PZ_VERSION.findProperty(project);
		if (modPzVersion != null) {
			return project.files(String.format("lib/zdoc-lua-%s.jar", modPzVersion));
		}
		else return null;
	});

	final String configuration;
	private final DependencyResolver resolver;

	Dependencies(String configuration, DependencyResolver resolver) {
		this.configuration = configuration;
		this.resolver = resolver;
	}

	/**
	 * Register this dependency for {@code Project} with the given {@code DependencyHandler}.
	 *
	 * @param project {@code Project} to register the dependency for.
	 * @param dependencies handler used to register dependency.
	 * @return instance of the registered dependency or {@code null} if none registered.
	 */
	public @Nullable Dependency register(Project project, DependencyHandler dependencies) {

		Object dependencyNotation = resolver.resolveDependency(project);
		if (dependencyNotation != null) {
			return dependencies.add(configuration, dependencyNotation);
		}
		else return null;
	}
}
