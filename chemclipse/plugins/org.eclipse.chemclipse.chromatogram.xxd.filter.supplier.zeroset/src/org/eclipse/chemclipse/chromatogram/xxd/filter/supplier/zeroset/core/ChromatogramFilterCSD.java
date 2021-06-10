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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.core;

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.AbstractChromatogramFilterCSD;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.core.settings.FilterSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterCSD extends AbstractChromatogramFilterCSD {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionCSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			applyFilter(chromatogramSelection);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection was successfully set to zero"));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsCSD filterSettings = PreferenceSupplier.getFilterSettingsCSD();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void applyFilter(IChromatogramSelectionCSD chromatogramSelection) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		float minSignal = chromatogram.getMinSignal();
		if(minSignal < 0) {
			float delta = minSignal * -1;
			for(IScan scan : chromatogram.getScans()) {
				float adjustedIntensity = scan.getTotalSignal() + delta;
				scan.adjustTotalSignal(adjustedIntensity);
			}
		}
	}
}
