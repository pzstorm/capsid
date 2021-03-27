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
package io.pzstorm.capsid.setup;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.api.InvalidUserDataException;
import org.jetbrains.annotations.Nullable;

public class LocalProperty<T> {

	final String name, env, comment;
	final Class<T> type;
	final @Nullable T defaultValue;
	final boolean required;

	private LocalProperty(Builder<T> builder) {

		this.name = builder.name;
		this.env = builder.env;
		this.comment = builder.comment;
		this.type = builder.type;
		this.defaultValue = builder.defaultValue;
		this.required = builder.required;
	}
	/**
	 * <p>Returns the value assigned to key matching this property.</p>
	 * If no property was found try the resolve the value in the following order:
	 * <ul>
	 *     <li>Find the value in system properties.</li>
	 *     <li>Find the value in environment variables.</li>
	 *     <li>Return the default value.</li>
	 * </ul>
	 * @return value matching this property or default value.
	 */
	@SuppressWarnings("unchecked")
	@Nullable T getProperty() {

		String property = LocalProperties.PROPERTIES.getProperty(name, "");
		if (property.isEmpty())
		{
			// try to find a matching system property first
			String sysProperty = System.getProperty(name);
			if (sysProperty == null)
			{
				// when env parameter is not defined search for env variable with property name
				String envVar = System.getenv(env != null && !env.isEmpty() ? env : name);
				if (envVar != null) {
					property = envVar;
				}
				else if (required && defaultValue == null) {
					throw new InvalidUserDataException("Unable to find local project property " + name);
				}
				else return defaultValue;
			}
			else property = sysProperty;
		}
		if (type.equals(Path.class)) {
			return (T) Paths.get(property);
		}
		else if (type.equals(String.class)) {
			return (T) property;
		}
		else throw new InvalidUserDataException("Unsupported local property type " + type.getName());
	}

	/** Returns the name of this property. */
	public String getName() {
		return name;
	}

	static class Builder<T> {

		private final String name;
		private String env;
		private Class<T> type;
		private String comment = "";
		private @Nullable T defaultValue = null;
		private boolean required = true;

		Builder(String name) {
			this.name = name;
		}

		Builder<T> withEnvironmentVar(String env) {
			this.env = env;
			return this;
		}

		Builder<T> withType(Class<T> type) {
			this.type = type;
			return this;
		}

		Builder<T> withDefaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		Builder<T> withComment(String comment) {
			this.comment = comment;
			return this;
		}

		Builder<T> isRequired(boolean required) {
			this.required = required;
			return this;
		}

		LocalProperty<T> build() {
			return new LocalProperty<>(this);
		}
	}
}
