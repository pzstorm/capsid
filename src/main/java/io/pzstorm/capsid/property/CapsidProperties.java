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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public abstract class CapsidProperties {

	private final Path propertiesFilePath;

	public CapsidProperties(Path propertiesFilePath) {
		this.propertiesFilePath = propertiesFilePath;
	}

	/**
	 * Returns all registered local properties.
	 */
	@Unmodifiable
	@Contract(pure = true)
	protected abstract Set<CapsidProperty<?>> getProperties();

	/**
	 * Load properties from file to given {@link Project} instance.
	 *
	 * @param project {@code Project} to load properties to.
	 *
	 * @return {@code true} if properties were successfully loaded, {@code false} otherwise.
	 * @throws GradleException when an I/O error occurred while loading file.
	 */
	public boolean load(Project project) throws GradleException {

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
			for (CapsidProperty<?> property : getProperties())
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
			throw new GradleException("I/O exception occurred while loading capsid properties", e);
		}
		return true;
	}

	/**
	 * Find property that matches the given name.
	 *
	 * @param name property name to match.
	 */
	public @Nullable CapsidProperty<?> getProperty(String name) {
		return getProperties().stream().filter(p -> p.name.equals(name)).findFirst().orElse(null);
	}

	/**
	 * Returns properties {@code File} used to hold local properties.
	 */
	@Contract(pure = true)
	public File getFile(Project project) {
		return project.getProjectDir().toPath().resolve(propertiesFilePath).toFile();
	}

	/**
	 * Write these properties to file for given {@link Project} instance.
	 *
	 * @param project {@link Project} instance to write properties for.
	 *
	 * @throws IOException when an I/O exception occurred while writing to file.
	 */
	public abstract void writeToFile(Project project) throws IOException;
}
