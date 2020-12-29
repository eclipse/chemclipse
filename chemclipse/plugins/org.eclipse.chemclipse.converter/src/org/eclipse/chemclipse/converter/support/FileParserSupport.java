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
import java.util.List;

public class FileParserSupport {

	public static List<File> match(File directory, String filePrefix, String fileExtension) {

		String prefix = filePrefix.toLowerCase();
		String extension = fileExtension.toLowerCase();
		//
		List<File> files = new ArrayList<>();
		for(File file : directory.listFiles()) {
			String name = file.getName().toLowerCase();
			if(name.startsWith(prefix) && name.endsWith(extension)) {
				files.add(file);
			}
		}
		//
		Collections.sort(files);
		return files;
	}
}
