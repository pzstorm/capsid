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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DependenciesIntegrationTest extends PluginIntegrationTest {

	@Test
	void shouldRegisterDependencies() {

		Project project1 = getProject(false);
		project1.getPlugins().apply("java");

		ExtraPropertiesExtension ext = project1.getExtensions().getExtraProperties();
		File gameDir =  new File(project1.getRootDir(), "gameDir");
		ext.set("gameDir", gameDir.toPath().toString());
		ext.set("mod.pzversion", "41.50");

		DependencyHandler handler1 = project1.getDependencies();
		ConfigurationContainer configurations1 = project1.getConfigurations();
		Map<Dependencies, Set<Dependency>> dependencyData = new HashMap<>();

		// register all configurations before resolving them
		for (Configurations value : Configurations.values()) {
			value.register(configurations1);
		}
		for (Dependencies value : Dependencies.values())
		{
			Configuration configuration = configurations1.getByName(value.configuration);
			Set<Dependency> dependencies = value.register(project1, handler1);

			DependencySet dependencySet = configuration.getDependencies();
			for (Dependency dependency : dependencies) {
				Assertions.assertTrue(dependencySet.contains(dependency));
			}
			dependencyData.put(value, dependencies);
		}
		Project project2 = ProjectBuilder.builder().build();
		project2.getPlugins().apply("java");

		gameDir =  new File(project2.getRootDir(), "gameDir");
		project2.getExtensions().getExtraProperties().set("gameDir", gameDir.toPath().toString());

		ConfigurationContainer configurations2 = project2.getConfigurations();

		// register all configurations before resolving them
		for (Configurations value : Configurations.values()) {
			value.register(configurations2);
		}
		// confirm that dependencies are not registered in new project
		for (Dependencies value : Dependencies.values())
		{
			Configuration configuration = configurations2.getByName(value.configuration);
			DependencySet dependencySet = configuration.getDependencies();
			for (Dependency dependency : dependencyData.get(value)) {
				Assertions.assertFalse(dependencySet.contains(dependency));
			}
		}
	}
}
