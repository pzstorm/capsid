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
import java.nio.file.Paths;
import java.util.*;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.setup.task.SetupTasks;
import io.pzstorm.capsid.util.UnixPath;

@SuppressWarnings("UnstableApiUsage")
public class CapsidPlugin implements Plugin<Project> {

    public static final Logger LOGGER = Logging.getLogger("capsid");

    public void apply(Project project) {

        // add the plugin extension object
        ExtensionContainer extensions = project.getExtensions();
        CapsidPluginExtension capsid = extensions.create("capsid", CapsidPluginExtension.class);

        // apply all core plugins to this project
        CorePlugin.applyAll(project);

        // add Maven Central repository
        project.getRepositories().mavenCentral();

        JavaPluginExtension javaExtension = Objects.requireNonNull(
                extensions.getByType(JavaPluginExtension.class)
        );
        // ZomboidDoc can only be executed with Java 8
        javaExtension.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(8));

        try {
            // load local properties
            LocalProperties.load(project);
        }
        catch (IOException e) {
            throw new GradleException("I/O exception occurred while loading properties");
        }
        // register all setup tasks
        for (SetupTasks task : SetupTasks.values()) {
            task.register(project);
        }
        // path to game installation directory
        UnixPath gameDir = Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(project));

        Convention convention = project.getConvention();
        JavaPluginConvention javaPlugin = convention.getPlugin(JavaPluginConvention.class);
        SourceSet media = javaPlugin.getSourceSets().create("media");

        // set media java source directory
        media.getJava().setSrcDirs(Collections.singletonList("media/lua"));

        List<File> mediaFiles = Arrays.asList(gameDir.convert().resolve("media").toFile().listFiles(pathname ->
                pathname.isDirectory() && !capsid.isExcludedResource("media/" + pathname.getName()))
        );
        Set<File> resourceSrcDirs = new HashSet<>();
        mediaFiles.forEach(f -> resourceSrcDirs.add(
                Paths.get(project.getProjectDir().toPath().toString(), "media", f.getName()).toFile())
        );
        // set media resource source directories
        media.getResources().setSrcDirs(resourceSrcDirs);
    }
}
