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
 * Christoph Läubrich - refactor to use noise segments for calculations
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.noise.stein.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.results.ChromatogramSegmentation;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.chemclipse.model.support.SegmentValidator;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.noise.CalculatorSupport;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.IonNoiseSegment;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/*
 * S/N = Math.sqrt(intensity) * noiseFactor
 */
public class NoiseCalculator implements INoiseCalculator {

	private IChromatogram<?> chromatogram;
	private float noiseFactor = Float.NaN;

	@Override
	public float getSignalToNoiseRatio(IChromatogram<?> chromatogram, float intensity) {

		if(this.chromatogram != chromatogram) {
			noiseFactor = calculateNoiseFactorByStein(chromatogram);
			this.chromatogram = chromatogram;
		}
		if(Float.isFinite(noiseFactor) && noiseFactor > 0) {
			return (float)(Math.sqrt(intensity) * noiseFactor);
		} else {
			return Float.NaN;
		}
	}

	/**
	 * See S.E. Stein:
	 * "An Integrated Method for Spectrum Extraction and Compound Identification from Gas Chromatography/Mass Spectrometry Data"
	 * 
	 * @param IChromatogram
	 */
	private float calculateNoiseFactorByStein(IChromatogram<?> chromatogram) {

		if(chromatogram != null) {
			List<NoiseSegment> noiseSegments = getNoiseSegments(chromatogram, null);
			List<Double> noiseFactors = new ArrayList<Double>();
			for(NoiseSegment noiseSegment : noiseSegments) {
				addValue(noiseFactors, noiseSegment);
				for(IAnalysisSegment childSegment : noiseSegment.getChildSegments()) {
					if(childSegment instanceof IonNoiseSegment) {
						addValue(noiseFactors, (IonNoiseSegment)childSegment);
					}
				}
			}
			double median = Calculations.getMedian(noiseFactors);
			if(median > 0) {
				return (float)median;
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

	private void addValue(List<Double> noiseFactors, NoiseSegment noiseSegment) {

		if(noiseSegment.hasNoise()) {
			double nf = noiseSegment.getNoiseFactor();
			noiseFactors.add(nf);
		}
	}

	/**
	 * Calculates the noise factor for the given segment if the segment is valid.
	 * 
	 * @param baseSegment
	 * 
	 * @param values
	 * @return
	 */
	private static Double calculateNoiseFactor(SegmentValidator segmentValidator, float[] values) {

		double mean = Calculations.getMean(values);
		if(!segmentValidator.acceptSegment(values, mean)) {
			return null;
		} else {
			/*
			 * Calculate the median from mean.
			 */
			double medianFromMedian = Calculations.getMedianDeviationFromMedian(values);
			return medianFromMedian / Math.sqrt(mean);
		}
	}

	@Override
	public List<NoiseSegment> getNoiseSegments(IChromatogram<?> chromatogram, IProgressMonitor monitor) {

		if(chromatogram != null) {
			ChromatogramSegmentation segmentation = chromatogram.getMeasurementResult(ChromatogramSegmentation.class);
			if(segmentation != null) {
				SegmentValidator segmentValidator = new SegmentValidator();
				if(chromatogram instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
					IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogramMSD);
					IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals();
					return getNoiseSegments(segmentation.getResult(), signals, segmentValidator, monitor);
				} else {
					ITotalScanSignals signals = new TotalScanSignals(chromatogram);
					return getNoiseSegments(segmentation.getResult(), signals, segmentValidator, monitor);
				}
			}
		}
		return Collections.emptyList();
	}

	private List<NoiseSegment> getNoiseSegments(List<? extends IAnalysisSegment> segments, ITotalScanSignals totalSignals, SegmentValidator segmentValidator, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, segments.size());
		List<NoiseSegment> result = new ArrayList<>();
		for(IAnalysisSegment segment : segments) {
			/*
			 * TIC (use only the tic signal)
			 */
			Double factor = calculateNoiseFactor(segmentValidator, totalSignals.getValues(segment));
			if(factor != null) {
				NoiseSegment noiseSegment = new SteinNoiseSegment(segment, factor);
				result.add(noiseSegment);
			}
			subMonitor.worked(1);
		}
		return result;
	}

	private List<NoiseSegment> getNoiseSegments(List<? extends IAnalysisSegment> segments, IExtractedIonSignals extractedSignals, SegmentValidator segmentValidator, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, segments.size());
		List<NoiseSegment> result = new ArrayList<>();
		int startIon = extractedSignals.getStartIon();
		int stopIon = extractedSignals.getStopIon();
		for(IAnalysisSegment segment : segments) {
			/*
			 * XIC (use the ion range, e.g. ion 1 to ion 600)
			 */
			List<IonNoiseSegment> ionSegments = new ArrayList<>(stopIon - startIon + 1);
			for(int ion = startIon; ion <= stopIon; ion++) {
				Double factor = calculateNoiseFactor(segmentValidator, extractedSignals.getValues(segment, ion));
				if(factor != null) {
					ionSegments.add(new SteinIonNoiseSegment(segment, factor, ion));
				}
			}
			/*
			 * TIC (use only the tic signal)
			 */
			Double factor = calculateNoiseFactor(segmentValidator, extractedSignals.getTotalIonSignals().getValues(segment));
			if(factor != null) {
				if(ionSegments.isEmpty()) {
					result.add(new SteinNoiseSegment(segment, factor, CalculatorSupport.getCombinedMassSpectrum(extractedSignals, segment)));
				} else {
					result.add(new SteinNoiseSegment(segment, factor, Collections.unmodifiableList(ionSegments), CalculatorSupport.getCombinedMassSpectrum(extractedSignals, segment)));
				}
			} else {
				if(ionSegments.isEmpty()) {
					// no noise at all
					continue;
				} else {
					// there is noise but only on the XIC
					result.add(new SteinNoiseSegment(segment, Double.NaN, Collections.unmodifiableList(ionSegments), CalculatorSupport.getCombinedMassSpectrum(extractedSignals, segment)));
				}
			}
			subMonitor.worked(1);
		}
		return result;
	}

	private static class SteinNoiseSegment implements NoiseSegment {

		private final IAnalysisSegment baseSegment;
		private final double noiseFactor;
		private final Collection<IAnalysisSegment> children;
		private final IScan combinedMassSpectrum;

		public SteinNoiseSegment(IAnalysisSegment baseSegment, double noiseFactor) {
			this(baseSegment, noiseFactor, null);
		}

		public SteinNoiseSegment(IAnalysisSegment baseSegment, double noiseFactor, IScan combinedMassSpectrum) {
			this(baseSegment, noiseFactor, Collections.singleton(baseSegment), combinedMassSpectrum);
		}

		public SteinNoiseSegment(IAnalysisSegment baseSegment, double noiseFactor, Collection<IAnalysisSegment> children, IScan combinedMassSpectrum) {
			this.baseSegment = baseSegment;
			this.noiseFactor = noiseFactor;
			this.children = children;
			this.combinedMassSpectrum = combinedMassSpectrum;
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
		public Collection<IAnalysisSegment> getChildSegments() {

			return children;
		}

		@Override
		public double getNoiseFactor() {

			return noiseFactor;
		}

		@Override
		public IScan getScan() {

			return combinedMassSpectrum;
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

	private static class SteinIonNoiseSegment extends SteinNoiseSegment implements IonNoiseSegment {

		private final int ion;

		public SteinIonNoiseSegment(IAnalysisSegment baseSegment, double noiseFactor, int ion) {
			super(baseSegment, noiseFactor);
			this.ion = ion;
		}

		@Override
		public double getIon() {

			return ion;
		}
	}
}
