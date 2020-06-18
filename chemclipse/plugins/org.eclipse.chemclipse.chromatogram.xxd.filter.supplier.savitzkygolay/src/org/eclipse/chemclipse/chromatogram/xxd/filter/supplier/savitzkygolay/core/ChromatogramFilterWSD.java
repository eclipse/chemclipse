/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - Ion-wise msd chromatogram filter
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import java.util.Iterator;

import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.AbstractChromatogramFilterWSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterWSD extends AbstractChromatogramFilterWSD {

	private IChromatogramFilterResult process(IChromatogramSelectionWSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IChromatogramWSD chromatogramWSD = chromatogramSelection.getChromatogram();
		/*
		 * 1. step - export signal from chromatogram
		 */
		TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramWSD);
		/*
		 * here is different between CSD and MSD, in case csd is NOT check if value are negative
		 */
		ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, false);
		/*
		 * 2. step - process signal
		 */
		IChromatogramFilterResult chromatogramFilterResult = SavitzkyGolayProcessor.apply(totalSignals, (ChromatogramFilterSettings)chromatogramFilterSettings, monitor);
		/*
		 * 3. step - adjust signal in chromatogram if data has been process successfully
		 */
		if(chromatogramFilterResult.getResultStatus().equals(ResultStatus.OK)) {
			Iterator<Integer> itScan = totalSignals.iterator();
			while(itScan.hasNext()) {
				Integer scan = itScan.next();
				IScanWSD scanWSD = chromatogramWSD.getSupplierScan(scan);
				ITotalScanSignal totalscanSignal = totalSignals.getTotalScanSignal(scan);
				scanWSD.adjustTotalSignal(totalscanSignal.getTotalSignal());
			}
		}
		return chromatogramFilterResult;
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo applyFilter(IChromatogramSelectionWSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<IChromatogramFilterResult>();
		processingInfo.setProcessingResult(process(chromatogramSelection, chromatogramFilterSettings, monitor));
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo applyFilter(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		ChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getFilterSettings();
		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<IChromatogramFilterResult>();
		processingInfo.setProcessingResult(process(chromatogramSelection, chromatogramFilterSettings, monitor));
		return processingInfo;
	}
}
