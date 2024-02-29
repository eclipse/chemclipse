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
import java.text.DecimalFormat;
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
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model.CondenseOption;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model.VendorScan;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsTandemMS;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.support.ScanSupport;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterTandemMS extends AbstractChromatogramFilterMSD {

	private static final int DEFAULT_SCAN_DELAY = 0;
	private static final int DEFAULT_SCAN_INTERVAL = 100;
	private static final String LINE_DELIMITER = "\n";

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
				 * Split all or only selected transitions.
				 */
				Set<String> specificTransitions = getSpecificIonTransitions(chromatogramFilterSettings);
				if(specificTransitions.isEmpty()) {
					splitIonTransitions(chromatogramMSD, headerField);
				} else {
					DecimalFormat decimalFormat = getDecimalFormat(chromatogramFilterSettings);
					splitSpecificIonTransitions(chromatogramMSD, headerField, decimalFormat, specificTransitions);
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

		FilterSettingsTandemMS splitterSettings = PreferenceSupplier.getFilterSettingsTandemMS();
		return applyFilter(chromatogramSelection, splitterSettings, monitor);
	}

	private void splitIonTransitions(IChromatogramMSD chromatogramMSD, HeaderField headerField) {

		/*
		 * Ion Transitions
		 */
		Map<IIonTransition, String> ionTransitionLabelMap = new HashMap<>();
		List<String> ionTransitionLabels = getIonTransitionLabels(chromatogramMSD);
		for(String ionTransitionLabel : ionTransitionLabels) {
			/*
			 * Reference Chromatograms
			 */
			IChromatogramMSD chromatogramReferenceMSD = new ChromatogramMSD();
			chromatogramReferenceMSD.setConverterId(chromatogramMSD.getConverterId());
			assignIdentifier(chromatogramReferenceMSD, headerField, ionTransitionLabel);
			//
			for(IScan scan : chromatogramMSD.getScans()) {
				if(scan instanceof IScanMSD scanMSD) {
					VendorScan vendorScanMSD = null;
					for(IIon ion : scanMSD.getIons()) {
						IIonTransition ionTransition = ion.getIonTransition();
						if(ionTransition != null) {
							/*
							 * Identifier
							 */
							String identifier = ionTransitionLabelMap.get(ionTransition);
							if(identifier == null) {
								identifier = ScanSupport.getLabelTandemMS(ionTransition);
								ionTransitionLabelMap.put(ionTransition, identifier);
							}
							//
							if(ionTransitionLabel.equals(identifier)) {
								/*
								 * Create scan on demand.
								 */
								if(vendorScanMSD == null) {
									vendorScanMSD = new VendorScan();
									vendorScanMSD.setRetentionTime(scanMSD.getRetentionTime());
									chromatogramReferenceMSD.addScan(vendorScanMSD);
								}
								/*
								 * Add ion.
								 */
								vendorScanMSD.addIon(ion);
							}
						}
					}
				}
			}
			//
			calculateScanIntervalAndDelay(chromatogramReferenceMSD);
			chromatogramMSD.addReferencedChromatogram(chromatogramReferenceMSD);
		}
	}

	private List<String> getIonTransitionLabels(IChromatogramMSD chromatogramMSD) {

		Set<String> ionTransitions = new HashSet<>();
		for(IIonTransition ionTransition : chromatogramMSD.getIonTransitionSettings().getIonTransitions()) {
			ionTransitions.add(ScanSupport.getLabelTandemMS(ionTransition));
		}
		//
		List<String> ionTransitionLabels = new ArrayList<>(ionTransitions);
		Collections.sort(ionTransitionLabels);
		//
		return ionTransitionLabels;
	}

	private void splitSpecificIonTransitions(IChromatogramMSD chromatogramMSD, HeaderField headerField, DecimalFormat decimalFormat, Set<String> targets) {

		Map<String, List<IIon>> scanMap = new HashMap<>();
		/*
		 * Ion Transitions
		 */
		Set<IIonTransition> ionTransitions = chromatogramMSD.getIonTransitionSettings().getIonTransitions();
		for(IIonTransition ionTransition : ionTransitions) {
			String identifier = ScanSupport.getLabelTandemMS(ionTransition);
			if(targets.contains(identifier)) {
				for(IScan scan : chromatogramMSD.getScans()) {
					if(scan instanceof IScanMSD scanMSD) {
						double positionX = scan.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
						String retentionTimeMinutes = decimalFormat != null ? decimalFormat.format(positionX) : Double.toString(positionX);
						for(IIon ion : scanMSD.getIons()) {
							/*
							 * Collect
							 */
							IIonTransition ionTransitionSelection = ion.getIonTransition();
							if(ionTransitionSelection == ionTransition) {
								List<IIon> ions = scanMap.get(retentionTimeMinutes);
								if(ions == null) {
									ions = new ArrayList<>();
									scanMap.put(retentionTimeMinutes, ions);
								}
								ions.add(ion);
							}
						}
					}
				}
			}
		}
		//
		IChromatogramMSD chromatogramReferenceMSD = new ChromatogramMSD();
		chromatogramReferenceMSD.setConverterId(chromatogramMSD.getConverterId());
		assignIdentifier(chromatogramReferenceMSD, headerField, "Specific Ion Transitions");
		//
		List<IScan> vendorScans = new ArrayList<>();
		for(Map.Entry<String, List<IIon>> entry : scanMap.entrySet()) {
			int retentionTime = (int)Math.round(Double.valueOf(entry.getKey()) * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
			VendorScan vendorScanMSD = new VendorScan();
			vendorScanMSD.setRetentionTime(retentionTime);
			vendorScanMSD.addIons(entry.getValue(), true);
			vendorScans.add(vendorScanMSD);
		}
		//
		Collections.sort(vendorScans, (s1, s2) -> Integer.compare(s1.getRetentionTime(), s2.getRetentionTime()));
		chromatogramReferenceMSD.addScans(vendorScans);
		calculateScanIntervalAndDelay(chromatogramReferenceMSD);
		chromatogramMSD.addReferencedChromatogram(chromatogramReferenceMSD);
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

	private HeaderField getHeaderField(IChromatogramFilterSettings chromatogramFilterSettings) {

		HeaderField headerField = HeaderField.DATA_NAME;
		if(chromatogramFilterSettings instanceof FilterSettingsTandemMS filterSettingsTandemMS) {
			headerField = filterSettingsTandemMS.getHeaderField();
		}
		//
		return headerField;
	}

	/**
	 * May return null.
	 * 
	 * @param chromatogramFilterSettings
	 * @return {@link DecimalFormat}
	 */
	private DecimalFormat getDecimalFormat(IChromatogramFilterSettings chromatogramFilterSettings) {

		DecimalFormat decimalFormat = null;
		if(chromatogramFilterSettings instanceof FilterSettingsTandemMS filterSettingsTandemMS) {
			CondenseOption condenseOption = filterSettingsTandemMS.getCondenseOption();
			switch(condenseOption) {
				case COARSE:
				case STANDARD:
				case SENSITIVE:
					decimalFormat = ValueFormat.getDecimalFormatEnglish(condenseOption.decimalPattern());
					break;
				default:
					break;
			}
		}
		//
		return decimalFormat;
	}

	/*
	 * 267 > 159.1 @15
	 * 301 > 160.1 @10
	 * 333 > 269.1 @10
	 */
	private Set<String> getSpecificIonTransitions(IChromatogramFilterSettings chromatogramFilterSettings) {

		Set<String> specificTransitions = new HashSet<>();
		if(chromatogramFilterSettings instanceof FilterSettingsTandemMS filterSettingsTandemMS) {
			String[] parts = filterSettingsTandemMS.getSpecificTransitions().split(LINE_DELIMITER);
			for(String part : parts) {
				String specificTransition = part.trim();
				if(!specificTransition.isEmpty()) {
					specificTransitions.add(specificTransition);
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
			default:
				break;
		}
	}
}