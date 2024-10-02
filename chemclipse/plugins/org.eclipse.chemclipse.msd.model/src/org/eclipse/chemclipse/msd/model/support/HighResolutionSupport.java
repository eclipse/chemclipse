/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.RegularMassSpectrum;
import org.eclipse.chemclipse.support.traces.TraceHighResMSD;

public class HighResolutionSupport {

	private static final int DEFAULT_SCAN_DELAY = 0;
	private static final int DEFAULT_SCAN_INTERVAL = 100;

	public static List<IChromatogramMSD> extractHighResolutionData(IChromatogramMSD chromatogramMSD, HeaderField headerField, Set<TraceHighResMSD> traces) {

		List<IChromatogramMSD> chromatograms = new ArrayList<>();
		/*
		 * Collect
		 */
		Map<TraceHighResMSD, Map<Integer, Float>> scanRangesMap = new HashMap<>();
		for(IScan scan : chromatogramMSD.getScans()) {
			if(scan instanceof IScanMSD scanMSD) {
				int retentionTime = scan.getRetentionTime();
				for(TraceHighResMSD trace : traces) {
					for(IIon ion : scanMSD.getIons()) {
						if(trace.matches(ion.getIon())) {
							Map<Integer, Float> scans = scanRangesMap.get(trace);
							if(scans == null) {
								scans = new HashMap<>();
								scans.put(scan.getRetentionTime(), ion.getAbundance());
								scanRangesMap.put(trace, scans);
							} else {
								Float intensity = scans.get(retentionTime);
								if(intensity == null) {
									scans.put(retentionTime, ion.getAbundance());
								} else {
									scans.put(retentionTime, intensity + ion.getAbundance());
								}
							}
						}
					}
				}
			}
		}
		/*
		 * Trace Chromatograms
		 */
		for(TraceHighResMSD trace : traces) {
			Map<Integer, Float> scans = scanRangesMap.get(trace);
			if(scans != null) {
				IChromatogramMSD chromatogramReferenceMSD = new ChromatogramMSD();
				chromatogramReferenceMSD.setConverterId(chromatogramMSD.getConverterId());
				assignIdentifier(chromatogramReferenceMSD, headerField, trace.toString());
				//
				List<Integer> retentionTimes = new ArrayList<>(scans.keySet());
				Collections.sort(retentionTimes);
				for(int retentionTime : retentionTimes) {
					float intensity = scans.getOrDefault(retentionTime, 0.0f);
					if(intensity > 0.0f) {
						IIon ion = new Ion(trace.getMZ(), scans.get(retentionTime));
						RegularMassSpectrum scanMSD = new RegularMassSpectrum();
						scanMSD.setRetentionTime(retentionTime);
						scanMSD.addIon(ion);
						chromatogramReferenceMSD.addScan(scanMSD);
					}
				}
				//
				calculateScanIntervalAndDelay(chromatogramReferenceMSD);
				chromatograms.add(chromatogramReferenceMSD);
			}
		}
		//
		return chromatograms;
	}

	private static void assignIdentifier(IChromatogram<?> chromatogram, HeaderField headerField, String identifier) {

		switch(headerField) {
			case NAME:
				chromatogram.setFile(new File(identifier));
				break;
			case DATA_NAME:
				chromatogram.setDataName(identifier);
				break;
			case MISC_INFO:
				chromatogram.setMiscInfo(identifier);
				break;
			case SAMPLE_GROUP:
				chromatogram.setSampleGroup(identifier);
				break;
			case SAMPLE_NAME:
				chromatogram.setSampleName(identifier);
				break;
			case SHORT_INFO:
				chromatogram.setShortInfo(identifier);
				break;
			case TAGS:
				chromatogram.setTags(identifier);
				break;
			default:
				break;
		}
	}

	private static void calculateScanIntervalAndDelay(IChromatogram<?> chromatogram) {

		int startRetentionTime = chromatogram.getStartRetentionTime();
		int stopRetentionTime = chromatogram.getStopRetentionTime();
		float deltaRetentionTime = stopRetentionTime - startRetentionTime + 1;
		int numberOfScans = chromatogram.getNumberOfScans();
		/*
		 * Delay
		 */
		int scanDelay = DEFAULT_SCAN_DELAY;
		if(startRetentionTime > 0) {
			scanDelay = startRetentionTime;
		}
		/*
		 * Interval
		 */
		int scanInterval = DEFAULT_SCAN_INTERVAL;
		if(numberOfScans > 0 && deltaRetentionTime > 0) {
			float calculation = deltaRetentionTime / numberOfScans / 10.0f;
			scanInterval = Math.round(calculation) * 10;
		}
		/*
		 * Adjust the scan delay and interval.
		 * But don't recalculate the retention times here:
		 * chromatogram.recalculateRetentionTimes();
		 */
		chromatogram.setScanDelay(scanDelay);
		chromatogram.setScanInterval(scanInterval);
	}
}