/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.editor;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;

import org.eclipse.chemclipse.logging.core.Logger;

public class SystemEditor {

	private static final Logger logger = Logger.getLogger(SystemEditor.class);

	/*
	 * Open the file in the default system editor.
	 */
	public static boolean open(File file) {

		boolean success = false;
		try {
			if(Desktop.isDesktopSupported()) {
				if(file != null && file.exists()) {
					Desktop.getDesktop().open(file);
					success = true;
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		//
		return success;
	}

	public static boolean browse(URL url) {

		boolean success = false;
		try {
			if(Desktop.isDesktopSupported()) {
				if(url != null) {
					Desktop.getDesktop().browse(url.toURI());
					success = true;
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		//
		return success;
	}
}