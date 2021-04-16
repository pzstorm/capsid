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

import org.gradle.api.InvalidUserDataException;
import org.jetbrains.annotations.Contract;

import io.pzstorm.capsid.property.InvalidCapsidPropertyException;

/**
 * This class validates properties according to implementation criteria.
 *
 * @param <T> type of property.
 */
public interface PropertyValidator<T> {

	/**
	 * Returns {@code true} if the given property is valid.
	 *
	 * @param property property to validate.
	 */
	@Contract(pure = true)
	boolean isValid(T property);

	/**
	 * Validate the given property.
	 *
	 * @param property property to validate.
	 * @return the given property.
	 *
	 * @throws InvalidCapsidPropertyException if property is invalid.
	 */
	@Contract(pure = true, value = "_ -> param1")
	T validate(T property) throws InvalidUserDataException;
}
