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

import com.google.common.collect.ImmutableMap;
import org.gradle.api.Project;
import org.w3c.dom.Element;

import java.nio.file.Paths;
import java.util.Map;

public class GradleRunConfig extends XMLDocument {

	//@formatter:off
	public static final GradleRunConfig SETUP_WORKSPACE = new GradleRunConfig(
			"setupWorkspace", ImmutableMap.<String, OptionType>builder()
					.put("setGameDirectory", OptionType.BEFORE_RUN_TASK)
					.put("createRunConfigurations", OptionType.BEFORE_RUN_TASK)
					.put("createSearchScopes", OptionType.BEFORE_RUN_TASK)
					.put("createDiscordIntegration", OptionType.BEFORE_RUN_TASK)
					.put("zomboidJar", OptionType.BEFORE_RUN_TASK)
					.put("decompileZomboid", OptionType.BEFORE_RUN_TASK)
					.put("annotateZomboidLua", OptionType.BEFORE_RUN_TASK)
					.put("compileZomboidLua", OptionType.BEFORE_RUN_TASK)
					.put("zomboidLuaJar", OptionType.BEFORE_RUN_TASK)
					.build()
	);
	public static final GradleRunConfig INITIALIZE_MOD = new GradleRunConfig(
			"initializeMod", ImmutableMap.of(
					"setGameDirectory", OptionType.BEFORE_RUN_TASK,
					"initModMetadata", OptionType.BEFORE_RUN_TASK,
					"createModStructure", OptionType.BEFORE_RUN_TASK,
					"applyModTemplate", OptionType.BEFORE_RUN_TASK
			)
	);//@formatter:on
	private final Map<String, OptionType> options;

	public GradleRunConfig(String name, Map<String, OptionType> options) {

		super(name, Paths.get(".idea/runConfigurations"));
		this.options = options;
	}

	@Override
	public XMLDocument configure(Project project) {

		// <component name="ProjectRunConfigurationManager">
		Element component = document.createElement("component");
		component.setAttribute("name", "ProjectRunConfigurationManager");
		appendOrReplaceRootElement(component);

		// <configuration default="false" name=<name> type="GradleRunConfiguration" factoryName="Gradle">
		Element configuration = document.createElement("configuration");

		configuration.setAttribute("default", "false");
		configuration.setAttribute("name", name);
		configuration.setAttribute("type", "GradleRunConfiguration");
		configuration.setAttribute("factoryName", "Gradle");
		component.appendChild(configuration);

		// <ExternalSystemSettings>
		Element externalSystemSettings = document.createElement("ExternalSystemSettings");
		configuration.appendChild(externalSystemSettings);

		// <option name="executionName" />
		Element executionNameOption = document.createElement("option");
		executionNameOption.setAttribute("name", "executionName");
		externalSystemSettings.appendChild(executionNameOption);

		// <option name="externalProjectPath" value="$PROJECT_DIR$" />
		Element externalProjectPath = document.createElement("option");
		externalProjectPath.setAttribute("name", "externalProjectPath");
		externalProjectPath.setAttribute("value", "$PROJECT_DIR$");
		externalSystemSettings.appendChild(externalProjectPath);

		// <option name="externalSystemIdString" value="GRADLE" />
		Element externalSystemIdString = document.createElement("option");
		externalSystemIdString.setAttribute("name", "externalSystemIdString");
		externalSystemIdString.setAttribute("value", "GRADLE");
		externalSystemSettings.appendChild(externalSystemIdString);

		// <option name="scriptParameters" value="" />
		Element scriptParameters = document.createElement("option");
		scriptParameters.setAttribute("name", "scriptParameters");
		scriptParameters.setAttribute("value", "");
		externalSystemSettings.appendChild(scriptParameters);

		// <ExternalSystemDebugServerProcess>true</ExternalSystemDebugServerProcess>
		Element extSysDebugProc = document.createElement("ExternalSystemDebugServerProcess");
		extSysDebugProc.setTextContent("true");
		configuration.appendChild(extSysDebugProc);

		// <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
		Element extSysReattachProc = document.createElement("ExternalSystemReattachDebugProcess");
		extSysReattachProc.setTextContent("true");
		configuration.appendChild(extSysReattachProc);

		// <DebugAllEnabled>false</DebugAllEnabled>
		Element debugAllEnabled = document.createElement("DebugAllEnabled");
		debugAllEnabled.setTextContent("true");
		configuration.appendChild(debugAllEnabled);

		// <method v="2">
		Element method = document.createElement("method");
		method.setAttribute("v", "2");
		configuration.appendChild(method);

		for (Map.Entry<String, OptionType> entry : options.entrySet())
		{
			OptionType type = entry.getValue();
			if (type == OptionType.BEFORE_RUN_TASK)
			{
				Element beforeRunTask = document.createElement("option");
				beforeRunTask.setAttribute("name", type.name);
				beforeRunTask.setAttribute("enabled", "true");
				beforeRunTask.setAttribute("tasks", entry.getKey());
				beforeRunTask.setAttribute("externalProjectPath", "$PROJECT_DIR$");
				beforeRunTask.setAttribute("vmOptions", "");
				beforeRunTask.setAttribute("scriptParameters", "");
				method.appendChild(beforeRunTask);
			}
			else if (type == OptionType.RUN_CONFIG_TASK)
			{
				Element decompileZomboid = document.createElement("option");
				decompileZomboid.setAttribute("name", type.name);
				decompileZomboid.setAttribute("enabled", "true");
				decompileZomboid.setAttribute("run_configuration_name", entry.getKey());
				decompileZomboid.setAttribute("run_configuration_type", "GradleRunConfiguration");
				method.appendChild(decompileZomboid);
			}
		}
		return super.configure(project);
	}

	public enum OptionType {

		BEFORE_RUN_TASK("Gradle.BeforeRunTask"),
		RUN_CONFIG_TASK("RunConfigurationTask");

		private final String name;
		OptionType(String name) {
			this.name = name;
		}
	}
}
