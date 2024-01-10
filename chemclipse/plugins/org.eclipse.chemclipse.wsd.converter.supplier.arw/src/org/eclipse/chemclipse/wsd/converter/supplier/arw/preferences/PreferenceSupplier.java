/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.arw.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.wsd.converter.supplier.arw.Activator;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_NORMALIZE_SCANS = "normalizeScans";
	public static final boolean DEF_NORMALIZE_SCANS = true;
	public static final String P_NORMALIZATION_STEPS = "normalizationSteps";
	public static final int DEF_NORMALIZATION_STEPS = 1;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_NORMALIZE_SCANS, Boolean.toString(DEF_NORMALIZE_SCANS));
		putDefault(P_NORMALIZATION_STEPS, Integer.toString(DEF_NORMALIZATION_STEPS));
	}

	public static boolean isNormalizeScans() {

		return INSTANCE().getBoolean(P_NORMALIZE_SCANS, DEF_NORMALIZE_SCANS);
	}

	public static int getNormalizationSteps() {

		return INSTANCE().getInteger(P_NORMALIZATION_STEPS, DEF_NORMALIZATION_STEPS);
	}
}