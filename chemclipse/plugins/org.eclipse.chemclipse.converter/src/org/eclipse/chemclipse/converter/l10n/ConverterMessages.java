/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.l10n;

import org.eclipse.osgi.util.NLS;

public class ConverterMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.converter.l10n.messages"; //$NON-NLS-1$
	//
	public static String exportChromatogram;
	public static String importChromatogram;
	public static String importChromatogramOverview;
	public static String scan;
	public static String importPeaks;
	public static String failedToWriteFile;
	public static String fileNotWritable;
	public static String failedToReadFile;
	public static String fileNotFound;
	public static String fileNotReadable;
	public static String emptyFile;
	public static String cancelledConversion;
	public static String importScan;
	public static String importBaseline;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, ConverterMessages.class);
	}

	private ConverterMessages() {

	}
}
