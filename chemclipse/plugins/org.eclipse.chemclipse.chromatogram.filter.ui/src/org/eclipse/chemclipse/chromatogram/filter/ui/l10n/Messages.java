/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.ui.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.chromatogram.filter.ui.l10n.messages"; //$NON-NLS-1$
	//
	public static String chromotogramSelectionFilter;
	public static String countIncludingZero;
	public static String deltaRetentionTimeMinutes;
	public static String detectMinima;
	public static String filter;
	public static String ionRoundMethod;
	public static String matchFactor;
	public static String peakTargetsReferenceChromatogram;
	public static String qcUseRententionIndexProcessor;
	public static String scanMaximaDetectionSuccessful;
	public static String scanMaximaDetectors;
	public static String scanMaximaDetectorUI;
	public static String scanTargetsToPeakTransfer;
	public static String scanTargetsToReferenceChromatogramsTransfer;
	public static String selectScanMaximaDetector;
	public static String settingsNotOfType;
	public static String startRetentionTimeMinutes;
	public static String stopRetentionTimeMinutes;
	public static String targetName;
	public static String transferClosestScan;
	public static String useBestTargetOnly;
	public static String transformMZ;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {

	}
}
