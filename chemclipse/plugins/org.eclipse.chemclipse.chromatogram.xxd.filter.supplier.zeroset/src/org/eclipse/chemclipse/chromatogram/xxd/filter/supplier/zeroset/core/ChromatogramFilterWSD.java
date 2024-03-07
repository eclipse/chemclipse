/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.AbstractChromatogramFilterWSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.core.settings.FilterSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterWSD extends AbstractChromatogramFilterWSD {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionWSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			applyFilter(chromatogramSelection);
			chromatogramSelection.getChromatogram().setDirty(true);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection was successfully set to zero"));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsWSD filterSettings = PreferenceSupplier.getFilterSettingsWSD();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void applyFilter(IChromatogramSelectionWSD chromatogramSelection) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		adjustMinSignalByWavelength(chromatogram);
		chromatogramSelection.reset();
	}

	private void adjustMinSignalByWavelength(IChromatogram<? extends IPeak> chromatogram) {

		/*
		 * Detect global min signal
		 */
		float minSignal = Float.MAX_VALUE;
		for(IScan scan : chromatogram.getScans()) {
			if(scan instanceof IScanWSD scanWSD) {
				minSignal = Math.min(minSignal, getMinSignal(scanWSD));
			}
		}
		/*
		 * Adjust
		 */
		if(minSignal < 0) {
			float offsetSignal = Math.abs(minSignal);
			for(IScan scan : chromatogram.getScans()) {
				if(scan instanceof IScanWSD scanWSD) {
					adjustSignal(scanWSD, offsetSignal);
				}
			}
			chromatogram.setDirty(true);
		}
	}

	private float getMinSignal(IScanWSD scanWSD) {

		float minSignal = Float.MAX_VALUE;
		for(IScanSignalWSD scanSignal : scanWSD.getScanSignals()) {
			minSignal = Math.min(minSignal, scanSignal.getAbsorbance());
		}
		//
		return minSignal;
	}

	private void adjustSignal(IScanWSD scanWSD, float offsetSignal) {

		for(IScanSignalWSD scanSignal : scanWSD.getScanSignals()) {
			scanSignal.setAbsorbance(scanSignal.getAbsorbance() + offsetSignal);
		}
	}
}
