/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics, Logging
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core.PeakIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.CalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IAreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IIntegrationSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.model.DetectorType;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class AlkanePatternDetectorMSD {

	private static final Logger logger = Logger.getLogger(AlkanePatternDetectorMSD.class);

	public IChromatogramMSD parseChromatogram(String chromatogramPath, String pathRetentionIndexFile, boolean useAlreadyDetectedPeaks, IProgressMonitor monitor) {

		IChromatogramMSD chromatogramMSD = null;
		try {
			/*
			 * Import the chromatogram.
			 */
			File file = new File(chromatogramPath);
			IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(file, monitor);
			chromatogramMSD = processingInfo.getProcessingResult();
			/*
			 * Create a selection
			 */
			IChromatogramSelectionMSD chromatogramSelectionMSD = new ChromatogramSelectionMSD(chromatogramMSD);
			List<IChromatogramPeakMSD> peaks;
			if(useAlreadyDetectedPeaks) {
				/*
				 * Use existing peaks.
				 */
				peaks = extractPeaks(chromatogramMSD);
			} else {
				/*
				 * Peak detector.
				 */
				chromatogramMSD.removeAllPeaks();
				PeakDetectorMSD<?, ?, ?> peakDetectorMSD = new PeakDetectorMSD<>();
				PeakDetectorSettingsMSD peakDetectorSettings = new PeakDetectorSettingsMSD();
				peakDetectorSettings.setThreshold(Threshold.LOW);
				peakDetectorSettings.setDetectorType(DetectorType.BB);
				peakDetectorSettings.setMinimumSignalToNoiseRatio(50.0f);
				peakDetectorSettings.setMovingAverageWindowSize(5);
				peakDetectorMSD.detect(chromatogramSelectionMSD, peakDetectorSettings, monitor);
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
				peakIntegratorSettings.getMarkedTraces().clear();
				peakIntegrator.integrate(chromatogramSelectionMSD, monitor);
				peaks = extractPeaks(chromatogramMSD);
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
				retentionIndexCalculator.calculateIndices(chromatogramSelectionMSD, calculatorSettings);
			}
			/*
			 * Peak identifier
			 * The settings are not needed by the CALRI peak identifier.
			 */
			if(peaks.stream().allMatch(p -> p.getTargets().isEmpty())) {
				PeakIdentifier peakIdentifier = new PeakIdentifier();
				IPeakIdentifierSettingsMSD peakIdentifierSettings = null;
				peakIdentifier.identify(peaks, peakIdentifierSettings, monitor);
			}
			chromatogramSelectionMSD.getChromatogram().setDirty(true);
		} catch(Exception e) {
			logger.error(e);
		}
		return chromatogramMSD;
	}

	private List<IChromatogramPeakMSD> extractPeaks(IChromatogramMSD chromatogramMSD) {

		List<IChromatogramPeakMSD> peaks = new ArrayList<>();
		for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
			peaks.add(peak);
		}
		return peaks;
	}
}