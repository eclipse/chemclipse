/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Lorenz Gerber - DAD signal shift
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.AbstractChromatogramFilterWSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.core.settings.FilterSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterWSD extends AbstractChromatogramFilterWSD {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionWSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			applyFilter(chromatogramSelection);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection was successfully set to zero"));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsWSD filterSettings = PreferenceSupplier.getFilterSettingsWSD();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void applyFilter(IChromatogramSelectionWSD chromatogramSelection) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		adjustMinSignalByWavelength(chromatogram);
	}

	private void adjustMinSignalByWavelength(IChromatogram<? extends IPeak> chromatogram) {

		List<IExtractedWavelengthSignal> extractedSignals = new ArrayList<>();
		int startWavelength = Integer.MAX_VALUE;
		int stopWavelength = Integer.MIN_VALUE;
		/*
		 * Extract
		 */
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanWSD) {
				IScanWSD scanWSD = (IScanWSD)scan;
				IExtractedWavelengthSignal extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
				startWavelength = Math.min(extractedWavelengthSignal.getStartWavelength(), startWavelength);
				stopWavelength = Math.max(extractedWavelengthSignal.getStopWavelength(), stopWavelength);
				extractedSignals.add(extractedWavelengthSignal);
			}
		}
		/*
		 * Detect global min signal
		 */
		float minSignal = 0;
		for(int wavelength = startWavelength; wavelength <= stopWavelength; wavelength++) {
			minSignal = Math.min(getMinSignal(extractedSignals, wavelength), minSignal);
		}
		/*
		 * Shift Signal
		 */
		for(int wavelength = startWavelength; wavelength <= stopWavelength; wavelength++) {
			adjustMinSignal(extractedSignals, wavelength, minSignal);
		}
		/*
		 * Transfer the data
		 */
		transferData(chromatogram, extractedSignals);
	}

	private float getMinSignal(List<IExtractedWavelengthSignal> extractedSignals, int wavelength) {

		float minSignal = Float.MAX_VALUE;
		for(IExtractedWavelengthSignal extractedSignal : extractedSignals) {
			minSignal = Math.min(extractedSignal.getAbundance(wavelength), minSignal);
		}
		//
		return minSignal;
	}

	private void adjustMinSignal(List<IExtractedWavelengthSignal> extractedSignals, int wavelength, float minSignal) {

		float offsetSignal = Math.abs(minSignal);
		for(IExtractedWavelengthSignal extractedSignal : extractedSignals) {
			extractedSignal.setAbundance(wavelength, extractedSignal.getAbundance(wavelength) + offsetSignal);
		}
	}

	private void transferData(IChromatogram<? extends IPeak> chromatogram, List<IExtractedWavelengthSignal> extractedSignals) {

		int index = 0;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanWSD) {
				IScanWSD scanWSD = (IScanWSD)scan;
				scanWSD.deleteScanSignals();
				IExtractedWavelengthSignal extractedWavelengthSignal = extractedSignals.get(index);
				int startWavelength = extractedWavelengthSignal.getStartWavelength();
				int stopWavelength = extractedWavelengthSignal.getStopWavelength();
				for(int wavelength = startWavelength; wavelength <= stopWavelength; wavelength++) {
					float intensity = extractedWavelengthSignal.getAbundance(wavelength);
					if(intensity != 0.0f) {
						scanWSD.addScanSignal(new ScanSignalWSD(wavelength, intensity));
					}
				}
				index++;
			}
		}
	}
}
