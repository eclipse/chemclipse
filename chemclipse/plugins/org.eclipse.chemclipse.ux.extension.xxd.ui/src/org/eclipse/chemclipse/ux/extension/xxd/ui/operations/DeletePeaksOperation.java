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
package org.eclipse.chemclipse.ux.extension.xxd.ui.operations;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("rawtypes")
public class DeletePeaksOperation extends AbstractOperation {

	private IChromatogramSelection<?, ?> chromatogramSelection;
	private Display display;
	private List<IPeak> peaksToDelete;

	public DeletePeaksOperation(Display display, IChromatogramSelection chromatogramSelection, List<IPeak> peaksToDelete) {

		super(ExtensionMessages.deletePeaks);
		this.display = display;
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

	@SuppressWarnings("unchecked")
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		chromatogram.removePeaks(peaksToDelete);
		update(ExtensionMessages.peaksDeleted);
		return Status.OK_STATUS;
	}

	private void update(String message) {

		chromatogramSelection.setSelectedPeak(null);
		UpdateNotifierUI.update(display, chromatogramSelection);
		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION, message);
	}

	@Override
	public String getLabel() {

		return ExtensionMessages.deletePeaks;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		return execute(monitor, info);
	}

	@Override
	@SuppressWarnings("unchecked")
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		for(IPeak peak : peaksToDelete) {
			chromatogram.addPeak(peak);
		}
		update(ExtensionMessages.peaksUndeleted);
		return Status.OK_STATUS;
	}
}
