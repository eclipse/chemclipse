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
package org.eclipse.chemclipse.chromatogram.filter.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.chromatogram.filter.l10n.messages"; //$NON-NLS-1$
	//
	public static String chromatogramSelectionApplied;
	public static String chromatogramSelectionInvalid;
	public static String chromatogramSelectionReset;
	public static String ionRoundMethodDescription;
	public static String invalidChromatogram;
	public static String invalidFilterSettings;
	public static String ionRoundMethod;
	public static String noChromatogramFilterAvailable;
	public static String qcRetentionIndex;
	public static String qcRetentionIndexDescription;
	public static String selectRange;
	public static String startRetentionTimeOutsideRange;
	public static String stopRetentionTimeOutsideRange;
	public static String targetsTransferredSuccessfully;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {

	}
}
