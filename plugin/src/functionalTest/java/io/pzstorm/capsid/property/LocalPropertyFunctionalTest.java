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

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.UnixPath;

class LocalPropertyFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldCorrectlyConvertLocalPropertyToUnixPath() throws IOException {

		File targetDir = getProjectDir().toPath().resolve("targetDir").toFile();
		Files.createDirectory(targetDir.toPath());

		Assertions.assertTrue(targetDir.exists());
		LocalProperty<UnixPath> testProperty = new LocalProperty.Builder<UnixPath>("testProperty")
				.withType(UnixPath.class)
				.withValidator(LocalProperty.DIRECTORY_PATH_VALIDATOR)
				.build();

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
		Assertions.assertThrows(InvalidUserDataException.class,
				() -> testProperty.findProperty(project)
		);
	}
}