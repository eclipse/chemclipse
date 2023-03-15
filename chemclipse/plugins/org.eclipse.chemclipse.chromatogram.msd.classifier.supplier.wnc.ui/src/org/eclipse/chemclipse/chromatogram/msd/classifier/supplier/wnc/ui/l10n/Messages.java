/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * matthias - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.l10n.messages"; //$NON-NLS-1$
	//
	public static String addIonToAnalyze;
	public static String ion;
	public static String ionValueMustBeInteger;
	public static String name;
	public static String nameMustBeSpecified;
	public static String nameWithoutDisallowedCharacters;
	public static String nv;
	public static String percentageMaxIntensity;
	public static String percentageSumIntensity;
	public static String wncClassifierSettings;
	public static String wncIon;
	//
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {

	}
}
