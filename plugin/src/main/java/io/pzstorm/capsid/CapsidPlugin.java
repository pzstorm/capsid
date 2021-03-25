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
import java.io.IOException;
import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.Plugin;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

@SuppressWarnings("UnstableApiUsage")
public class CapsidPlugin implements Plugin<Project> {

    public static final Logger LOGGER = Logging.getLogger("capsid");

    public void apply(Project project) {

        // apply all core plugins to this project
        CorePlugin.applyAll(project);

        // add Maven Central repository
        project.getRepositories().mavenCentral();

        // get project DSL extensions
        ExtensionContainer extensions = project.getExtensions();

        JavaPluginExtension java = Objects.requireNonNull(
                extensions.getByType(JavaPluginExtension.class)
        );
        // ZomboidDoc can only be executed with Java 8
        java.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(8));

        // load local properties (create file if it doesn't exist)
        try {
            File localProperties = LocalProperties.getFile();
            if (!localProperties.exists() && !localProperties.createNewFile()) {
                throw new IOException("Unable to create new local.properties file");
            }
            LocalProperties.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
