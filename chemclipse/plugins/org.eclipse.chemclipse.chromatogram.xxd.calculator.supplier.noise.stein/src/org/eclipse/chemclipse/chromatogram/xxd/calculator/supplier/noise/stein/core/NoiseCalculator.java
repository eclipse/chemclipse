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
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.stein.core;

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
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

/*
 * S/N = Math.sqrt(intensity) * noiseFactor
 */
public class NoiseCalculator extends AbstractNoiseCalculator implements INoiseCalculator {

	private static final Logger logger = Logger.getLogger(NoiseCalculator.class);
	private IChromatogram<?> chromatogram;
	private int segmentWidth;
	private float noiseFactor = 0.0f;

	@Override
	public float getSignalToNoiseRatio(IChromatogram<?> chromatogram, int segmentWidth, float intensity) {

		if(this.chromatogram != chromatogram || this.segmentWidth != segmentWidth) {
			noiseFactor = calculateNoiseFactorByStein(chromatogram, segmentWidth);
			this.chromatogram = chromatogram;
			this.segmentWidth = segmentWidth;
		}
		if(noiseFactor > 0) {
			return (float)(Math.sqrt(intensity) * noiseFactor);
		} else {
			return 0;
		}
	}

	/**
	 * Calculates the noise factor.
	 * 
	 * @param IChromatogram
	 */
	private static float calculateNoiseFactorByStein(IChromatogram<?> chromatogram, int segmentWidth) {

		float noiseValue = 0.0f;
		if(chromatogram != null) {
			try {
				IAnalysisSupport analysisSupport = new AnalysisSupport(chromatogram.getNumberOfScans(), segmentWidth);
				List<IAnalysisSegment> segments = analysisSupport.getAnalysisSegments();
				//
				if(chromatogram instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
					IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogramMSD);
					IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals();
					noiseValue = performNoiseFactorCalculationByStein(segments, signals);
				} else {
					ITotalScanSignals signals = new TotalScanSignals(chromatogram);
					noiseValue = performNoiseFactorCalculationByStein(segments, signals);
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
		}
		return noiseValue;
	}

	/**
	 * See S.E. Stein:
	 * "An Integrated Method for Spectrum Extraction and Compound Identification from Gas Chromatography/Mass Spectrometry Data"
	 * .
	 * 
	 * @param segments
	 * @return float
	 */
	private static float performNoiseFactorCalculationByStein(List<IAnalysisSegment> segments, Object signals) {

		List<Double> noiseFactors = getNoiseFactors(segments, signals);
		double medianNoiseFactor = Calculations.getMedian(noiseFactors);
		return (float)medianNoiseFactor;
	}

	/**
	 * Extracts the noise factors.
	 * 
	 * @param segments
	 * @param signals
	 * @return List
	 */
	private static List<Double> getNoiseFactors(List<IAnalysisSegment> segments, Object signals) {

		List<Double> noiseFactors = new ArrayList<Double>();
		int startIon;
		int stopIon;
		double noiseValue = 0;
		SegmentValidator segmentValidator = new SegmentValidator();
		//
		if(signals instanceof IExtractedIonSignals) {
			/*
			 * XIC + TIC (MSD)
			 */
			IExtractedIonSignals extractedSignals = (IExtractedIonSignals)signals;
			startIon = extractedSignals.getStartIon();
			stopIon = extractedSignals.getStopIon();
			for(IAnalysisSegment segment : segments) {
				/*
				 * XIC (use the ion range, e.g. ion 1 to ion 600)
				 */
				for(int ion = startIon; ion <= stopIon; ion++) {
					try {
						noiseValue = getMedianFromMeanByStein(segment, segmentValidator, ion, extractedSignals);
						noiseFactors.add(noiseValue);
					} catch(SegmentNotAcceptedException e) {
					}
				}
				/*
				 * TIC (use only the tic signal)
				 */
				try {
					noiseValue = getMedianFromMeanByStein(segment, segmentValidator, (int)IIon.TIC_ION, extractedSignals);
					noiseFactors.add(noiseValue);
				} catch(SegmentNotAcceptedException e) {
				}
			}
		} else if(signals instanceof ITotalScanSignals) {
			/*
			 * TIC (FID ...)
			 */
			ITotalScanSignals totalSignals = (ITotalScanSignals)signals;
			for(IAnalysisSegment segment : segments) {
				/*
				 * TIC (use only the tic signal)
				 */
				try {
					noiseValue = getMedianFromMeanByStein(segment, segmentValidator, totalSignals);
					noiseFactors.add(noiseValue);
				} catch(SegmentNotAcceptedException e) {
				}
			}
		}
		//
		return noiseFactors;
	}

	/**
	 * Calculates the median from mean using the extracted ion signals.
	 * 
	 * @param segment
	 * @param ion
	 * @param signals
	 * @return double
	 * @throws SegmentNotAcceptedException
	 */
	private static double getMedianFromMeanByStein(IAnalysisSegment segment, SegmentValidator segmentValidator, int ion, IExtractedIonSignals signals) throws SegmentNotAcceptedException {

		IExtractedIonSignal signal;
		double[] values = new double[segment.getSegmentWidth()];
		int counter = 0;
		for(int scan = segment.getStartScan(); scan <= segment.getStopScan(); scan++) {
			try {
				signal = signals.getExtractedIonSignal(scan);
				/*
				 * If the ion represents the TIC than use the total signal,
				 * otherwise get the abundance of the given ion.
				 */
				if(ion == IIon.TIC_ION) {
					values[counter] = signal.getTotalSignal();
				} else {
					values[counter] = signal.getAbundance(ion);
				}
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
			} finally {
				/*
				 * Increment counters position.
				 */
				counter++;
			}
		}
		/*
		 * Calculate noise factor if the segment is accepted.
		 */
		return calculateNoiseFactorValue(segmentValidator, values);
	}

	/**
	 * Calculates the median from mean using the total scan signals.
	 * 
	 * @param segment
	 * @param signals
	 * @return double
	 * @throws SegmentNotAcceptedException
	 */
	private static double getMedianFromMeanByStein(IAnalysisSegment segment, SegmentValidator segmentValidator, ITotalScanSignals signals) throws SegmentNotAcceptedException {

		double[] values = new double[segment.getSegmentWidth()];
		int counter = 0;
		for(int scan = segment.getStartScan(); scan <= segment.getStopScan(); scan++) {
			ITotalScanSignal signal = signals.getTotalScanSignal(scan);
			values[counter] = signal.getTotalSignal();
			counter++;
		}
		/*
		 * Calculate noise factor if the segment is accepted.
		 */
		return calculateNoiseFactorValue(segmentValidator, values);
	}

	/**
	 * Calculates the noise factor for the given segment if the segment is valid.
	 * 
	 * @param values
	 * @return
	 * @throws SegmentNotAcceptedException
	 */
	private static double calculateNoiseFactorValue(SegmentValidator segmentValidator, double[] values) throws SegmentNotAcceptedException {

		/*
		 * Check if the segment is accepted.<br/> If yes, than calculate its
		 * median.<br/> If no, than throw an exception.
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
			 * Calculate the median from mean.
			 */
			double medianFromMedian = Calculations.getMedianDeviationFromMedian(values);
			double nf = medianFromMedian / Math.sqrt(mean);
			return nf;
		}
	}
}
