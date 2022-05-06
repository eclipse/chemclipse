/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.support;

import java.util.ArrayList;
import java.util.List;

/**
 * This class helps to compile a file extensions string.<br/>
 * E.g. "*.ionXML;*.ionxml;*.IonXML"
 */
public class FileExtensionCompiler {

	private static final String SELECT_ALL = "*";
	private List<String> extensions;

	/**
	 * Use as input a file extension string, e.g. ".ionXML";
	 * 
	 * @param extension
	 */
	public FileExtensionCompiler(String extension, boolean useLowerAndUpperCase) {

		/*
		 * Avoid null pointer exceptions.
		 */
		if(extension == null) {
			extension = "";
		}
		extensions = new ArrayList<>();
		/*
		 * Add ".ionXML"
		 */
		addExtension(extension);
		/*
		 * Try to add the lower and upper case extension.
		 */
		if(useLowerAndUpperCase) {
			addExtension(extension.toLowerCase());
			addExtension(extension.toUpperCase());
		}
	}

	/**
	 * Returns e.g. "*.ionXML;*.ionxml;*.IonXML".
	 * 
	 * @return String
	 */
	public String getCompiledFileExtension() {

		StringBuilder builder = new StringBuilder();
		int last = extensions.size() - 1;
		/*
		 * Use last as the last extension should not be extended by an ";".
		 */
		for(int index = 0; index < last; index++) {
			builder.append(SELECT_ALL);
			builder.append(extensions.get(index));
			builder.append(";");
		}
		builder.append(SELECT_ALL);
		builder.append(extensions.get(last));
		return builder.toString();
	}

	private void addExtension(String extension) {

		/*
		 * Add the extension only if it's still not in the list.
		 */
		if(!extensions.contains(extension)) {
			extensions.add(extension);
		}
	}
}
