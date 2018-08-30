/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.IVendorPeakIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.VendorPeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Dr. Philip Wenig
 */
public class PeakIdentifierRunnable implements IRunnableWithProgress {

	private static final String DESCRIPTION = "NIST Peak Identifier";
	private static final String IDENTIFIER_ID = "org.eclipse.chemclipse.msd.identifier.supplier.nist.peak";
	private IChromatogramSelectionMSD chromatogramSelection;

	public PeakIdentifierRunnable(IChromatogramSelectionMSD chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			/*
			 * Identify Peaks in actual chromatogram selection.
			 */
			IVendorPeakIdentifierSettings identifierSettings = new VendorPeakIdentifierSettings();
			identifierSettings.setNistApplication(PreferenceSupplier.getNistApplication());
			identifierSettings.setNumberOfTargets(PreferenceSupplier.getNumberOfTargets());
			identifierSettings.setStoreTargets(PreferenceSupplier.getStoreTargets());
			identifierSettings.setTimeoutInMinutes(PreferenceSupplier.getTimeoutInMinutes());
			IChromatogramPeakMSD peak = chromatogramSelection.getSelectedPeak();
			IProcessingInfo processingInfo = PeakIdentifierMSD.identify(peak, identifierSettings, IDENTIFIER_ID, monitor);
			/*
			 * Update the chromatogram selection.
			 */
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				((ChromatogramSelectionMSD)chromatogramSelection).update(false);
			}
		} finally {
			monitor.done();
		}
	}
}
