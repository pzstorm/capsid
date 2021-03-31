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
package io.pzstorm.capsid.property;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.property.validator.PropertyValidators;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.UnixPath;

class CapsidPropertyFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldLoadLocalPropertiesFromFile() throws IOException {

		writeLocalPropertiesToFile();
		Assertions.assertDoesNotThrow(() -> getRunner().withArguments(new ArrayList<>()).build());
		assertLocalPropertiesNotNull(false);
	}

	@Test
	void shouldLoadLocalPropertiesFromProjectProperties() throws IOException {

		Assertions.assertDoesNotThrow(() -> getRunner().build());
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldLoadLocalPropertiesFromSystemProperties() throws IOException {

		GradleRunner runner = getRunner();
		runner.withArguments(
				String.format("-DgameDir=%s", getGameDirPath().toString()),
				String.format("-DideaHome=%s", getIdeaHomePath().toString())
		);
		Assertions.assertDoesNotThrow(runner::build);
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldLoadLocalPropertiesFromEnvironmentVariables() throws IOException {

		GradleRunner runner = getRunner().withArguments(new ArrayList<>());

		Map<String, String> arguments = new HashMap<>();
		arguments.put("PZ_DIR_PATH", getGameDirPath().toString());
		arguments.put("IDEA_HOME", getIdeaHomePath().toString());
		runner.withEnvironment(arguments);

		// runner cannot run in debug mode with environment variables
		runner.withDebug(false);

		Assertions.assertDoesNotThrow(runner::build);
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldCorrectlyConvertLocalPropertyToUnixPath() throws IOException {

		File targetDir = getProjectDir().toPath().resolve("targetDir").toFile();
		Files.createDirectory(targetDir.toPath());

		Assertions.assertTrue(targetDir.exists());
		CapsidProperty<UnixPath> testProperty = new CapsidProperty.Builder<>("testProperty", UnixPath.class)
				.withValidator(PropertyValidators.DIRECTORY_PATH_VALIDATOR).build();

		Project project = getProject();
		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		UnixPath expectedPath = UnixPath.get(targetDir);

		// test converting from string to path
		ext.set("testProperty", expectedPath.toString());
		Assertions.assertEquals(expectedPath, testProperty.findProperty(project));

		// test not converting and just validating
		ext.set("testProperty", expectedPath);
		Assertions.assertEquals(expectedPath, testProperty.findProperty(project));

		// test unsupported type throwing exception
		ext.set("testProperty", new Object());
		Assertions.assertThrows(InvalidCapsidPropertyException.class,
				() -> testProperty.findProperty(project)
		);
	}

	private void assertLocalPropertiesNotNull(boolean writeBeforeAssert) throws IOException {

		if (writeBeforeAssert) {
			writeLocalPropertiesToFile();
		}
		// load properties for project before asserting
		LocalProperties localProperties = LocalProperties.get();
		localProperties.load(getProject());

		for (CapsidProperty<?> capsidPropertyEnum : localProperties.getProperties()) {
			Assertions.assertNotNull(capsidPropertyEnum.findProperty(getProject()));
		}
	}

	private void writeLocalPropertiesToFile() throws IOException {

		writeToFile(new File(getProjectDir(), "local.properties"), new String[] {
				String.format("gameDir=%s", getGameDirPath().toString()),
				String.format("ideaHome=%s", getIdeaHomePath().toString())
		});
	}
}