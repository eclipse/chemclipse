/*******************************************************************************
 * Copyright (c) 2014, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 * Christoph Läubrich - add method getDefaultSegmentWidth that return the raw enum instead, null check
 * Matthias Mailänder - remove getDefaultSegmentWidth and add min/max segment size
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences;

import org.eclipse.chemclipse.support.model.SegmentWidth;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class PreferenceSupplier extends AbstractPreferenceSupplier {

	public static final String P_NOISE_CALCULATOR_ID = "noiseCalculatorId";
	public static final String DEF_NOISE_CALCULATOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson";

	public static final String P_SEGMENT_WIDTH = "segmentWidth";
	public static final String DEF_SEGMENT_WIDTH = "9";
	public static final int MIN_SEGMENT_SIZE = 5;
	public static final int MAX_SEGMENT_SIZE = 19;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		Bundle bundle = FrameworkUtil.getBundle(PreferenceSupplier.class);
		if(bundle != null) {
			return bundle.getSymbolicName();
		} else {
			return getClass().getName();
		}
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_NOISE_CALCULATOR_ID, DEF_NOISE_CALCULATOR_ID);
		putDefault(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH);
	}

	/**
	 * Returns the selected noise calculator id or the default
	 * if none has been selected yet.
	 *
	 * @return String
	 */
	public static String getSelectedNoiseCalculatorId() {

		return INSTANCE().get(P_NOISE_CALCULATOR_ID, DEF_NOISE_CALCULATOR_ID);
	}

	/**
	 * Returns the selected segment width.
	 *
	 * @return
	 */
	public static int getSelectedSegmentWidth() {

		return SegmentWidth.getAdjustedSetting(INSTANCE().get(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH));
	}
}