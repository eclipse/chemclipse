/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkanePatternDetectorCSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkanePatternDetectorMSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards.IRetentionIndexWizardElements;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramImportRunnable implements IRunnableWithProgress {

	private IRetentionIndexWizardElements wizardElements;
	private IChromatogram chromatogram;

	public ChromatogramImportRunnable(IRetentionIndexWizardElements wizardElements) {
		this.wizardElements = wizardElements;
	}

	public IChromatogram getChromatogram() {

		return chromatogram;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * Calibration file
		 */
		String pathRetentionIndexFile = "";
		if(wizardElements.isUseExistingRetentionIndexFile()) {
			pathRetentionIndexFile = wizardElements.getPathRetentionIndexFile();
		}
		/*
		 * MSD/CSD
		 */
		boolean useAlreadyDetectedPeaks = wizardElements.isUseAlreadyDetectedPeaks();
		if(wizardElements.isUseMassSpectrometryData()) {
			String chromatogramPath = wizardElements.getChromatogramWizardElementsMSD().getSelectedChromatograms().get(0);
			AlkanePatternDetectorMSD alkanePatternDetector = new AlkanePatternDetectorMSD();
			chromatogram = alkanePatternDetector.parseChromatogram(chromatogramPath, pathRetentionIndexFile, useAlreadyDetectedPeaks, monitor);
		} else {
			String chromatogramPath = wizardElements.getChromatogramWizardElementsCSD().getSelectedChromatograms().get(0);
			AlkanePatternDetectorCSD alkanePatternDetector = new AlkanePatternDetectorCSD();
			chromatogram = alkanePatternDetector.parseChromatogram(chromatogramPath, pathRetentionIndexFile, useAlreadyDetectedPeaks, monitor);
		}
	}
}
