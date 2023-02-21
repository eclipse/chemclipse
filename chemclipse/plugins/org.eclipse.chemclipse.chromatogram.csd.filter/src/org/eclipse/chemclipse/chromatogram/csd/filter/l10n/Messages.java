/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.chromatogram.csd.filter.l10n.messages"; //$NON-NLS-1$
	//
	public static String chromatogramFilter;
	public static String invalidChromatogram;
	public static String invalidChromatogramSelection;
	public static String invalidFilterSettings;
	public static String invalidPeak;
	public static String invalidPeakList;
	public static String noChromatogramFilterAvailable;
	public static String noPeakFilterAvailable;
	public static String onlyCSDchromatogramSupported;
	public static String peakFilter;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {

	}
}
