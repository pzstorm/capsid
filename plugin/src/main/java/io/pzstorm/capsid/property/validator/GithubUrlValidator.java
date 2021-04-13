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

import java.net.URL;

import org.gradle.api.InvalidUserDataException;

import io.pzstorm.capsid.property.InvalidCapsidPropertyException;

/**
 * This class validates a {@code URL} as a valid Github page {@code URL}.
 *
 * @see <a href="https://github.com/">Github website</a>
 */
public class GithubUrlValidator implements PropertyValidator<URL> {

	/**
	 * Returns {@code true} if given {@code URL} is a valid link to a Github page.
	 *
	 * @param property property to validate.
	 */
	@Override
	public boolean isValid(URL property) {
		return property.getHost().equals("github.com");
	}

	@Override
	public URL validate(URL property) throws InvalidUserDataException {

		if (!isValid(property))
		{
			String msg = "URL '%s' is not a valid Github URL";
			throw new InvalidCapsidPropertyException(String.format(msg, property.toString()));
		}
		else return property;
	}
}
