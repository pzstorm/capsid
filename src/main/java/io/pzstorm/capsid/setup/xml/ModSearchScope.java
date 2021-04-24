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
package io.pzstorm.capsid.setup.xml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.gradle.api.Project;
import org.w3c.dom.Element;

public class ModSearchScope extends XMLDocument {

	public static final ModSearchScope PZ_JAVA = new ModSearchScope(
			"pz-java", "lib:zombie..*||lib:se..*||lib:fmod..*", true
	);
	public static final ModSearchScope PZ_LUA = new ModSearchScope(
			"pz-lua", "lib:media.lua..*", true
	);
	public static final ModSearchScope MOD_LUA = new ModSearchScope(
			"mod-lua", "file[%s.media]:*.lua", false
	);
	public static final ModSearchScope MOD_MEDIA = new ModSearchScope(
			"mod-media", "file[%s.media]:*.*", false
	);
	private final String pattern;
	private final boolean unique;

	private ModSearchScope(String name, String pattern, boolean unique) {

		super(name, Paths.get(".idea/scopes/"));
		this.pattern = pattern;
		this.unique = unique;
	}

	@Override
	protected File getAsFile(boolean create) throws IOException {

		// translate config name to filename (similar to what IDEA is doing)
		String filename = translateConfigNameToFilename();

		// resolve filename for subproject
		if (!unique && !project.getRootDir().equals(project.getProjectDir())) {
			filename += '_' + project.getName();
		}
		// append XML file extension
		filename += ".xml";

		// resolve parent directory of this document
		File parentDir = new File(project.getRootDir(), dirPath.toString());

		// file representing this document
		File xmlFile = new File(parentDir, filename);
		if (create && !xmlFile.exists())
		{
			if (!parentDir.exists() && !parentDir.mkdirs()) {
				throw new IOException("Unable to create directory structure for configuration file '" + filename + '\'');
			}
			if (!xmlFile.createNewFile()) {
				throw new IOException("Unable to create run configuration file '" + filename + '\'');
			}
		}
		return xmlFile;
	}

	@Override
	public ModSearchScope configure(Project project) {

		// <component name="DependencyValidationManager">
		Element component = document.createElement("component");
		component.setAttribute("name", "DependencyValidationManager");
		appendOrReplaceRootElement(component);

		// <scope name="mod-lua" pattern="<pattern>" />
		Element scope = document.createElement("scope");

		scope.setAttribute("name", name);
		String scopeValue = project.getRootProject().getName();
		if (!unique && !project.getProjectDir().equals(project.getRootDir())) {
			scopeValue += '.' + project.getProject().getName();
		}
		String attribute = !unique ? String.format(pattern, scopeValue) : pattern;
		scope.setAttribute("pattern", attribute);

		component.appendChild(scope);
		return (ModSearchScope) super.configure(project);
	}
}
