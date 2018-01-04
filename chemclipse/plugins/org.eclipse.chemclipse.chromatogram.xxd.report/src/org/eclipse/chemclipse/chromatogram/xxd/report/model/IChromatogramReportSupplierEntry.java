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
package org.eclipse.chemclipse.chromatogram.xxd.report.model;

/**
 * @author Dr. Philip Wenig
 * 
 */
public interface IChromatogramReportSupplierEntry {

	/**
	 * Returns the report folder or file which shall be appended.
	 * 
	 * @return String
	 */
	String getReportFolderOrFile();

	/**
	 * Returns the report supplier id.
	 * 
	 * @return String
	 */
	String getReportSupplierId();
}
