/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support;

import java.util.zip.ZipOutputStream;

public interface IFormat {

	String CONVERTER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	/*
	 * Zip Settings
	 */
	int COMPRESSION_LEVEL = 0; // 0 - 9 => 0 no compression
	int METHOD = ZipOutputStream.DEFLATED;
	/*
	 * File Settings
	 */
	String VERSION_0701 = "0.7.0.1"; // Version 0.7.0, Release 1 (Nernst)
	String VERSION_0801 = "0.8.0.1"; // Version 0.8.0, Release 1
	String VERSION_0802 = "0.8.0.2"; // Version 0.8.0, Release 2
	String VERSION_0803 = "0.8.0.3"; // Version 0.8.0, Release 3 (Dempster)
	String VERSION_0901 = "0.9.0.1"; // Version 0.9.0, Release 1
	String VERSION_0902 = "0.9.0.2"; // Version 0.9.0, Release 2
	String VERSION_0903 = "0.9.0.3"; // Version 0.9.0, Release 3 (Mattauch)
	String VERSION_1001 = "1.0.0.1"; // Version 1.0.0, Release 1
	String VERSION_1002 = "1.0.0.2"; // Version 1.0.0, Release 2
	String VERSION_1003 = "1.0.0.3"; // Version 1.0.0, Release 3
	String VERSION_1004 = "1.0.0.4"; // Version 1.0.0, Release 4 (Aston)
	String VERSION_1005 = "1.0.0.5"; // Version 1.0.0, Release 5 ... currently under development
	String VERSION_LATEST = VERSION_1004;
	String FILE_VERSION = "VERSION";
	//
	String DIR_SEPARATOR = "/";
	String WSD = "WSD";
	String MSD = "MSD";
	String FID = "FID";
	String OVERVIEW = "OVERVIEW";
	String CHROMATOGRAM = "CHROMATOGRAM";
	String TIC = "TIC";
	String SCANS = "SCANS";
	String BASELINE = "BASELINE";
	String PEAKS = "PEAKS";
	String IDENTIFICATION = "IDENTIFICATION";
	String AREA = "AREA";
	String HISTORY = "HISTORY";
	String MISC = "MISC";
	String SCANPROXIES = "SCANPROXIES"; // since 1.0.0.3
	/*
	 * Directories for MSD, FID and other detectors.
	 */
	String DIR_MSD = MSD + DIR_SEPARATOR;
	String DIR_FID = FID + DIR_SEPARATOR;
	String DIR_WSD = WSD + DIR_SEPARATOR;
	/*
	 * Overview / TIC
	 * Versions <= 0.9.0.3
	 * MSD / FID extension
	 * Versions >= 1.0.0.1
	 */
	String DIR_OVERVIEW = OVERVIEW + DIR_SEPARATOR;
	String FILE_TIC = DIR_OVERVIEW + TIC;
	//
	String DIR_OVERVIEW_MSD = DIR_MSD + DIR_OVERVIEW;
	String FILE_TIC_MSD = DIR_OVERVIEW_MSD + TIC;
	//
	String DIR_OVERVIEW_FID = DIR_FID + DIR_OVERVIEW;
	String FILE_TIC_FID = DIR_OVERVIEW_FID + TIC;
	// WSD stuff
	String DIR_OVERVIEW_WSD = DIR_WSD + DIR_OVERVIEW;
	String FILE_TIC_WSD = DIR_OVERVIEW_WSD + TIC;
	/*
	 * Chromatogram
	 * Versions <= 0.9.0.3
	 * MSD / FID extension
	 * Versions >= 1.0.0.1
	 */
	String DIR_CHROMATOGRAM = CHROMATOGRAM + DIR_SEPARATOR;
	String FILE_SCANS = DIR_CHROMATOGRAM + SCANS;
	String FILE_BASELINE = DIR_CHROMATOGRAM + BASELINE;
	String FILE_PEAKS = DIR_CHROMATOGRAM + PEAKS;
	String FILE_IDENTIFICATION = DIR_CHROMATOGRAM + IDENTIFICATION;
	String FILE_AREA = DIR_CHROMATOGRAM + AREA;
	String FILE_HISTORY = DIR_CHROMATOGRAM + HISTORY;
	String FILE_MISC = DIR_CHROMATOGRAM + MISC;
	//
	String DIR_CHROMATOGRAM_MSD = DIR_MSD + DIR_CHROMATOGRAM;
	String FILE_SCANS_MSD = DIR_CHROMATOGRAM_MSD + SCANS;
	String FILE_BASELINE_MSD = DIR_CHROMATOGRAM_MSD + BASELINE;
	String FILE_PEAKS_MSD = DIR_CHROMATOGRAM_MSD + PEAKS;
	String FILE_IDENTIFICATION_MSD = DIR_CHROMATOGRAM_MSD + IDENTIFICATION;
	String FILE_AREA_MSD = DIR_CHROMATOGRAM_MSD + AREA;
	String FILE_HISTORY_MSD = DIR_CHROMATOGRAM_MSD + HISTORY;
	String FILE_MISC_MSD = DIR_CHROMATOGRAM_MSD + MISC;
	String FILE_SCANPROXIES_MSD = DIR_CHROMATOGRAM_MSD + SCANPROXIES;
	//
	String DIR_CHROMATOGRAM_FID = DIR_FID + DIR_CHROMATOGRAM;
	String FILE_SCANS_FID = DIR_CHROMATOGRAM_FID + SCANS;
	String FILE_BASELINE_FID = DIR_CHROMATOGRAM_FID + BASELINE;
	String FILE_PEAKS_FID = DIR_CHROMATOGRAM_FID + PEAKS;
	String FILE_AREA_FID = DIR_CHROMATOGRAM_FID + AREA;
	// WSD stuff
	String DIR_CHROMATOGRAM_WSD = DIR_WSD + DIR_CHROMATOGRAM;
	String FILE_SCANS_WSD = DIR_CHROMATOGRAM_WSD + SCANS;
	String FILE_BASELINE_WSD = DIR_CHROMATOGRAM_WSD + BASELINE;
	String FILE_HISTORY_WSD = DIR_CHROMATOGRAM_WSD + HISTORY;
	String FILE_MISC_WSD = DIR_CHROMATOGRAM_WSD + MISC;
}
