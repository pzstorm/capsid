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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.property.CapsidProperties;
import io.pzstorm.capsid.property.CapsidProperty;
import io.pzstorm.capsid.property.validator.PropertyValidators;

/**
 * This class represents properties from {@code mod.info} file.
 */
public class ModProperties extends CapsidProperties {

	private static final ModProperties INSTANCE = new ModProperties();
	private static final @Unmodifiable Set<CapsidProperty<?>> PROPERTIES;

	/**
	 * Display name of the mod visible in game main menu.
	 */
	public static final CapsidProperty<String> MOD_NAME;

	/**
	 * Mod description visible in game main menu.
	 */
	public static final CapsidProperty<String> MOD_DESCRIPTION;

	/**
	 * Hyperlink pointing to a project repository or workshop page.
	 */
	public static final CapsidProperty<URL> MOD_URL;

	/**
	 * Latest version of Project Zomboid compatible with this mod.
	 */
	public static final CapsidProperty<String> MOD_PZ_VERSION;

	static
	{
		MOD_NAME = new CapsidProperty.Builder<>("mod.name", String.class)
				.withEnvironmentVar("MOD_NAME").build();

		MOD_DESCRIPTION = new CapsidProperty.Builder<>("mod.desc", String.class)
				.withEnvironmentVar("MOD_DESC")
				.isRequired(false)
				.build();

		MOD_URL = new CapsidProperty.Builder<>("mod.url", URL.class)
				.withEnvironmentVar("MOD_URL")
				.isRequired(false)
				.build();

		//noinspection SpellCheckingInspection
		MOD_PZ_VERSION = new CapsidProperty.Builder<String>("mod.pzversion", String.class)
				.withValidator(PropertyValidators.SEM_VERSION_VALIDATOR)
				.withEnvironmentVar("MOD_VERSION")
				.isRequired(false)
				.build();

		PROPERTIES = ImmutableSet.of(MOD_NAME, MOD_DESCRIPTION, MOD_URL, MOD_PZ_VERSION);
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
	 * Returns all registered mod properties.
	 */
	@Override
	@Contract(pure = true)
	public @Unmodifiable Set<CapsidProperty<?>> getProperties() {
		return PROPERTIES;
	}

	@Override
	public void writeToFile(Project project) throws IOException {

		try (Writer writer = new FileWriter(getFile(project)))
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
				// remove 'mod.' part of the property before appending
				sb.append(property.name.substring(4)).append('=').append(value);
			}
			writer.write(sb.toString());
		}
	}
}
