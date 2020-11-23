/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import java.util.Iterator;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsMSD;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.matrix.ExtractedMatrix;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterMSD extends AbstractChromatogramFilterMSD {

	private IChromatogramFilterResult process(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogram();
		ChromatogramFilterSettingsMSD settings = (ChromatogramFilterSettingsMSD)filterSettings;
		/*
		 * 1. step - export signal from chromatogram
		 */
		IChromatogramFilterResult chromatogramFilterResult;
		if(settings.getPerIonCalculation() == true) {
			try {
				ExtractedMatrix extractedMatrix = new ExtractedMatrix(chromatogramSelection);
				double[][] matrix = extractedMatrix.getMatrix();
				SavitzkyGolayProcessor.apply(matrix, (ChromatogramFilterSettings)filterSettings, monitor);
				extractedMatrix.updateSignal();
				chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been applied successfully.");
			} catch(IllegalArgumentException e) {
				chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, "High Resolution Data is not supported.");
			}
		} else {
			TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramMSD);
			ITotalScanSignals totalSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, true);
			chromatogramFilterResult = SavitzkyGolayProcessor.apply(totalSignals, (ChromatogramFilterSettings)filterSettings, monitor);
			totalSignals.setNegativeTotalSignalsToZero();
			if(chromatogramFilterResult.getResultStatus().equals(ResultStatus.OK)) {
				updateSignal(totalSignals, chromatogramMSD);
			}
		}
		return chromatogramFilterResult;
	}

	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<IChromatogramFilterResult>();
		processingInfo.setProcessingResult(process(chromatogramSelection, chromatogramFilterSettings, monitor));
		if(processingInfo.getProcessingResult().getResultStatus().equals(ResultStatus.EXCEPTION)) {
			StringBuilder proposedString = new StringBuilder("Please run the Scan Filter to nominalize chromatogram first.");
			processingInfo.addErrorMessage(processingInfo.getProcessingResult().getResultStatus().name(), processingInfo.getProcessingResult().getDescription(), proposedString.toString());
		}
		if(processingInfo.getProcessingResult().getResultStatus().equals(ResultStatus.OK)) {
			processingInfo.addInfoMessage(processingInfo.getProcessingResult().getResultStatus().name(), processingInfo.getProcessingResult().getDescription());
		}
		return processingInfo;
	}

	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		ChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getFilterSettingsMSD();
		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<IChromatogramFilterResult>();
		processingInfo.setProcessingResult(process(chromatogramSelection, chromatogramFilterSettings, monitor));
		return processingInfo;
	}

	private void updateSignal(ITotalScanSignals totalSignals, IChromatogramMSD chromatogram) {

		Iterator<Integer> itScan = totalSignals.iterator();
		while(itScan.hasNext()) {
			Integer scan = itScan.next();
			IScanMSD scanMSD = chromatogram.getSupplierScan(scan);
			ITotalScanSignal totalscanSignal = totalSignals.getTotalScanSignal(scan);
			scanMSD.adjustTotalSignal(totalscanSignal.getTotalSignal());
		}
	}
}
