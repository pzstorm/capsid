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
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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
	final String dirPath;
	final Document document;

	/** Instance of {@code Project} that owns this document. */
	private Project project;

	public XMLDocument(String name, String dirPath) {

		this.name = name;
		this.dirPath = dirPath;
		try {
			this.document = FACTORY.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public XMLDocument configure(Project project) {
		this.project = project;
		return this;
	}

	/**
	 * <p>Append or replace child {@link Node} with the given element.</p>
	 * This is a way to prevent {@code HIERARCHY_REQUEST_ERR} from occurring.
	 *
	 * @param element element to append or replace child with.
	 */
	protected void appendOrReplaceRootElement(Element element) {

		Node childNode = document.getFirstChild();
		if (childNode == null) {
			document.appendChild(element);
		}
		else document.replaceChild(element, childNode);
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

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		// translate config name to filename (similar to what IDEA is doing)
		String filename = name
				// replace dashed with underscores
				.replace('-', '_')
				// replace whitespaces with underscores
				.replaceAll("\\s", "_")
				// remove all non-word characters
				.replaceAll("[^\\w_]", "")
				// replace consecutive with single underscores
				.replaceAll("_+", "_") + ".xml";

		// parent directory
		if (project == null) {
			throw new GradleException("Tried writing XMLDocument to file with null project");
		}
		File projectDir = project.getProjectDir();
		File parentDir = projectDir.toPath().resolve(dirPath).toFile();

		// file to print the contents of this document
		File destination = new File(parentDir, filename);

		// create destination file before trying to write to it
		if (!destination.exists())
		{
			if (!parentDir.exists() && !parentDir.mkdirs()) {
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
		transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(destination)));
	}

	/**
	 * <p>Returns project associated with this {@code XMLDocument}.</p>
	 * @return {@code Project} instance or {@code null} if document was not configured.
	 */
	protected @Nullable Project getProject() {
		return project;
	}
}
