/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support;

public interface IChromatogramTags {

	String UTF8 = "UTF-8";
	/*
	 * Start and Stop Element /
	 */
	String CHROMATOGRAM = "Chromatogram";
	/*
	 * Identifier
	 */
	String IDENTIFIER = "Identifier";
	String IDENTIFIER_ID = "ChemClipseChromatogram";
	/*
	 * Header
	 */
	String HEADER = "Header";
	String OPERATOR = "Operator";
	String DATE = "Date";
	String MISC_INFO = "MiscInfo";
	/*
	 * Edit History
	 */
	String EDIT_HISTORY = "EditHistory";
	String EDIT_INFORMATION = "EditInformation";
	String EDIT_DATE = "date";
	/*
	 * Attributes
	 */
	String RETENTION_TIME = "retentionTime";
	String RETENTION_INDEX = "retentionIndex";
	String TOTAL_SIGNAL = "totalSignal";
	/*
	 * Elements
	 */
	String SCANS = "Scans";
	String SUPPLIER_MASS_SPECTRUM = "SupplierMassSpectrum";
	String SUPPLIER_IONS = "SupplierMassFragments";
}
