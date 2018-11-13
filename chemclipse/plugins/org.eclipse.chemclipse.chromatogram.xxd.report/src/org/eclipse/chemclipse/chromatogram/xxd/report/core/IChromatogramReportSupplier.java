/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.core;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;

public interface IChromatogramReportSupplier {

	/**
	 * The id of the extension point: e.g.
	 * (org.eclipse.chemclipse.chromatogram.xxd.report.supplier.peaks)
	 * 
	 * @return String
	 */
	String getId();

	/**
	 * A short description of the functionality of the extension point.
	 * 
	 * @return String
	 */
	String getDescription();

	/**
	 * The report name that will be shown in the FileDialog.
	 * 
	 * @return String
	 */
	String getReportName();

	/**
	 * The file extension, e.g. Peaks (.pdf) will be returned.<br/>
	 * If the file extension has a value, it starts in every case with a point.
	 * 
	 * @return String
	 */
	String getFileExtension();

	/**
	 * The default file name, e.g. PeakReport.pdf.
	 * 
	 * @return String
	 */
	String getFileName();

	/**
	 * TODO: either returns a bean-like class or with annotations ..., with a public default constructor, ... or returns <code>null</code> if no filter settings are associated
	 * 
	 * @return
	 */
	Class<? extends IChromatogramReportSettings> getSettingsClass();
}
