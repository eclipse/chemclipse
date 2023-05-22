/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
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
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.results.ChromatogramSegmentation;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.ChromatogramSegment;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.chemclipse.model.support.SegmentValidator;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.noise.CalculatorSupport;
import org.eclipse.chemclipse.msd.model.noise.IonNoiseCalculator;
import org.eclipse.chemclipse.msd.model.noise.IonNoiseSegment;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/*
 * S/N = Math.sqrt(intensity) * noiseFactor
 */
public class NoiseCalculator implements IonNoiseCalculator {

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
			List<Double> noiseFactors = new ArrayList<Double>();
			Consumer<NoiseSegment> consumer = segment -> noiseFactors.add(segment.getNoiseFactor());
			getNoiseSegments(chromatogram, null).forEach(consumer);
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				SegmentValidator segmentValidator = new SegmentValidator();
				IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogramMSD);
				IExtractedIonSignals extractedSignals = extractedIonSignalExtractor.getExtractedIonSignals();
				int startIon = extractedSignals.getStartIon();
				int stopIon = extractedSignals.getStopIon();
				for(int ion = startIon; ion <= stopIon; ion++) {
					List<NoiseSegment> ionNoiseSegments = getNoiseSegments(ion, chromatogram.getMeasurementResult(ChromatogramSegmentation.class), segmentValidator, extractedSignals);
					ionNoiseSegments.forEach(consumer);
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

		if(chromatogram instanceof IChromatogramMSD) {
			return getNoiseSegments(chromatogram, IIon.TIC_ION, monitor);
		}
		if(chromatogram != null) {
			ChromatogramSegmentation segmentation = chromatogram.getMeasurementResult(ChromatogramSegmentation.class);
			if(segmentation != null) {
				SegmentValidator segmentValidator = new SegmentValidator();
				ITotalScanSignals signals = new TotalScanSignals(chromatogram);
				List<ChromatogramSegment> segments = segmentation.getResult();
				SubMonitor subMonitor = SubMonitor.convert(monitor, segments.size());
				List<NoiseSegment> result = new ArrayList<>();
				for(IAnalysisSegment segment : segments) {
					Double factor = calculateNoiseFactor(segmentValidator, signals.getValues(segment));
					if(factor != null) {
						NoiseSegment noiseSegment = new SteinNoiseSegment(segment, factor);
						result.add(noiseSegment);
					}
					subMonitor.worked(1);
				}
				return result;
			}
		}
		return Collections.emptyList();
	}

	@Override
	public List<NoiseSegment> getNoiseSegments(IChromatogram<?> chromatogram, double ion, IProgressMonitor monitor) {

		if(chromatogram instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
			ChromatogramSegmentation segmentation = chromatogram.getMeasurementResult(ChromatogramSegmentation.class);
			if(segmentation != null) {
				SegmentValidator segmentValidator = new SegmentValidator();
				IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogramMSD);
				IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals();
				return getNoiseSegments(ion, segmentation, segmentValidator, signals);
			}
		}
		return Collections.emptyList();
	}

	private List<NoiseSegment> getNoiseSegments(double ion, ChromatogramSegmentation segmentation, SegmentValidator segmentValidator, IExtractedIonSignals signals) {

		List<NoiseSegment> result = new ArrayList<>();
		if(segmentation != null) {
			List<ChromatogramSegment> segments = segmentation.getResult();
			for(IAnalysisSegment segment : segments) {
				Double factor = calculateNoiseFactor(segmentValidator, signals.getValues(segment, (int)ion));
				if(factor != null) {
					IScan scan;
					if(ion == IIon.TIC_ION) {
						scan = CalculatorSupport.getCombinedMassSpectrum(signals, segment).normalize();
					} else {
						try {
							scan = new ScanMSD(Collections.singleton(new Ion(ion))).normalize();
						} catch(IonLimitExceededException e) {
							scan = null;
						}
					}
					result.add(new SteinIonNoiseSegment(segment, factor, ion, scan));
				}
			}
		}
		return result;
	}

	private static class SteinNoiseSegment implements NoiseSegment {

		private static final long serialVersionUID = 6666299719479935503L;
		private final IAnalysisSegment baseSegment;
		private final double noiseFactor;

		public SteinNoiseSegment(IAnalysisSegment baseSegment, double noiseFactor) {

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
		public Collection<IAnalysisSegment> getChildSegments() {

			return Collections.singleton(baseSegment);
		}

		@Override
		public double getNoiseFactor() {

			return noiseFactor;
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

	private static class SteinIonNoiseSegment extends SteinNoiseSegment implements IonNoiseSegment, IAdaptable {

		private static final long serialVersionUID = 1277953700111903075L;
		private final double ion;
		private final IScan combinedMassSpectrum;

		public SteinIonNoiseSegment(IAnalysisSegment baseSegment, double noiseFactor, double ion, IScan combinedMassSpectrum) {

			super(baseSegment, noiseFactor);
			this.ion = ion;
			this.combinedMassSpectrum = combinedMassSpectrum;
		}

		@Override
		public double getIon() {

			return ion;
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {

			if(adapter.isInstance(combinedMassSpectrum)) {
				return adapter.cast(combinedMassSpectrum);
			}
			return null;
		}
	}
}
