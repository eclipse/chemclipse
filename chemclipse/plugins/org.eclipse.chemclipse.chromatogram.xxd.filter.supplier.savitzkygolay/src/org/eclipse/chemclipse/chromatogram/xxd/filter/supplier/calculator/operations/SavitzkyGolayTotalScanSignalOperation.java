/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.calculator.operations;

import java.util.Iterator;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsMSD;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SavitzkyGolayTotalScanSignalOperation extends AbstractOperation implements ISavitzkyGolayOperation {

	private IChromatogramSelectionMSD chromatogramSelection;
	private ChromatogramFilterSettingsMSD filterSettings;
	//
	private ITotalScanSignals totalScanSignals;
	private ITotalScanSignals previousTotalScanSignals;
	private IChromatogramFilterResult chromatogramFilterResult;

	public SavitzkyGolayTotalScanSignalOperation(IChromatogramSelectionMSD chromatogramSelection, ChromatogramFilterSettingsMSD filterSettings) {

		super("Savitzky-Golay");
		this.chromatogramSelection = chromatogramSelection;
		this.filterSettings = filterSettings;
	}

	@Override
	public boolean canExecute() {

		return chromatogramSelection != null && chromatogramSelection.getChromatogram() instanceof IChromatogramMSD;
	}

	@Override
	public boolean canRedo() {

		return totalScanSignals != null;
	}

	@Override
	public boolean canUndo() {

		return previousTotalScanSignals != null;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) {

		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogram();
		TotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogramMSD);
		totalScanSignals = totalScanSignalExtractor.getTotalScanSignals(chromatogramSelection, true);
		previousTotalScanSignals = totalScanSignals.makeDeepCopy();
		chromatogramFilterResult = SavitzkyGolayProcessor.apply(totalScanSignals, filterSettings, monitor);
		totalScanSignals.setNegativeTotalSignalsToZero();
		if(chromatogramFilterResult.getResultStatus().equals(ResultStatus.OK)) {
			updateSignal(totalScanSignals, chromatogramMSD);
			updateChromatogramSelection();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IChromatogramFilterResult getChromatogramFilterResult() {

		return chromatogramFilterResult;
	}

	@Override
	public String getLabel() {

		return "Savitzky Golay Total Signal Filter";
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) {

		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogram();
		updateSignal(totalScanSignals, chromatogramMSD);
		chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been re-applied successfully.");
		updateChromatogramSelection();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) {

		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogram();
		updateSignal(previousTotalScanSignals, chromatogramMSD);
		chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, "The Savitzky-Golay filter has been reverted successfully.");
		updateChromatogramSelection();
		return Status.OK_STATUS;
	}

	private void updateSignal(ITotalScanSignals totalSignals, IChromatogramMSD chromatogram) {

		Iterator<Integer> iteratorScan = totalSignals.iterator();
		while(iteratorScan.hasNext()) {
			Integer scan = iteratorScan.next();
			IScanMSD scanMSD = chromatogram.getSupplierScan(scan);
			ITotalScanSignal totalscanSignal = totalSignals.getTotalScanSignal(scan);
			scanMSD.adjustTotalSignal(totalscanSignal.getTotalSignal());
		}
	}

	private void updateChromatogramSelection() {

		chromatogramSelection.update(true);
		chromatogramSelection.getChromatogram().setDirty(true);
	}
}