/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.Activator;
import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_NOISE_CALCULATOR_ID = "noiseCalculatorId";
	public static final String DEF_NOISE_CALCULATOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson";
	//
	public static final String P_SEGMENT_WIDTH = "segmentWidth";
	public static final String DEF_SEGMENT_WIDTH = SegmentWidth.WIDTH_13.toString();
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	public static boolean isAvailable() {

		return Activator.getContext() != null;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_NOISE_CALCULATOR_ID, DEF_NOISE_CALCULATOR_ID);
		defaultValues.put(DEF_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns the selected noise calculator id or the default
	 * if none has been selected yet.
	 * 
	 * @return String
	 */
	public static String getSelectedNoiseCalculatorId() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_NOISE_CALCULATOR_ID, DEF_NOISE_CALCULATOR_ID);
	}

	/**
	 * Returns the selected segment width.
	 * 
	 * @return
	 */
	public static int getSelectedSegmentWidth() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		SegmentWidth segmentWidth = SegmentWidth.valueOf(preferences.get(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH));
		return segmentWidth.getWidth();
	}
}
