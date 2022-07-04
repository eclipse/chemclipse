/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 * Matthias Mailänder - undo/redo
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.calculator.operations.ISavitzkyGolayOperation;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.calculator.operations.SavitzkyGolayPerIonOperation;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.calculator.operations.SavitzkyGolayTotalScanSignalOperation;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterMSD extends AbstractChromatogramFilterMSD {

	private static final Logger logger = Logger.getLogger(ChromatogramFilterMSD.class);

	private IChromatogramFilterResult process(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		ChromatogramFilterSettingsMSD settings = (ChromatogramFilterSettingsMSD)filterSettings;
		ISavitzkyGolayOperation savitzkyGolayOperation;
		if(settings.getPerIonCalculation()) {
			savitzkyGolayOperation = new SavitzkyGolayPerIonOperation(chromatogramSelection, settings);
		} else {
			savitzkyGolayOperation = new SavitzkyGolayTotalScanSignalOperation(chromatogramSelection, settings);
		}
		savitzkyGolayOperation.addContext(UndoContextFactory.getUndoContext());
		try {
			OperationHistoryFactory.getOperationHistory().execute(savitzkyGolayOperation, monitor, null);
		} catch(ExecutionException e) {
			logger.warn(e);
		}
		return savitzkyGolayOperation.getChromatogramFilterResult();
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<>();
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

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		ChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getFilterSettingsMSD();
		IProcessingInfo<IChromatogramFilterResult> processingInfo = new ProcessingInfo<>();
		processingInfo.setProcessingResult(process(chromatogramSelection, chromatogramFilterSettings, monitor));
		return processingInfo;
	}
}
