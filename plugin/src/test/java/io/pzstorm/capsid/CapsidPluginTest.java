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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CapsidPluginTest {

	private Project project;
	private Plugin<Project> plugin;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void createProjectAndApplyPlugin() {

		project = ProjectBuilder.builder().build();
		plugin = project.getPlugins().apply("io.pzstorm.capsid");
	}

	@Test
	void shouldApplyAllCorePlugins() {

		PluginContainer plugins = project.getPlugins();
		for (CorePlugin plugin : CorePlugin.values()) {
			Assertions.assertTrue(plugins.hasPlugin(plugin.getID()));
		}
	}
}
