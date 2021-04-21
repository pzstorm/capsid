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

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import com.google.common.collect.ImmutableSet;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.property.CapsidProperties;
import io.pzstorm.capsid.property.CapsidProperty;
import io.pzstorm.capsid.property.validator.PropertyValidators;
import io.pzstorm.capsid.util.UnixPath;

/**
 * This class represents properties from {@code local.properties} file.
 */
public class LocalProperties extends CapsidProperties {

	/**
	 * {@code Path} to Project Zomboid installation directory.
	 */
	public static final CapsidProperty<UnixPath> GAME_DIR;

	private static final LocalProperties INSTANCE = new LocalProperties();
	private static final @Unmodifiable Set<CapsidProperty<?>> PROPERTIES;

	static
	{
		GAME_DIR = new CapsidProperty.Builder<>("gameDir", UnixPath.class)
				.withComment("Path to game installation directory")
				.withValidator(PropertyValidators.DIRECTORY_PATH_VALIDATOR)
				.withEnvironmentVar("PZ_DIR_PATH")
				.build();

		PROPERTIES = ImmutableSet.of(GAME_DIR);
	}

	private LocalProperties() {
		super(Paths.get("local.properties"));
	}

	/**
	 * Returns singleton instance of {@link LocalProperties}.
	 */
	public static LocalProperties get() {
		return INSTANCE;
	}

	@Override
	@Contract(pure = true)
	public @Unmodifiable Set<CapsidProperty<?>> getProperties() {
		return PROPERTIES;
	}

	/**
	 * Write properties with comments to {@code local.properties} file.
	 *
	 * @param project {@link Project} instance used to resolve the {@code File}.
	 *
	 * @throws IOException when an I/O exception occurred while writing to file.
	 */
	@Override
	public void writeToFile(Project project) throws IOException {

		try (Writer writer = Files.newBufferedWriter(getFile(project).toPath(), StandardCharsets.UTF_8))
		{
			StringBuilder sb = new StringBuilder();
			// file comments at the top of the file
			for (String comment : new String[]{
					"This file contains local properties used to configure project build",
					"Note: paths need to be Unix-style where segments need to be separated with forward-slashes (/)",
					"this is for compatibility and stability purposes as backslashes don't play well." }) {
				sb.append('#').append(comment).append('\n');
			}
			// remove last '\n' character
			sb.deleteCharAt(sb.length() - 1);

			// write properties and their comments to file
			for (CapsidProperty<?> property : PROPERTIES)
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
