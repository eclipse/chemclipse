/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.files;

import java.io.File;

public interface ISupplierFileIdentifier {

	String TYPE_MSD = "MSD";
	String TYPE_CSD = "CSD";
	String TYPE_WSD = "WSD";
	String TYPE_XIR = "XIR";
	String TYPE_NMR = "NMR";
	String TYPE_DATABASE_MSD = "DATABASE_MSD";
	String TYPE_DATABASE_CSD = "DATABASE_CSD";
	String TYPE_DATABASE_WSD = "DATABASE_WSD";
	String TYPE_DATABASE_NMR = "DATABASE_NMR";
	String TYPE_PEAKLIST_MSD = "PEAKLIST MSD"; // e.g. Matlab Parafac Peaklist *.mpl
	String TYPE_SCAN_MSD = "SCAN_MSD"; // MALDI-TOF
	String TYPE_CAL = "CAL"; // RI Calibration
	String TYPE_PCR = "PCR"; // Polymerase Chain Reaction
	String TYPE_SEQ = "SEQ"; // Sequence
	String TYPE_MTH = "MTH"; // Method
	String TYPE_QDB = "QDB"; // Quantitation Database

	/**
	 * Returns the identifier type.
	 * 
	 * @return String
	 */
	String getType();

	/**
	 * Check whether the file is a supplied file type or not.
	 * 
	 * @param file
	 * @return boolean
	 */
	boolean isSupplierFile(File file);

	/**
	 * Check whether the file is a supplied file type directory or not.
	 * 
	 * @param file
	 * @return boolean
	 */
	boolean isSupplierFileDirectory(File file);

	/**
	 * Try to match the magic number of the file format.
	 * If true, it's pretty likely that the format can be imported.
	 * 
	 * @param file
	 * @return true
	 */
	boolean isMatchMagicNumber(File file);
}
