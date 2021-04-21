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
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.util.SemanticVersion;

/**
 * This class holds properties related to tracking versions.
 */
public class VersionProperties extends CapsidProperties {

	/**
	 * {@code ZomboidDoc} version registered last time {@code ZomboidDoc} task was run.
	 */
	public static final CapsidProperty<SemanticVersion> LAST_ZDOC_VERSION;

	/**
	 * This property will be {@code true} if {@link #LAST_ZDOC_VERSION} recently changed.
	 */
	public static final CapsidProperty<Boolean> HAS_ZDOC_VERSION_CHANGED;

	private static final VersionProperties INSTANCE = new VersionProperties();
	private static final @Unmodifiable Set<CapsidProperty<?>> PROPERTIES;

	static {
		LAST_ZDOC_VERSION = new CapsidProperty.Builder<>("lastZDocVersion", SemanticVersion.class)
				.withComment("ZomboidDoc version registered last time ZomboidDoc task was run")
				.withDefaultValue(new SemanticVersion("0.0.0"))
				.build();

		HAS_ZDOC_VERSION_CHANGED = new CapsidProperty.Builder<>("zDocVersionChanged", Boolean.class)
				.withComment("This property will be true if zDoc version recently changed.")
				.withDefaultValue(false)
				.build();

		PROPERTIES = ImmutableSet.of(LAST_ZDOC_VERSION, HAS_ZDOC_VERSION_CHANGED);
	}

	private VersionProperties() {
		super(Paths.get("version.properties"));
	}

	/**
	 * Returns singleton instance of {@link VersionProperties}.
	 */
	public static VersionProperties get() {
		return INSTANCE;
	}

	@Override
	@Contract(pure = true)
	public @Unmodifiable Set<CapsidProperty<?>> getProperties() {
		return PROPERTIES;
	}

	/**
	 * Write properties with comments to {@code version.properties} file.
	 *
	 * @param project {@link Project} instance used to resolve the {@code File}.
	 *
	 * @throws IOException when an I/O exception occurred while writing to file.
	 */
	@Override
	public void writeToFile(Project project) throws IOException {

		File target = getFile(project);
		if (!target.exists() && ! target.createNewFile()) {
			throw new IOException("Unable to create 'version.properties' file in root directory");
		}
		try (Writer writer = Files.newBufferedWriter(getFile(project).toPath(), StandardCharsets.UTF_8))
		{
			StringBuilder sb = new StringBuilder();

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
					sb.append('#').append(comment).append('\n');
				}
				sb.append(property.name).append('=').append(value).append("\n\n");
			}
			writer.write(sb.toString());
		}
	}
}
