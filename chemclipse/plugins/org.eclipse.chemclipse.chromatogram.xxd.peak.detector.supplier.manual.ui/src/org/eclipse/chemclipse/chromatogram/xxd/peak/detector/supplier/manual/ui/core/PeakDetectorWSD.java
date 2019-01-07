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

import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.AbstractPeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.settings.IPeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.settings.PeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.wizards.PeakDetectorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.wizards.WizardRunnable;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

public class PeakDetectorWSD extends AbstractPeakDetectorWSD {

	@Override
	public IProcessingInfo detect(IChromatogramSelectionWSD chromatogramSelection, IPeakDetectorSettingsWSD peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		if(peakDetectorSettings instanceof PeakDetectorSettingsWSD) {
			PeakDetectorSettingsWSD settings = (PeakDetectorSettingsWSD)peakDetectorSettings;
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
	public IProcessingInfo detect(IChromatogramSelectionWSD chromatogramSelection, IProgressMonitor monitor) {

		PeakDetectorSettingsWSD settings = PreferenceSupplier.getPeakDetectorSettingsWSD();
		return detect(chromatogramSelection, settings, monitor);
	}
}
