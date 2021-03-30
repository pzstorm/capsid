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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginUnitTest;
import io.pzstorm.capsid.UnixPath;

class DirectoryPathValidatorTest extends PluginUnitTest {

	@Test
	void shouldCorrectlyValidateDirectoryPath() throws IOException {

		File projectDir = getProject().getProjectDir();
		File targetDir = new File(projectDir, "targetDir");
		Files.createDirectory(targetDir.toPath());

		Assertions.assertTrue(targetDir.exists());
		Assertions.assertTrue(targetDir.isDirectory());

		DirectoryPathValidator validator = LocalProperty.DIRECTORY_PATH_VALIDATOR;

		UnixPath unixTargetDir = UnixPath.get(targetDir);
		Assertions.assertTrue(validator.isValid(unixTargetDir));
		Assertions.assertDoesNotThrow(() -> validator.validate(unixTargetDir));

		// target does not exits
		Assertions.assertTrue(targetDir.delete());
		Assertions.assertFalse(validator.isValid(unixTargetDir));
		Assertions.assertThrows(InvalidUserDataException.class,
				() -> validator.validate(unixTargetDir)
		);
		File targetFile = new File(projectDir, "targetFile");
		Assertions.assertTrue(targetFile.createNewFile());

		// target is not a directory
		UnixPath unixTargetFile = UnixPath.get(targetFile);
		Assertions.assertFalse(validator.isValid(unixTargetFile));
		Assertions.assertThrows(InvalidUserDataException.class,
				() -> validator.validate(unixTargetFile)
		);
	}
}
