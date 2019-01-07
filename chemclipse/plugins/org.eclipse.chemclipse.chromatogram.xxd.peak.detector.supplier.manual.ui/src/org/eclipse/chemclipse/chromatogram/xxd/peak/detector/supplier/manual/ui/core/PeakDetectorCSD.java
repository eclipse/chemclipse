/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.AbstractPeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.settings.PeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.wizards.PeakDetectorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.wizards.WizardRunnable;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

public class PeakDetectorCSD extends AbstractPeakDetectorCSD {

	@Override
	public IProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IPeakDetectorSettingsCSD peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(peakDetectorSettings instanceof PeakDetectorSettingsCSD) {
			PeakDetectorSettingsCSD settings = (PeakDetectorSettingsCSD)peakDetectorSettings;
			//
			List<IPeak> peaks = new ArrayList<>();
			Shell shell = DisplayUtils.getShell();
			if(shell != null) {
				PeakDetectorSupport peakDetectorSupport = new PeakDetectorSupport();
				peakDetectorSupport.addPeaks(peaks, shell, processingInfo);
			} else {
				WizardRunnable wizardRunnable = new WizardRunnable(processingInfo, peaks);
				DisplayUtils.getDisplay().syncExec(wizardRunnable);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo detect(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor) {

		PeakDetectorSettingsCSD settings = PreferenceSupplier.getPeakDetectorSettingsCSD();
		return detect(chromatogramSelection, settings, monitor);
	}
}
