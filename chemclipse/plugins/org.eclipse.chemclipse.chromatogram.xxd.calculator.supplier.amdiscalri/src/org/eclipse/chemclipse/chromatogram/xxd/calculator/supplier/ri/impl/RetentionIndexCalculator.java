/*******************************************************************************
 * Copyright (c) 2014, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Matthias Mailänder - match alkanes by CAS or IUPAC name
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.io.CalibrationFileReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.settings.CalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.settings.ResetterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class RetentionIndexCalculator {

	private static final Logger logger = Logger.getLogger(RetentionIndexCalculator.class);
	//
	public static final String ALKANE_PREFIX = "C";
	public static final String ALKANE_REGEX = "(C)(\\d+)";
	public static final int ALKANE_MISSING = 0;
	public static final int INDEX_MISSING = 0;
	//
	private static final Pattern PATTERN_ALKANE = Pattern.compile(ALKANE_REGEX);
	private static final String DESCRIPTION = "Retention Index Calculator";

	public static String[] getStandards() {

		Map<Integer, String> prefix = new HashMap<>();
		prefix.put(30, "Triacontane");
		prefix.put(40, "Tetracontane");
		prefix.put(50, "Pentacontane");
		prefix.put(60, "Hexacontane");
		prefix.put(70, "Heptacontane");
		prefix.put(80, "Octacontane");
		prefix.put(90, "Nonacontane");
		//
		Map<Integer, String> postfix = new HashMap<>();
		postfix.put(1, "Hen");
		postfix.put(2, "Do");
		postfix.put(3, "Tri");
		postfix.put(4, "Tetra");
		postfix.put(5, "Penta");
		postfix.put(6, "Hexa");
		postfix.put(7, "Hepta");
		postfix.put(8, "Octa");
		postfix.put(9, "Nona");
		//
		List<String> standards = new ArrayList<>();
		/*
		 * C1 - C9
		 */
		standards.add("C1 (Methane)");
		standards.add("C2 (Ethane)");
		standards.add("C3 (Propane)");
		standards.add("C4 (Butane)");
		standards.add("C5 (Pentane)");
		standards.add("C6 (Hexane)");
		standards.add("C7 (Heptane)");
		standards.add("C8 (Octane)");
		standards.add("C9 (Nonane)");
		/*
		 * C10 - C19
		 */
		standards.add("C10 (Decane)");
		standards.add("C11 (Undecane)");
		standards.add("C12 (Dodecane)");
		standards.add("C13 (Tridecane)");
		standards.add("C14 (Tetradecane)");
		standards.add("C15 (Pentadecane)");
		standards.add("C16 (Hexadecane)");
		standards.add("C17 (Heptadecane)");
		standards.add("C18 (Octadecane)");
		standards.add("C19 (Nonadecane)");
		/*
		 * C20 - C29
		 */
		standards.add("C20 (Eicosane)");
		standards.add("C21 (Heneicosane)");
		standards.add("C22 (Docosane)");
		standards.add("C23 (Tricosane)");
		standards.add("C24 (Tetracosane)");
		standards.add("C25 (Pentacosane)");
		standards.add("C26 (Hexacosane)");
		standards.add("C27 (Heptacosane)");
		standards.add("C28 (Octacosane)");
		standards.add("C29 (Nonacosane)");
		/*
		 * C30 - C99
		 */
		for(int i = 3; i <= 9; i++) {
			int carbon = i * 10;
			String name = prefix.getOrDefault(carbon, "");
			//
			for(int j = 0; j <= 9; j++) {
				StringBuilder builder = new StringBuilder();
				builder.append("C");
				builder.append(Integer.toString(carbon + j));
				builder.append(" (");
				if(j == 0) {
					/*
					 * C30 (Triacontane)
					 */
					builder.append(name);
				} else {
					/*
					 * C31 (Hentriacontane)
					 * ...
					 */
					builder.append(postfix.getOrDefault(j, ""));
					builder.append(name.toLowerCase());
				}
				builder.append(")");
				standards.add(builder.toString());
			}
		}
		//
		return standards.toArray(new String[standards.size()]);
	}

	public static LinkedHashMap<String, String> getAlkanesByCAS() {

		LinkedHashMap<String, String> alkanesByCAS = new LinkedHashMap<>();
		alkanesByCAS.put("74-82-8", "Methane");
		alkanesByCAS.put("74-84-0", "Ethane");
		alkanesByCAS.put("74-98-6", "Propane");
		alkanesByCAS.put("106-97-8", "Butane");
		alkanesByCAS.put("109-66-0", "Pentane");
		alkanesByCAS.put("110-54-3", "Hexane");
		alkanesByCAS.put("142-82-5", "Heptane");
		alkanesByCAS.put("111-65-9", "Octane");
		alkanesByCAS.put("111-84-2", "Nonane");
		alkanesByCAS.put("124-18-5", "Decane");
		alkanesByCAS.put("1120-21-4", "Undecane");
		alkanesByCAS.put("112-40-3", "Dodecane");
		alkanesByCAS.put("629-50-5", "Tridecane");
		alkanesByCAS.put("629-59-4", "Tetradecane");
		alkanesByCAS.put("629-62-9", "Pentadecane");
		alkanesByCAS.put("544-76-3", "Hexadecane/Cetane");
		alkanesByCAS.put("629-78-7", "Heptadecane");
		alkanesByCAS.put("593-45-3", "Octadecane");
		alkanesByCAS.put("629-92-5", "Nonadecane");
		alkanesByCAS.put("112-95-8", "Icosane/Eicosane");
		alkanesByCAS.put("629-94-7", "Heneicosane");
		alkanesByCAS.put("629-97-0", "Docosane");
		alkanesByCAS.put("638-67-5", "Tricosane");
		alkanesByCAS.put("646-31-1", "Tetracosane");
		alkanesByCAS.put("629-99-2", "Pentacosane");
		alkanesByCAS.put("630-01-3", "Hexacosane");
		alkanesByCAS.put("593-49-7", "Heptacosane");
		alkanesByCAS.put("630-02-4", "Octacosane");
		alkanesByCAS.put("630-03-5", "Nonacosane");
		alkanesByCAS.put("638-68-6", "Triacontane");
		alkanesByCAS.put("630-04-6", "Hentriacontane/Untriacontane");
		alkanesByCAS.put("544-85-4", "Dotriacontane");
		return alkanesByCAS;
	}

	public static int getRetentionIndex(String carbons) {

		return getAlkaneNumber(carbons) * 100;
	}

	public static int getAlkaneNumber(String carbons) {

		int alkaneNumber = ALKANE_MISSING;
		Matcher carbonMatcher = PATTERN_ALKANE.matcher(carbons);
		if(carbonMatcher.find()) {
			try {
				/*
				 * C8 (Octane) => 8
				 */
				alkaneNumber = Integer.parseInt(carbonMatcher.group(2));
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		return alkaneNumber;
	}

	public static int getRetentionIndex(ILibraryInformation libraryInformation) {

		return getAlkaneNumber(libraryInformation) * 100;
	}

	public static int getAlkaneNumber(ILibraryInformation libraryInformation) {

		int alkaneNumber = ALKANE_MISSING;
		LinkedHashMap<String, String> alkanesByCAS = getAlkanesByCAS();
		String cas = libraryInformation.getCasNumber().trim();
		String name = libraryInformation.getName().trim();
		if(getAlkanesByCAS().containsKey(cas)) {
			// 111-65-9 => 8
			ArrayList<String> casNumbers = new ArrayList<>(alkanesByCAS.keySet());
			alkaneNumber = casNumbers.indexOf(cas) + 1;
		} else if(getAlkanesByCAS().containsValue(name)) {
			// Octane => 8
			ArrayList<String> iupacNames = new ArrayList<>(alkanesByCAS.values());
			alkaneNumber = iupacNames.stream().filter(v -> v.contains(name)).map(iupacNames::indexOf).findFirst().orElse(-1) + 1;
		}
		//
		return alkaneNumber;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public IProcessingInfo calculateIndices(IChromatogramSelection chromatogramSelection, CalculatorSettings calculatorSettings) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(chromatogramSelection != null) {
			ISeparationColumnIndices separationColumnIndices = getSeparationColumnIndices(chromatogramSelection, calculatorSettings);
			if(separationColumnIndices != null) {
				/*
				 * Master
				 */
				calculateIndex(chromatogramSelection.getChromatogram(), separationColumnIndices);
				if(calculatorSettings.isProcessReferencedChromatograms()) {
					/*
					 * References
					 */
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					for(Object object : chromatogram.getReferencedChromatograms()) {
						if(object instanceof IChromatogram) {
							calculateIndex((IChromatogram)object, separationColumnIndices);
						}
					}
				}
				processingInfo.addInfoMessage(DESCRIPTION, "The retention indices have been calculated.");
			} else {
				processingInfo.addErrorMessage(DESCRIPTION, "The retention index map could not be found. Is a calibration (*.cal) file or chromatogram RI map available?");
			}
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram selection is not available.");
		}
		//
		return processingInfo;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public IProcessingInfo resetIndices(IChromatogramSelection chromatogramSelection, ResetterSettings resetterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		/*
		 * Master
		 */
		if(chromatogramSelection != null) {
			resetIndex(chromatogramSelection.getChromatogram());
			if(resetterSettings.isProcessReferencedChromatograms()) {
				/*
				 * References
				 */
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				for(Object object : chromatogram.getReferencedChromatograms()) {
					if(object instanceof IChromatogram) {
						resetIndex((IChromatogram)object);
					}
				}
			}
			processingInfo.addInfoMessage(DESCRIPTION, "The retention indices have been set to 0.");
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram selection is not available.");
		}
		//
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	private ISeparationColumnIndices getSeparationColumnIndices(IChromatogramSelection chromatogramSelection, CalculatorSettings calculatorSettings) {

		ISeparationColumnIndices separationColumnIndices;
		switch(calculatorSettings.getCalculatorStrategy()) {
			case AUTO:
				separationColumnIndices = getAutoIndices(chromatogramSelection, calculatorSettings);
				break;
			case CHROMATOGRAM:
				separationColumnIndices = getChromatogramIndices(chromatogramSelection);
				break;
			case FILES:
				separationColumnIndices = getFileIndices(chromatogramSelection, calculatorSettings);
				break;
			default:
				separationColumnIndices = null;
				break;
		}
		return separationColumnIndices;
	}

	@SuppressWarnings("rawtypes")
	private ISeparationColumnIndices getAutoIndices(IChromatogramSelection chromatogramSelection, CalculatorSettings calculatorSettings) {

		ISeparationColumnIndices separationColumnIndices = getChromatogramIndices(chromatogramSelection);
		if(separationColumnIndices == null) {
			separationColumnIndices = getFileIndices(chromatogramSelection, calculatorSettings);
		} else {
			if(separationColumnIndices.isEmpty()) {
				separationColumnIndices = getFileIndices(chromatogramSelection, calculatorSettings);
			}
		}
		//
		return separationColumnIndices;
	}

	@SuppressWarnings("rawtypes")
	private ISeparationColumnIndices getChromatogramIndices(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null) {
			return chromatogramSelection.getChromatogram().getSeparationColumnIndices();
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private ISeparationColumnIndices getFileIndices(IChromatogramSelection chromatogramSelection, CalculatorSettings calculatorSettings) {

		/*
		 * Prepare the index map.
		 */
		List<String> retentionIndexFiles = calculatorSettings.getRetentionIndexFiles();
		CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
		Map<String, ISeparationColumnIndices> calibrationMap = new HashMap<>();
		for(String retentionIndexFile : retentionIndexFiles) {
			File file = new File(retentionIndexFile);
			ISeparationColumnIndices separationColumnIndices = calibrationFileReader.parse(file);
			ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
			/*
			 * Put both name and separation type name() to be able to map first the specific
			 * column and if that fails, the generic description.
			 */
			calibrationMap.put(separationColumn.getName(), separationColumnIndices);
			calibrationMap.put(separationColumn.getSeparationColumnType().name(), separationColumnIndices);
		}
		/*
		 * Run the calculation.
		 */
		ISeparationColumnIndices separationColumnIndicesChromatogram = chromatogramSelection.getChromatogram().getSeparationColumnIndices();
		ISeparationColumn separationColumn = separationColumnIndicesChromatogram.getSeparationColumn();
		ISeparationColumnIndices separationColumnIndices = calibrationMap.get(separationColumn.getName());
		/*
		 * First try by column name.
		 */
		if(separationColumnIndices == null && calculatorSettings.isUseDefaultColumn()) {
			/*
			 * Second try by separation column.
			 */
			separationColumnIndices = calibrationMap.get(separationColumn.getSeparationColumnType().name());
			if(separationColumnIndices == null && calculatorSettings.isUseDefaultColumn()) {
				separationColumnIndices = calibrationMap.get(SeparationColumnType.DEFAULT.name());
			}
		}
		//
		return separationColumnIndices;
	}

	/**
	 * Calculate the value if both entries exists.
	 * See AMDIS manual:
	 * RIcomp = RIlo + ( (RIhi - RIlo) * (RTact - RTlo) / (RThi - RTlo) )
	 */
	public float calculateRetentionIndex(int retentionTime, ISeparationColumnIndices separationColumnIndices) {

		float retentionIndex = 0;
		Map.Entry<Integer, IRetentionIndexEntry> floorEntry = separationColumnIndices.floorEntry(retentionTime);
		Map.Entry<Integer, IRetentionIndexEntry> ceilingEntry = separationColumnIndices.ceilingEntry(retentionTime);
		//
		if(floorEntry != null && ceilingEntry != null) {
			/*
			 * Get the values.
			 */
			IRetentionIndexEntry floorIndex = floorEntry.getValue();
			IRetentionIndexEntry ceilingIndex = ceilingEntry.getValue();
			float retentionIndexLow = floorIndex.getRetentionIndex();
			int retentionTimeLow = floorIndex.getRetentionTime();
			float retentionIndexHigh = ceilingIndex.getRetentionIndex();
			int retentionTimeHigh = ceilingIndex.getRetentionTime();
			retentionIndex = calculateRetentionIndex(retentionTime, retentionTimeLow, retentionTimeHigh, retentionIndexLow, retentionIndexHigh);
		}
		//
		return retentionIndex;
	}

	public float calculateRetentionIndex(int retentionTime, int retentionTimeLow, int retentionTimeHigh, float retentionIndexLow, float retentionIndexHigh) {

		float retentionIndex = 0;
		//
		if(retentionTimeLow == retentionTimeHigh) {
			/*
			 * We are at an exact value, return simply the RI, otherwise it will fail at if(denominatorRT != 0)
			 */
			return retentionIndexLow;
		}
		/*
		 * Execute the calculation.
		 */
		float factorRetentionIndex = retentionIndexHigh - retentionIndexLow;
		float nominatorRT = retentionTime - retentionTimeLow;
		float denominatorRT = retentionTimeHigh - retentionTimeLow;
		if(denominatorRT != 0) {
			/*
			 * Calculate the retention index.
			 */
			retentionIndex = retentionIndexLow + factorRetentionIndex * nominatorRT / denominatorRT;
		}
		//
		return retentionIndex;
	}

	private void resetIndex(IChromatogram<? extends IPeak> chromatogram) {

		float retentionIndex = 0.0f;
		/*
		 * Scans
		 */
		for(IScan scan : chromatogram.getScans()) {
			scan.setRetentionIndex(retentionIndex);
			/*
			 * Calculate RI also for the optimized MS.
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
				if(optimizedMassSpectrum != null) {
					optimizedMassSpectrum.setRetentionIndex(retentionIndex);
				}
			}
		}
		/*
		 * Peaks
		 */
		for(IPeak peak : chromatogram.getPeaks()) {
			IScan scan = peak.getPeakModel().getPeakMaximum();
			scan.setRetentionIndex(retentionIndex);
		}
	}

	private void calculateIndex(IChromatogram<? extends IPeak> chromatogram, ISeparationColumnIndices separationColumnIndices) {

		if(separationColumnIndices != null) {
			/*
			 * Scans
			 */
			for(IScan scan : chromatogram.getScans()) {
				int retentionTime = scan.getRetentionTime();
				float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
				scan.setRetentionIndex(retentionIndex);
				/*
				 * Calculate RI also for the optimized MS.
				 */
				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
					if(optimizedMassSpectrum != null) {
						optimizedMassSpectrum.setRetentionIndex(retentionIndex);
					}
				}
			}
			/*
			 * Peaks
			 */
			for(IPeak peak : chromatogram.getPeaks()) {
				IScan scan = peak.getPeakModel().getPeakMaximum();
				int retentionTime = scan.getRetentionTime();
				float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
				scan.setRetentionIndex(retentionIndex);
			}
		}
	}
}
