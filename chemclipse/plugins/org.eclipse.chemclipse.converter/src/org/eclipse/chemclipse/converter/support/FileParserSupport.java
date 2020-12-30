/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.support;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileParserSupport {

	/**
	 * The first matched/sorted file is returned that matches the prefix and extension pattern.
	 * Null if none could be matched.
	 * 
	 * @param directory
	 * @param filePrefix
	 * @param fileExtension
	 * @return {@link File}
	 */
	public static File matchFile(File directory, String filePrefix, String fileExtension) {

		Set<String> filePrefixes = new HashSet<>();
		filePrefixes.add(filePrefix);
		List<File> files = match(directory, filePrefixes, fileExtension);
		return files.size() > 0 ? files.get(0) : null;
	}

	/**
	 * The first matched/sorted file is returned that matches the prefix and extension pattern.
	 * Null if none could be matched.
	 * 
	 * @param directory
	 * @param filePrefix
	 * @param fileExtension
	 * @return {@link File}
	 */
	public static File matchFile(File directory, Set<String> filePrefixes, String fileExtension) {

		List<File> files = match(directory, filePrefixes, fileExtension);
		return files.size() > 0 ? files.get(0) : null;
	}

	/**
	 * A list of file(s) is returned that matches the prefix and extension pattern.
	 * 
	 * @param directory
	 * @param filePrefix
	 * @param fileExtension
	 * @return {@link List}
	 */
	public static List<File> match(File directory, Set<String> filePrefixes, String fileExtension) {

		String extension = fileExtension.toLowerCase();
		Set<String> prefixes = new HashSet<>();
		for(String prefix : filePrefixes) {
			prefixes.add(prefix.toLowerCase());
		}
		//
		List<File> files = new ArrayList<>();
		for(File file : directory.listFiles()) {
			String name = file.getName().toLowerCase();
			if(name.endsWith(extension)) {
				exitloop:
				for(String prefix : prefixes) {
					if(name.startsWith(prefix)) {
						files.add(file);
						break exitloop;
					}
				}
			}
		}
		//
		Collections.sort(files);
		return files;
	}

	/**
	 * Try to get the file given by the directory and name.
	 * Additional tries to lower and upper case of the name are performed to get the file.
	 * 
	 * @param baseFileDirectory
	 * @param name
	 * @return {@link File}
	 */
	public static File find(String directory, String name) {

		File file = new File(directory + File.separator + name);
		if(!file.exists()) {
			file = new File(directory + File.separator + name.toLowerCase());
			if(!file.exists()) {
				file = new File(directory + File.separator + name.toUpperCase());
			}
		}
		//
		return file;
	}
}
