/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core.PeakIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ISupplierCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.SupplierCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IAreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IIntegrationSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.core.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.FirstDerivativePeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.IFirstDerivativePeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.Threshold;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
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
			IProcessingInfo processingInfo = ChromatogramConverterMSD.convert(file, monitor);
			chromatogramMSD = processingInfo.getProcessingResult(IChromatogramMSD.class);
			/*
			 * Create a selection
			 */
			IChromatogramSelectionMSD chromatogramSelectionMSD = new ChromatogramSelectionMSD(chromatogramMSD);
			List<IPeakMSD> peaks;
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
				PeakDetectorMSD peakDetectorMSD = new PeakDetectorMSD();
				IFirstDerivativePeakDetectorMSDSettings peakDetectorSettings = new FirstDerivativePeakDetectorMSDSettings();
				peakDetectorSettings.setThreshold(Threshold.LOW);
				peakDetectorSettings.setIncludeBackground(false);
				peakDetectorSettings.setMinimumSignalToNoiseRatio(50.0f);
				peakDetectorSettings.setMovingAverageWindowSize(WindowSize.WIDTH_5);
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
				peakIntegratorSettings.getSelectedIons().clear();
				peakIntegrator.integrate(chromatogramSelectionMSD, monitor);
				peaks = extractPeaks(chromatogramMSD);
			}
			/*
			 * Run the retention index calculator.
			 */
			if(!"".equals(pathRetentionIndexFile)) {
				RetentionIndexCalculator retentionIndexCalculator = new RetentionIndexCalculator();
				ISupplierCalculatorSettings supplierCalculatorSettings = new SupplierCalculatorSettings();
				List<String> retentionIndexFiles = new ArrayList<String>();
				retentionIndexFiles.add(pathRetentionIndexFile);
				supplierCalculatorSettings.setRetentionIndexFiles(retentionIndexFiles);
				retentionIndexCalculator.apply(chromatogramSelectionMSD, supplierCalculatorSettings, monitor);
			}
			/*
			 * Peak identifier
			 * The settings are not needed by the CALRI peak identifier.
			 */
			PeakIdentifier peakIdentifier = new PeakIdentifier();
			IPeakIdentifierSettingsMSD peakIdentifierSettings = null;
			peakIdentifier.identify(peaks, peakIdentifierSettings, monitor);
		} catch(Exception e) {
			logger.warn(e);
		}
		return chromatogramMSD;
	}

	private List<IPeakMSD> extractPeaks(IChromatogramMSD chromatogramMSD) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
			peaks.add(peak);
		}
		return peaks;
	}
}
