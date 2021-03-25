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

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;

public enum LocalProperties {

	/**
	 * {@code Path} to Project Zomboid installation directory.
	 */
	GAME_DIR("gameDir", Path.class),

	/**
	 * {@code Path} to IntelliJ IDEA installation directory.
	 */
	IDEA_HOME("ideaHome", Path.class);

	private static final Properties PROPERTIES = new Properties();

	final String name;
	final Class<?> type;
	final boolean required;

	//@formatter:off
	LocalProperties(String name, Class<?> type, boolean required) {
		this.name = name; this.type = type; this.required = required;
	}
	// @formatter:on
	LocalProperties(String name, Class<?> type) {
		this(name, type, true);
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
			for (LocalProperties property : LocalProperties.values())
			{
				String oProperty = PROPERTIES.getProperty(property.name);
				if (property.required && oProperty == null)
				{
					String msg = "WARN: Local property %s is not defined";
					CapsidPlugin.LOGGER.warn(String.format(msg, property.name));
					continue;
				}
				// this is where we will save properties to
				ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();

				// save the project property as a Path
				if (property.type.equals(Path.class)) {
					ext.set(property.name, Paths.get(oProperty));
				}
				else ext.set(property.name, oProperty);
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
}
