/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors;

import java.io.File;

import org.eclipse.chemclipse.processing.converter.ISupplier;

public class CalibrationFileSupplier implements ISupplier {

	@Override
	public String getId() {

		return "org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.cal.file";
	}

	@Override
	public String getDescription() {

		return "This is the AMDIS *.cal Calibration File support.";
	}

	@Override
	public String getFilterName() {

		return "AMDIS Calibration File";
	}

	@Override
	public String getFileExtension() {

		return ".cal";
	}

	@Override
	public String getFileName() {

		return "Calibration";
	}

	@Override
	public String getDirectoryExtension() {

		return "";
	}

	@Override
	public boolean isExportable() {

		return true;
	}

	@Override
	public boolean isImportable() {

		return true;
	}

	@Override
	public boolean isMatchMagicNumber(File file) {

		String fileName = file.getName().toLowerCase();
		if(fileName.endsWith(".cal")) {
			return true;
		} else {
			return false;
		}
	}
}
