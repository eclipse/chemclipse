/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.chromatogram;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.implementation.DefaultNoiseCalculator;

public class ChromatogramConfiguration {

	public final static int DEFAULT_SELECTED_SEGMENT_WIDTH = 13;
	public final static INoiseCalculator DEFAULT_NOISE_CALCULATOR = new DefaultNoiseCalculator();
	private INoiseCalculator noiseCalculator;
	private int selectedSegmentWidth;

	public ChromatogramConfiguration() {
		boolean isPreferencesSupplierAvailable = PreferenceSupplier.isAvailable();
		if(isPreferencesSupplierAvailable) {
			initPreferenesFromPreferencesSupplier();
		} else {
			initPreferencesFromDefaults();
		}
	}

	protected void initPreferencesFromDefaults() {

		selectedSegmentWidth = DEFAULT_SELECTED_SEGMENT_WIDTH;
		noiseCalculator = DEFAULT_NOISE_CALCULATOR;
	}

	protected void initPreferenesFromPreferencesSupplier() {

		noiseCalculator = NoiseCalculator.getNoiseCalculator(PreferenceSupplier.getSelectedNoiseCalculatorId());
		selectedSegmentWidth = PreferenceSupplier.getSelectedSegmentWidth();
	}

	public int getSelectedSegmentWidth() {

		return selectedSegmentWidth;
	}

	public ChromatogramConfiguration setSelectedSegmentWidth(int selectedSegmentWidth) {

		this.selectedSegmentWidth = selectedSegmentWidth;
		return this;
	}

	public INoiseCalculator getNoiseCalculator() {

		return noiseCalculator;
	}

	public ChromatogramConfiguration setNoiseCalculator(INoiseCalculator noiseCalculator) {

		this.noiseCalculator = noiseCalculator;
		return this;
	}
}
