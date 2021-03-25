package io.pzstorm.capsid;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * A simple unit test for the 'io.pzstorm.capsid.greeting' plugin.
 */
class CapsidPluginTest {

    @Test
    void pluginRegistersATask() {

        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("io.pzstorm.capsid.greeting");

        // Verify the result
        Assertions.assertNotNull(project.getTasks().findByName("greeting"));
    }
}
