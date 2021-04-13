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

import java.io.File;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.jetbrains.annotations.Unmodifiable;

import com.google.common.collect.ImmutableSet;

public class ProjectProperty<T> {

	/**
	 * Directory containing Project Zomboid classes.
	 */
	public static final ProjectProperty<File> ZOMBOID_CLASSES_DIR;
	/**
	 * Directory containing Project Zomboid sources.
	 */
	public static final ProjectProperty<File> ZOMBOID_SOURCES_DIR;
	/**
	 * Directory containing Lua library compiled with ZomboidDoc.
	 */
	public static final ProjectProperty<File> ZDOC_LUA_DIR;
	/**
	 * File containing last ZomboidDoc version text.
	 */
	public static final ProjectProperty<File> ZDOC_VERSION_FILE;
	/**
	 * File where mod properties are stored.
	 */
	public static final ProjectProperty<File> MOD_INFO_FILE;
	/**
	 * Directory containing built classes for {@code media} module.
	 */
	public static final ProjectProperty<File> MEDIA_CLASSES_DIR;
	/**
	 * Directory containing resources for {@code media} module.
	 */
	public static final ProjectProperty<File> MEDIA_RESOURCES_DIR;
	@Unmodifiable
	static final Set<ProjectProperty<?>> PROPERTIES;

	static
	{
		ZOMBOID_CLASSES_DIR = new ProjectProperty<>("zomboidClassesDir", project ->
				project.file(project.getBuildDir() + "/classes/zomboid")
		);
		ZOMBOID_SOURCES_DIR = new ProjectProperty<>("zomboidSourcesDir", project ->
				project.file(project.getBuildDir() + "/generated/sources/zomboid")
		);
		ZDOC_LUA_DIR = new ProjectProperty<>("zDocLuaDir", project ->
				project.file(project.getBuildDir().getPath() + "/generated/sources/zdoc")
		);
		ZDOC_VERSION_FILE = new ProjectProperty<>("zDocVersionFile", project ->
				project.file("zdoc.version")
		);
		MOD_INFO_FILE = new ProjectProperty<>("modInfoFile", project ->
				project.file("mod.info")
		);
		MEDIA_CLASSES_DIR = new ProjectProperty<>("mediaClassesDir", project ->
				new File(project.getBuildDir(), "classes/lua/media")
		);
		MEDIA_RESOURCES_DIR = new ProjectProperty<>("mediaResourcesDir", project ->
				new File(project.getBuildDir(), "resources/media")
		);
		PROPERTIES = ImmutableSet.of(
				ZOMBOID_CLASSES_DIR, ZOMBOID_SOURCES_DIR, ZDOC_LUA_DIR,
				ZDOC_VERSION_FILE, MOD_INFO_FILE,
				MEDIA_CLASSES_DIR, MEDIA_RESOURCES_DIR
		);
	}

	private final String propertyName;
	private final ProjectPropertiesSupplier<T> propertySupplier;

	private ProjectProperty(String name, ProjectPropertiesSupplier<T> supplier) {
		this.propertyName = name;
		this.propertySupplier = supplier;
	}

	/**
	 * Register this property with the given {@link Project}.
	 *
	 * @return instance of the registered property.
	 *
	 * @throws ClassCastException if property is not assignable to {@code T}.
	 */
	T register(Project project) {

		T property = propertySupplier.getProjectProperty(project);

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		ext.set(propertyName, property);

		return property;
	}

	/**
	 * Returns property for given project.
	 */
	public T get(Project project) {
		return propertySupplier.getProjectProperty(project);
	}
}
