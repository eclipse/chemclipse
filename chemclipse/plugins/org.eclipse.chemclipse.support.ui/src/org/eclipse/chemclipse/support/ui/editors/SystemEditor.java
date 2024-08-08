/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.editors;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.swt.program.Program;

public class SystemEditor {

	private static final Logger logger = Logger.getLogger(SystemEditor.class);

	/*
	 * Open the file in the default system editor.
	 */
	public static boolean open(File file) {

		boolean success = false;
		try {
			Program program = Program.findProgram(FilenameUtils.getExtension(file.getName()));
			if(program == null) {
				program = Program.findProgram("txt");
			}
			if(program != null) {
				program.execute(file.getAbsolutePath());
				success = true;
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		return success;
	}

	public static boolean browse(URL url) {

		boolean success = false;
		try {
			if(url != null) {
				Program.launch(url.toString());
				success = true;
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		return success;
	}
}