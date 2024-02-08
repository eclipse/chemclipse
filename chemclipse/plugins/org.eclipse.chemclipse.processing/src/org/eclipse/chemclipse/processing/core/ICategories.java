/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import org.eclipse.chemclipse.processing.l10n.Messages;

public interface ICategories {

	String BASELINE_DETECTOR = Messages.baselineDetector;
	String CHROMATOGRAM_CALCULATOR = Messages.chromatogramCalculator;
	String CHROMATOGRAM_CLASSIFIER = Messages.chromatogramClassifier;
	String CHROMATOGRAM_EXPORT = Messages.chromatogramExport;
	String CHROMATOGRAM_FILTER = Messages.chromatogramFilter;
	String CHROMATOGRAM_IDENTIFIER = Messages.chromatogramIdentifier;
	String CHROMATOGRAM_INTEGRATOR = Messages.chromatogramIntegrator;
	String CHROMATOGRAM_REPORTS = Messages.chromatogramReports;
	String COMBINED_CHROMATOGRAM_PEAK_INTEGRATOR = Messages.combinedChromatogramPeakIntegrator;
	String PEAK_DETECTOR = Messages.peakDetector;
	String PEAK_EXPORT = Messages.peakExport;
	String PEAK_FILTER = Messages.peakFilter;
	String PEAK_IDENTIFIER = Messages.peakIdentifier;
	String PEAK_INTEGRATOR = Messages.peakIntegrator;
	String PEAK_QUANTIFIER = Messages.peakQuantifier;
	String PROCESS = Messages.process;
	String SCAN_FILTER = Messages.scanFilter;
	String SCAN_IDENTIFIER = Messages.scanIdentifier;
	String PEAK_MASS_SPECTRUM_FILTER = Messages.peakMassSpectrumFilter;
	String SCAN_MASS_SPECTRUM_FILTER = Messages.scanMassSpectrumFilter;
	String SYSTEM = Messages.system;
	String USER_METHODS = Messages.userMethods;
	String USER_INTERFACE = Messages.userInterface;
	String FILTER = Messages.filter;
	String EXTERNAL_PROGRAMS = Messages.externalPrograms;
	String MASS_SPECTRUM = Messages.massSpectrum;
	String PROCEDURES = Messages.procedures;
	String IDENTIFIER = Messages.identifier;
	String EXPORT = Messages.export;
}