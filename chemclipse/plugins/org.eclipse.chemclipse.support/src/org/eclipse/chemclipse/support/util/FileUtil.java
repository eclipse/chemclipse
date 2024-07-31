/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.util;

import java.io.File;

public class FileUtil {

	/**
	 * Use only static methods.
	 */
	private FileUtil() {

	}

	/**
	 * Checks if the file has an extension or not.
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean fileHasExtension(File file) {

		boolean hasExtension = false;
		/*
		 * Extract the file name
		 */
		if(file != null) {
			String fileName = file.getName();
			if(fileName != null && !"".equals(fileName)) {
				/*
				 * Extract the file name.
				 */
				String[] parts = fileName.split("\\.");
				if(parts.length >= 2) {
					hasExtension = true;
				} else {
					hasExtension = false;
				}
			}
		}
		return hasExtension;
	}
}
