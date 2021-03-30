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

import org.gradle.api.InvalidUserDataException;

import io.pzstorm.capsid.util.UnixPath;

/**
 * This class validates a directory {@link UnixPath}.
 */
public class DirectoryPathValidator extends PropertyValidator<UnixPath> {

	// make constructor available only from package
	DirectoryPathValidator() {}

	/**
	 * Returns {@code true} if given path represents an existing directory.
	 * @param property property to validate.
	 */
	@Override
	public boolean isValid(UnixPath property) {

		File file = property.convert().toFile();
		return file.exists() && file.isDirectory();
	}

	@Override
	public UnixPath validate(UnixPath property) {

		if (!isValid(property))
		{
			String message = "Invalid directory path '%s'";
			throw new InvalidUserDataException(String.format(message, property.toString()));
		}
		return property;
	}
}
