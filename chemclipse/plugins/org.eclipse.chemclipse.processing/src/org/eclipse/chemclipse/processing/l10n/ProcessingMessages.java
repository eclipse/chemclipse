/*******************************************************************************
 * Copyright (c) 2023, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - refactor menu categories
 *******************************************************************************/
package org.eclipse.chemclipse.processing.l10n;

import org.eclipse.osgi.util.NLS;

public class ProcessingMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.processing.l10n.messages"; //$NON-NLS-1$

	public static String baselineDetector;
	public static String chromatogramCalculator;
	public static String chromatogramClassifier;
	public static String chromatogramExport;
	public static String chromatogramFilter;
	public static String chromatogramIdentifier;
	public static String chromatogramIntegrator;
	public static String chromatogramReports;
	public static String combinedChromatogramPeakIntegrator;
	public static String peakDetector;
	public static String peakExport;
	public static String peakFilter;
	public static String peakIdentifier;
	public static String peakIntegrator;
	public static String peakPicker;
	public static String peakQuantifier;
	public static String process;
	public static String scanFilter;
	public static String scanIdentifier;
	public static String peakMassSpectrumFilter;
	public static String scanMassSpectrumFilter;
	public static String massSpectrumExport;
	public static String massSpectrumFilter;
	public static String massSpectrumIdentifier;
	public static String massSpectrumReports;
	public static String system;
	public static String userMethods;
	public static String userInterface;
	public static String filter;
	public static String massSpectrum;
	public static String procedures;
	public static String identifier;
	public static String export;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ProcessingMessages.class);
	}

	private ProcessingMessages() {

	}
}
