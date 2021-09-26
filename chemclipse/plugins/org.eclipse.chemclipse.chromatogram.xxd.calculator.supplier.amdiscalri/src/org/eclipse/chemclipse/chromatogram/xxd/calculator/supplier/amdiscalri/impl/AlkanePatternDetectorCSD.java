/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics, Loggging
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.CalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IAreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IIntegrationSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core.PeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsCSD;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class AlkanePatternDetectorCSD {

	private static final Logger logger = Logger.getLogger(AlkanePatternDetectorCSD.class);

	public IChromatogramCSD parseChromatogram(String chromatogramPath, String pathRetentionIndexFile, boolean useAlreadyDetectedPeaks, IProgressMonitor monitor) {

		IChromatogramCSD chromatogramCSD = null;
		try {
			/*
			 * Import the chromatogram.
			 */
			File file = new File(chromatogramPath);
			IProcessingInfo<IChromatogramCSD> processingInfo = ChromatogramConverterCSD.getInstance().convert(file, monitor);
			chromatogramCSD = processingInfo.getProcessingResult();
			/*
			 * Create a selection
			 */
			IChromatogramSelectionCSD chromatogramSelectionCSD = new ChromatogramSelectionCSD(chromatogramCSD);
			if(!useAlreadyDetectedPeaks) {
				/*
				 * Peak detector.
				 */
				PeakDetectorCSD peakDetectorCSD = new PeakDetectorCSD();
				PeakDetectorSettingsCSD peakDetectorSettings = new PeakDetectorSettingsCSD();
				peakDetectorSettings.setThreshold(Threshold.LOW);
				peakDetectorSettings.setIncludeBackground(false);
				peakDetectorSettings.setMinimumSignalToNoiseRatio(50.0f);
				peakDetectorSettings.setMovingAverageWindowSize(5);
				peakDetectorCSD.detect(chromatogramSelectionCSD, peakDetectorSettings, monitor);
				/*
				 * Peak integrator
				 */
				PeakIntegrator peakIntegrator = new PeakIntegrator();
				PeakIntegrationSettings peakIntegratorSettings = new PeakIntegrationSettings();
				peakIntegratorSettings.setIncludeBackground(false);
				IAreaSupport areaSupport = new AreaSupport();
				areaSupport.setMinimumArea(0.0d);
				IIntegrationSupport integrationSupport = peakIntegratorSettings.getIntegrationSupport();
				integrationSupport.setMinimumPeakWidth(0);
				integrationSupport.setMinimumSignalToNoiseRatio(0);
				peakIntegratorSettings.getSelectedIons().clear();
				peakIntegrator.integrate(chromatogramSelectionCSD, monitor);
			}
			/*
			 * Run the retention index calculator.
			 */
			if(!"".equals(pathRetentionIndexFile)) {
				RetentionIndexCalculator retentionIndexCalculator = new RetentionIndexCalculator();
				CalculatorSettings calculatorSettings = new CalculatorSettings();
				List<String> retentionIndexFiles = new ArrayList<>();
				retentionIndexFiles.add(pathRetentionIndexFile);
				calculatorSettings.setRetentionIndexFiles(retentionIndexFiles);
				retentionIndexCalculator.calculateIndices(chromatogramSelectionCSD, calculatorSettings, monitor);
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return chromatogramCSD;
	}
}
