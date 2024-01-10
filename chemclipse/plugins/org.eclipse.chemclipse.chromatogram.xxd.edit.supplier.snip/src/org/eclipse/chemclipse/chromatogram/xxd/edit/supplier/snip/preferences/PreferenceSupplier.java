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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings.BaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.settings.PeakFilterSettings;
import org.eclipse.chemclipse.support.model.WindowSize;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_ITERATIONS = "iterations";
	public static final int DEF_ITERATIONS = 100;
	public static final int MIN_ITERATIONS = 5;
	public static final int MAX_ITERATIONS = 2000;
	public static final String P_WINDOW_SIZE = "windowSize";
	public static final int DEF_WINDOW_SIZE = 7;
	public static final int MIN_WINDOW_SIZE = 0;
	public static final int MAX_WINDOW_SIZE = 45;
	public static final String P_MAGNIFICATION_FACTOR = "magnificationFactor";
	public static final double DEF_MAGNIFICATION_FACTOR = 1;
	public static final double MIN_MAGNIFICATION_FACTOR = 0.01d;
	public static final double MAX_MAGNIFICATION_FACTOR = 5.0d;
	public static final String P_TRANSITIONS = "transitions";
	public static final int DEF_TRANSITIONS = 1;
	public static final int MIN_TRANSITIONS = 1;
	public static final int MAX_TRANSITIONS = 100;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_ITERATIONS, Integer.toString(DEF_ITERATIONS));
		putDefault(P_MAGNIFICATION_FACTOR, Double.toString(DEF_MAGNIFICATION_FACTOR));
		putDefault(P_TRANSITIONS, Integer.toString(DEF_TRANSITIONS));
		putDefault(P_WINDOW_SIZE, Integer.toString(DEF_WINDOW_SIZE));
	}

	public static BaselineDetectorSettings getBaselineDetectorSettings() {

		BaselineDetectorSettings settings = new BaselineDetectorSettings();
		settings.setIterations(getIterations());
		settings.setWindowSize(getWindowSize());
		return settings;
	}

	public static PeakFilterSettings getPeakFilterSettings() {

		PeakFilterSettings settings = new PeakFilterSettings();
		settings.setIterations(getIterations());
		settings.setMagnificationFactor(getMagnificationFactor());
		settings.setTransitions(getTransitions());
		return settings;
	}

	public static MassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		MassSpectrumFilterSettings settings = new MassSpectrumFilterSettings();
		settings.setIterations(getIterations());
		settings.setMagnificationFactor(getMagnificationFactor());
		settings.setTransitions(getTransitions());
		return settings;
	}

	public static int getIterations() {

		return INSTANCE().getInteger(P_ITERATIONS, DEF_ITERATIONS);
	}

	public static int getWindowSize() {

		return WindowSize.getAdjustedSetting(INSTANCE().get(P_WINDOW_SIZE, Integer.toString(DEF_WINDOW_SIZE)));
	}

	public static double getMagnificationFactor() {

		return INSTANCE().getDouble(P_MAGNIFICATION_FACTOR, DEF_MAGNIFICATION_FACTOR);
	}

	public static int getTransitions() {

		return INSTANCE().getInteger(P_TRANSITIONS, DEF_TRANSITIONS);
	}
}