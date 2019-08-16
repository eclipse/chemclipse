/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class MassSpectrumIdentifierRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "NIST Mass Spectrum Identifier";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.msd.identifier.supplier.nist.massSpectrum";
	private IChromatogramSelectionMSD chromatogramSelection;

	public MassSpectrumIdentifierRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			/*
			 * Identify Peaks in actual chromatogram selection.
			 */
			MassSpectrumIdentifierSettings identifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
			IScanMSD massSpectrum = chromatogramSelection.getSelectedScan();
			IProcessingInfo processingInfo = MassSpectrumIdentifier.identify(massSpectrum, identifierSettings, IDENTIFIER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
		} finally {
			monitor.done();
		}
	}
}
