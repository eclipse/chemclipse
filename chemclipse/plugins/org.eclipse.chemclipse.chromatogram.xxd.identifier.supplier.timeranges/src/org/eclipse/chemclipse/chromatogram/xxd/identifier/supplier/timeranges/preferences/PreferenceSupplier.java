/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.timeranges.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.timeranges.Activator;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final float MIN_FACTOR = 0.0f;
	public static final float MAX_FACTOR = 100.0f;
	//
	public static final String P_LIMIT_MATCH_FACTOR_UNKNOWN = "limitMatchFactor";
	public static final float DEF_LIMIT_MATCH_FACTOR_UNKOWN = 80.0f;
	public static final String P_MATCH_QUALITY_UNKNOWN = "matchQuality";
	public static final float DEF_MATCH_QUALITY_UNKNOWN = 80.0f;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_LIMIT_MATCH_FACTOR_UNKNOWN, Float.toString(DEF_LIMIT_MATCH_FACTOR_UNKOWN));
		putDefault(P_MATCH_QUALITY_UNKNOWN, Float.toString(DEF_MATCH_QUALITY_UNKNOWN));
	}
}