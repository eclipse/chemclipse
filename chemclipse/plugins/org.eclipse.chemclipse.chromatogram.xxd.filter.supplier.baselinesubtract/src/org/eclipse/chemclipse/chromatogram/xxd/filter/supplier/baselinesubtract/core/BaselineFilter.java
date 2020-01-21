/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class BaselineFilter extends AbstractChromatogramFilter {

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			int retentionTime = scan.getRetentionTime();
			float backgroundSignal = baselineModel.getBackground(retentionTime);
			if(backgroundSignal > 0) {
				scan.adjustTotalSignal(backgroundSignal);
			} else {
				scan.adjustTotalSignal(Float.MIN_VALUE);
			}
		}
		chromatogram.recalculateTheNoiseFactor();
		chromatogram.removeAllPeaks();
		baselineModel.removeBaseline();
		return new ProcessingInfo();
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		return applyFilter(chromatogramSelection, null, monitor);
	}
}
