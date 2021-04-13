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

import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;

/**
 * Core plugins are plugins which Gradle provides as part of its distribution.
 * They are automatically resolved and do not need to be fully qualified.
 *
 * @see <a href="https://docs.gradle.org/current/userguide/plugin_reference.html">
 * 		Gradle ProjectPlugin Reference</a>
 */
public enum CorePlugin {

	/**
	 * The Java plugin adds Java compilation along with testing and bundling
	 * capabilities to a project. It serves as the basis for many of the other
	 * JVM language Gradle plugins. You can find a comprehensive introduction
	 * and overview to the Java ProjectPlugin in the Building Java Projects chapter.
	 *
	 * @see <a href="https://docs.gradle.org/current/userguide/java_plugin.html#java_plugin">
	 * 		The Java Plugin Documentation</a>
	 */
	JAVA("java"),

	/**
	 * The Distribution Plugin facilitates building archives that serve as
	 * distributions of the project. Distribution archives typically contain
	 * the executable application and other supporting files, such as documentation.
	 *
	 * @see <a href="https://docs.gradle.org/current/userguide/distribution_plugin.html">
	 * 		The Distribution Plugin Documentation</a>
	 */
	DISTRIBUTION("distribution");

	private final String id;

	CorePlugin(String id) {
		this.id = id;
	}

	/**
	 * Apply all core plugins to the given {@code Project}.
	 *
	 * @param project {@code Project} to apply the plugins to.
	 */
	public static void applyAll(Project project) {

		PluginManager pluginManager = project.getPluginManager();
		for (CorePlugin plugin : CorePlugin.values())
		{
			pluginManager.apply(plugin.id);
		}
	}

	/** {@code String} identifier used to resolve the plugin. */
	public String getID() {
		return id;
	}
}
