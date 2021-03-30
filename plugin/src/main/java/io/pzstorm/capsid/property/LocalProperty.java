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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import io.pzstorm.capsid.UnixPath;

/**
 * This class represents a property loaded from local properties file.
 *
 * @param <T> type of property.
 */
public class LocalProperty<T> {

	public static final DirectoryPathValidator DIRECTORY_PATH_VALIDATOR = new DirectoryPathValidator();

	public final String name;
	public final String comment;
	public final Class<T> type;
	public final boolean required;

	private final String env;
	private final @Nullable T defaultValue;
	private final PropertyValidator<T> validator;

	private LocalProperty(Builder<T> builder) {

		this.name = builder.name;
		this.env = builder.env;
		this.comment = builder.comment;
		this.type = builder.type;
		this.defaultValue = builder.defaultValue;
		this.required = builder.required;
		this.validator = builder.validator;
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
			return convertAndValidateProperty((String) Objects.requireNonNull(ext.get(name)));
		}
		// try to find a matching system property first
		String sysProperty = System.getProperty(name);
		if (sysProperty == null)
		{
			// when env parameter is not defined search for env variable with property name
			String envVar = System.getenv(env != null && !env.isEmpty() ? env : name);
			if (envVar != null) {
				return convertAndValidateProperty(envVar);
			}
			else if (required && defaultValue == null) {
				throw new InvalidUserDataException("Unable to find local project property " + name);
			}
			else return defaultValue;
		}
		else return convertAndValidateProperty(sysProperty);
	}

	/**
	 * Convert and validate the given property to {@code Class} {@link #type}.
	 *
	 * @param property property to convert and validate.
	 * @return converted and validated property.
	 *
	 * @throws InvalidUserDataException if property is of unsupported type.
	 */
	@SuppressWarnings("unchecked")
	private T convertAndValidateProperty(String property) {

		if (type.equals(UnixPath.class)) {
			return validator.validate((T) UnixPath.get(property));
		}
		else if (type.equals(String.class)) {
			return validator.validate((T) property);
		}
		else throw new InvalidUserDataException("Unsupported local property type " + type.getName());
	}

	public static class Builder<T> {

		private final String name;
		private String env;
		private Class<T> type;
		private String comment = "";
		private PropertyValidator<T> validator;
		private @Nullable T defaultValue = null;
		private boolean required = true;

		/**
		 * Start building {@link LocalProperty} instance.
		 * @param name name of the property to build.
		 */
		public Builder(String name) {
			this.name = name;
		}

		/**
		 * Associate property with given environment variable.
		 */
		@Contract("_ -> this")
		public Builder<T> withEnvironmentVar(String env) {
			this.env = env;
			return this;
		}

		/**
		 * Set property to be of given class type.
		 */
		@Contract("_ -> this")
		public Builder<T> withType(Class<T> type) {
			this.type = type;
			return this;
		}

		/**
		 * Set default value to use when property is not found.
		 *
		 * @see #isRequired(boolean)
		 */
		@Contract("_ -> this")
		public Builder<T> withDefaultValue(@Nullable T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		/**
		 * Set property comment to be written when saved to {@code Properties} file.
		 */
		@Contract("_ -> this")
		public Builder<T> withComment(String comment) {
			this.comment = comment;
			return this;
		}

		/**
		 * Marks the property as <i>required</i> or not. Finding the property with
		 * {@link #findProperty(Project)} will produce an exception if a required
		 * property cannot be found and no default value is designated.
		 */
		@Contract("_ -> this")
		public Builder<T> isRequired(boolean required) {
			this.required = required;
			return this;
		}

		/**
		 * Designate a property validator used to assert if property is valid or not.
		 */
		@Contract("_ -> this")
		public Builder<T> withValidator(PropertyValidator<T> validator) {
			this.validator = validator;
			return this;
		}

		/**
		 * Returns a new instance of {@link LocalProperty} with held configuration.
		 */
		@Contract(" -> new")
		public LocalProperty<T> build() {
			return new LocalProperty<>(this);
		}
	}
}
