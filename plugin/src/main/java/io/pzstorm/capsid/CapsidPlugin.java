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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.*;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.mod.ModTasks;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.setup.task.SetupTasks;
import io.pzstorm.capsid.util.UnixPath;
import io.pzstorm.capsid.zomboid.task.ZomboidTasks;

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

        // load local properties
        LocalProperties.get().load(project);

        // register all setup tasks
        for (SetupTasks task : SetupTasks.values()) {
            task.register(project);
        }
        // path to game installation directory
        File gameDir = CapsidPlugin.getGameDirProperty(project);

        Convention convention = project.getConvention();
        JavaPluginConvention javaPlugin = convention.getPlugin(JavaPluginConvention.class);
        SourceSet media = javaPlugin.getSourceSets().create("media");

        // set media java source directory
        media.getJava().setSrcDirs(Collections.singletonList("media/lua"));

        // plugin extension will be configured in evaluation phase
        project.afterEvaluate(p ->
        {
            List<File> mediaFiles = Arrays.asList(new File(gameDir, "media").listFiles(pathname ->
                    pathname.isDirectory() && !capsid.isExcludedResource("media/" + pathname.getName()))
            );
            Set<File> resourceSrcDirs = new HashSet<>();
            mediaFiles.forEach(f -> resourceSrcDirs.add(
                    Paths.get(project.getProjectDir().toPath().toString(), "media", f.getName()).toFile())
            );
            // set media resource source directories
            media.getResources().setSrcDirs(resourceSrcDirs);
        });
        // register all mod tasks
        for (ModTasks task : ModTasks.values()) {
            task.register(project);
        }
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();

        // directory containing Project Zomboid classes
        ext.set("zomboidClassesDir", getZomboidClassesDir(project));

        // directory containing Project Zomboid sources
        ext.set("zomboidSourcesDir", getZomboidSourcesDir(project));

        // register project configurations
        ConfigurationContainer configurations = project.getConfigurations();

        Configuration implementation = configurations.getByName("implementation");
        Configuration runtimeOnly = configurations.getByName("runtimeOnly");

        Configuration zomboidRuntimeOnly = configurations.create("zomboidRuntimeOnly");
        Configuration zomboidImplementation = configurations.create("zomboidImplementation");

        runtimeOnly.extendsFrom(zomboidRuntimeOnly);
        implementation.extendsFrom(zomboidImplementation);

        // register project dependencies
        DependencyHandler dependencies = project.getDependencies();

        // Project Zomboid libraries
        dependencies.add("zomboidRuntimeOnly",
                project.fileTree(gameDir, tree -> tree.include("*.jar"))
        );
        // Project Zomboid assets
        dependencies.add("zomboidImplementation",
                project.files(new File(gameDir, "media"))
        );
        // Project Zomboid classes
        String modPzVersion = ModProperties.MOD_PZ_VERSION.findProperty(project);
        if (modPzVersion != null)
        {
            Path jarPath = Paths.get("lib", String.format("zomboid-%s.jar", modPzVersion));
            dependencies.add("zomboidRuntimeOnly", project.files(jarPath));
        }
        // register all zomboid tasks
        for (ZomboidTasks task : ZomboidTasks.values()) {
            task.register(project);
        }
        TaskContainer tasks = project.getTasks();
        tasks.getByName("classes").dependsOn(tasks.getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
    }

    public static File getZomboidClassesDir(Project project) {
        return new File(project.getBuildDir(), "classes/zomboid").getAbsoluteFile();
    }

    public static File getZomboidSourcesDir(Project project) {
        return new File(project.getBuildDir(), "generated/sources/zomboid").getAbsoluteFile();
    }

    /**
     * Returns {@code Path} to Project Zomboid installation directory.
     *
     * @param project {@link Project} requesting the property.
     * @see LocalProperties#GAME_DIR
     */
    public static File getGameDirProperty(Project project) {

        UnixPath property = Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(project));
        return property.convert().toAbsolutePath().toFile();
    }
}
