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
package io.pzstorm.capsid.property.validator;

import java.util.regex.Pattern;

import org.gradle.api.InvalidUserDataException;

/**
 * This class validates a {@code String} representing a semantic version.
 *
 * @see <a href="https://semver.org/">Semantic Versioning</a>
 */
public class SemVersionValidator implements PropertyValidator<String> {

	private static final Pattern PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+(-.*)?$");

	@Override
	public boolean isValid(String property) {
		return PATTERN.matcher(property).find();
	}

	@Override
	public String validate(String property) throws InvalidUserDataException {

		if (!isValid(property))
		{
			String message = "Property '%s' does not follow semantic versioning rules.";
			throw new InvalidUserDataException(String.format(message, property));
		}
		return property;
	}
}
