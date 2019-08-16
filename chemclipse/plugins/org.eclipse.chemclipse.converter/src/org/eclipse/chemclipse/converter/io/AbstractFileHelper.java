/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io;

import java.io.File;

public abstract class AbstractFileHelper implements IFileHelper {

	@Override
	public String getBaseFileDirectory(File file) {

		String baseFileDirectory = "";
		/*
		 * Extract the path.
		 */
		if(file != null) {
			if(file.isFile()) {
				baseFileDirectory = file.getParent();
			} else if(file.isDirectory()) {
				baseFileDirectory = file.getPath();
			}
		}
		return baseFileDirectory;
	}

	@Override
	public String getBaseFileName(File file) {

		String baseFileName = "";
		/*
		 * Extract the file name
		 */
		if(file != null) {
			String fileName = file.getName();
			if(fileName != "" && fileName != null) {
				/*
				 * Extract the file name.
				 */
				String[] parts = fileName.split("\\.");
				if(parts.length > 2) {
					StringBuilder builder = new StringBuilder();
					for(int i = 0; i < parts.length - 1; i++) {
						builder.append(parts[i]);
						builder.append(".");
					}
					String name = builder.toString();
					baseFileName = name.substring(0, name.length() - 1);
				} else {
					/*
					 * If there are not 2 parts, it's assumed that the file had no extension.
					 */
					if(parts.length == 2) {
						baseFileName = parts[0];
					}
				}
			}
		}
		return baseFileName;
	}
}
