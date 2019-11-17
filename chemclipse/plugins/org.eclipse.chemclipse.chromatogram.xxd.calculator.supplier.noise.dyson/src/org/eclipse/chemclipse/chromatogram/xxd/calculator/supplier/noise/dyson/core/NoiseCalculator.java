/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - refactor the code to use NoiseSegments
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.dyson.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.results.ChromatogramSegmentation;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.chemclipse.model.support.SegmentValidator;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.core.runtime.IProgressMonitor;

/*
 * S/N = intensity / noiseValue
 */
public class NoiseCalculator implements INoiseCalculator {

	private IChromatogram<?> chromatogram;
	private float noiseValue = Float.NaN;

	@Override
	public float getSignalToNoiseRatio(IChromatogram<?> chromatogram, float intensity) {

		if(chromatogram != this.chromatogram) {
			noiseValue = calculateNoiseFactorByDyson(chromatogram);
			this.chromatogram = chromatogram;
		}
		if(Float.isFinite(noiseValue) && noiseValue > 0) {
			return intensity / noiseValue;
		} else {
			return Float.NaN;
		}
	}

	/**
	 * Method described by "Norman Dyson".
	 * Chromatographic Integration Methods, Seconds edition
	 * 
	 * @param IChromatogram
	 */
	private float calculateNoiseFactorByDyson(IChromatogram<?> chromatogram) {

		if(chromatogram != null) {
			List<NoiseSegment> noiseSegments = getNoiseSegments(chromatogram, null);
			List<Double> deltaNoiseHeights = new ArrayList<Double>();
			for(NoiseSegment noiseSegment : noiseSegments) {
				if(noiseSegment.hasNoise()) {
					deltaNoiseHeights.add(noiseSegment.getNoiseFactor());
				}
			}
			/*
			 * Calculate the mean value of the standard deviations.
			 */
			double medianNoiseHeight = Calculations.getMedian(deltaNoiseHeights);
			if(medianNoiseHeight > 0) {
				return (float)medianNoiseHeight;
			} else {
				/*
				 * If there is no noise segment at all, take the min signal.
				 * It's not the best solution, but 0 is no option.
				 */
				return chromatogram.getMinSignal();
			}
		}
		return Float.NaN;
	}

	private static Double getDeltaNoiseHeight(IAnalysisSegment segment, SegmentValidator segmentValidator, ITotalScanSignals signals) {

		/*
		 * Check that there is at least a width of 1.
		 */
		int segmentWidth = segment.getWidth();
		if(segmentWidth < 1) {
			return null;
		}
		/*
		 * Get the total signal values.
		 */
		double[] values = new double[segmentWidth];
		int counter = 0;
		for(int scan = segment.getStartScan(); scan <= segment.getStopScan(); scan++) {
			ITotalScanSignal signal = signals.getTotalScanSignal(scan);
			values[counter] = signal.getTotalSignal();
			counter++;
		}
		/*
		 * Check if the segment is accepted.<br/> If yes, than calculate its
		 * delta signal height.<br/> If no, than throw an exception.
		 */
		double mean = Calculations.getMean(values);
		if(!segmentValidator.acceptSegment(values, mean)) {
			/*
			 * The calling method has now the chance to not add the value to its
			 * calculation.
			 */
			return null;
		} else {
			/*
			 * Calculate the difference between highest and lowest value.
			 */
			double highestValue = Calculations.getMax(values);
			double lowestValue = Calculations.getMin(values);
			return highestValue - lowestValue;
		}
	}

	@Override
	public List<NoiseSegment> getNoiseSegments(IChromatogram<?> chromatogram, IProgressMonitor monitor) {

		if(chromatogram != null) {
			ChromatogramSegmentation segmentation = chromatogram.getMeasurementResult(ChromatogramSegmentation.class);
			if(segmentation != null) {
				SegmentValidator validator = new SegmentValidator();
				ITotalScanSignals signals = new TotalScanSignals(chromatogram);
				List<NoiseSegment> result = new ArrayList<>();
				for(IAnalysisSegment segment : segmentation.getResult()) {
					/*
					 * TIC (use only the tic signal)
					 */
					Double deltaNoiseHeight = getDeltaNoiseHeight(segment, validator, signals);
					if(deltaNoiseHeight != null) {
						result.add(new NormanDysonNoiseSegment(segment, deltaNoiseHeight));
					}
				}
				return result;
			}
		}
		return Collections.emptyList();
	}

	private static final class NormanDysonNoiseSegment implements NoiseSegment {

		private final IAnalysisSegment baseSegment;
		private final double noiseFactor;

		public NormanDysonNoiseSegment(IAnalysisSegment baseSegment, double noiseFactor) {
			this.baseSegment = baseSegment;
			this.noiseFactor = noiseFactor;
		}

		@Override
		public int getStartScan() {

			return baseSegment.getStartScan();
		}

		@Override
		public int getStopScan() {

			return baseSegment.getStopScan();
		}

		@Override
		public double getNoiseFactor() {

			return noiseFactor;
		}

		@Override
		public Collection<? extends IAnalysisSegment> getChildSegments() {

			return Collections.singleton(baseSegment);
		}

		@Override
		public int getStartRetentionTime() {

			return baseSegment.getStartRetentionTime();
		}

		@Override
		public int getStopRetentionTime() {

			return baseSegment.getStopRetentionTime();
		}
	}
}
