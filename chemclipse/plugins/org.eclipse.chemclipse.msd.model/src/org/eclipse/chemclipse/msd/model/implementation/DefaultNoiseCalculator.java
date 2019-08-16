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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.AbstractNoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.SegmentNotAcceptedException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.AnalysisSupport;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.IAnalysisSupport;
import org.eclipse.chemclipse.model.support.SegmentValidator;
import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

/*
 * S/N = intensity / noiseValue
 */
public class DefaultNoiseCalculator extends AbstractNoiseCalculator implements INoiseCalculator {

	private static final Logger logger = Logger.getLogger(DefaultNoiseCalculator.class);
	@SuppressWarnings("rawtypes")
	private IChromatogram chromatogram;
	private int segmentWidth;
	private boolean recalculate = false;
	private float noiseValue = 0.0f;

	@SuppressWarnings("rawtypes")
	@Override
	public void setChromatogram(IChromatogram chromatogram, int segmentWidth) {

		this.chromatogram = chromatogram;
		this.segmentWidth = segmentWidth;
		recalculate = true;
	}

	@Override
	public void recalculate() {

		recalculate = true;
	}

	@Override
	public float getSignalToNoiseRatio(float intensity) {

		/*
		 * Recalculate the noise value if neccessary.
		 */
		if(recalculate && chromatogram != null) {
			noiseValue = calculateNoiseFactorByDyson(chromatogram);
			recalculate = false;
		}
		/*
		 * Make the calculation.
		 */
		if(noiseValue != 0) {
			return intensity / noiseValue;
		} else {
			return 0;
		}
	}

	/**
	 * Calculates the noise factor.
	 * 
	 * @param IChromatogram
	 */
	@SuppressWarnings("rawtypes")
	private float calculateNoiseFactorByDyson(IChromatogram chromatogram) {

		float noiseValue = 0.0f;
		SegmentValidator segmentValidator = new SegmentValidator();
		//
		try {
			IAnalysisSupport analysisSupport;
			List<IAnalysisSegment> segments;
			ITotalScanSignals signals = new TotalScanSignals(chromatogram);
			/*
			 * User selected segment width.
			 */
			analysisSupport = new AnalysisSupport(chromatogram.getNumberOfScans(), segmentWidth);
			segments = analysisSupport.getAnalysisSegments();
			noiseValue = performNoiseFactorCalculation(segments, segmentValidator, signals);
			/*
			 * If the noise value is 0, try the lowest segment width.
			 */
			if(noiseValue == 0) {
				analysisSupport = new AnalysisSupport(chromatogram.getNumberOfScans(), SegmentWidth.WIDTH_5.getWidth());
				segments = analysisSupport.getAnalysisSegments();
				noiseValue = performNoiseFactorCalculation(segments, segmentValidator, signals);
			}
		} catch(AnalysisSupportException e) {
			noiseValue = 0.0f;
			logger.warn(e);
		} catch(ChromatogramIsNullException e) {
			noiseValue = 0.0f;
			logger.warn(e);
		}
		/*
		 * If there is no noise segment at all, take the min signal.
		 * It's not the best solution, but 0 is no option.
		 */
		if(noiseValue == 0) {
			noiseValue = chromatogram.getMinSignal();
		}
		return noiseValue;
	}

	/**
	 * Method described by "Norman Dyson".
	 * Chromatographic Integration Methods, Seconds edition
	 * 
	 * @param segments
	 * @return float
	 */
	private float performNoiseFactorCalculation(List<IAnalysisSegment> segments, SegmentValidator segmentValidator, ITotalScanSignals signals) {

		double noiseValue = 0.0d;
		List<Double> deltaNoiseHeights = new ArrayList<Double>();
		for(IAnalysisSegment segment : segments) {
			/*
			 * TIC (use only the tic signal)
			 */
			try {
				noiseValue = getDeltaNoiseHeight(segment, segmentValidator, signals);
				deltaNoiseHeights.add(noiseValue);
			} catch(SegmentNotAcceptedException e) {
			}
		}
		/*
		 * Convert the ArrayList to a double[] array.
		 */
		double[] values = new double[deltaNoiseHeights.size()];
		int counter = 0;
		for(double deltaNoiseHeight : deltaNoiseHeights) {
			values[counter++] = deltaNoiseHeight;
		}
		/*
		 * Calculate the mean value of the standard deviations.
		 */
		double medianNoiseHeight = Calculations.getMedian(values);
		return (float)medianNoiseHeight;
	}

	private double getDeltaNoiseHeight(IAnalysisSegment segment, SegmentValidator segmentValidator, ITotalScanSignals signals) throws SegmentNotAcceptedException {

		/*
		 * Check that there is at least a width of 1.
		 */
		int segmentWidth = segment.getSegmentWidth();
		if(segmentWidth < 1) {
			throw new SegmentNotAcceptedException("The segment width must be greater than 0.");
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
			throw new SegmentNotAcceptedException();
		} else {
			/*
			 * Calculate the difference between highest and lowest value.
			 */
			double highestValue = Calculations.getMax(values);
			double lowestValue = Calculations.getMin(values);
			return highestValue - lowestValue;
		}
	}
}
