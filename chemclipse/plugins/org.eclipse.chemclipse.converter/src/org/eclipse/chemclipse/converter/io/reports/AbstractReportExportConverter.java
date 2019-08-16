/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.reports;

import java.io.File;

public abstract class AbstractReportExportConverter {

	protected static final String DELIMITER = ",";

	protected File getFile(File sinkDirectory, String fileName, String extension) {

		return getFile(sinkDirectory, "", fileName, extension);
	}

	protected File getFile(File sinkDirectory, String folder, String fileName, String extension) {

		String directory = sinkDirectory.getAbsolutePath();
		if(!directory.endsWith(File.separator)) {
			directory += File.separator;
		}
		/*
		 * Try to create the specified folder on demand.
		 */
		if(folder != null && !folder.equals("")) {
			if(!folder.endsWith(File.separator)) {
				folder += File.separator;
			}
			//
			directory += folder;
			//
			File exportFolder = new File(directory);
			if(!exportFolder.exists()) {
				exportFolder.mkdir();
			}
		}
		//
		return new File(directory + fileName + extension);
	}
}
