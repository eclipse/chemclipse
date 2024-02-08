/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - refactor menu categories
 *******************************************************************************/
package org.eclipse.chemclipse.processing.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.processing.l10n.messages"; //$NON-NLS-1$
	//
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
	public static String peakQuantifier;
	public static String process;
	public static String scanFilter;
	public static String scanIdentifier;
	public static String peakMassSpectrumFilter;
	public static String scanMassSpectrumFilter;
	public static String system;
	public static String userMethods;
	public static String userInterface;
	public static String filter;
	public static String externalPrograms;
	public static String massSpectrum;
	public static String procedures;
	public static String identifier;
	public static String export;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {

	}
}