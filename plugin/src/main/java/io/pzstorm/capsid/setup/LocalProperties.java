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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.jetbrains.annotations.Nullable;

public enum LocalProperties {

	/**
	 * {@code Path} to Project Zomboid installation directory.
	 */
	GAME_DIR(new LocalProperty.Builder<Path>("gameDir")
			.withComment("Path to game installation directory")
			.withType(Path.class).withEnvironmentVar("PZ_DIR_PATH").build()),
	/**
	 * {@code Path} to IntelliJ IDEA installation directory.
	 */
	IDEA_HOME(new LocalProperty.Builder<Path>("ideaHome")
			.withComment("Path to IntelliJ IDEA installation directory")
			.withType(Path.class).withEnvironmentVar("IDEA_HOME").build());

	/**
	 * When the properties are saved to file these lines will be included
	 * at the very top of the file in form of single line comments.
	 */
	private static final @UnmodifiableView List<String> COMMENTS = List.of(
			"This file contains local properties used to configure project build",
			"Note: paths need to be Unix-style where segments need to be separated with forward-slashes (/)",
			"this is for compatibility and stability purposes as backslashes don't play well."
	);
	static final Properties PROPERTIES = new Properties();

	final LocalProperty<?> data;
	LocalProperties(LocalProperty<?> property) {
		this.data = property;
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
		if (!propertiesFile.exists())
		{
			if (!propertiesFile.createNewFile()) {
				throw new IOException("Unable to create new local.properties file");
			}
			return false;
		}
		try (InputStream stream = new FileInputStream(propertiesFile))
		{
			// read properties from byte stream
			PROPERTIES.load(stream);

			// save properties as project extended properties
			ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
			for (LocalProperties property : LocalProperties.values()) {
				ext.set(property.data.getName(), property.data.getProperty());
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
	public static @Nullable LocalProperties getProperty(String name) {
		return Arrays.stream(values()).filter(p -> p.data.name.equals(name)).findFirst().orElse(null);
	}

	/** Returns properties {@code File} used to hold local properties. */
	public static File getFile(Project project) {
		return project.getProjectDir().toPath().resolve("local.properties").toFile();
	}
}
