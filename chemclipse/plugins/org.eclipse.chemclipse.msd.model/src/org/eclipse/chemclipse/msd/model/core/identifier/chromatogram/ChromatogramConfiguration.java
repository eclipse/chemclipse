package org.eclipse.chemclipse.msd.model.core.identifier.chromatogram;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.noise.INoiseCalculator;

public class ChromatogramConfiguration {

	private INoiseCalculator noiseCalculator;
	private int selectedSegmentWidth;

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
