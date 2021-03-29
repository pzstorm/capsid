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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.w3c.dom.Document;

public abstract class XMLDocument {

	/**
	 * Instance of factory used to create new {@link Document} instances.
	 */
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

	final String name;
	final Document document;

	Project project;

	public XMLDocument(String name) {

		this.name = name;
		try {
			this.document = FACTORY.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Contract("_ -> this")
	public XMLDocument configure(Project project) {
		this.project = project;
		return this;
	}

	/**
	 * Write contents of this document to {@code XML} file.
	 *
	 * @throws TransformerException if an unrecoverable error occurred while creating an
	 * 		an instance of {@code Transformer} or during the course of the transformation.
	 * @throws IOException if the given file does not exist but cannot be created,
	 * 		or cannot be opened for any other reason.
	 */
	protected void writeToFile(Transformer transformer, File file) throws IOException, TransformerException {

		DOMSource source = new DOMSource(document);
		String filename = file.getName();

		// create destination file before trying to write to it
		if (!file.exists())
		{
			File parentFile = file.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new IOException("Unable to create directory structure for configuration file '" + filename + '\'');
			}
			if (!file.createNewFile()) {
				throw new IOException("Unable to create run configuration file '" + filename + '\'');
			}
		}
		// enable xml line indenting
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		// omit xml declaration at top of the file
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		// write to file and return destination file
		transformer.transform(source, new StreamResult(new FileWriter(file)));
	}

	public abstract void writeToFile() throws TransformerException, IOException;
}
