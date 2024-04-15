/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsMSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.matrix.ExtractedMatrix;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SavitzkyGolayPerIonOperation extends AbstractOperation implements ISavitzkyGolayOperation {

	private IChromatogramSelectionMSD chromatogramSelection;
	private ChromatogramFilterSettingsMSD filterSettings;
	//
	private List<IScanMSD> previousScans;
	private IChromatogramFilterResult chromatogramFilterResult;

	public SavitzkyGolayPerIonOperation(IChromatogramSelectionMSD chromatogramSelection, ChromatogramFilterSettingsMSD filterSettings) {

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

		return canExecute();
	}

	@Override
	public boolean canUndo() {

		return previousScans != null;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) {

		try {
			previousScans = new ArrayList<>();
			for(IScan scan : chromatogramSelection.getChromatogram().getScans()) {
				IScanMSD scanMSD = (IScanMSD)scan;
				previousScans.add(scanMSD.makeDeepCopy());
			}
			ExtractedMatrix extractedMatrix = new ExtractedMatrix(chromatogramSelection);
			double[][] matrix = extractedMatrix.getMatrix();
			SavitzkyGolayProcessor.apply(matrix, filterSettings);
			extractedMatrix.updateSignal();
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.OK, MessageFormat.format("Smoothed {0} scans.", extractedMatrix.getScanNumbers().length));
			updateChromatogramSelection();
		} catch(IllegalArgumentException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, "High Resolution Data is not supported.");
			return Status.CANCEL_STATUS;
		} catch(CloneNotSupportedException e) {
			chromatogramFilterResult = new ChromatogramFilterResult(ResultStatus.EXCEPTION, "Failed to backup data. Aborting Savitzky-Golay filter.");
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	@Override
	public IChromatogramFilterResult getChromatogramFilterResult() {

		return chromatogramFilterResult;
	}

	@Override
	public String getLabel() {

		return "Savitzky Golay per Ion Filter";
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) {

		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) {

		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		chromatogram.getScans().clear();
		for(IScanMSD scan : previousScans) {
			chromatogram.addScan(scan);
		}
		updateChromatogramSelection();
		return Status.OK_STATUS;
	}

	private void updateChromatogramSelection() {

		chromatogramSelection.update(true);
		chromatogramSelection.getChromatogram().setDirty(true);
	}
}