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
package io.pzstorm.capsid.setup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LaunchRunConfig {

	/** Instance of factory used to create new {@link Document} instances. */
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

	public static final LaunchRunConfig RUN_ZOMBOID = new LaunchRunConfig(
			"Run Zomboid",
			new VmParameter.Builder().build()
	);
	public static final LaunchRunConfig RUN_ZOMBOID_LOCAL = new LaunchRunConfig(
			"Run Zomboid (local)",
			new VmParameter.Builder().withSteamIntegration(false).build()
	);
	public static final LaunchRunConfig DEBUG_ZOMBOID = new LaunchRunConfig(
			"Debug Zomboid",
			new VmParameter.Builder().withDebug(true).build()
	);
	public static final LaunchRunConfig DEBUG_ZOMBOID_LOCAL = new LaunchRunConfig(
			"Debug Zomboid (local)",
			new VmParameter.Builder().withDebug(true).withSteamIntegration(false).build()
	);
	private final String name;
	private final Document document;
	private final VmParameter vmParameters;

	private Project project;

	public LaunchRunConfig(String name, VmParameter vmParameters) {

		this.name = name;
		try {
			this.document = FACTORY.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new ExceptionInInitializerError(e);
		}
		this.vmParameters = vmParameters;
	}

	/**
	 * Configure this instance of {@code ZomboidLaunchRunConfig} for given {@code Project}.
	 *
	 * @param project instance of {@link Project} requesting configuration.
	 * @return an instance of this {@code ZomboidLaunchRunConfig}.
	 *
	 * @throws InvalidUserDataException if {@code gameDir} local property is not initialized.
	 */
	@Contract("_ -> this")
	public LaunchRunConfig configure(Project project) {

		this.project = project;

		// <component name="ProjectRunConfigurationManager">
		Element component = document.createElement("component");
		component.setAttribute("name", "ProjectRunConfigurationManager");
		/*
		 * make sure that we replace the root element if one exists already,
		 * otherwise HIERARCHY_REQUEST_ERR error will be thrown
		 */
		Node childNode = document.getFirstChild();
		if (childNode == null) {
			document.appendChild(component);
		}
		else document.replaceChild(component, childNode);

		// <configuration default="false" name="<name>" type="Application" factoryName="Application">
		Element configuration = document.createElement("configuration");

		configuration.setAttribute("default", "false");
		configuration.setAttribute("name", name);
		configuration.setAttribute("type", "Application");
		configuration.setAttribute("factoryName", "Application");
		component.appendChild(configuration);

		// <option name="MAIN_CLASS_NAME" value="zombie.gameStates.MainScreenState" />
		Element optionClass = document.createElement("option");

		optionClass.setAttribute("name", "MAIN_CLASS_NAME");
		optionClass.setAttribute("value", "zombie.gameStates.MainScreenState");
		configuration.appendChild(optionClass);

		// <module name="<rootProjectName>.main" />",
		Element module = document.createElement("module");

		String rootProjectName = project.getRootProject().getName();
		module.setAttribute("name", rootProjectName + ".main");
		configuration.appendChild(module);

		// <option name="VM_PARAMETERS" value="<launchParameters> />
		Element optionParams = document.createElement("option");

		optionParams.setAttribute("name", "VM_PARAMETERS");
		optionParams.setAttribute("value", vmParameters.toString());
		configuration.appendChild(optionParams);

		// <option name="WORKING_DIRECTORY" value="<game_dir>" />
		Element optionWorkDir = document.createElement("option");

		Path gameDir = LocalProperties.GAME_DIR.findProperty(project);
		if (gameDir == null) {
			throw new InvalidUserDataException("Unable to find gameDir local property");
		}
		optionWorkDir.setAttribute("name", "WORKING_DIRECTORY");
		optionWorkDir.setAttribute("value", gameDir.toString().replace('\\', '/'));
		configuration.appendChild(optionWorkDir);

		// TODO: uncomment this when zomboidClasses task is implemented
		// <method v="2">
//		Element method = document.createElement("method");
//		method.setAttribute("v", "2");
//		configuration.appendChild(method);
//
//		Element optionBeforeRunTask = document.createElement("option");
//
//		optionBeforeRunTask.setAttribute("name", "Gradle.BeforeRunTask");
//		optionBeforeRunTask.setAttribute("enabled", "true");
//		optionBeforeRunTask.setAttribute("tasks", "zomboidClasses");
//		optionBeforeRunTask.setAttribute("externalProjectPath", project.getPath());
//		optionBeforeRunTask.setAttribute("vmOptions", "");
//		optionBeforeRunTask.setAttribute("scriptParameters", "");
//
//		method.appendChild(optionBeforeRunTask);

		return this;
	}

	/**
	 * Write this launch run configuration to {@code XML} file.
	 *
	 * @return {@code File} the launch run configuration was written to.
	 *
	 * @throws TransformerException if an unrecoverable error occurred while creating an
	 * 		an instance of {@code Transformer} or during the course of the transformation.
	 * @throws IOException if the run configuration file does not exist but cannot be created,
	 * 		or cannot be opened for any other reason.
	 */
	public File writeToFile() throws TransformerException, IOException {

		// translate config name to filename (similar to what IDEA is doing)
		String filename = name.replaceAll("\\s", "_")
				.replaceAll("[^\\w_]", "").replaceAll("__", "_") + ".xml";

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		File destination = new File(project.getProjectDir(), ".idea/runConfigurations/" + filename);

		// create destination file before trying to write to it
		if (!destination.exists())
		{
			File parentFile = destination.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new IOException("Unable to create directory structure for configuration file '" + filename + '\'');
			}
			if (!destination.createNewFile()) {
				throw new IOException("Unable to create run configuration file '" + filename + '\'');
			}
		}
		// enable xml line indenting
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		// omit xml declaration at top of the file
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		// write to file and return destination file
		transformer.transform(source, new StreamResult(new FileWriter(destination)));
		return destination;
	}
}
