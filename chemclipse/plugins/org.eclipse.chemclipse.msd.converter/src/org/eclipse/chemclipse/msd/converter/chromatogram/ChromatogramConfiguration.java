package org.eclipse.chemclipse.msd.converter.chromatogram;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.noise.INoiseCalculator;

public class ChromatogramConfiguration {

	private INoiseCalculator noiseCalculator;

	public INoiseCalculator getNoiseCalculator() {

		return noiseCalculator;
	}

	public ChromatogramConfiguration setNoiseCalculator(INoiseCalculator noiseCalculator) {

		this.noiseCalculator = noiseCalculator;
		return this;
	}
}
