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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.AlkanePatternDetector;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards.IRetentionIndexWizardElements;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ImportChromatogramRunnable implements IRunnableWithProgress {

	private IRetentionIndexWizardElements wizardElements;
	private IChromatogramMSD chromatogramMSD;

	public ImportChromatogramRunnable(IRetentionIndexWizardElements wizardElements) {
		this.wizardElements = wizardElements;
	}

	public IChromatogramMSD getChromatogramMSD() {

		return chromatogramMSD;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		String chromatogramPath = wizardElements.getSelectedChromatograms().get(0);
		String pathRetentionIndexFile = "";
		if(wizardElements.isUseExistingRetentionIndexFile()) {
			pathRetentionIndexFile = wizardElements.getPathRetentionIndexFile();
		}
		boolean useAlreadyDetectedPeaks = wizardElements.isUseAlreadyDetectedPeaks();
		AlkanePatternDetector alkanePatternDetector = new AlkanePatternDetector();
		chromatogramMSD = alkanePatternDetector.parseChromatogram(chromatogramPath, pathRetentionIndexFile, useAlreadyDetectedPeaks, monitor);
	}
}
