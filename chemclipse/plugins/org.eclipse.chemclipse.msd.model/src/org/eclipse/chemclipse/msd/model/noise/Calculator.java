/*******************************************************************************
 * Copyright (c) 2010, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Chrsitoph Läubrich - don't use exceptions as return values
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.noise;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;
import org.eclipse.chemclipse.model.support.AnalysisSupport;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.FilterException;
import org.eclipse.chemclipse.msd.model.support.CombinedMassSpectrumCalculator;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.core.runtime.IProgressMonitor;

public class Calculator {

	private static final Logger logger = Logger.getLogger(Calculator.class);
	private final CalculatorSupport calculatorSupport;

	public Calculator() {

		/*
		 * Why do we instantiate a class CalculatorSupport instance? If e.g. the
		 * getNoiseMassSpectrum(...) method is called 1000 times, as much
		 * CalculatorSupport instances will be created. It's maybe a performance
		 * problem. That's why the calculator support will be instantiated on
		 * class creation once.
		 */
		calculatorSupport = new CalculatorSupport();
	}

	/**
	 * Calculates a noise mass spectrum, normalized to 1000 by the given noise
	 * mass spectra.
	 */
	public ICombinedMassSpectrum getNoiseMassSpectrum(List<ICombinedMassSpectrum> noiseMassSpectra, IMarkedIons ionsToPreserve, IProgressMonitor monitor) {

		CombinedMassSpectrumCalculator combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
		/*
		 * Iterate through all given noise mass spectra.
		 */
		for(ICombinedMassSpectrum noiseMassSpectrum : noiseMassSpectra) {
			/*
			 * Add the value of each ion to the combined mass spectrum
			 * calculator.
			 */
			for(IIon ion : noiseMassSpectrum.getIons()) {
				combinedMassSpectrumCalculator.addIon(ion.getIon(), ion.getAbundance());
			}
		}
		return calculatorSupport.getNoiseMassSpectrum(combinedMassSpectrumCalculator, ionsToPreserve, monitor);
	}

	/**
	 * Calculates the noise segments. May return null.
	 * 
	 * @param IChromatogram
	 */
	public List<INoiseSegment> getNoiseSegments(IExtractedIonSignals extractedIonSignals, IMarkedIons ionsToPreserve, int segmentWidth, IProgressMonitor monitor) throws FilterException {

		/*
		 * Check the scan range.
		 */
		int width = segmentWidth;
		ScanRange scanRange = new ScanRange(extractedIonSignals.getStartScan(), extractedIonSignals.getStopScan());
		calculatorSupport.checkScanRange(scanRange, width);
		/*
		 * Try to calculate an appropriate set of segments.
		 */
		List<INoiseSegment> noiseSegments = null;
		try {
			AnalysisSupport analysisSupport = new AnalysisSupport(scanRange, width);
			List<IAnalysisSegment> analysisSegments = analysisSupport.getAnalysisSegments();
			noiseSegments = calculateNoiseSegments(analysisSegments, extractedIonSignals, ionsToPreserve, monitor);
		} catch(AnalysisSupportException e) {
			logger.warn(e);
		}
		return noiseSegments;
	}

	// --------------------------------------------private Methods
	/**
	 * See S.E. Stein:
	 * "An Integrated Method for Spectrum Extraction and Compound Identification from Gas Chromatography/Mass Spectrometry Data"
	 * .
	 * 
	 * @param segments
	 * @return float
	 */
	private List<INoiseSegment> calculateNoiseSegments(List<IAnalysisSegment> analysisSegments, IExtractedIonSignals extractedIonSignals, IMarkedIons ionsToPreserve, IProgressMonitor monitor) {

		@SuppressWarnings("unused")
		int rejected = 0;
		@SuppressWarnings("unused")
		int accepted = 0;
		//
		List<INoiseSegment> noiseSegments = new ArrayList<INoiseSegment>();
		for(IAnalysisSegment analysisSegment : analysisSegments) {
			/*
			 * TIC
			 */
			if(calculateMedianFromMean(analysisSegment, extractedIonSignals)) {
				accepted++;
				/*
				 * If no exception will be thrown, the segment is accepted. The
				 * combined mass spectrum will be calculated using the segment
				 * and will be converted in a noise mass spectrum, associated
				 * with the segment. The noise segment will be stored in the
				 * noise segment list.
				 */
				CombinedMassSpectrumCalculator combinedMassSpectrumCalculator = calculatorSupport.getCombinedMassSpectrumCalculator(analysisSegment, extractedIonSignals);
				ICombinedMassSpectrum noiseMassSpectrum = calculatorSupport.getNoiseMassSpectrum(combinedMassSpectrumCalculator, ionsToPreserve, monitor);
				INoiseSegment noiseSegment = new NoiseSegment(analysisSegment, noiseMassSpectrum);
				noiseSegments.add(noiseSegment);
			} else {
				rejected++;
			}
		}
		return noiseSegments;
	}

	/*
	 * Calculates the median from mean.
	 */
	private boolean calculateMedianFromMean(IAnalysisSegment analysisSegment, IExtractedIonSignals extractedIonSignals) {

		IExtractedIonSignal signal;
		int size = analysisSegment.getWidth();
		if(size <= 0) {
			return false;
		}
		double[] values = new double[size];
		int counter = 0;
		for(int scan = analysisSegment.getStartScan(); scan <= analysisSegment.getStopScan(); scan++) {
			try {
				signal = extractedIonSignals.getExtractedIonSignal(scan);
				values[counter] = signal.getTotalSignal();
			} catch(Exception e) {
				logger.warn(e);
			} finally {
				/*
				 * Increment counters position.
				 */
				counter++;
			}
		}
		/*
		 * Check if the segment is accepted.<br/> If yes, than calculate its
		 * median.<br/> If no, than throw an exception.
		 */
		double mean = Calculations.getMean(values);
		return calculatorSupport.acceptSegment(values, mean);
	}
	// --------------------------------------------private Methods
}
