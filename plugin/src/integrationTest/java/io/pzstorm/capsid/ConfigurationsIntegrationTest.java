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

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigurationsIntegrationTest extends PluginIntegrationTest {

	@Test
	void shouldRegisterConfigurations() {

		ConfigurationContainer configs1 = getProject(false).getConfigurations();
		Map<Configurations, Configuration> configData = new HashMap<>();

		for (Configurations value : Configurations.values())
		{
			Configuration configuration = value.register(configs1);
			Assertions.assertTrue(configs1.contains(configuration));
			configData.put(value, configuration);
		}
		Project project = ProjectBuilder.builder().build();
		ConfigurationContainer configs2 = project.getConfigurations();

		// confirm that configurations are not registered in new project
		for (Configurations value : Configurations.values())
		{
			Assertions.assertFalse(configs2.contains(configData.get(value)));
		}
	}
}
