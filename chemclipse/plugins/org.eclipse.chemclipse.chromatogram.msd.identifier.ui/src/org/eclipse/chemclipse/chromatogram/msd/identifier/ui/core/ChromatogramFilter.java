/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.ui.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("rawtypes")
public class ChromatogramFilter extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			Shell shell = DisplayUtils.getShell();
			if(shell != null) {
				identifyScanMaxima(shell, chromatogramSelection, Display.getDefault(), monitor);
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
						identifyScanMaxima(shell, chromatogramSelection, Display.getDefault(), monitor);
						shell.close();
					}
				});
			}
			chromatogramSelection.getChromatogram().setDirty(true);
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The scan maxima have been identified successfully."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings filterSettings = new FilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void identifyScanMaxima(Shell shell, IChromatogramSelection chromatogramSelection, Display display, IProgressMonitor monitor) {

		ChromatogramFilterDialog dialog = new ChromatogramFilterDialog(shell);
		if(IDialogConstants.OK_ID == dialog.open()) {
			IMassSpectrumIdentifierSupplier massSpectrumIdentifierSupplier = dialog.getMassSpectrumIdentifierSupplier();
			if(massSpectrumIdentifierSupplier != null) {
				/*
				 * Extract the selection.
				 */
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				int startScan = chromatogramSelection.getStartScan();
				int stopScan = chromatogramSelection.getStopScan();
				//
				List<IScanMSD> massSpectra = new ArrayList<>();
				for(int i = startScan; i <= stopScan; i++) {
					IScan scan = chromatogram.getScan(i);
					if(scan instanceof IScanMSD && scan.getTargets().size() > 0) {
						massSpectra.add((IScanMSD)scan);
					}
				}
				/*
				 * Identification
				 * TODO - service settings could be displayed dynamically via JsonAnnotations in the dialog page.
				 */
				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						MassSpectrumIdentifier.identify(massSpectra, massSpectrumIdentifierSupplier.getId(), monitor);
						chromatogram.setDirty(true);
					}
				});
			}
		}
	}
}
