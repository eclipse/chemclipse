/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 * Christoph Läubrich - add method getDefaultSegmentWidth that return the raw enum instead, null check
 * Matthias Mailänder - remove getDefaultSegmentWidth and add min/max segment size
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.Activator;
import org.eclipse.chemclipse.support.model.SegmentWidth;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_NOISE_CALCULATOR_ID = "noiseCalculatorId";
	public static final String DEF_NOISE_CALCULATOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson";
	//
	public static final String P_SEGMENT_WIDTH = "segmentWidth";
	public static final String DEF_SEGMENT_WIDTH = String.valueOf(9);
	public static final int MIN_SEGMENT_SIZE = 5;
	public static final int MAX_SEGMENT_SIZE = 19;
	//
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

		BundleContext context = Activator.getContext();
		if(context != null) {
			return context.getBundle().getSymbolicName();
		} else {
			return getClass().getName();
		}
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<>();
		defaultValues.put(P_NOISE_CALCULATOR_ID, DEF_NOISE_CALCULATOR_ID);
		defaultValues.put(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH);
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