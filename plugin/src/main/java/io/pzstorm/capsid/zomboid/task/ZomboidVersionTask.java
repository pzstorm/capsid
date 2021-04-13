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
package io.pzstorm.capsid.zomboid.task;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import com.google.common.collect.ImmutableList;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.Configurations;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

/**
 * This task saves and prints Project Zomboid game version.
 */
public class ZomboidVersionTask extends JavaExec implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		setMain("io.cocolabs.pz.zdoc.Main");
		setClasspath(Configurations.ZOMBOID_DOC.resolve(project));
		setArgs(ImmutableList.of("version"));

		setStandardOutput(new ByteArrayOutputStream());
		dependsOn(ZomboidTasks.ZOMBOID_CLASSES.name);
	}

	@TaskAction
	void execute() throws UnsupportedEncodingException {

		// get command output from stream
		ByteArrayOutputStream stream = (ByteArrayOutputStream) getStandardOutput();
		String[] versionText = stream.toString(StandardCharsets.UTF_8.name()).split("\r\n|\r|\n");

		// ZomboidDoc version
		CapsidPlugin.LOGGER.lifecycle(versionText[0]);

		// get version number and classifier (ex. 41.50-IWBUMS)
		ExtraPropertiesExtension ext = getProject().getExtensions().getExtraProperties();
		String gameVersion = versionText[1].substring(12).replaceAll(" ", "").trim();
		ext.set("mod.pzversion", gameVersion);

		CapsidPlugin.LOGGER.lifecycle("game version " + gameVersion);
	}
}
