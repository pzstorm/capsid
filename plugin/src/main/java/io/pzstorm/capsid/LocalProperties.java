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
package io.pzstorm.capsid;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.jetbrains.annotations.Nullable;

public enum LocalProperties {

	/**
	 * {@code Path} to Project Zomboid installation directory.
	 */
	GAME_DIR(new Property<>("gameDir", "PZ_DIR_PATH", Path.class, null)),

	/**
	 * {@code Path} to IntelliJ IDEA installation directory.
	 */
	IDEA_HOME(new Property<>("ideaHome", "IDEA_HOME", Path.class, null));

	private static final Properties PROPERTIES = new Properties();

	final Property<?> data;
	LocalProperties(Property<?> property) {
		this.data = property;
	}
	/**
	 * Load properties from local {@code Properties} file.
	 *
	 * @param project {@code Project} to load properties to.
	 *
	 * @return {@code true} if properties were successfully loaded, {@code false} otherwise.
	 * @throws RuntimeException when an {@link IOException} occurred while loading file.
	 */
	public static boolean load(Project project) {

		File propertiesFile = getFile(project);
		if (!propertiesFile.exists())
		{
			CapsidPlugin.LOGGER.warn("WARN: Tried to load local properties, but file does not exist");
			return false;
		}
		// TODO: write integration test to verify properties were loaded
		try (InputStream stream = new FileInputStream(propertiesFile))
		{
			// read properties from byte stream
			PROPERTIES.load(stream);

			// save properties as project extended properties
			ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
			for (LocalProperties property : LocalProperties.values()) {
				ext.set(property.data.name, property.data.getProperty(project));
			}
		}
		catch (IOException e) {
			throw new RuntimeException("An I/O exception occurred while loading local.properties", e);
		}
		return true;
	}

	/** Returns properties {@code File} used to hold local properties. */
	public static File getFile(Project project) {
		return project.getProjectDir().toPath().resolve("local.properties").toFile();
	}

	public static class Property<T> {

		private final String name;
		private final String env;
		private final Class<T> type;
		private final @Nullable T defaultValue;
		private final boolean required;

		// @formatter:off
		private Property(String name, String env, Class<T> type, @Nullable T defaultValue, boolean required) {
			this.name = name; this.env = env; this.type = type;
			this.defaultValue = defaultValue; this.required = required;
		}
		// @formatter:on
		private Property(String name, String env, Class<T> type, @Nullable T defaultValue) {
			this(name, env, type, defaultValue, true);
		}

		@SuppressWarnings("unchecked")
		@Nullable T getProperty(Project project) {

			String property = PROPERTIES.getProperty(name, "");
			if (property.isEmpty())
			{
				// try to find a matching system property first
				String sysProperty = System.getProperty(name);
				if (sysProperty == null)
				{
					// when env parameter is not defined search for env variable with property name
					String sEnv = env != null && !env.isEmpty() ? env : name;

					ProviderFactory providers = project.getProviders();
					Provider<String> envVar = providers.environmentVariable(sEnv).forUseAtConfigurationTime();
					if (envVar.isPresent()) {
						property = envVar.get();
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

		public String getName() {
			return name;
		}
	}
}
