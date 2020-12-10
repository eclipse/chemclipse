/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.support;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.support.PropertiesUtil;

public class FilesSupport {

	public static File getLogDirectory() {

		return new File(PropertiesUtil.getLogFilePath());
	}

	public static long getUsedSpaceInMegabytes() {

		long usedSpace = 0;
		File directory = getLogDirectory();
		for(File file : directory.listFiles()) {
			if(file.isFile() && file.getName().contains(PropertiesUtil.LOG_EXTENSION)) {
				usedSpace += file.length();
			}
		}
		//
		long megabytes = Math.round(usedSpace / 1000000.0d);
		return megabytes;
	}

	public static List<File> getSortedLogFiles(File directory) {

		List<File> files = new ArrayList<>();
		if(directory != null && directory.isDirectory()) {
			/*
			 * *.log
			 */
			for(File file : directory.listFiles()) {
				if(file.isFile() && file.getName().contains(PropertiesUtil.LOG_EXTENSION)) {
					files.add(file);
				}
			}
			/*
			 * Sort latest file on top.
			 */
			Collections.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
		}
		//
		return files;
	}
}
