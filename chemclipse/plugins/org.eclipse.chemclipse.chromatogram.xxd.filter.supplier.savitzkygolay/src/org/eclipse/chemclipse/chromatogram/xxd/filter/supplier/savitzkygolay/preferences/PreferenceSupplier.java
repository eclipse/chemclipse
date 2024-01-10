/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Lorenz Gerber - adjustments for per ion calculation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_DERIVATIVE = 0;
	public static final int MAX_DERIVATIVE = 5;
	public static final String P_DERIVATIVE = "derivative";
	public static final int DEF_DERIVATIVE = MIN_DERIVATIVE;
	public static final int STEP_DERIVATIVE = 1;
	//
	public static final int MIN_ORDER = 2;
	public static final int MAX_ORDER = 5;
	public static final String P_ORDER = "order";
	public static final int DEF_ORDER = MIN_ORDER;
	public static final int STEP_ORDER = 1;
	//
	public static final int MIN_WIDTH = 5;
	public static final int MAX_WIDTH = 51;
	public static final String P_WIDTH = "width";
	public static final int DEF_WIDTH = 7;
	public static final int STEP_WIDTH = 1;
	//
	public static final String P_PER_ION_CALCULATION = "perIonCalculation";
	public static final boolean DEF_PER_ION_CALCULATION = true;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_DERIVATIVE, Integer.toString(DEF_DERIVATIVE));
		putDefault(P_ORDER, Integer.toString(DEF_ORDER));
		putDefault(P_WIDTH, Integer.toString(DEF_WIDTH));
		putDefault(P_PER_ION_CALCULATION, Boolean.toString(DEF_PER_ION_CALCULATION));
	}

	public static ChromatogramFilterSettingsCSD getFilterSettings() {

		ChromatogramFilterSettingsCSD settings = new ChromatogramFilterSettingsCSD();
		settings.setOrder(INSTANCE().getInteger(P_ORDER, DEF_ORDER));
		settings.setWidth(INSTANCE().getInteger(P_WIDTH, DEF_WIDTH));
		return settings;
	}

	public static ChromatogramFilterSettingsMSD getFilterSettingsMSD() {

		ChromatogramFilterSettingsMSD settings = new ChromatogramFilterSettingsMSD();
		settings.setOrder(INSTANCE().getInteger(P_ORDER, DEF_ORDER));
		settings.setWidth(INSTANCE().getInteger(P_WIDTH, DEF_WIDTH));
		settings.setPerIonCalculation(INSTANCE().getBoolean(P_PER_ION_CALCULATION, DEF_PER_ION_CALCULATION));
		return settings;
	}

	public static ChromatogramFilterSettingsWSD getFilterSettingsWSD() {

		ChromatogramFilterSettingsWSD settings = new ChromatogramFilterSettingsWSD();
		settings.setOrder(INSTANCE().getInteger(P_ORDER, DEF_ORDER));
		settings.setWidth(INSTANCE().getInteger(P_WIDTH, DEF_WIDTH));
		return settings;
	}

	public static MassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		MassSpectrumFilterSettings settings = new MassSpectrumFilterSettings();
		settings.setOrder(getOrder());
		settings.setWidth(getWidth());
		return settings;
	}

	public static int getDerivative() {

		return INSTANCE().getInteger(P_DERIVATIVE, DEF_DERIVATIVE);
	}

	public static int getOrder() {

		return INSTANCE().getInteger(P_ORDER, DEF_ORDER);
	}

	public static int getWidth() {

		return INSTANCE().getInteger(P_WIDTH, DEF_WIDTH);
	}

	public static boolean getPerIonCalculation() {

		return INSTANCE().getBoolean(P_PER_ION_CALCULATION, DEF_PER_ION_CALCULATION);
	}
}