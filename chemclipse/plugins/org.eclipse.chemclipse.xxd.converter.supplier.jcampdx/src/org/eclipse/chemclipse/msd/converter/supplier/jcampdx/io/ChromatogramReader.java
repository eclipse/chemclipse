/*******************************************************************************
 * Copyright (c) 2013, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.support.ChromatogramSupport;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.VendorScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramMSDReader {

	public static final String CONVERTER_ID_MSD = "org.eclipse.chemclipse.msd.converter.supplier.jcampdx";

	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);

	/*
	 * Note MassFinder5 - no '##SCAN_NUMBER=' marker
	 * ##TIC=11974
	 * ##TIME=4.0930
	 * ##NPOINTS=22
	 * ##XYDATA=(X,Y)
	 * 39 332
	 * 40 1642
	 * 41 791
	 */
	private static final String HEADER_MASSFINDER_3 = "##PROGRAM=MassFinder3";
	private static final String HEADER_MASSFINDER_4 = "##PROGRAM=MassFinder4";
	private static final String HEADER_MASSFINDER_5 = "##PROGRAM=MassFinder5";
	/*
	 * ##NAME=Toluol
	 * ##TIC=11143
	 * ##SCAN=530
	 * ##RI1=765
	 * ##TIME=6.5635
	 * ##KEY=3
	 * ##LIB=279181791
	 * ##MARKED=1
	 */
	private static final String HEADER_MARKER = "##";
	private static final String HEADER_TITLE = "##TITLE=";
	private static final String HEADER_PROGRAM = "##PROGRAM=";
	private static final String RETENTION_TIME_MARKER = "##RETENTION_TIME=";
	private static final String TIME_MARKER = "##TIME=";
	private static final String TIC_MARKER = "##TIC=";
	private static final String NAME_MARKER = "##NAME=";
	private static final String SCAN_NUMBER_MARKER = "##SCAN_NUMBER=";
	private static final String SCAN_MARKER = "##SCAN=";
	private static final String XYDATA_MARKER_SPACE = "##XYDATA= (XY..XY)";
	private static final String XYDATA_MARKER_SHORT = "##XYDATA=(X,Y)";
	private static final String ION_DELIMITER_COMMA = ",";
	private static final String ION_DELIMITER_WHITESPACE = " ";

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, monitor);
		}

		return null;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws IOException {

		if(isValidFileFormat(file)) {
			return readChromatogramOverview(file);
		}

		return null;
	}

	private IChromatogramMSD readChromatogram(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			/*
			 * Scans
			 */
			RetentionIndexSupport retentionIndexSupport = new RetentionIndexSupport();
			IVendorScan massSpectrum = null;
			IIdentificationTarget identificationTarget = null;
			int retentionTime = 0;
			boolean readIons = false;
			boolean readIonsSpace = false;
			boolean adjustTotalSignal = false;
			float totalSignalFromFile = 0.0f;
			boolean specialCaseTIC = false;

			String line;
			while((line = bufferedReader.readLine()) != null && !monitor.isCanceled()) {
				/*
				 * Each scan starts with the marker:
				 * ##SCAN_NUMBER= 1
				 * ##RETENTION_TIME= 1.78
				 * ##NPOINTS= 3
				 * ##XYDATA= (XY..XY)
				 * 40.01681, 4352
				 * 41.07158, 221
				 * 44.05768, 36
				 * ...
				 * ##XYDATA=(X,Y)
				 * 19 350
				 * 26 1176
				 * 27 3691
				 * 28 1631
				 * 29 3914
				 */
				if(line.startsWith(SCAN_NUMBER_MARKER) || line.startsWith(SCAN_MARKER) || (specialCaseTIC && line.startsWith(TIC_MARKER))) {
					/*
					 * Store an existing scan.
					 */
					if(massSpectrum != null) {
						if(adjustTotalSignal && totalSignalFromFile != 0.0d) {
							massSpectrum.adjustTotalSignal(totalSignalFromFile);
						}
						chromatogram.addScan(massSpectrum);
					}
					/*
					 * TIC
					 */
					if(specialCaseTIC) {
						if(line.startsWith(TIC_MARKER)) {
							String value = line.replace(TIC_MARKER, "").trim();
							totalSignalFromFile = Float.parseFloat(value);
						}
					}
					/*
					 * Create a new scan.
					 */
					massSpectrum = new VendorScan();
					identificationTarget = null;
					totalSignalFromFile = 0.0f;
					readIons = false;
					/*
					 * Read the next line.
					 */
					continue;
				} else if(line.startsWith(HEADER_MASSFINDER_3)) {
					adjustTotalSignal = true;
				} else if(line.startsWith(HEADER_MASSFINDER_4)) {
					adjustTotalSignal = false;
				} else if(line.startsWith(HEADER_MASSFINDER_5)) {
					adjustTotalSignal = false;
					specialCaseTIC = true;
				}
				/*
				 * Read the scan data.
				 * The SCAN_MARKER section has been accessed.
				 */
				if(massSpectrum != null) {
					/*
					 * Parse the scan data
					 */
					if(line.startsWith(RETENTION_TIME_MARKER) || line.startsWith(TIME_MARKER)) {
						retentionTime = getRetentionTime(line);
						massSpectrum.setRetentionTime(retentionTime);
					} else if(line.startsWith(TIC_MARKER)) {
						String value = line.replace(TIC_MARKER, "").trim();
						totalSignalFromFile = Float.parseFloat(value);
					} else if(line.startsWith(NAME_MARKER)) {
						try {
							/*
							 * Create Scan Target
							 */
							String name = line.replace(NAME_MARKER, "").trim();
							if(identificationTarget == null) {
								identificationTarget = createIdentificationTarget(name);
							} else {
								ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
								if(libraryInformation.getName().equals(RetentionIndexSupport.RETENTION_INDEX_MARKER)) {
									libraryInformation.setName(name);
								}
							}
							massSpectrum.getTargets().add(identificationTarget);
							identificationTarget = null;
						} catch(ReferenceMustNotBeNullException e) {
							logger.warn(e);
						}
					} else if(line.startsWith(RetentionIndexSupport.RETENTION_INDEX_MARKER)) {
						if(identificationTarget == null) {
							identificationTarget = createIdentificationTarget(RetentionIndexSupport.RETENTION_INDEX_MARKER);
							retentionIndexSupport.extract(identificationTarget, line);
						}
					} else if(line.startsWith(XYDATA_MARKER_SPACE) || line.startsWith(XYDATA_MARKER_SHORT)) {
						/*
						 * Mark to read ions.
						 */
						readIons = true;
						readIonsSpace = false;
						if(line.startsWith(XYDATA_MARKER_SPACE)) {
							readIonsSpace = true;
						}
					} else if(!line.startsWith(HEADER_MARKER) && readIons) {
						/*
						 * Parse the ions.
						 */
						try {
							line = line.trim();
							if(readIonsSpace) {
								String[] values = line.split(ION_DELIMITER_COMMA);
								if(values.length == 2) {
									double mz = Double.parseDouble(values[0].trim());
									float abundance = Float.parseFloat(values[1].trim());
									IVendorIon ion = new VendorIon(mz, abundance);
									massSpectrum.addIon(ion);
								}
							} else {
								String[] values = line.split(ION_DELIMITER_WHITESPACE);
								if(values.length == 2) {
									double mz = Double.parseDouble(values[0].trim());
									float abundance = Float.parseFloat(values[1].trim());
									IVendorIon ion = new VendorIon(mz, abundance);
									massSpectrum.addIon(ion);
								}
							}
						} catch(NumberFormatException e) {
							logger.warn(e);
						}
					}
				}
			}
			/*
			 * Add the last scan.
			 */
			if(massSpectrum != null) {
				if(adjustTotalSignal && totalSignalFromFile != 0.0d) {
					massSpectrum.adjustTotalSignal(totalSignalFromFile);
				}
				chromatogram.addScan(massSpectrum);
			}
			/*
			 * Set the scan delay and interval
			 * file and converter id.
			 */
			chromatogram.setFile(file);
			ChromatogramSupport.calculateScanIntervalAndDelay(chromatogram);
			chromatogram.setConverterId(CONVERTER_ID_MSD);
		}

		return chromatogram;
	}

	private IIdentificationTarget createIdentificationTarget(String name) {

		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setName(name);
		libraryInformation.setComments("JCAMP-DX");
		IComparisonResult comparisonResult = new ComparisonResult(IComparisonResult.MAX_MATCH_FACTOR, IComparisonResult.MAX_REVERSE_MATCH_FACTOR, 0.0f, 0.0f);

		return new IdentificationTarget(libraryInformation, comparisonResult);
	}

	private IChromatogramMSD readChromatogramOverview(File file) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			boolean retentionTimeIsAvailable = false;
			int retentionTime = 0;
			/*
			 * Parse each line
			 */
			while((line = bufferedReader.readLine()) != null) {
				/*
				 * Extract the TIC values
				 * ##RETENTION_TIME= 2.90
				 * ##TIC= 3875
				 */
				if(line.startsWith(RETENTION_TIME_MARKER) || line.startsWith(TIME_MARKER)) {
					retentionTime = getRetentionTime(line);
					retentionTimeIsAvailable = true;
				} else {
					/*
					 * Set a TIC value only if a retention time is available.
					 */
					if(line.startsWith(TIC_MARKER) && retentionTimeIsAvailable) {
						try {
							String value = line.replace(TIC_MARKER, "").trim();
							float abundance = Float.parseFloat(value);
							IVendorIon ion = new VendorIon(IIon.TIC_ION);
							ion.setAbundance(abundance);
							IVendorScan massSpectrum = new VendorScan();
							/*
							 * Set retentionTimeIsAvailable = false
							 * to wait for the next retention
							 * time event and to avoid setting
							 * a wrong retention time for the
							 * current scan.
							 */
							massSpectrum.setRetentionTime(retentionTime);
							retentionTimeIsAvailable = false;

							if(retentionTime >= 0) {
								massSpectrum.addIon(ion);
								chromatogram.addScan(massSpectrum);
							}
						} catch(NumberFormatException e) {
							logger.warn(e);
						}
					}
				}
			}
		}

		return chromatogram;
	}

	private int getRetentionTime(String line) {

		/*
		 * The retention time is stored in seconds scale.
		 * Milliseconds = seconds * 1000.0d
		 */
		int retentionTime = -1;
		try {
			if(line.startsWith(RETENTION_TIME_MARKER)) {
				String value = line.replace(RETENTION_TIME_MARKER, "").trim();
				retentionTime = (int)(Double.parseDouble(value) * 1000.0d);
			} else if(line.startsWith(TIME_MARKER)) {
				String value = line.replace(TIME_MARKER, "").trim();
				retentionTime = (int)(Double.parseDouble(value) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
		}

		return retentionTime;
	}

	private boolean isValidFileFormat(File file) throws IOException {

		boolean isValidFormat = false;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			/*
			 * Check the first column header.
			 */
			String line = bufferedReader.readLine();
			if(line.startsWith(HEADER_TITLE) || line.startsWith(HEADER_PROGRAM)) {
				boolean readIons = false;
				exitloop:
				while((line = bufferedReader.readLine()) != null) {
					if(line.startsWith(XYDATA_MARKER_SPACE) || line.startsWith(XYDATA_MARKER_SHORT)) {
						readIons = true;
					} else if(!line.startsWith(HEADER_MARKER) && readIons) {
						isValidFormat = true;
						break exitloop;
					}
				}
			}
		}

		return isValidFormat;
	}
}