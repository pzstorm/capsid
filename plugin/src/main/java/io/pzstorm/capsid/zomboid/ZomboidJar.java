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
package io.pzstorm.capsid.zomboid;

import java.util.Objects;
import javax.annotation.Nullable;

import groovy.lang.Closure;

import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.jvm.tasks.Jar;
import org.gradle.util.GUtil;

@NonNullApi
public class ZomboidJar extends Jar {

	private static final String GAME_VERSION_PROPERTY = "mod.pzversion";

	public ZomboidJar() {

		Project project = getProject();
		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		if (ext.has(GAME_VERSION_PROPERTY))
		{
			getArchiveFileName().set(project.provider(() ->
			{
				String name = GUtil.elvis(getArchiveBaseName().getOrNull(), "");
				name = name + maybe(name, getArchiveAppendix().getOrNull());

				Object pGameVersion = Objects.requireNonNull(ext.get(GAME_VERSION_PROPERTY));
				name = name + maybe(name, pGameVersion.toString().trim());
				name = name + maybe(name, getArchiveClassifier().getOrNull());

				String extension = this.getArchiveExtension().getOrNull();
				return name + (GUtil.isTrue(extension) ? "." + extension : "");
			}));
		}
	}

	private static String maybe(@Nullable String prefix, @Nullable String value) {
		return GUtil.isTrue(value) ? GUtil.isTrue(prefix) ? "-".concat(value) : value : "";
	}

	@Override
	public Task configure(Closure closure) {

		Project project = getProject();

		dependsOn(project.getTasks().named("zomboidVersion"));
		project.getTasks().getByName("jar").dependsOn(this);

		return super.configure(closure);
	}
}
