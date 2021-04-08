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

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.gradle.api.*;
import org.gradle.api.plugins.ExtraPropertiesExtension;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;

/**
 * This task loads mod metadata information from {@code mod.info} file.
 */
@NonNullApi
public class LoadModInfoTask extends DefaultTask implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {

		setGroup(group);
		setDescription(description);

		File modInfoFile = ProjectProperty.MOD_INFO_FILE.get(project);
		if (modInfoFile.exists())
		{
			Properties properties = new Properties();
			try (InputStream inputStream = new FileInputStream(modInfoFile))
			{
				ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();

				// load properties from properties file
				properties.load(inputStream);

				// load mod properties as project extra properties
				for (Map.Entry<Object, Object> entry : properties.entrySet())
				{
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();

					CapsidPlugin.LOGGER.info("Loading property " + key + ':' + value);
					ext.set("mod." + entry.getKey(), entry.getValue());
				}
				// read repository information from url property
				String sUrl = properties.getProperty("url");
				if (!Strings.isNullOrEmpty(sUrl))
				{
					StringBuilder sb = new StringBuilder();
					char[] charArray = new URL(sUrl).getPath().toCharArray();

					int beforeLastIndex = charArray.length - 1;
					int startIndex = charArray[0] != '/' ? 0 : 1;
					int endIndex = charArray[beforeLastIndex] != '/' ? charArray.length : beforeLastIndex;

					// remove slashes from first and last string index
					for (int i = startIndex; i < endIndex; i++) {
						sb.append(charArray[i]);
					}
					String urlPath = sb.toString();

					List<String> pathElements = Splitter.on("/").splitToList(urlPath);
					if (pathElements.size() != 2) {
						throw new InvalidUserDataException("Unexpected mod url format " + urlPath);
					}
					// these properties are used to generate changelog
					ext.set("repo.owner", pathElements.get(0));
					ext.set("repo.name", pathElements.get(1));
				}
			}
			catch (IOException e) {
				throw new GradleException("I/O exception occurred while loading mod info.", e);
			}
		}
		else CapsidPlugin.LOGGER.warn("WARN: Unable to find mod.info file");
	}
}
