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
 * Christoph Läubrich - add new format
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support;

import java.util.zip.ZipOutputStream;

public class Format {

	private Format() {

	}

	/*
	 * CHANGES MUST BE APPROVED BY PHILIP WENIG!
	 */
	public static final String CONVERTER_ID_CHROMATOGRAM = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	public static final String CONVERTER_ID_METHOD = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.processMethodSupplier";
	/*
	 * Version
	 */
	public static final String FILE_VERSION = "VERSION";
	/*
	 * Chromatogram (*.ocb)
	 */
	public static final int CHROMATOGRAM_COMPRESSION_LEVEL = 9; // 0 - 9 => 0 no compression
	public static final int CHROMATOGRAM_COMPRESSION_TYPE = ZipOutputStream.DEFLATED;
	//
	public static final String CHROMATOGRAM_VERSION_0701 = "0.7.0.1"; // Version 0.7.0, Release 1 (Nernst)
	public static final String CHROMATOGRAM_VERSION_0801 = "0.8.0.1"; // Version 0.8.0, Release 1
	public static final String CHROMATOGRAM_VERSION_0802 = "0.8.0.2"; // Version 0.8.0, Release 2
	public static final String CHROMATOGRAM_VERSION_0803 = "0.8.0.3"; // Version 0.8.0, Release 3 (Dempster)
	public static final String CHROMATOGRAM_VERSION_0901 = "0.9.0.1"; // Version 0.9.0, Release 1
	public static final String CHROMATOGRAM_VERSION_0902 = "0.9.0.2"; // Version 0.9.0, Release 2
	public static final String CHROMATOGRAM_VERSION_0903 = "0.9.0.3"; // Version 0.9.0, Release 3 (Mattauch)
	public static final String CHROMATOGRAM_VERSION_1001 = "1.0.0.1"; // Version 1.0.0, Release 1
	public static final String CHROMATOGRAM_VERSION_1002 = "1.0.0.2"; // Version 1.0.0, Release 2
	public static final String CHROMATOGRAM_VERSION_1003 = "1.0.0.3"; // Version 1.0.0, Release 3
	public static final String CHROMATOGRAM_VERSION_1004 = "1.0.0.4"; // Version 1.0.0, Release 4 (Aston)
	public static final String CHROMATOGRAM_VERSION_1005 = "1.0.0.5"; // Version 1.0.0, Release 5
	public static final String CHROMATOGRAM_VERSION_1006 = "1.0.0.6"; // Version 1.0.0, Release 6
	public static final String CHROMATOGRAM_VERSION_1007 = "1.0.0.7"; // Version 1.0.0, Release 7
	public static final String CHROMATOGRAM_VERSION_1100 = "1.1.0.0"; // Version 1.1.0, Release 0 (Diels)
	public static final String CHROMATOGRAM_VERSION_1300 = "1.3.0.0"; // Version 1.3.0, Release 0 (Dalton)
	public static final String CHROMATOGRAM_VERSION_1301 = "1.3.0.1"; // Version 1.3.0, Release 1
	public static final String CHROMATOGRAM_VERSION_1400 = "1.4.0.0"; // Version 1.4.0, Release 0 (Lawrence)
	public static final String CHROMATOGRAM_VERSION_1500 = "1.5.0.0"; // Version 1.5.0, Release 0 (McLafferty)
	public static final String CHROMATOGRAM_VERSION_1501 = "1.5.0.1"; // Version 1.5.0, Release 1 (McLafferty)
	public static final String CHROMATOGRAM_VERSION_1502 = "1.5.0.2"; // Version 1.5.0, Release 2 (McLafferty)
	public static final String CHROMATOGRAM_VERSION_LATEST = CHROMATOGRAM_VERSION_1502;
	/*
	 * Method (*.ocm)
	 */
	public static final int METHOD_COMPRESSION_LEVEL = 0; // 0 - 9 => 0 no compression
	public static final int METHOD_COMPRESSION_TYPE = ZipOutputStream.DEFLATED;
	//
	public static final String METHOD_VERSION_0001 = "0.0.0.1";
	public static final String METHOD_VERSION_0002 = "0.0.0.2";
	public static final String METHOD_VERSION_0003 = "0.0.0.3";
	public static final String METHOD_VERSION_1400 = "1.4.0.0";
	public static final String METHOD_VERSION_1401 = "1.4.0.1";
	public static final String METHOD_VERSION_LATEST = METHOD_VERSION_1401;
	/*
	 * Quantitation DB (*.ocq)
	 */
	public static final int QUANTDB_COMPRESSION_LEVEL = 0; // 0 - 9 => 0 no compression
	public static final int QUANTDB_COMPRESSION_TYPE = ZipOutputStream.DEFLATED;
	//
	public static final String QUANTDB_VERSION_0001 = "0.0.0.1";
	public static final String QUANTDB_VERSION_LATEST = QUANTDB_VERSION_0001;
	//
	public static final String DIR_SEPARATOR = "/";
	public static final String WSD = "WSD";
	public static final String MSD = "MSD";
	public static final String FID_LEGACY = "FID"; // Legacy!
	public static final String CSD = "CSD";
	public static final String OVERVIEW = "OVERVIEW";
	public static final String CHROMATOGRAM = "CHROMATOGRAM";
	public static final String TIC = "TIC";
	public static final String SCANS = "SCANS";
	public static final String BASELINE = "BASELINE";
	public static final String PEAKS = "PEAKS";
	public static final String IDENTIFICATION = "IDENTIFICATION";
	public static final String AREA = "AREA";
	public static final String HISTORY = "HISTORY";
	public static final String MISC = "MISC";
	public static final String SCANPROXIES = "SCANPROXIES"; // since 1.0.0.3
	public static final String SYSTEM_SETTINGS = "SYSTEM_SETTINGS"; // since 1.0.0.5
	public static final String SEPARATION_COLUMN = "SEPARATION_COLUMN"; // since 1.3.0.0
	public static final String DATA_TYPE_MSD = "MSD";
	public static final String DATA_TYPE_CSD = "CSD";
	public static final String DATA_TYPE_WSD = "WSD";
	/*
	 * Directories for MSD, FID and other detectors.
	 */
	public static final String DIR_MSD = MSD + DIR_SEPARATOR;
	public static final String DIR_FID_LEGACY = FID_LEGACY + DIR_SEPARATOR;
	public static final String DIR_CSD = CSD + DIR_SEPARATOR;
	public static final String DIR_WSD = WSD + DIR_SEPARATOR;
	/*
	 * Overview / TIC
	 * Versions <= 0.9.0.3
	 * MSD / FID extension
	 * Versions >= 1.0.0.1
	 */
	public static final String DIR_OVERVIEW = OVERVIEW + DIR_SEPARATOR;
	public static final String FILE_TIC = DIR_OVERVIEW + TIC;
	//
	public static final String DIR_OVERVIEW_MSD = DIR_MSD + DIR_OVERVIEW;
	public static final String FILE_TIC_MSD = DIR_OVERVIEW_MSD + TIC;
	//
	public static final String DIR_OVERVIEW_FID = DIR_FID_LEGACY + DIR_OVERVIEW;
	public static final String FILE_TIC_FID = DIR_OVERVIEW_FID + TIC;
	// WSD stuff
	public static final String DIR_OVERVIEW_WSD = DIR_WSD + DIR_OVERVIEW;
	public static final String FILE_TIC_WSD = DIR_OVERVIEW_WSD + TIC;
	/*
	 * Chromatogram
	 * Versions <= 0.9.0.3
	 * MSD / FID extension
	 * Versions >= 1.0.0.1
	 */
	public static final String DIR_CHROMATOGRAM = CHROMATOGRAM + DIR_SEPARATOR;
	public static final String FILE_SCANS = DIR_CHROMATOGRAM + SCANS;
	public static final String FILE_BASELINE = DIR_CHROMATOGRAM + BASELINE;
	public static final String FILE_PEAKS = DIR_CHROMATOGRAM + PEAKS;
	public static final String FILE_IDENTIFICATION = DIR_CHROMATOGRAM + IDENTIFICATION;
	public static final String FILE_AREA = DIR_CHROMATOGRAM + AREA;
	public static final String FILE_HISTORY = DIR_CHROMATOGRAM + HISTORY;
	public static final String FILE_MISC = DIR_CHROMATOGRAM + MISC;
	/*
	 * CHROMATOGRAM REFERENCES
	 */
	public static final String CHROMATOGRAM_REFERENCE_SEPARATOR = "_";
	public static final String FILE_REFERENCE_INFO = "REFERENCE_INFO"; // since 1.3.0.0
	public static final String DIR_CHROMATOGRAM_REFERENCE = "CHROMATOGRAM_REFERENCE";
	public static final String FILE_CHROMATOGRAM_TYPE = "CHROMATOGRAM_TYPE";
	/*
	 * MSD
	 */
	public static final String DIR_CHROMATOGRAM_MSD = DIR_MSD + DIR_CHROMATOGRAM;
	public static final String FILE_SCANS_MSD = DIR_CHROMATOGRAM_MSD + SCANS;
	public static final String FILE_BASELINE_MSD = DIR_CHROMATOGRAM_MSD + BASELINE;
	public static final String FILE_PEAKS_MSD = DIR_CHROMATOGRAM_MSD + PEAKS;
	public static final String FILE_IDENTIFICATION_MSD = DIR_CHROMATOGRAM_MSD + IDENTIFICATION;
	public static final String FILE_AREA_MSD = DIR_CHROMATOGRAM_MSD + AREA;
	public static final String FILE_HISTORY_MSD = DIR_CHROMATOGRAM_MSD + HISTORY;
	public static final String FILE_MISC_MSD = DIR_CHROMATOGRAM_MSD + MISC;
	public static final String FILE_SCANPROXIES_MSD = DIR_CHROMATOGRAM_MSD + SCANPROXIES;
	public static final String FILE_SYSTEM_SETTINGS_MSD = DIR_CHROMATOGRAM_MSD + SYSTEM_SETTINGS;
	public static final String FILE_SEPARATION_COLUMN_MSD = DIR_CHROMATOGRAM_MSD + SEPARATION_COLUMN;
	/*
	 * FID LEGACY
	 */
	public static final String DIR_CHROMATOGRAM_FID = DIR_FID_LEGACY + DIR_CHROMATOGRAM;
	public static final String FILE_SCANS_FID = DIR_CHROMATOGRAM_FID + SCANS;
	public static final String FILE_BASELINE_FID = DIR_CHROMATOGRAM_FID + BASELINE;
	public static final String FILE_PEAKS_FID = DIR_CHROMATOGRAM_FID + PEAKS;
	public static final String FILE_AREA_FID = DIR_CHROMATOGRAM_FID + AREA;
	public static final String FILE_SYSTEM_SETTINGS_FID = DIR_CHROMATOGRAM_FID + SYSTEM_SETTINGS;
	/*
	 * CSD
	 */
	public static final String DIR_CHROMATOGRAM_CSD = DIR_CSD + DIR_CHROMATOGRAM;
	public static final String FILE_SCANS_CSD = DIR_CHROMATOGRAM_CSD + SCANS;
	public static final String FILE_BASELINE_CSD = DIR_CHROMATOGRAM_CSD + BASELINE;
	public static final String FILE_PEAKS_CSD = DIR_CHROMATOGRAM_CSD + PEAKS;
	public static final String FILE_IDENTIFICATION_CSD = DIR_CHROMATOGRAM_CSD + IDENTIFICATION;
	public static final String FILE_AREA_CSD = DIR_CHROMATOGRAM_CSD + AREA;
	public static final String FILE_HISTORY_CSD = DIR_CHROMATOGRAM_CSD + HISTORY;
	public static final String FILE_MISC_CSD = DIR_CHROMATOGRAM_CSD + MISC;
	public static final String FILE_SYSTEM_SETTINGS_CSD = DIR_CHROMATOGRAM_CSD + SYSTEM_SETTINGS;
	public static final String FILE_SEPARATION_COLUMN_CSD = DIR_CHROMATOGRAM_CSD + SEPARATION_COLUMN;
	/*
	 * WSD
	 */
	public static final String DIR_CHROMATOGRAM_WSD = DIR_WSD + DIR_CHROMATOGRAM;
	public static final String FILE_SCANS_WSD = DIR_CHROMATOGRAM_WSD + SCANS;
	public static final String FILE_BASELINE_WSD = DIR_CHROMATOGRAM_WSD + BASELINE;
	public static final String FILE_PEAKS_WSD = DIR_CHROMATOGRAM_WSD + PEAKS;
	public static final String FILE_IDENTIFICATION_WSD = DIR_CHROMATOGRAM_WSD + IDENTIFICATION;
	public static final String FILE_AREA_WSD = DIR_CHROMATOGRAM_WSD + AREA;
	public static final String FILE_HISTORY_WSD = DIR_CHROMATOGRAM_WSD + HISTORY;
	public static final String FILE_MISC_WSD = DIR_CHROMATOGRAM_WSD + MISC;
	public static final String FILE_SYSTEM_SETTINGS_WSD = DIR_CHROMATOGRAM_WSD + SYSTEM_SETTINGS;
	public static final String FILE_SEPARATION_COLUMN_WSD = DIR_CHROMATOGRAM_WSD + SEPARATION_COLUMN;
	/*
	 * Process Method
	 */
	public static final String FILE_PROCESS_METHOD = "PROCESS_METHOD";
	/*
	 * MSD
	 */
	public static final String QUANTDB = "QUANTDB";
	public static final String QUANTITATION_DATABASE = "QUANTITATION_DATABASE";
	public static final String DIR_QUANTDB = QUANTDB + DIR_SEPARATOR;
	public static final String FILE_QUANTDB = DIR_QUANTDB + QUANTITATION_DATABASE;
}