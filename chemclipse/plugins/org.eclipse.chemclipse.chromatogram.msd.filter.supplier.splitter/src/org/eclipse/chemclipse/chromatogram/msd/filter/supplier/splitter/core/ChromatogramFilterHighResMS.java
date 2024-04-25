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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model.HighResolutionRange;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model.VendorScan;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsHighResMS;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterHighResMS extends AbstractChromatogramFilterMSD {

	private static final int DEFAULT_SCAN_DELAY = 0;
	private static final int DEFAULT_SCAN_INTERVAL = 100;
	private static final String LINE_DELIMITER = "\n";
	private static final String RANGE_SYMBOL = "±";
	private static final String PPM = "ppm";

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> validation = validate(chromatogramSelection, chromatogramFilterSettings);
		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validation);
		//
		if(!processingInfo.hasErrorMessages()) {
			HeaderField headerField = getHeaderField(chromatogramFilterSettings);
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
				/*
				 * Split selected traces.
				 */
				int binning = getBinning(chromatogramFilterSettings);
				Set<String> specificTraces = getSpecificTraces(chromatogramFilterSettings);
				if(!specificTraces.isEmpty()) {
					splitHighResData(chromatogramMSD, headerField, binning, specificTraces);
				}
			}
			//
			chromatogramSelection.getChromatogram().setDirty(true);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram was splitted into MS/MS reference chromatograms."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsHighResMS splitterSettings = PreferenceSupplier.getFilterSettingsHighResMS();
		return applyFilter(chromatogramSelection, splitterSettings, monitor);
	}

	private void splitHighResData(IChromatogramMSD chromatogramMSD, HeaderField headerField, int binning, Set<String> traces) {

		List<HighResolutionRange> highResolutionRanges = getHighResolutionRanges(binning, traces);
		Map<HighResolutionRange, Map<Integer, Float>> scanRangesMap = new HashMap<>();
		/*
		 * Collect
		 */
		for(IScan scan : chromatogramMSD.getScans()) {
			if(scan instanceof IScanMSD scanMSD) {
				int retentionTime = scan.getRetentionTime();
				for(HighResolutionRange highResolutionRange : highResolutionRanges) {
					for(IIon ion : scanMSD.getIons()) {
						if(highResolutionRange.matches(ion.getIon())) {
							Map<Integer, Float> scans = scanRangesMap.get(highResolutionRange);
							if(scans == null) {
								scans = new HashMap<>();
								scans.put(scan.getRetentionTime(), ion.getAbundance());
								scanRangesMap.put(highResolutionRange, scans);
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
		 * Reference Chromatograms
		 */
		for(HighResolutionRange highResolutionRange : highResolutionRanges) {
			Map<Integer, Float> scans = scanRangesMap.get(highResolutionRange);
			if(scans != null) {
				double mz = highResolutionRange.getMZ();
				IChromatogramMSD chromatogramReferenceMSD = new ChromatogramMSD();
				chromatogramReferenceMSD.setConverterId(chromatogramMSD.getConverterId());
				assignIdentifier(chromatogramReferenceMSD, headerField, Double.toString(mz));
				//
				List<Integer> retentionTimes = new ArrayList<>(scans.keySet());
				Collections.sort(retentionTimes);
				for(int retentionTime : retentionTimes) {
					float intensity = scans.getOrDefault(retentionTime, 0.0f);
					if(intensity > 0.0f) {
						IIon ion = new Ion(mz, scans.get(retentionTime));
						VendorScan vendorScanMSD = new VendorScan();
						vendorScanMSD.setRetentionTime(retentionTime);
						vendorScanMSD.addIon(ion);
						chromatogramReferenceMSD.addScan(vendorScanMSD);
					}
				}
				//
				calculateScanIntervalAndDelay(chromatogramReferenceMSD);
				chromatogramMSD.addReferencedChromatogram(chromatogramReferenceMSD);
			}
		}
	}

	private List<HighResolutionRange> getHighResolutionRanges(int binning, Set<String> traces) {

		Set<HighResolutionRange> highResolutionRanges = new HashSet<>();
		for(String trace : traces) {
			if(trace.contains(RANGE_SYMBOL) && trace.contains(PPM)) {
				/*
				 * PPM
				 * 400.01627±10ppm
				 */
				String[] parts = trace.split(RANGE_SYMBOL);
				if(parts.length == 2) {
					try {
						double mz = Double.parseDouble(parts[0].trim());
						int binningSpecific = Integer.parseInt(parts[1].replace(PPM, "").trim());
						highResolutionRanges.add(new HighResolutionRange(mz, binningSpecific));
					} catch(NumberFormatException e) {
					}
				}
			} else {
				/*
				 * Default
				 * 400.01627
				 */
				try {
					double mz = Double.parseDouble(trace.trim());
					highResolutionRanges.add(new HighResolutionRange(mz, binning));
				} catch(NumberFormatException e) {
				}
			}
		}
		//
		List<HighResolutionRange> highResolutionRangesSorted = new ArrayList<HighResolutionRange>(highResolutionRanges);
		Collections.sort(highResolutionRangesSorted, (r1, r2) -> Double.compare(r1.getTraceStart(), r2.getTraceStart()));
		//
		return highResolutionRangesSorted;
	}

	private void calculateScanIntervalAndDelay(IChromatogram<?> chromatogram) {

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
		 * Adjust the retention times.
		 */
		chromatogram.setScanDelay(scanDelay);
		chromatogram.setScanInterval(scanInterval);
		chromatogram.recalculateRetentionTimes();
	}

	private int getBinning(IChromatogramFilterSettings chromatogramFilterSettings) {

		int binning = 0;
		if(chromatogramFilterSettings instanceof FilterSettingsHighResMS filterSettingsHighResMS) {
			binning = filterSettingsHighResMS.getBinning();
		}
		//
		return binning;
	}

	private HeaderField getHeaderField(IChromatogramFilterSettings chromatogramFilterSettings) {

		HeaderField headerField = HeaderField.DATA_NAME;
		if(chromatogramFilterSettings instanceof FilterSettingsHighResMS filterSettingsHighResMS) {
			headerField = filterSettingsHighResMS.getHeaderField();
		}
		//
		return headerField;
	}

	/*
	 */
	private Set<String> getSpecificTraces(IChromatogramFilterSettings chromatogramFilterSettings) {

		Set<String> specificTransitions = new HashSet<>();
		if(chromatogramFilterSettings instanceof FilterSettingsHighResMS filterSettingsHighResMS) {
			String[] parts = filterSettingsHighResMS.getSpecificTraces().split(LINE_DELIMITER);
			for(String part : parts) {
				String specificTrace = part.trim();
				if(!specificTrace.isEmpty()) {
					specificTransitions.add(specificTrace);
				}
			}
		}
		//
		return specificTransitions;
	}

	private void assignIdentifier(IChromatogram<?> chromatogram, HeaderField headerField, String identifier) {

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
}