/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.l10n;

import org.eclipse.osgi.util.NLS;

public class ExtrasMessages extends NLS {

	public static String unitMicrogram;
	public static String unitMilligram;
	public static String unitGram;
	public static String unitKilogram;
	public static String unitMicroliter;
	public static String unitMilliliter;
	public static String unitLiter;
	public static String unitGramOrMilliliter;
	public static String unitMilligramPerKilogramOrLiter;
	public static String unitMilligramPerLiter;
	public static String unitGramPerHundredGramOrMilliliter;
	//
	static {
		NLS.initializeMessages("org.eclipse.chemclipse.numeric.l10n.messages", ExtrasMessages.class); //$NON-NLS-1$
	}

	private ExtrasMessages() {

	}
}
