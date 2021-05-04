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
package io.pzstorm.capsid.mod;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.property.CapsidProperties;
import io.pzstorm.capsid.property.CapsidProperty;
import io.pzstorm.capsid.util.SemanticVersion;

/**
 * This class represents properties from {@code mod.info} file.
 */
@SuppressWarnings("WeakerAccess")
public class ModProperties extends CapsidProperties {

	/**
	 * Display name of the mod visible in game main menu.
	 */
	public static final CapsidProperty<String> MOD_NAME;
	/**
	 * Path to image that represents this mod.
	 */
	public static final CapsidProperty<Path> MOD_POSTER;
	/**
	 * Mod description visible in game main menu.
	 */
	public static final CapsidProperty<String> MOD_DESCRIPTION;
	/**
	 * Unique string that identifies this mod.
	 */
	public static final CapsidProperty<String> MOD_ID;
	/**
	 * Hyperlink pointing to a project repository or workshop page.
	 */
	public static final CapsidProperty<URL> MOD_URL;
	/**
	 * Current mod project semantic version.
	 */
	public static final CapsidProperty<SemanticVersion> MOD_VERSION;
	/**
	 * Latest version of Project Zomboid compatible with this mod.
	 */
	public static final CapsidProperty<String> PZ_VERSION;

	/**
	 * This map maps {@link CapsidProperty} entries to their respective metadata entries.
	 * Metadata with specified names (keys) will be set as project properties (values).
	 */
	public static final Map<String, CapsidProperty<?>> METADATA_MAPPING;

	private static final ModProperties INSTANCE = new ModProperties();
	private static final @Unmodifiable Set<CapsidProperty<?>> PROPERTIES;

	static
	{
		MOD_NAME = new CapsidProperty.Builder<>("modname", String.class)
				.withEnvironmentVar("MOD_NAME")
				.withDefaultValue("Project Zomboid mod")
				.build();

		MOD_POSTER = new CapsidProperty.Builder<>("modposter", Path.class)
				.withEnvironmentVar("MOD_POSTER")
				.withDefaultValue(Paths.get("poster.png"))
				.isRequired(false)
				.build();

		MOD_DESCRIPTION = new CapsidProperty.Builder<>("moddesc", String.class)
				.withEnvironmentVar("MOD_DESC")
				.withDefaultValue("No description available.")
				.isRequired(false)
				.build();

		MOD_ID = new CapsidProperty.Builder<>("modid", String.class)
				.withEnvironmentVar("MOD_ID")
				.build();

		MOD_URL = new CapsidProperty.Builder<>("modurl", URL.class)
				.withEnvironmentVar("MOD_URL")
				.isRequired(false)
				.build();

		MOD_VERSION = new CapsidProperty.Builder<>("modversion", SemanticVersion.class)
				.withEnvironmentVar("MOD_VERSION")
				.withDefaultValue(new SemanticVersion("0.1.0"))
				.build();

		//noinspection SpellCheckingInspection
		PZ_VERSION = new CapsidProperty.Builder<>("pzversion", String.class)
				.withEnvironmentVar("PZ_VERSION")
				.isRequired(false)
				.build();

		METADATA_MAPPING = ImmutableMap.<String, CapsidProperty<?>>builder()
				.put("name", ModProperties.MOD_NAME)
				.put("poster", ModProperties.MOD_POSTER)
				.put("description", ModProperties.MOD_DESCRIPTION)
				.put("id", ModProperties.MOD_ID)
				.put("url", ModProperties.MOD_URL)
				.put("modversion", ModProperties.MOD_VERSION)
				.put("pzversion", ModProperties.PZ_VERSION)
				.build();

		PROPERTIES = ImmutableSet.of(
				MOD_NAME, MOD_POSTER, MOD_DESCRIPTION,
				MOD_ID, MOD_URL, MOD_VERSION, PZ_VERSION
		);
	}

	private ModProperties() {
		super(Paths.get("mod.info"));
	}

	/**
	 * Returns singleton instance of {@link ModProperties}.
	 */
	public static ModProperties get() {
		return INSTANCE;
	}

	/**
	 * Returns the mapping key associated with given {@link CapsidProperty}.
	 *
	 * @param property {@code CapsidProperty} associated with key to return.
	 * @return mapping key or empty {@code String} if no mapping is found.
	 */
	public static String getMetadataMappingKey(CapsidProperty<?> property) {

		Optional<Map.Entry<String, CapsidProperty<?>>> entry = METADATA_MAPPING.entrySet()
				.stream().filter(m -> m.getValue() == property).findFirst();

		return entry.isPresent() ? entry.get().getKey() : "";
	}

	/**
	 * Returns all registered mod properties.
	 */
	@Override
	@Contract(pure = true)
	public @Unmodifiable Set<CapsidProperty<?>> getProperties() {
		return PROPERTIES;
	}

	@Override
	public void writeToFile(Project project) throws IOException {

		try (Writer writer = Files.newBufferedWriter(getFile(project).toPath(), StandardCharsets.UTF_8))
		{
			StringBuilder sb = new StringBuilder();
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
				String name = getMetadataMappingKey(property);
				sb.append(name).append('=').append(value).append('\n');
			}
			writer.write(sb.toString());
		}
	}
}
