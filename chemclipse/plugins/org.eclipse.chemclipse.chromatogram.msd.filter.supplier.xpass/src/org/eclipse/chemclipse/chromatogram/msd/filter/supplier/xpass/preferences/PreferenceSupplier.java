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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.HighPassFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.LowPassFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.NominalizeFilterSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_NUMBER_LOWEST = "numberLowest";
	public static final int DEF_NUMBER_LOWEST = 5;
	public static final int MIN_NUMBER_LOWEST = 1;
	public static final int MAX_NUMBER_LOWEST = Integer.MAX_VALUE;
	//
	public static final String P_NUMBER_HIGHEST = "numberHighest";
	public static final int DEF_NUMBER_HIGHEST = 5;
	public static final int MIN_NUMBER_HIGHEST = 1;
	public static final int MAX_NUMBER_HIGHEST = Integer.MAX_VALUE;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_NUMBER_LOWEST, Integer.toString(DEF_NUMBER_LOWEST));
		putDefault(P_NUMBER_HIGHEST, Integer.toString(DEF_NUMBER_HIGHEST));
	}

	public static HighPassFilterSettings getHighPassFilterSettings() {

		HighPassFilterSettings settings = new HighPassFilterSettings();
		settings.setNumberHighest(getNumberHighest());
		return settings;
	}

	public static LowPassFilterSettings getLowPassFilterSettings() {

		LowPassFilterSettings settings = new LowPassFilterSettings();
		settings.setNumberLowest(getNumberLowest());
		return settings;
	}

	public static NominalizeFilterSettings getNominalizeFilterSettings() {

		NominalizeFilterSettings settings = new NominalizeFilterSettings();
		// No settinsg available yet.
		return settings;
	}

	public static int getNumberHighest() {

		return INSTANCE().getInteger(P_NUMBER_HIGHEST, DEF_NUMBER_HIGHEST);
	}

	public static int getNumberLowest() {

		return INSTANCE().getInteger(P_NUMBER_LOWEST, DEF_NUMBER_LOWEST);
	}
}