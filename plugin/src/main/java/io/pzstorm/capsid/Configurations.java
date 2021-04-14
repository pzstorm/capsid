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

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.jetbrains.annotations.Nullable;

public enum Configurations {

	/**
	 * Project Zomboid dependencies only available during runtime.
	 */
	ZOMBOID_RUNTIME_ONLY("zomboidRuntimeOnly", ImmutableSet.of("runtimeOnly"), null),

	/**
	 * Project Zomboid dependencies available during compile and runtime.
	 */
	ZOMBOID_IMPLEMENTATION("zomboidImplementation", ImmutableSet.of("implementation" ), null),

	/**
	 * Zomboid Doc dependencies only available during runtime.
	 */
	ZOMBOID_DOC("zomboidDoc", null, ImmutableSet.of("zomboidRuntimeOnly"));

	private final String name;
	private final ImmutableSet<String> extendedTo, extendsFrom;

	Configurations(String name, @Nullable ImmutableSet<String> extendedTo, @Nullable ImmutableSet<String> extendsFrom) {
		this.name = name;
		this.extendedTo = extendedTo != null ? extendedTo : ImmutableSet.of();
		this.extendsFrom = extendsFrom != null ? extendsFrom : ImmutableSet.of();
	}

	/**
	 * Resolve a {@link Configuration} of a given name from {@code ConfigurationContainer}.
	 *
	 * @param name name of the configuration to resolve.
	 * @param configurations where to resolve the configuration from.
	 * @return configuration with the given name. Never returns {@code null}.
	 */
	private static Configuration resolve(String name, ConfigurationContainer configurations) {

		try {
			return configurations.getByName(name);
		}
		catch (GradleException e) {
			return configurations.create(name);
		}
	}

	/**
	 * Register this configuration with the given {@link ConfigurationContainer}.
	 *
	 * @param configurations where to register the configuration.
	 * @return the registered configuration.
	 */
	Configuration register(ConfigurationContainer configurations) {

		Configuration config = configurations.create(name);

		Set<Configuration> extendsFromConfigs = new HashSet<>();
		for (String entry : extendsFrom) {
			extendsFromConfigs.add(resolve(entry, configurations));
		}
		config.extendsFrom(extendsFromConfigs.toArray(new Configuration[]{}));

		for (String entry : extendedTo) {
			resolve(entry, configurations).extendsFrom(config);
		}
		return config;
	}

	/**
	 * Resolve a {@link Configuration} matching this enum from {@code ConfigurationContainer}.
	 *
	 * @param project {@code Project} to resolve the configuration from.
	 * @return configuration with the given name. Never returns {@code null}.
	 */
	public Configuration resolve(Project project) {
		return resolve(name, project.getConfigurations());
	}
}
