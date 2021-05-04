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
import java.util.HashSet;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import com.google.common.collect.ImmutableSet;

import io.pzstorm.capsid.mod.ModProperties;

public enum Dependencies {

	/**
	 * Libraries used by Project Zomboid during runtime.
	 */
	ZOMBOID_LIBRARIES("zomboidRuntimeOnly", project -> ImmutableSet.of(
			project.fileTree(CapsidPlugin.getGameDirProperty(project), t -> t.include("*.jar"))
	)),

	/**
	 * Project Zomboid assets in {@code media} directory.
	 */
	ZOMBOID_ASSETS("zomboidImplementation", project -> ImmutableSet.of(
			project.files(new File(CapsidPlugin.getGameDirProperty(project), "media"))
	)),

	/**
	 * Project Zomboid Java classes.
	 */
	ZOMBOID_CLASSES("zomboidImplementation", false, project -> {
		String modPzVersion = ModProperties.PZ_VERSION.findProperty(project);
		return ImmutableSet.of(project.files(String.format("lib/zomboid%s.jar",
				modPzVersion != null && !modPzVersion.isEmpty() ? "-" + modPzVersion : "")
		));
	}),

	/**
	 * Lua library compiler for Project Zomboid.
	 *
	 * @see <a href="https://search.maven.org/artifact/io.github.cocolabs/pz-zdoc">
	 * 		Artifact on Central Maven</a>
	 */
	ZOMBOID_DOC("zomboidDoc", project -> ImmutableSet.of(
			"io.github.cocolabs:pz-zdoc:3.+",
			project.files(ProjectProperty.ZOMBOID_CLASSES_DIR.get(project))
	)),

	/**
	 * Lua library compiled with ZomboidDoc.
	 */
	LUA_LIBRARY("compileOnly", false, project -> {
		String modPzVersion = ModProperties.PZ_VERSION.findProperty(project);
		return ImmutableSet.of(project.files(String.format("lib/zdoc-lua%s.jar",
				modPzVersion != null && !modPzVersion.isEmpty() ? "-" + modPzVersion : "")
		));
	});

	final String configuration;
	final boolean availablePreEval;
	private final DependencyResolver resolver;

	Dependencies(String configuration, boolean availablePreEval, DependencyResolver resolver) {

		this.configuration = configuration;
		this.availablePreEval = availablePreEval;
		this.resolver = resolver;
	}

	Dependencies(String configuration, DependencyResolver resolver) {
		this(configuration, true, resolver);
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
		Set<Object> dependencyNotations = resolver.resolveDependencies(project);
		for (Object notation : dependencyNotations) {
			result.add(dependencies.add(configuration, notation));
		}
		return result;
	}
}
