/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.AbstractPeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.settings.PeakDatabaseSettings;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards.AddPeaksWizardESTD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class AddPeaksESTD extends AbstractPeakQuantifier implements IPeakQuantifier {

	private static final String DESCRIPTION = "Peaks to DB (ESTD)";

	private class WizardRunnable implements Runnable {

		private IProcessingInfo<?> processingInfo;
		private List<IPeak> peaks;

		public WizardRunnable(IProcessingInfo<?> processingInfo, List<IPeak> peaks) {

			this.processingInfo = processingInfo;
			this.peaks = peaks;
		}

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
			addPeaks(peaks, shell, processingInfo);
			shell.close();
		}
	}

	@Override
	public IProcessingInfo<?> quantify(List<IPeak> peaks, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(peakQuantifierSettings instanceof PeakDatabaseSettings) {
			Shell shell = DisplayUtils.getShell();
			if(shell != null) {
				/*
				 * Normal
				 */
				addPeaks(peaks, shell, processingInfo);
			} else {
				/*
				 * Called from a modal dialog, e.g. from
				 * a popup menu.
				 */
				WizardRunnable wizardRunnable = new WizardRunnable(processingInfo, peaks);
				DisplayUtils.getDisplay().syncExec(wizardRunnable);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> quantify(IPeak peak, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		peaks.add(peak);
		return quantify(peaks, peakQuantifierSettings, monitor);
	}

	@Override
	public IProcessingInfo<?> quantify(IPeak peak, IProgressMonitor monitor) {

		List<IPeak> peaks = new ArrayList<IPeak>();
		peaks.add(peak);
		PeakDatabaseSettings peakDatabaseSettings = PreferenceSupplier.getPeakDatabaseSettings();
		return quantify(peaks, peakDatabaseSettings, monitor);
	}

	@Override
	public IProcessingInfo<?> quantify(List<IPeak> peaks, IProgressMonitor monitor) {

		PeakDatabaseSettings peakDatabaseSettings = PreferenceSupplier.getPeakDatabaseSettings();
		return quantify(peaks, peakDatabaseSettings, monitor);
	}

	private void addPeaks(List<IPeak> peaks, Shell shell, IProcessingInfo<?> processingInfo) {

		AddPeaksWizardESTD wizard = new AddPeaksWizardESTD(peaks);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		if(dialog.open() == Dialog.OK) {
			StatusLineLogger.setInfo(InfoType.MESSAGE, "Done: The peaks have been added to the quantitation table.");
			processingInfo.addErrorMessage(DESCRIPTION, "Successfully added the peaks to the database.");
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "Something went wrong to add the peaks.");
		}
	}
}