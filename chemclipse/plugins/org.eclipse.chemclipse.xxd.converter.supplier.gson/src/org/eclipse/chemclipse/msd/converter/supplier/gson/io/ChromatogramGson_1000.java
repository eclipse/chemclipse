/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.gson.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.gson.Gson;

public class ChromatogramGson_1000 {

	public static final String MGC = "BSPW";
	public static final String VERSION = "1.0.0.0";
	//
	private static final String SHARE_MGC = "SHARE_MGC";
	private static final String SHARE_VERSION = "VERSION";
	private static final String SHARE_GUID = "GUID";
	//
	private static final String NAME = "NAME";
	//
	private static final String CHROMATOGRAM = "CHROMATOGRAM";
	private static final String DATA_NAME = "DATA_NAME";
	private static final String TYPE = "TYPE";
	private static final String DATE = "DATE";
	private static final String OPERATOR = "OPERATOR";
	private static final String BARCODE = "BARCODE";
	//
	private static final String RTS = "RTS"; // Retention Times (milliseconds)
	private static final String RT = "RT"; // Retention Time [ms]
	private static final String RI = "RI"; // Retention Index
	private static final String TIC = "TIC";
	//
	private static final String MS = "MS";
	private static final String IONS = "IONS"; // m/z
	private static final String INTENSITIES = "INTENSITIES";
	//
	private static final String START_RT = "START_RT"; // Start Retention Time
	private static final String STOP_RT = "STOP_RT"; // Stop Retention Time
	private static final String LEADING = "LEADING";
	private static final String TAILING = "TAILING";
	private static final String AREA = "AREA";
	private static final String SN = "SN"; // Signal to Noise Ratio
	private static final String HEIGHT = "HEIGHT";
	private static final String GRADIENT_ANGLE = "GRADIENT_ANGLE";
	private static final String START_BASELINE = "START_BASELINE";
	private static final String STOP_BASELINE = "STOP_BASELINE";
	//
	private static final String TARGETS = "TARGETS";
	private static final String CAS = "CAS";
	private static final String MF = "MF"; // Match Factor
	private static final String RMF = "RMF"; // Reverse Match Factor
	private static final String MFD = "MFD"; // Match Factor Direct
	private static final String RMFD = "RMFD"; // Reverse Match Factor Direct
	private static final String PROB = "PROB"; // Probability
	private static final String FORMULA = "FORMULA";
	private static final String SMILES = "SMILES";
	private static final String INCHI = "INCHI";
	private static final String MW = "MW"; // Mol weight
	private static final String MISC = "MISC";
	private static final String CONTR = "CONTR";
	private static final String DB = "DB";
	private static final String IDENTIFIER = "IDENTIFIER";
	private static final String ADVISE = "ADVISE";
	//
	private static final String SECTION_OVERVIEW = "SECTION_OVERVIEW";
	private static final String SECTION_IDENTIFIED_SCANS = "SECTION_IDENTIFIED_SCANS";
	private static final String SECTION_IDENTIFIED_PEAKS = "SECTION_IDENTIFIED_PEAKS";

	public String getJSON(IChromatogramMSD chromatogram, IProgressMonitor monitor) {

		/*
		 * File Header
		 */
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put(SHARE_MGC, MGC);
		jsonMap.put(SHARE_VERSION, VERSION);
		jsonMap.put(SHARE_GUID, UUID.randomUUID().toString());
		/*
		 * Chromatogram
		 */
		Map<String, Object> chromatogramMap = getChromatogramMap(chromatogram);
		jsonMap.put(CHROMATOGRAM, chromatogramMap);
		addSectionChromatogramOverview(chromatogram, chromatogramMap);
		addSectionIdentifiedScans(chromatogram, chromatogramMap);
		addSectionIdentifiedPeaks(chromatogram, chromatogramMap);
		/*
		 * Create JSON file.
		 */
		Gson gson = new Gson();
		return gson.toJson(jsonMap);
	}

	private Map<String, Object> getChromatogramMap(IChromatogramMSD chromatogram) {

		Map<String, Object> chromatogramMap = new HashMap<String, Object>();
		chromatogramMap.put(NAME, chromatogram.getName());
		chromatogramMap.put(DATA_NAME, chromatogram.getDataName());
		chromatogramMap.put(OPERATOR, chromatogram.getOperator());
		chromatogramMap.put(BARCODE, chromatogram.getBarcode());
		chromatogramMap.put(TYPE, "");
		chromatogramMap.put(DATE, chromatogram.getDate().toString());
		return chromatogramMap;
	}

	private void addSectionChromatogramOverview(IChromatogramMSD chromatogram, Map<String, Object> chromatogramMap) {

		Map<String, Object> sectionOverview = new HashMap<String, Object>();
		List<Integer> retentionTimes = new ArrayList<Integer>();
		List<Float> retentionIndices = new ArrayList<Float>();
		List<Float> totalIntensities = new ArrayList<Float>();
		for(IScan scan : chromatogram.getScans()) {
			retentionTimes.add(scan.getRetentionTime());
			retentionIndices.add(scan.getRetentionIndex());
			totalIntensities.add(scan.getTotalSignal());
		}
		sectionOverview.put(RT, retentionTimes);
		sectionOverview.put(RI, retentionIndices);
		sectionOverview.put(TIC, totalIntensities);
		chromatogramMap.put(SECTION_OVERVIEW, sectionOverview);
	}

