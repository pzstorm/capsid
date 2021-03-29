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

import org.gradle.api.Project;
import org.w3c.dom.Element;

public class ModSearchScope extends XMLDocument {

	public static final ModSearchScope MOD_LUA = new ModSearchScope(
			"mod-lua", "file[%s.media]:*.lua"
	);
	public static final ModSearchScope MOD_MEDIA = new ModSearchScope(
			"mod-media", "file[%s.media]:*.*"
	);
	private final String pattern;

	public ModSearchScope(String name, String pattern) {
		super(name, ".idea/scopes/");
		this.pattern = pattern;
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
		scope.setAttribute("pattern", String.format(pattern, project.getRootProject().getName()));

		component.appendChild(scope);
		return (ModSearchScope) super.configure(project);
	}
}
