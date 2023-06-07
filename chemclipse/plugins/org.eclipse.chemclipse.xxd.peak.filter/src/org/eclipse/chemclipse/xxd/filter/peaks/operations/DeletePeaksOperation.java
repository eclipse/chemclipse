/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks.operations;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class DeletePeaksOperation extends AbstractOperation {

	private IChromatogramSelection<?, ?> chromatogramSelection;
	private List<IPeak> peaksToDelete;

	public DeletePeaksOperation(IChromatogramSelection<?, ?> chromatogramSelection, List<IPeak> peaksToDelete) {

		super("Delete Peaks");
		this.chromatogramSelection = chromatogramSelection;
		this.peaksToDelete = peaksToDelete;
	}

	@Override
	public boolean canExecute() {

		return !peaksToDelete.isEmpty() && chromatogramSelection != null;
	}

	@Override
	public boolean canRedo() {

		return !peaksToDelete.isEmpty();
	}

	@Override
	public boolean canUndo() {

		return !peaksToDelete.isEmpty();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		chromatogram.removePeaks(peaksToDelete);
		updateChromatogramSelection();
		return Status.OK_STATUS;
	}

	@Override
	public String getLabel() {

		return "Delete Peaks";
	}

	private void updateChromatogramSelection() {

		chromatogramSelection.setSelectedPeak(null);
		chromatogramSelection.update(true);
		chromatogramSelection.getChromatogram().setDirty(true);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		return execute(monitor, info);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		for(IPeak peak : peaksToDelete) {
			chromatogram.addPeak(peak);
		}
		updateChromatogramSelection();
		return Status.OK_STATUS;
	}
}
