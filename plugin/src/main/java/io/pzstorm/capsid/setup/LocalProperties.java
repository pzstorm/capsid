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

import java.io.*;
import java.util.*;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.UnixPath;
import io.pzstorm.capsid.property.LocalProperty;

public class LocalProperties {

	@UnmodifiableView
	private static final Set<LocalProperty<?>> PROPERTIES;

	/**
	 * {@code Path} to Project Zomboid installation directory.
	 */
	public static final LocalProperty<UnixPath> GAME_DIR;

	/**
	 * {@code Path} to IntelliJ IDEA installation directory.
	 */
	public static final LocalProperty<UnixPath> IDEA_HOME;

	static
	{
		Set<LocalProperty<?>> properties = new HashSet<>();

		GAME_DIR = new LocalProperty.Builder<UnixPath>("gameDir")
				.withComment("Path to game installation directory")
				.withType(UnixPath.class).withEnvironmentVar("PZ_DIR_PATH")
				.build();

		IDEA_HOME = new LocalProperty.Builder<UnixPath>("ideaHome")
				.withComment("Path to IntelliJ IDEA installation directory")
				.withType(UnixPath.class).withEnvironmentVar("IDEA_HOME")
				.build();

		properties.add(GAME_DIR);
		properties.add(IDEA_HOME);

		PROPERTIES = Collections.unmodifiableSet(properties);
	}

	private LocalProperties() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns all registered local properties.
	 */
	@UnmodifiableView
	public static Set<LocalProperty<?>> get() {
		return PROPERTIES;
	}

	/**
	 * Load properties from local {@code Properties} file.
	 *
	 * @param project {@code Project} to load properties to.
	 *
	 * @return {@code true} if properties were successfully loaded, {@code false} otherwise.
	 * @throws IOException when an I/O error occurred while loading file.
	 */
	public static boolean load(Project project) throws IOException {

		File propertiesFile = getFile(project);
		if (!propertiesFile.exists()) {
			return false;
		}
		try (InputStream stream = new FileInputStream(propertiesFile))
		{
			// read properties from byte stream
			Properties properties = new Properties();
			properties.load(stream);

			// save properties as project extended properties
			ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
			for (LocalProperty<?> property : PROPERTIES)
			{
				String foundProperty = properties.getProperty(property.name, "");
				if (!foundProperty.isEmpty()) {
					ext.set(property.name, foundProperty);
				}
				// if no property found from file try other locations
				else if (!ext.has(property.name)) {
					ext.set(property.name, property.findProperty(project));
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException("An I/O exception occurred while loading local.properties", e);
		}
		return true;
	}

	/**
	 * Find a property that matches the given name.
	 * @param name property name to match.
	 */
	public static @Nullable LocalProperty<?> getProperty(String name) {
		return PROPERTIES.stream().filter(p -> p.name.equals(name)).findFirst().orElse(null);
	}

	/** Returns properties {@code File} used to hold local properties. */
	public static File getFile(Project project) {
		return project.getProjectDir().toPath().resolve("local.properties").toFile();
	}

	/**
	 * Write properties with comments to {@code local.properties} file.
	 *
	 * @param project {@link Project} instance used to resolve the {@code File}.
	 * @throws IOException when an I/O exception occurred while writing to file.
	 */
	public static void writeToFile(Project project) throws IOException {

		try (Writer writer = new FileWriter(getFile(project)))
		{
			StringBuilder sb = new StringBuilder();
			// file comments at the top of the file
			for (String comment : new String[] {
					"This file contains local properties used to configure project build",
					"Note: paths need to be Unix-style where segments need to be separated with forward-slashes (/)",
					"this is for compatibility and stability purposes as backslashes don't play well." })
			{
				sb.append('#').append(comment).append('\n');
			}
			// remove last '\n' character
			sb.deleteCharAt(sb.length() - 1);

			// write properties and their comments to file
			for (LocalProperty<?> property : PROPERTIES)
			{
				String value = "";
				Object oProperty = property.findProperty(project);
				if (oProperty != null) {
					value = oProperty.toString();
				}
				else if (property.required) {
					CapsidPlugin.LOGGER.warn("WARN: Missing property value " + property.name);
				}
				String comment = property.comment;
				if (comment != null && !comment.isEmpty()) {
					sb.append("\n\n").append('#').append(comment).append('\n');
				}
				sb.append(property.name).append('=').append(value);
			}
			writer.write(sb.toString());
		}
	}
}
