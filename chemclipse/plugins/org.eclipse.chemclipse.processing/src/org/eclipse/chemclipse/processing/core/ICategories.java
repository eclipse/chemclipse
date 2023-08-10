/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

	String BASELINE_DETECTOR = "Baseline Detector";
	String CHROMATOGRAM_CALCULATOR = "Chromatogram Calculator";
	String CHROMATOGRAM_CLASSIFIER = Messages.chromatogramClassifier;
	String CHROMATOGRAM_EXPORT = "Chromatogram Export";
	String CHROMATOGRAM_FILTER = Messages.chromatogramFilter;
	String CHROMATOGRAM_IDENTIFIER = "Chromatogram Identifier";
	String CHROMATOGRAM_INTEGRATOR = "Chromatogram Integrator";
	String CHROMATOGRAM_REPORTS = "Chromatogram Reports";
	String PEAK_DETECTOR = Messages.peakDetector;
	String PEAK_EXPORT = "Peak Export";
	String PEAK_FILTER = Messages.peakFilter;
	String PEAK_IDENTIFIER = Messages.peakIdentifier;
	String PEAK_INTEGRATOR = "Peak Integrator";
	String PEAK_QUANTIFIER = "Peak Quantifier";
	String PROCESS = "Process";
	String SCAN_FILTER = "Scan Filter";
	String SCAN_IDENTIFIER = "Scan Identifier";
	String PEAK_MASSSPECTRUM_FILTER = "Peak Massspectrum Filter";
	String SCAN_MASSSPECTRUM_FILTER = "Scan Massspectrum Filter";
	String SYSTEM = "System";
	String USER_METHODS = "User Methods";
	String USER_INTERFACE = "User Interface";
	String FILTER = "Filter";
	String EXTERNAL_PROGRAMS = "External Programs";
	String MASS_SPECTRUM = "Mass Spectrum";
	String PROCEDURES = "Procedures";
	String IDENTIFIER = "Identifier";
	String EXPORT = "Export";
}