/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.CalculatorSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class RetentionIndexCalculator {

	private static final Logger logger = Logger.getLogger(RetentionIndexCalculator.class);
	private static final Pattern PATTERN_ALKANE = Pattern.compile("(C)(\\d+)");

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

	public static float getRetentionIndex(String name) {

		float retentionIndex = 0.0f;
		Matcher matcher = PATTERN_ALKANE.matcher(name);
		if(matcher.find()) {
			try {
				/*
				 * C8 (Octane)
				 * => 800
				 */
				retentionIndex = Integer.parseInt(matcher.group(2)) * 100;
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		//
		return retentionIndex;
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo apply(IChromatogramSelection chromatogramSelection, CalculatorSettings calculatorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(chromatogramSelection != null) {
			ISeparationColumnIndices separationColumnIndices = getSeparationColumnIndices(chromatogramSelection, calculatorSettings);
			if(separationColumnIndices != null) {
				/*
				 * Master
				 */
				calculateIndex(chromatogramSelection, separationColumnIndices);
				if(PreferenceSupplier.isProcessReferencedChromatograms()) {
					/*
					 * References
					 */
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					for(Object object : chromatogram.getReferencedChromatograms()) {
						IChromatogramSelection chromatogramSelectionReference = getChromatogramSelection(object);
						if(chromatogramSelectionReference != null) {
							calculateIndex(chromatogramSelectionReference, separationColumnIndices);
						}
					}
				}
			}
		}
		//
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	private ISeparationColumnIndices getSeparationColumnIndices(IChromatogramSelection chromatogramSelection, CalculatorSettings calculatorSettings) {

		ISeparationColumnIndices separationColumnIndices;
		switch(PreferenceSupplier.getDetectionStrategy()) {
			case PreferenceSupplier.DETECTION_STRATEGY_AUTO:
				separationColumnIndices = getAutoIndices(chromatogramSelection, calculatorSettings);
				break;
			case PreferenceSupplier.DETECTION_STRATEGY_CHROMATOGRAM:
				separationColumnIndices = getChromatogramIndices(chromatogramSelection);
				break;
			case PreferenceSupplier.DETECTION_STRATEGY_FILES:
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
			if(separationColumnIndices.size() == 0) {
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
			calibrationMap.put(separationColumn.getName(), separationColumnIndices);
		}
		/*
		 * Run the calculation.
		 */
		String columnName = chromatogramSelection.getChromatogram().getSeparationColumnIndices().getSeparationColumn().getName();
		ISeparationColumnIndices separationColumnIndices = calibrationMap.get(columnName);
		if(separationColumnIndices == null) {
			if(PreferenceSupplier.isUseDefaultColumn()) {
				separationColumnIndices = calibrationMap.get(SeparationColumnFactory.TYPE_DEFAULT);
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void calculateIndex(IChromatogramSelection chromatogramSelection, ISeparationColumnIndices separationColumnIndices) {

		if(separationColumnIndices != null) {
			IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int startScan = chromatogram.getScanNumber(startRetentionTime);
			int stopScan = chromatogram.getScanNumber(stopRetentionTime);
			/*
			 * Scans
			 */
			for(int scan = startScan; scan <= stopScan; scan++) {
				IScan supplierScan = chromatogram.getScan(scan);
				int retentionTime = supplierScan.getRetentionTime();
				float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
				supplierScan.setRetentionIndex(retentionIndex);
				/*
				 * Calculate RI also for the optimized MS.
				 */
				if(supplierScan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)supplierScan;
					IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
					if(optimizedMassSpectrum != null) {
						optimizedMassSpectrum.setRetentionIndex(retentionIndex);
					}
				}
			}
			/*
			 * Peaks
			 */
			List<? extends IPeak> peaks = chromatogram.getPeaks();
			for(IPeak peak : peaks) {
				IScan scan = peak.getPeakModel().getPeakMaximum();
				int retentionTime = scan.getRetentionTime();
				if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
					float retentionIndex = calculateRetentionIndex(retentionTime, separationColumnIndices);
					scan.setRetentionIndex(retentionIndex);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public IChromatogramSelection getChromatogramSelection(Object object) {

		if(object instanceof IChromatogramSelection) {
			return (IChromatogramSelection)object;
		} else if(object instanceof IChromatogramCSD) {
			return new ChromatogramSelectionCSD((IChromatogramCSD)object);
		} else if(object instanceof IChromatogramMSD) {
			return new ChromatogramSelectionMSD((IChromatogramMSD)object);
		} else if(object instanceof IChromatogramWSD) {
			return new ChromatogramSelectionWSD((IChromatogramWSD)object);
		} else {
			return null;
		}
	}
}
