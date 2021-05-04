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
package io.pzstorm.capsid.mod.task;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import io.pzstorm.capsid.PluginIntegrationTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.mod.ModTasks;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

class LoadModMetadataTaskIntegrationTest extends PluginIntegrationTest {

	private static final ImmutableMap<String, String> MOD_METADATA =
			new ImmutableMap.Builder<String, String>()
					.put("name", "TestMod").put("description", "None")
					.put("url", "https://github.com/pzstorm/capsid/")
					.put("modversion", "0.1.0")
					.put("pzversion", "41.50-IWBUMS")
					.build();

	private Project project;

	@BeforeEach
	void writeModMetadataToFile() throws IOException {

		List<String> modInfoArray = new ArrayList<>();
		for (Map.Entry<String, String> entry : MOD_METADATA.entrySet()) {
			modInfoArray.add(entry.getKey() + '=' + entry.getValue());
		}
		project = getProject(false);
		Path modMetadataFile = ProjectProperty.MOD_INFO_FILE.get(project).toPath();
		try (Writer writer = Files.newBufferedWriter(modMetadataFile, StandardCharsets.UTF_8)) {
			writer.write(String.join("\n", modInfoArray));
		}
	}

	@Test
	void shouldLoadModInfoFromProjectProperties() {

		// apply plugin before validating properties
		applyCapsidPlugin();

		// register all mod tasks
		registerModTasks();

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		for (Map.Entry<String, String> entry : MOD_METADATA.entrySet())
		{
			String propertyName = ModProperties.METADATA_MAPPING.get(entry.getKey()).name;
			Assertions.assertTrue(ext.has(propertyName));
			Assertions.assertEquals(entry.getValue(), ext.get(propertyName));
		}
	}

	@Test
	void whenModInfoFileMissingShouldInheritFromProjectName() {

		// make sure mod.info file is deleted
		Assertions.assertTrue(ProjectProperty.MOD_INFO_FILE.get(project).delete());

		// apply plugin before validating properties
		applyCapsidPlugin();

		// register all mod tasks
		registerModTasks();

		Assertions.assertEquals(project.getName(), ModProperties.MOD_NAME.findProperty(project));
	}

	@Test
	void whenModInfoFileMissingShouldNotLoadProperties() {

		// make sure mod.info file is deleted
		Assertions.assertTrue(ProjectProperty.MOD_INFO_FILE.get(project).delete());

		// apply plugin before validating properties
		applyCapsidPlugin();

		// register all mod tasks
		registerModTasks();

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		for (Map.Entry<String, String> entry : MOD_METADATA.entrySet())
		{
			String key = "mod." + entry.getKey();
			// mod name and ID properties are always loaded
			if (!key.equals(ModProperties.MOD_NAME.name) && !key.equals(ModProperties.MOD_ID.name)) {
				Assertions.assertFalse(ext.has(key));
			}
		}
	}

	@Test
	void shouldReadRepositoryInformationFromUrlProperty() {

		// apply plugin before validating properties
		applyCapsidPlugin();

		// register all mod tasks
		registerModTasks();

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();

		Assertions.assertTrue(ext.has("repo.owner"));
		Assertions.assertTrue(ext.has("repo.name"));

		Assertions.assertEquals("pzstorm", ext.get("repo.owner"));
		Assertions.assertEquals("capsid", ext.get("repo.name"));
	}

	@Test
	void shouldInheritModIdPropertyFromRootProjectName() {

		// apply plugin before validating properties
		applyCapsidPlugin();

		// register all mod tasks
		registerModTasks();

		String projectName = project.getRootProject().getName();
		Assertions.assertEquals(projectName, ModProperties.MOD_ID.findProperty(project));
	}

	private void registerModTasks() {

		ZomboidTasks.ZOMBOID_VERSION.register(project);
		Arrays.stream(ModTasks.values()).forEach(t -> t.createOrRegister(project));
	}
}
