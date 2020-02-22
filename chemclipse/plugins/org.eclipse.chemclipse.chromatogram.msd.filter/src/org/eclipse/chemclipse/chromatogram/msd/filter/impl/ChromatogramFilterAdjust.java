/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.impl;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.impl.settings.FilterSettingsAdjust;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalsModifier;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterAdjust extends AbstractChromatogramFilterMSD {

	private static final Logger logger = Logger.getLogger(ChromatogramFilterAdjust.class);

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsAdjust) {
				/*
				 * No settings needed yet.
				 */
				// FilterSettingsAdjust filterSettings = (FilterSettingsAdjust)chromatogramFilterSettings;
				if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
					IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
					try {
						IChromatogramMSD chromatogram = chromatogramSelectionMSD.getChromatogram();
						IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
						IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
						ExtractedIonSignalsModifier.adjustThresholdTransitions(extractedIonSignals);
						/*
						 * Apply the adjusted signals
						 */
						int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
						int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
						//
						for(int i = startScan; i <= stopScan; i++) {
							IScan scan = chromatogram.getScan(i);
							if(scan instanceof IScanMSD) {
								IScanMSD scanMSD = (IScanMSD)scan;
								IExtractedIonSignal extractedIonSignalOriginal = scanMSD.getExtractedIonSignal();
								IExtractedIonSignal extractedIonSignalAdjusted = extractedIonSignals.getExtractedIonSignal(i);
								int startMZ = extractedIonSignalAdjusted.getStartIon();
								int stopMZ = extractedIonSignalAdjusted.getStopIon();
								for(int j = startMZ; j <= stopMZ; j++) {
									/*
									 * The abundance must be > 0. Dropped intensities due
									 * to adjustment of the threshold transition must have
									 * been at least adjusted to a value > 0.
									 */
									float intensity = extractedIonSignalAdjusted.getAbundance(j);
									if(intensity > 0) {
										/*
										 * If the original scan doesn't contain a m/z, it seems,
										 * that the intensity was below the predefined threshold
										 * of the system.
										 */
										if(extractedIonSignalOriginal.getAbundance(j) == 0) {
											IIon ion = new Ion(j, intensity);
											scanMSD.addIon(ion);
										}
									}
								}
							}
						}
					} catch(Exception e) {
						logger.warn(e);
					}
				}
				//
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Chromatogram Filter Adjust applied"));
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsAdjust filterSettings = PreferenceSupplier.getFilterSettingsAdjust();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}
}