	private void addSectionIdentifiedScans(IChromatogramMSD chromatogram, Map<String, Object> chromatogramMap) {

		List<Map<String, Object>> identifiedScans = new ArrayList<Map<String, Object>>();
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				if(scanMSD.getTargets().size() > 0) {
					Map<String, Object> scanMap = getScanMap(scanMSD);
					scanMap.put(TARGETS, getTargetList(scanMSD.getTargets()));
					identifiedScans.add(scanMap);
				}
			}
		}
		chromatogramMap.put(SECTION_IDENTIFIED_SCANS, identifiedScans);
	}

	private void addSectionIdentifiedPeaks(IChromatogramMSD chromatogram, Map<String, Object> chromatogramMap) {

		List<Map<String, Object>> peakList = new ArrayList<Map<String, Object>>();
		for(IChromatogramPeakMSD peak : chromatogram.getPeaks()) {
			Map<String, Object> peakMap = getPeakMap(peak);
			peakMap.put(MS, getScanMap(peak.getExtractedMassSpectrum()));
			peakMap.put(TARGETS, getTargetList(peak.getTargets()));
			peakList.add(peakMap);
		}
		chromatogramMap.put(SECTION_IDENTIFIED_PEAKS, peakList);
	}

	private Map<String, Object> getScanMap(IScanMSD scanMSD) {

		Map<String, Object> scanMap = new HashMap<String, Object>();
		scanMap.put(RT, scanMSD.getRetentionTime());
		scanMap.put(RI, scanMSD.getRetentionIndex());
		scanMap.put(TIC, scanMSD.getTotalSignal());
		//
		List<Double> ions = new ArrayList<Double>();
		List<Float> intensities = new ArrayList<Float>();
		//
		for(IIon ion : scanMSD.getIons()) {
			ions.add(ion.getIon());
			intensities.add(ion.getAbundance());
		}
		//
		scanMap.put(IONS, ions);
		scanMap.put(INTENSITIES, intensities);
		//
		return scanMap;
	}

	private Map<String, Object> getPeakMap(IChromatogramPeakMSD peak) {

		Map<String, Object> peakMap = new HashMap<String, Object>();
		IPeakModel peakModel = peak.getPeakModel();
		//
		int startRetentionTime = peakModel.getStartRetentionTime();
		int stopRetentionTime = peakModel.getStopRetentionTime();
		//
		peakMap.put(START_RT, startRetentionTime);
		peakMap.put(STOP_RT, stopRetentionTime);
		peakMap.put(LEADING, peakModel.getLeading());
		peakMap.put(TAILING, peakModel.getTailing());
		peakMap.put(AREA, peak.getIntegratedArea());
		peakMap.put(SN, peak.getSignalToNoiseRatio());
		peakMap.put(HEIGHT, peakModel.getPeakMaximum().getTotalSignal());
		peakMap.put(GRADIENT_ANGLE, peakModel.getGradientAngle());
		peakMap.put(START_BASELINE, peakModel.getBackgroundAbundance(startRetentionTime));
		peakMap.put(STOP_BASELINE, peakModel.getBackgroundAbundance(stopRetentionTime));
		//
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		List<Float> intensities = new ArrayList<Float>();
		//
		for(Integer retentionTime : retentionTimes) {
			intensities.add(peakModel.getPeakAbundance(retentionTime));
		}
		peakMap.put(RTS, retentionTimes);
		peakMap.put(INTENSITIES, intensities);
		//
		return peakMap;
	}

	private List<Map<String, Object>> getTargetList(Set<IIdentificationTarget> identificationTargets) {

		List<Map<String, Object>> targetList = new ArrayList<Map<String, Object>>();
		for(IIdentificationTarget identificationTarget : identificationTargets) {
			Map<String, Object> targetMap = new HashMap<String, Object>();
			IComparisonResult comparisonResult = identificationTarget.getComparisonResult();
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			targetMap.put(NAME, libraryInformation.getName());
			targetMap.put(CAS, libraryInformation.getCasNumber());
			targetMap.put(MF, comparisonResult.getMatchFactor());
			targetMap.put(MFD, comparisonResult.getMatchFactorDirect());
			targetMap.put(RMF, comparisonResult.getReverseMatchFactor());
			targetMap.put(RMFD, comparisonResult.getReverseMatchFactorDirect());
			targetMap.put(PROB, comparisonResult.getProbability());
			targetMap.put(FORMULA, libraryInformation.getFormula());
			targetMap.put(SMILES, libraryInformation.getSmiles());
			targetMap.put(INCHI, libraryInformation.getInChI());
			targetMap.put(MW, libraryInformation.getMolWeight());
			targetMap.put(MISC, libraryInformation.getMiscellaneous());
			targetMap.put(CONTR, libraryInformation.getContributor());
			targetMap.put(DB, libraryInformation.getDatabase());
			targetMap.put(IDENTIFIER, libraryInformation.getReferenceIdentifier());
			targetMap.put(ADVISE, comparisonResult.getAdvise());
			targetList.add(targetMap);
		}
		return targetList;
	}
}
