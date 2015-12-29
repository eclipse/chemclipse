/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.model;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class ChromatogramReportSupplierEntry implements IChromatogramReportSupplierEntry {

	private String reportFolderOrFile = "";
	private String reportSupplierId = "";

	/**
	 * Set the output file path and the converter id.
	 * 
	 * @param outputFile
	 * @param converterId
	 */
	public ChromatogramReportSupplierEntry(String reportFolderOrFile, String converterId) {
		if(reportFolderOrFile != null && converterId != null) {
			this.reportFolderOrFile = reportFolderOrFile;
			this.reportSupplierId = converterId;
		}
	}

	@Override
	public String getReportFolderOrFile() {

		return reportFolderOrFile;
	}

	@Override
	public String getReportSupplierId() {

		return reportSupplierId;
	}
}
