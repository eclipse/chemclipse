/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

public interface INistSupport {

	/*
	 * Note: The LibraryService requires the legacy label to load spectra for targets.
	 * If one loads an old file, containing the "NIST" label, the library service shall be
	 * able to locate the mass spectrum.
	 */
	String NIST_IDENTIFIER_LEGACY_1 = "NIST"; // $NON-NLS-N$
	String NIST_IDENTIFIER_LEGACY_2 = "NIST (external)"; // $NON-NLS-N$
	String NIST_IDENTIFIER_LEGACY_3 = "NIST (extern)"; // $NON-NLS-N$
	String NIST_IDENTIFIER = "org.eclipse.chemclipse.msd.identifier.supplier.nist"; // $NON-NLS-N$
	/*
	 * The parameter is used to run the NIST-DB in batch mode.
	 * NIST MS Search (nistms$.exe) Command Line Options, Ver24Man.pdf, page 60
	 */
	String PAR2 = "/PAR=2"; // $NON-NLS-N$
	String INSTRUMENT = "/INSTRUMENT"; // $NON-NLS-N$
	/*
	 * The path is NIST library specific.
	 */
	String NISTLOG_FILE = "NISTLOG.TXT"; // $NON-NLS-N$
	String SRCREADY_FILE = "SRCREADY.TXT"; // $NON-NLS-N$
	String SRCRESLT_FILE = "SRCRESLT.TXT"; // $NON-NLS-N$
	String AUTOIMP_FILE = "AUTOIMP.MSD"; // $NON-NLS-N$
	String NIST_SETTINGS_FILE = "nistms.INI"; // $NON-NLS-N$
	String HITS_TO_PRINT = "Hits to Print="; // $NON-NLS-N$
	/*
	 * The path is ChemClipse specific.
	 */
	String FILESPEC_FILE = "FILESPEC.FIL"; // $NON-NLS-N$
	String MASSSPECTRA_FILE = "MASSSPECTRA.MSL"; // $NON-NLS-N$
	/*
	 * Identifier for the NIST executable. It will be used to avoid killing other processes than the nistms$.exe.
	 */
	String NIST_EXE_IDENTIFIER_LC = "nistms"; // $NON-NLS-N$

	/**
	 * Validates the NIST executable;
	 * 
	 * @return boolean
	 */
	boolean validateExecutable();

	/**
	 * Sets the number of targets the NIST library shall report.
	 * 
	 * @param numberOfTargets
	 */
	void setNumberOfTargets(int numberOfTargets);

	/**
	 * Returns the path/file of the NISTLOG.TXT file.
	 * 
	 * @return String
	 */
	String getNistlogFile();

	/**
	 * Returns the path/file of the SRCREADY.TXT file.
	 * 
	 * @return String
	 */
	String getSrcreadyFile();

	/**
	 * Returns the path/file of the SRCRESLT.TXT file.
	 * 
	 * @return String
	 */
	String getSrcresltFile();

	/**
	 * Returns the path/file of the AUTOIMP.MSD file.
	 * 
	 * @return String
	 */
	String getAutoimpFile();

	/**
	 * Returns the path/file of the FILESPEC.FIL file.
	 * 
	 * @return String
	 */
	String getFilespecFile();

	/**
	 * Returns the path/file of the MASSSPECTRA.MSL file.
	 * 
	 * @return String
	 */
	String getMassSpectraFile();

	/**
	 * Returns the path/file of the nistms.INI file.
	 * 
	 * @return String
	 */
	String getNISTSettingsFile();

	/**
	 * Set the number of unknown entries to process.
	 * 
	 * @param numberOfUnknownEntriesToProcess
	 */
	void setNumberOfUnknownEntriesToProcess(int numberOfUnknownEntriesToProcess);

	/**
	 * Returns the number of unknown entries to process.
	 * 
	 * @return int
	 */
	int getNumberOfUnknownEntriesToProcess();
}