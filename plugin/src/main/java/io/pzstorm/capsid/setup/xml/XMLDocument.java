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
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class XMLDocument {

	/**
	 * Instance of factory used to create new {@link Document} instances.
	 */
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

	final String name;
	final Document document;
	private final Path dirPath;

	/** Instance of {@code Project} that owns this document. */
	private Project project;

	/**
	 * @param name name of the document
	 * @param dirPath path to parent directory relative to project root directory.
	 */
	XMLDocument(String name, Path dirPath) {

		this.name = name;
		this.dirPath = dirPath;
		try {
			this.document = FACTORY.newDocumentBuilder().newDocument();

			// configure document like this to omit "standalone" attribute in XML element
			// https://community.oracle.com/tech/developers/discussion/comment/6845084
			this.document.setXmlStandalone(true);
		}
		catch (ParserConfigurationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	XMLDocument configure(Project project) {
		this.project = project;
		return this;
	}

	/**
	 * <p>Append or replace child {@link Node} with the given element.</p>
	 * This is a way to prevent {@code HIERARCHY_REQUEST_ERR} from occurring.
	 *
	 * @param element element to append or replace child with.
	 */
	void appendOrReplaceRootElement(Element element) {

		Node childNode = document.getFirstChild();
		if (childNode == null) {
			document.appendChild(element);
		}
		else document.replaceChild(element, childNode);
	}

	/**
	 * Returns translated config name to filename. The translation process is similar to
	 * what IDEA is doing when it is naming {@code XML} configuration files.
	 */
	private String translateConfigNameToFilename() {

		// replace dashed with underscores and whitespaces with underscores
		return name.replace('-', '_').replaceAll("\\s", "_")
				// remove all non-word characters and consecutive with single underscores
				.replaceAll("[^\\w_]", "").replaceAll("_+", "_") + ".xml";
	}

	/**
	 * Returns {@code File} representing this document.
	 *
	 * @param create whether to create the resulting file if it doesn't exist already.
	 *
	 * @throws IOException if unable to create directory structure or resulting file.
	 */
	private File getAsFile(boolean create) throws IOException {

		// translate config name to filename (similar to what IDEA is doing)
		String filename = translateConfigNameToFilename();

		// resolve parent directory of this document
		File parentDir = new File(project.getProjectDir(), dirPath.toString());

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

	/**
	 * Create and configure {@link Transformer} instance to be used for writing document to file.
	 * Note that the returned instance can be further configured as desired.
	 *
	 * @throws TransformerConfigurationException when it is not possible to create a {@code Transformer} instance.
	 */
	Transformer createAndConfigureTransformer() throws TransformerConfigurationException {

		// create a new transformer instance
		Transformer transformer = TransformerFactory.newInstance().newTransformer();

		// parent directory
		if (project == null) {
			throw new GradleException("Tried writing XMLDocument to file with null project");
		}
		// enable xml line indenting
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		// omit xml declaration at top of the file
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		return transformer;
	}

	/**
	 * Write contents of this document to {@code XML} file.
	 *
	 * @throws TransformerException if an unrecoverable error occurred while creating an
	 * 		an instance of {@code Transformer} or during the course of the transformation.
	 * @throws GradleException when {@code Project} instance is {@code null}.
	 * @throws IOException if the given file does not exist but cannot be created,
	 * 		or cannot be opened for any other reason.
	 */
	public void writeToFile() throws IOException, TransformerException {

		Writer writer = Files.newBufferedWriter(getAsFile(true).toPath(), StandardCharsets.UTF_8);
		createAndConfigureTransformer().transform(new DOMSource(document), new StreamResult(writer));
	}

	/**
	 * <p>Returns project associated with this {@code XMLDocument}.</p>
	 *
	 * @return {@code Project} instance or {@code null} if document was not configured.
	 */
	protected @Nullable Project getProject() {
		return project;
	}
}
