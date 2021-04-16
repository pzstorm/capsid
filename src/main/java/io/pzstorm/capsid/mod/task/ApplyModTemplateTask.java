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
package io.pzstorm.capsid.mod.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.tasks.Copy;

import com.google.common.base.Splitter;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.util.Utils;

/**
 * This task applies Project Zomboid mod template to root directory.
 */
public class ApplyModTemplateTask extends Copy implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		// template files will be copied to this directory
		File templateTempDir;
		try {
			/* extract the files from jar to a temporary directory,
			 * then copy from there to project root directory
			 */
			templateTempDir = Files.createTempDirectory("capsidModTemplate").toFile();
			List<String> templateFilePaths = Splitter.on('\n').splitToList(
					Utils.readResourceAsTextFromStream(CapsidPlugin.class, "template/template.txt")
			);
			for (String templateFilePath : templateFilePaths)
			{
				// make sure directory structure exists before we write file from stream
				File targetFile = new File(templateTempDir, templateFilePath);
				File parentFile = targetFile.getParentFile();
				if (!parentFile.exists() && !parentFile.mkdirs()) {
					throw new IOException("Unable to create directory structure for path '" + parentFile.getPath() + '\'');
				}
				Utils.readResourceAsFileFromStream(CapsidPlugin.class, templateFilePath, targetFile);
			}
		}
		catch (IOException e) {
			throw new GradleException("I/O exception occurred while applying mod template", e);
		}
		from(new File(templateTempDir, "template"));
		into(project.getProjectDir());

		// overwrite if duplicate found in destination
		setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
	}
}
