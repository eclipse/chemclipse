/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
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

public class ExportSettings {

	private File sourceDirectory;
	private File sinkDirectory;
	private boolean exportSequences = true;
	private boolean exportReports = true;
	private boolean exportBatches = true;
	private boolean extendedFileRecognition = false;

	public File getSourceDirectory() {

		return sourceDirectory;
	}

	public void setSourceDirectory(File sourceDirectory) {

		this.sourceDirectory = sourceDirectory;
	}

	public File getSinkDirectory() {

		return sinkDirectory;
	}

	public void setSinkDirectory(File sinkDirectory) {

		this.sinkDirectory = sinkDirectory;
	}

	public boolean isExportSequences() {

		return exportSequences;
	}

	public void setExportSequences(boolean exportSequences) {

		this.exportSequences = exportSequences;
	}

	public boolean isExportReports() {

		return exportReports;
	}

	public void setExportReports(boolean exportReports) {

		this.exportReports = exportReports;
	}

	public boolean isExportBatches() {

		return exportBatches;
	}

	public void setExportBatches(boolean exportBatches) {

		this.exportBatches = exportBatches;
	}

	public boolean isExtendedFileRecognition() {

		return extendedFileRecognition;
	}

	public void setExtendedFileRecognition(boolean extendedFileRecognition) {

		this.extendedFileRecognition = extendedFileRecognition;
	}
}
