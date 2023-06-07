/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.ui.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.core.ChromatogramSubtractor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.ChromatogramEditorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ChromatogramFilter extends AbstractChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			Shell shell = DisplayUtils.getShell();
			if(shell != null) {
				subtractChromatogram(shell, chromatogramSelection);
			} else {
				DisplayUtils.getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {

						/*
						 * Create a new shell and set
						 * the size to 0 cause only the wizard
						 * will be shown.
						 */
						Shell shell = new Shell();
						shell.setSize(0, 0);
						shell.open();
						//
						subtractChromatogram(shell, chromatogramSelection);
						shell.close();
					}
				});
			}
			chromatogramSelection.getChromatogram().setDirty(true);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram was successfully subtracted."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		ChromatogramFilterSettings filterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void subtractChromatogram(Shell shell, IChromatogramSelection<?, ?> chromatogramSelectionMaster) {

		IChromatogram<?> chromatogramMaster = chromatogramSelectionMaster.getChromatogram();
		ChromatogramEditorDialog dialog = new ChromatogramEditorDialog(shell, chromatogramMaster);
		//
		if(IDialogConstants.OK_ID == dialog.open()) {
			IChromatogramSelection<?, ?> chromatogramSelectionSubtract = dialog.getChromatogramSelection();
			if(chromatogramSelectionSubtract != null) {
				/*
				 * Check that both chromatograms are not the same
				 */
				IChromatogram<?> chromatogramSubtract = chromatogramSelectionSubtract.getChromatogram();
				if(chromatogramMaster != chromatogramSubtract) {
					int startRetentionTime = chromatogramSelectionMaster.getStartRetentionTime();
					int stopRetentionTime = chromatogramSelectionMaster.getStopRetentionTime();
					ChromatogramSubtractor chromatogramSubtractor = new ChromatogramSubtractor();
					chromatogramSubtractor.perform(chromatogramMaster, chromatogramSubtract, startRetentionTime, stopRetentionTime);
				} else {
					MessageDialog.openWarning(shell, "Subtract Chromatogram", "The following chromatogram has been selected for subtraction.");
				}
			}
		}
	}
}
