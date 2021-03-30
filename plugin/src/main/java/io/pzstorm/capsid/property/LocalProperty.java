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

import java.util.Objects;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.jetbrains.annotations.Nullable;

import io.pzstorm.capsid.UnixPath;

public class LocalProperty<T> {

	public final String name, env, comment;
	public final Class<T> type;
	public final boolean required;
	private final @Nullable T defaultValue;

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
	 * Try to find the property value in the following locations:
	 * <ul>
	 *     <li>Project properties.</li>
	 *     <li>System properties.</li>
	 *     <li>Environment variables.</li>
	 * </ul>
	 * @return value matching this property or default value.
	 */
	public @Nullable T findProperty(Project project) {

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		if (ext.has(name)) {
			return parseProperty((String) Objects.requireNonNull(ext.get(name)));
		}
		// try to find a matching system property first
		String sysProperty = System.getProperty(name);
		if (sysProperty == null)
		{
			// when env parameter is not defined search for env variable with property name
			String envVar = System.getenv(env != null && !env.isEmpty() ? env : name);
			if (envVar != null) {
				return parseProperty(envVar);
			}
			else if (required && defaultValue == null) {
				throw new InvalidUserDataException("Unable to find local project property " + name);
			}
			else return defaultValue;
		}
		else return parseProperty(sysProperty);
	}

	@SuppressWarnings("unchecked")
	private T parseProperty(String property) {

		if (type.equals(UnixPath.class)) {
			return (T) UnixPath.get(property);
		}
		else if (type.equals(String.class)) {
			return (T) property;
		}
		else throw new InvalidUserDataException("Unsupported local property type " + type.getName());
	}

	public static class Builder<T> {

		private final String name;
		private String env;
		private Class<T> type;
		private String comment = "";
		private @Nullable T defaultValue = null;
		private boolean required = true;

		public Builder(String name) {
			this.name = name;
		}

		public Builder<T> withEnvironmentVar(String env) {
			this.env = env;
			return this;
		}

		public Builder<T> withType(Class<T> type) {
			this.type = type;
			return this;
		}

		public Builder<T> withDefaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder<T> withComment(String comment) {
			this.comment = comment;
			return this;
		}

		public Builder<T> isRequired(boolean required) {
			this.required = required;
			return this;
		}

		public LocalProperty<T> build() {
			return new LocalProperty<>(this);
		}
	}
}
