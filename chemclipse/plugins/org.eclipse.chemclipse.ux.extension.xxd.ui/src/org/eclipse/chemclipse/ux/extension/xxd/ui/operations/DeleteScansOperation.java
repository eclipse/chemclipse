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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.operations;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("rawtypes")
public class DeleteScansOperation extends AbstractOperation {

	private IChromatogramSelection<?, ?> chromatogramSelection;
	private Display display;
	private List<IScan> scansToClear;
	private List<IScan> backupScans;

	public DeleteScansOperation(Display display, IChromatogramSelection chromatogramSelection, List<IScan> scansToClear) {

		super("Delete Scans");
		this.display = display;
		this.chromatogramSelection = chromatogramSelection;
		this.scansToClear = scansToClear;
		backupScans = new ArrayList<IScan>();
	}

	@Override
	public boolean canExecute() {

		return !scansToClear.isEmpty() && chromatogramSelection != null;
	}

	@Override
	public boolean canRedo() {

		return !scansToClear.isEmpty() && chromatogramSelection != null;
	}

	@Override
	public boolean canUndo() {

		return !scansToClear.isEmpty();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		for(IScan scan : scansToClear) {
			IScan deepCopy = SerializationUtils.clone(scan);
			backupScans.add(deepCopy);
			scan.getTargets().clear();
		}
		chromatogramSelection.setSelectedIdentifiedScan(null);
		update();
		return Status.OK_STATUS;
	}

	private void update() {

		if(chromatogramSelection != null) {
			chromatogramSelection.setSelectedPeak(null);
			chromatogramSelection.update(true);
			//
			UpdateNotifierUI.update(display, chromatogramSelection);
		}
		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION, "Peaks were deleted.");
	}

	@Override
	public String getLabel() {

		return "Delete Scans";
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		for(IScan scan : backupScans) {
			chromatogramSelection.getChromatogram().getScan(scan.getScanNumber()).getTargets().addAll(scan.getTargets());
		}
		update();
		return Status.OK_STATUS;
	}
}
