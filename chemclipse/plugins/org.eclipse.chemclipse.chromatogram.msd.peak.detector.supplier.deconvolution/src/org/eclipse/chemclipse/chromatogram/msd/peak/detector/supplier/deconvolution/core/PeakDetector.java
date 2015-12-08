/*******************************************************************************
 * Copyright (c) 2015
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator.CodaCalculator;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.AbstractPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.IPeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.PeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.notifier.DeconvNotifier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.ArrayView;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.ArraysViewDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.DeconvHelper;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArrayView;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IDeconvHelper;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.internal.processor.DurbinWatsonProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.DurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IDurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.settings.IDurbinWatsonClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.internal.calculator.SnipCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetector extends AbstractPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetector.class);
	/*
	 * Derivatives
	 */
	private int noDerivative = 0;
	private int firstDerivative = 1;
	private int secondDerivative = 2;
	private int thirdDerivative = 3;
	private double[] detectPeakRanges;
	private double[] PeakRangesEndPoints;
	private double highestTicSignal = 0;
	private boolean validStartPoint;
	/*
	 * Coda Parameter
	 */
	private WindowSize MOVING_AVERAGE_WINDOW = WindowSize.SCANS_3;
	private float codaThreshold = 0.75f;
	/*
	 * Snip Parameter
	 */
	private int iterations = 70;
	/*
	 * Set ArraysViewDeconv
	 */
	private IArraysViewDeconv arraysViewDeconv;
	/*
	 * NoiseFactor by Vivo-Truyols
	 */
	private int factorNoise = 5;
	private int segmentSizeFactor = 30;

	@Override
	public IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorMSDSettings peakDetectorSettings, IProgressMonitor monitor) {

		IPeakDetectorMSDProcessingInfo processingInfo = new PeakDetectorMSDProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, peakDetectorSettings, monitor));
		if(!processingInfo.hasErrorMessages()) {
			ISupplierFilterSettings supplierFilterSettings = new SupplierFilterSettings();
			IDurbinWatsonClassifierResult durbinWatsonClassifierResult = new DurbinWatsonClassifierResult(ResultStatus.OK, "Test");
			// setMinScansPerPeak(peakDetectorSettings);
			// setThresholdValue(peakDetectorSettings);
			deconv(chromatogramSelection, durbinWatsonClassifierResult, supplierFilterSettings, monitor);
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Peak Detector Deconvolution", "Peaks have been detected successfully."));
		}
		return processingInfo;
	}

	@Override
	public IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		return null;
	}

	private void deconv(IChromatogramSelectionMSD chromatogramSelection, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, ISupplierFilterSettings supplierFilterSettings, IProgressMonitor monitor) {

		IDeconvHelper deconvHelper = new DeconvHelper();
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		try {
			/*
			 * TotalScanSignal
			 */
			// TotalScanSignalExtractor signalExtractor = new TotalScanSignalExtractor(chromatogram);
			// ITotalScanSignals totalScanSignals = signalExtractor.getTotalScanSignals(chromatogramSelection, false);
			/*
			 * TotalIonSignal
			 * MSD
			 */
			ITotalIonSignalExtractor totalIonSignalExtractor = new TotalIonSignalExtractor(chromatogram);
			ITotalScanSignals totalIONsignals = totalIonSignalExtractor.getTotalIonSignals(chromatogramSelection);
			IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			/*
			 * Set Size of ArraysViewDeconv
			 */
			arraysViewDeconv = new ArraysViewDeconv(totalIONsignals);
			double[] xScales = deconvHelper.setXValueforPrint(totalIONsignals);
			double[] yChromatogram = deconvHelper.getSignalAsArrayDoubleDeconv(totalIONsignals);
			for(int i = 0; i < yChromatogram.length; i++) {
				if(Double.compare(highestTicSignal, yChromatogram[i]) < 0) {
					highestTicSignal = yChromatogram[i];
				}
			}
			/*
			 * Durbin Watson
			 */
			DurbinWatsonRatings(yChromatogram, null, durbinWatsonClassifierResult, monitor);
			/*
			 * Coda
			 */
			IMarkedIons excludedIonsCODA = CodaFilter(extractedIonSignals);
			ITotalScanSignals totalIONsignalsExtractedCodaIons = totalIonSignalExtractor.getTotalIonSignals(excludedIonsCODA);
			double[] codaTicValues = deconvHelper.getSignalAsArrayDoubleDeconvCODA(totalIONsignalsExtractedCodaIons, totalIONsignals);
			/*
			 * SNIP
			 */
			float[] baselineValues = calculateSNIPBaseline(deconvHelper.getFloatArray(yChromatogram), iterations, monitor);
			/*
			 * Noise by Vivo-Truyols
			 * //
			 */
			double[] noiseValues = getNoiseOfTic(yChromatogram, false, false);
			/*
			 * Smoothed Values
			 */
			double[] smoothedValues = savitzkyGolaySmooth(noDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			/*
			 * Get Derivatives with SavitzkyGolay
			 */
			double[] firstDeriv = savitzkyGolaySmooth(firstDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			double[] secondDeriv = savitzkyGolaySmooth(secondDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			double[] thirdDeriv = savitzkyGolaySmooth(thirdDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			// -------------------------------------------------
			double[] firstDerivOriginal = getDerivative(yChromatogram, 1);
			double[] SmoothedFirstDerivOrig = savitzkyGolaySmooth(noDerivative, firstDerivOriginal, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			double[] secondDerivOriginal = getDerivative(firstDerivOriginal, 2);
			double[] SmoothedSecondDerivOrig = savitzkyGolaySmooth(noDerivative, secondDerivOriginal, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			/*
			 * fill PrintView
			 */
			/*
			 * alles normal anzeigen --- Savitzky Golay Derivs
			 */
			// fillArraysViewDeconv(xScales, yChromatogram, deconvHelper.getDoubleArray(baselineValues), noiseValues, smoothedValues, firstDeriv, secondDeriv, null, null, null);
			/*----------------------------------------------------------
			 *  alles normal anzeigen --- Deriv NOT SavitzkyGolay --- Smoothed with SG
			 */
			// fillArraysViewDeconv(xScales, yChromatogram, deconvHelper.getDoubleArray(baselineValues), noiseValues, smoothedValues, SmoothedFirstDerivOrig, SmoothedSecondDerivOrig, null, null, null);
			/*----------------------------------------------------------
			 * unterschiedliche erste Ableitungen
			 */
			// fillArraysViewDeconv(xScales, yChromatogram, deconvHelper.getDoubleArray(baselineValues), noiseValues, smoothedValues, firstDeriv, firstDerivOriginal, SmoothedFirstDerivOrig, null, null);
			/*----------------------------------------------------------
			 * unterschiedliche zweite Ableitungen
			 */
			// fillArraysViewDeconv(xScales, yChromatogram, null, noiseValues, smoothedValues, secondDeriv, secondDerivOriginal, SmoothedSecondDerivOrig, null, null);
			/*
			 * Tests
			 */
			double[] firstDerivCoda = getDerivative(codaTicValues, 1);
			double[] SmoothedFirstDerivCoda = savitzkyGolaySmooth(noDerivative, firstDerivCoda, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			double[] noiseValuesFirstDerivPositivTEST = getNoiseOfTic(SmoothedFirstDerivOrig, false, false);
			double[] noiseValuesFirstDerivPositiv = getNoiseOfTic(SmoothedFirstDerivOrig, true, true);
			double[] noiseValuesFirstDerivNegativ = getNoiseOfTic(SmoothedFirstDerivOrig, true, false);
			double[] noiseValuesFirstDerivPositivCODA = getNoiseOfTic(SmoothedFirstDerivCoda, true, true);
			double[] noiseValuesFirstDerivNegativCODA = getNoiseOfTic(SmoothedFirstDerivCoda, true, false);
			//
			/*
			 * CodaValues + Baseline
			 */
			// double[] codaWithBaselin = new double[xScales.length];
			// for(int i = 0; i < xScales.length; i++) {
			// codaWithBaselin[i] = codaTicValues[i] + baselineValues[i];
			// }
			/*
			 * TODO
			 * Mit Andreas mal anschauen und besprechen
			 * Vergleich mit Matlab
			 */
			// fillArraysViewDeconv(xScales, smoothedValues, codaWithBaselin, null, SmoothedFirstDerivCoda, SmoothedFirstDerivOrig, noiseValuesFirstDerivPositivTEST, noiseValuesFirstDerivNegativ, null, null);
			// ----------------------------------------------------------
			// fillArraysViewDeconv(xScales, yChromatogram, null, null, smoothedValues, SmoothedFirstDerivOrig, noiseValuesFirstDerivPositivTEST, noiseValuesFirstDerivNegativ, null, null);
			/*
			 * TODO
			 * Peakrange bestimmung
			 * Alle Ableitungen auswerten
			 */
			double[] derivNegativeNoise = positivToNegativ(noiseValuesFirstDerivPositivTEST);
			double[] peakRanges = detectPeakRanges(SmoothedFirstDerivOrig, noiseValuesFirstDerivPositivTEST, derivNegativeNoise);
			fillArraysViewDeconv(xScales, smoothedValues, null, null, null, SmoothedFirstDerivOrig, noiseValuesFirstDerivPositivTEST, derivNegativeNoise, detectPeakRanges, PeakRangesEndPoints);
			/*
			 * CODA
			 */
			// double[] peakRanges = detectPeakRanges(SmoothedFirstDerivCoda, noiseValuesFirstDerivPositiv, noiseValuesFirstDerivNegativ);
			// fillArraysViewDeconv(xScales, yChromatogram, null, null, smoothedValues, SmoothedFirstDerivCoda, noiseValuesFirstDerivPositivCODA, noiseValuesFirstDerivNegativCODA, peakRanges, null);
			/*
			 * TODO
			 * Deconvolution
			 * alle Peakranges => Massenspuren
			 */
			System.out.println("Deconv ist durchgelaufen");
			System.out.println("--------------------------------------------");
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
	}

	private double[] positivToNegativ(double[] noisePositiv) {

		double[] noiseNegativ = new double[noisePositiv.length];
		for(int i = 0; i < noisePositiv.length; i++) {
			noiseNegativ[i] = noisePositiv[i] * (-1);
		}
		return noiseNegativ;
	}

	private double[] detectPeakRanges(double[] firstDeriv, double[] noiseDerivPositiv, double[] noiseDerivNegativ) {

		int size = firstDeriv.length;
		detectPeakRanges = new double[size];
		PeakRangesEndPoints = new double[size];
		validStartPoint = true;
		double maxValueFirstDeriv = 0.0;
		int peakStart, peakEnd;
		/*
		 * MaxValue: Only for the ViewSize
		 */
		for(int i = 0; i < size - 4; i++) {
			if(Double.compare(maxValueFirstDeriv, firstDeriv[i]) < 0) {
				maxValueFirstDeriv = firstDeriv[i];
			}
		}
		double noisePositivAverage = 0.0f;
		double noiseNegativAverage = 0.0f;
		for(int i = 0; i < size; i++) {
			noisePositivAverage = noisePositivAverage + noiseDerivPositiv[i];
			noiseNegativAverage = noiseNegativAverage + noiseDerivNegativ[i];
		}
		noisePositivAverage = noisePositivAverage / size;
		noiseNegativAverage = noiseNegativAverage / size;
		/*
		 * Detect Start and End of a Peak
		 */
		for(int i = 1; i < size; i++) {
			peakStart = detectPeakStart(firstDeriv, i, noiseDerivPositiv, noisePositivAverage);
			if(validStartPoint) {
				peakEnd = detectPeakEnd(firstDeriv, peakStart, noiseDerivNegativ, noiseNegativAverage);
				if(peakEnd == 0) {
					detectPeakRanges[peakStart] = 0.0;
					if(peakStart != 0) {
						i = peakStart + 4;
					} else {
						i++;
					}
				} else {
					i = peakEnd - ((peakEnd - peakStart) / 2);// peakEnd;
				}
			} else {
				i = peakStart;
				validStartPoint = true;
			}
		}
		// checkStartEndPoints(firstDeriv);
		return detectPeakRanges;
	}

	/*
	 * TODO
	 * 1. Check if Values are rising => next deriv
	 * => Rising and Faling Flags
	 * 2. Check, Set Start and End Points
	 * => remember some EndPoints are also StartPoints
	 * 3. Be sure that the intervall of PeakRanges are big enough
	 */
	private void checkStartEndPoints(double[] firstDeriv) {

		boolean startPoint = false;
		boolean endPoint = true;
		int maxValueCheck = 0;
		/*
		 * detectPeakRanges filled with Start - End, Start - End
		 */
		for(int i = 0; i < detectPeakRanges.length; i++) {
			if((Double.compare(detectPeakRanges[i], 0.0f) > 0) && !startPoint) {
				for(int j = i; j > i - maxValueCheck; j--) {
					if((Double.compare(firstDeriv[j], 0.0) > 0) && (Double.compare(firstDeriv[j - 1], 0.0) < 0)) {
						detectPeakRanges[i] = 0.0;
						detectPeakRanges[j - 1] = highestTicSignal;
						break;
					}
				}
				maxValueCheck = 0;
				startPoint = true;
				endPoint = false;
			}
			if(!startPoint) {
				maxValueCheck++;
			}
			if((Double.compare(detectPeakRanges[i], 0.0f) > 0) && !endPoint) {
				for(int j = i - 1; j < checkMaxValueEndPoint(detectPeakRanges, i); j++) { // checkMaxValueEndPoint(detectPeakRanges, i)
					if((Double.compare(firstDeriv[j], 0.0) < 0) && (Double.compare(firstDeriv[j + 1], 0.0) > 0)) {
						detectPeakRanges[i] = 0.0;
						detectPeakRanges[j + 1] = highestTicSignal;
						break;
					}
				}
				startPoint = false;
				endPoint = true;
			}
		}
	}

	/**
	 * 
	 * @param detectPeakRangesCheck
	 * @param x
	 * @return
	 */
	private int checkMaxValueEndPoint(double[] detectPeakRangesCheck, int x) {

		int maxValueCheckEndPoint = 0;
		for(int i = x + 1; i < detectPeakRangesCheck.length; i++) {
			if(Double.compare(detectPeakRangesCheck[i], 0.0) > 0) {
				maxValueCheckEndPoint = i;
				break;
			}
		}
		return maxValueCheckEndPoint;
	}

	/*
	 * TODO
	 * Check if Start Point are one of the previous points of the last EndPoint of PeakRange
	 * aufsteigende Flanke als fallende Flanke betrachten und zurück gehen
	 */
	private int detectPeakStart(double[] firstDeriv, int startPoint, double[] noiseDerivPositiv, double noisePositivAverage) {

		double firstBefore, firstNext, NoisePositiv;
		int startPeak = 0;
		double noise = 0.0f;
		for(int i = startPoint; i < firstDeriv.length - 4; i++) {
			firstBefore = firstDeriv[i - 1];
			NoisePositiv = noiseDerivPositiv[i];
			firstNext = firstDeriv[i + 1];
			if(Double.compare(noiseDerivPositiv[i], noisePositivAverage) < 0) {
				noise = noisePositivAverage;
			} else {
				noise = noiseDerivPositiv[i];
			}
			if((Double.compare(firstDeriv[i - 1], noise) < 0) && (Double.compare(firstDeriv[i + 1], noise) > 0)) {
				/*
				 * mindestens die nächsten 2 steigend sind!
				 */
				if((Double.compare(firstDeriv[i + 1], firstDeriv[i + 2]) < 0) && (Double.compare(firstDeriv[i + 2], firstDeriv[i + 3]) < 0)) {
					startPeak = i;
					detectPeakRanges[i] = highestTicSignal;
					break;
				} else {
					startPeak = i + 3;
					validStartPoint = false;
					break;
				}
			}
		}
		return startPeak;
	}

	/*
	 * TODO
	 * Check if Points are in intervall of Positiv and Negativ Noise
	 */
	private int detectPeakEnd(double[] firstDeriv, int peakStart, double[] noiseDerivNegativ, double noiseNegativAverage) {

		double firstBefore, firstNext, NoisePositiv;
		int endPeak = 0;
		boolean risingFlagDone = false;
		double noise = 0.0f;
		if(peakStart > 0) {
			outerloop:
			for(int i = peakStart + 1; i < firstDeriv.length - 4; i++) {
				firstBefore = firstDeriv[i - 1];
				firstNext = firstDeriv[i + 1];
				if(Double.compare(firstDeriv[i - 1], firstDeriv[i + 1]) > 0) {
					risingFlagDone = true;
				}
				if((Double.compare(firstDeriv[i - 1], firstDeriv[i + 1]) < 0) && risingFlagDone) {
					if((Double.compare(firstDeriv[i + 1], firstDeriv[i + 2]) < 0) && (Double.compare(firstDeriv[i + 2], firstDeriv[i + 3]) < 0)) {
						risingFlagDone = false;
					} else {
						break outerloop;
					}
				} else if((Double.compare(firstDeriv[i - 1], 0) > 0) && (Double.compare(firstDeriv[i + 1], 0) < 0)) {
					/*
					 * TODO
					 * nächsten 3 fallen?
					 */
					for(int x = i; x < firstDeriv.length - 4; x++) {
						firstBefore = firstDeriv[x - 1];
						firstNext = firstDeriv[x + 1];
						if(Double.compare(noiseDerivNegativ[x], noiseNegativAverage) > 0) {
							noise = noiseDerivNegativ[x];// noiseNegativAverage;
						} else {
							noise = noiseDerivNegativ[x];
						}
						NoisePositiv = noiseDerivNegativ[x];
						if((Double.compare(firstDeriv[x - 1], noise) < 0) && (Double.compare(firstDeriv[x + 1], noise) > 0)) {
							endPeak = x;
							PeakRangesEndPoints[x + 1] = highestTicSignal;
							break outerloop;
						}
					}
				}
			}
		}
		return endPeak;
	}

	/**
	 * 
	 * @param extractedIonSignals
	 * @return excludedIons
	 */
	private IMarkedIons CodaFilter(IExtractedIonSignals extractedIonSignals) {

		IMarkedIons excludedIons = new MarkedIons();
		List<Float> mcqs = new ArrayList<Float>();
		float mcq;
		int startIon = extractedIonSignals.getStartIon();
		int stopIon = extractedIonSignals.getStopIon();
		for(int ion = startIon; ion <= stopIon; ion++) {
			mcq = CodaCalculator.getMCQValue(extractedIonSignals, MOVING_AVERAGE_WINDOW, ion);
			/*
			 * Add the ion value to the excluded ion list?
			 */
			if(mcq < codaThreshold) {
				excludedIons.add(new MarkedIon(ion));
			}
			/*
			 * Use mcqs to determine the data reduction value.
			 */
			mcqs.add(mcq);
		}
		return excludedIons;
	}

	/**
	 * 
	 * @param intensityValues
	 * @param iterations
	 * @param monitor
	 * @return
	 */
	private float[] calculateSNIPBaseline(float[] intensityValues, int iterations, IProgressMonitor monitor) {

		SnipCalculator snipCalculator = new SnipCalculator();
		snipCalculator.calculateBaselineIntensityValues(intensityValues, iterations, monitor);
		return intensityValues;
	}

	/**
	 * TODO
	 * Split into some more Functions
	 * 
	 * Problem with SegmentSizeFactor
	 * will be set with Size of TicValues divide by 30
	 * 
	 * @param ticValues
	 * @return
	 */
	private double[] getNoiseOfTic(double[] ticValues, boolean derivActive, boolean aboveNullDeriv) {

		int size = ticValues.length;
		segmentSizeFactor = size / 30;
		int segmentsize = size / segmentSizeFactor;
		int maxFor = 0;
		boolean setSegmentsize = false;
		if(segmentsize < 3) {
			maxFor = segmentsize * segmentSizeFactor;
			segmentsize = 3;
			setSegmentsize = true;
		} else {
			maxFor = segmentsize * segmentSizeFactor;
		}
		int counter = 0;
		int howManySegments = 1;
		double noise = 0;
		double[] segmentNoise = new double[segmentsize];
		double[] finalNoise = new double[size];
		/*
		 * Loop until MaxFor = segmentSize * segmentSizeFactor
		 */
		for(int i = 0; i < maxFor + 1; i++) {
			/*
			 * Check whether enough Values for one Segment was set
			 */
			if(i - (segmentsize * howManySegments) == 0) {
				/*
				 * Only for derivatives,
				 * => maybe segmentNoise = 0;
				 * => set Noise to latest Noise Value
				 */
				boolean segmentNoiseNull = true;
				for(int j = 0; j < segmentNoise.length; j++) {
					if(segmentNoise[j] > 0.0) {
						segmentNoiseNull = false;
					}
				}
				if(segmentNoiseNull) {
					if(howManySegments == 1) {
						noise = 0.0f;
					} else {
						noise = finalNoise[i - segmentsize - 1];
					}
				} else {
					noise = getNoiseTicValueByVivoTruyols(segmentNoise);
				}
				counter = 0;
				/*
				 * Set the Noise Value by VivoTruyols to finalNoise
				 */
				for(int j = i - segmentsize * howManySegments + 1; j < segmentsize + 1; j++) {
					if(derivActive && !aboveNullDeriv) {
						finalNoise[i + counter - (segmentsize)] = noise * (-1);
					} else {
						finalNoise[i + counter - (segmentsize)] = noise;
					}
					counter++;
				}
				/*
				 * Set segmentNoise to 0
				 */
				for(int p = 0; p < segmentsize; p++) {
					segmentNoise[p] = 0;
				}
				counter = 0;
				howManySegments++;
				/*
				 * (reach maxSegments) or (Counter=MaxFor and SegmentSize was set to default 3)
				 */
				if((howManySegments - 1 == (size / segmentsize)) || (i == maxFor && setSegmentsize)) {
					break;
				}
				i--;
			} else {
				/*
				 * Need Noise of Derivs
				 * else Noise of normal TicValue
				 */
				if(derivActive) {
					if(aboveNullDeriv) {
						if(ticValues[i] > 0) {
							segmentNoise[counter] = ticValues[i];
						} else {
							segmentNoise[counter] = 0.0;
						}
						counter++;
					} else {
						if(ticValues[i] < 0) {
							segmentNoise[counter] = Math.abs(ticValues[i]);
						} else {
							segmentNoise[counter] = 0.0;
						}
						counter++;
					}
				} else {
					segmentNoise[counter] = ticValues[i];
					counter++;
				}
			}
		}
		/*
		 * Loop for the Rest of TicValues
		 */
		if((size - maxFor - 1) >= 0) {
			for(int x = size - maxFor - 1; x < size - maxFor; x++) {
				if(derivActive) {
					if(aboveNullDeriv) {
						if(ticValues[x] > 0) {
							segmentNoise[x] = ticValues[x];
						} else {
							segmentNoise[x] = 0.0;
						}
						counter++;
					} else {
						if(ticValues[x] < 0) {
							segmentNoise[x] = ticValues[Math.abs(x)];
						} else {
							segmentNoise[x] = 0.0;
						}
						counter++;
					}
				} else {
					segmentNoise[x] = ticValues[x];
				}
			}
			/*
			 * Only for derivatives,
			 * => maybe segmentNoise = 0;
			 * => set Noise to latest Noise Value
			 */
			boolean segmentNoiseNull = true;
			for(int j = 0; j < segmentNoise.length; j++) {
				if(segmentNoise[j] > 0.0) {
					segmentNoiseNull = false;
				}
			}
			if(segmentNoiseNull) {
				noise = finalNoise[maxFor - 1];
			} else {
				noise = getNoiseTicValueByVivoTruyols(segmentNoise);
			}
			/*
			 * Set the Noise Value by VivoTruyols to finalNoise
			 */
			for(int i = maxFor; i < size; i++) {
				if(derivActive && !aboveNullDeriv) {
					finalNoise[i] = noise * (-1);
				} else {
					finalNoise[i] = noise;
				}
			}
		}
		return finalNoise;
	}

	/**
	 * 
	 * @param ticValues
	 * @return
	 */
	private double getNoiseTicValueByVivoTruyols(double[] ticValues) {

		double[] h = new double[ticValues.length];
		int i;
		int Factor = 3;
		if(ticValues.length == Factor) {
			Factor = 2;
		}
		for(i = 2 * Math.round(Factor / 2) - 1; i < ticValues.length - 1; i++) {
			h[i] = Math.abs(ticValues[i] - (ticValues[i - 1] + ticValues[i + 1]) / 2);
		}
		double Wert1 = 0.0f;
		int Wert2 = 0;
		for(int j = 0; j < i; j++) {
			if(h[j] > 0.0) {
				Wert1 = h[j] + Wert1;
				Wert2++;
			}
		}
		double Noiseh1 = Wert1 / Wert2;
		return Noiseh1 * factorNoise;
	}

	/**
	 * 
	 * @param ticValues
	 * @param supplierFilterSettings
	 * @param monitor
	 * @return
	 */
	private double[] savitzkyGolaySmooth(int whichDerivative, double[] ticValues, ISupplierFilterSettings supplierFilterSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		supplierFilterSettings.setDerivative(whichDerivative);
		supplierFilterSettings = DurbinWatsonSetBestValuesForSavitzkyGolay(durbinWatsonClassifierResult, supplierFilterSettings, whichDerivative);
		SavitzkyGolayProcessor savitzkyGolayProcessor = new SavitzkyGolayProcessor();
		double[] valuesSmoothed = savitzkyGolayProcessor.smooth(ticValues, supplierFilterSettings, monitor);
		return valuesSmoothed;
	}

	/**
	 * 
	 * @param chromatogramSelection
	 * @param classifierSettings
	 * @param durbinWatsonClassifierResult
	 * @param monitor
	 */
	private void DurbinWatsonRatings(double[] ticValues, IDurbinWatsonClassifierSettings classifierSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		DurbinWatsonProcessor durbinWatsonProcessor = new DurbinWatsonProcessor();
		durbinWatsonProcessor.durbinWatsonMain(ticValues, classifierSettings, durbinWatsonClassifierResult, monitor);
	}

	/**
	 * 
	 * @param durbinWatsonClassifierResult
	 * @param whichderivative
	 * @param supplierFilterSettings
	 * @return supplierFilterSettings
	 */
	private ISupplierFilterSettings DurbinWatsonSetBestValuesForSavitzkyGolay(IDurbinWatsonClassifierResult durbinWatsonClassifierResult, ISupplierFilterSettings supplierFilterSettings, int whichderivative) {

		if(durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings() != null) {
			int sizeRating = durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().size() - 1;
			double bestRating = 100.0f;
			double newRating = 0.0f;
			int bestDurbinWatson = 0;
			boolean setbestDB = false;
			for(int i = 1; i < sizeRating; i++) {
				if(durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().get(i).getSupplierFilterSettings().getDerivative() == whichderivative) {
					/*
					 * DW: PerfectValue is close to 2
					 */
					newRating = 2.0f - Math.abs(durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().get(i).getRating());
					if(Double.compare(Math.abs(newRating), bestRating) < 0) {
						bestRating = Math.abs(newRating);
						bestDurbinWatson = i;
						setbestDB = true;
					}
				} else {
					if(setbestDB) {
						break;
					}
				}
			}
			int order = durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().get(bestDurbinWatson).getSupplierFilterSettings().getOrder();
			int width = durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().get(bestDurbinWatson).getSupplierFilterSettings().getWidth();
			supplierFilterSettings.setDerivative(whichderivative);
			supplierFilterSettings.setOrder(order);
			supplierFilterSettings.setWidth(width);
		}
		return supplierFilterSettings;
	}

	/**
	 * 
	 * @param xScales
	 * @param yChromatogram
	 * @param baselineValues
	 * @param noiseValues
	 * @param smoothedValues
	 * @param firstDeriv
	 * @param secondDeriv
	 * @param thirdDeriv
	 * @param peakRanges
	 * @param peakRangesEndPoint
	 */
	private void fillArraysViewDeconv(double[] xScales, double[] yChromatogram, double[] baselineValues, double[] noiseValues, double[] smoothedValues, double[] firstDeriv, double[] secondDeriv, double[] thirdDeriv, double[] peakRanges, double[] peakRangesEndPoint) {

		if(arraysViewDeconv != null) {
			int startScan = arraysViewDeconv.getStartScan();
			int stopScan = arraysViewDeconv.getStopScan();
			int size = stopScan - startScan + 1;
			boolean baselineActive = false;
			/*
			 * xScale
			 */
			if(xScales != null && xScales.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView xScale = new ArrayView(xScales[i] + startScan);
					arraysViewDeconv.addxValues(xScale);
				}
			}
			/*
			 * yChromatogram
			 */
			if(yChromatogram != null && yChromatogram.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(yChromatogram[i]);
					arraysViewDeconv.addOriginalChromatogramValues(yScale);
				}
			}
			/*
			 * baselineValues
			 */
			if(baselineValues != null && baselineValues.length != 0) {
				baselineActive = true;
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(baselineValues[i]);
					arraysViewDeconv.addBaselineValues(yScale);
				}
			}
			/*
			 * Noise
			 */
			if(noiseValues != null && noiseValues.length != 0) {
				for(int i = 0; i < size; i++) {
					if(baselineActive) {
						if(baselineValues != null && baselineValues.length != 0) {
							noiseValues[i] = baselineValues[i] + noiseValues[i];
						}
					} else {
						noiseValues[i] = noiseValues[i];
					}
					IArrayView yScale = new ArrayView(noiseValues[i]);
					arraysViewDeconv.addNoiseValues(yScale);
				}
			}
			/*
			 * smoothedValues
			 */
			if(smoothedValues != null && smoothedValues.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(smoothedValues[i]);
					arraysViewDeconv.addSmoothedValues(yScale);
				}
			}
			/*
			 * firstDeriv
			 */
			if(firstDeriv != null && firstDeriv.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(firstDeriv[i]);
					arraysViewDeconv.addFirstDeriv(yScale);
				}
			}
			/*
			 * secondDeriv
			 */
			if(secondDeriv != null && secondDeriv.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(secondDeriv[i]);
					arraysViewDeconv.addSecondDeriv(yScale);
				}
			}
			/*
			 * thirdDeriv
			 */
			if(thirdDeriv != null && thirdDeriv.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(thirdDeriv[i]);
					arraysViewDeconv.addThirdDeriv(yScale);
				}
			}
			/*
			 * peakRanges
			 */
			if(peakRanges != null && peakRanges.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(peakRanges[i]);
					arraysViewDeconv.addPeakValues(yScale);
				}
			}
			if(peakRangesEndPoint != null && peakRangesEndPoint.length != 0) {
				for(int i = 0; i < size; i++) {
					IArrayView yScale = new ArrayView(peakRangesEndPoint[i]);
					arraysViewDeconv.addPeakRangesEndPoint(yScale);
				}
			}
			/*
			 * Send to EventBroker
			 */
			DeconvNotifier.fireUpdate(arraysViewDeconv);
		}
	}

	/**
	 * 
	 * @param yScaleChromatogram
	 * @return
	 */
	private double[] getDerivative(double[] yScaleChromatogram, int factor) {

		int size = yScaleChromatogram.length;
		double[] derivative = new double[size];
		double sP, s, sN;
		for(int i = 0; i < size; i++) {
			if(i == 0) {
				s = yScaleChromatogram[i];
				sN = yScaleChromatogram[i + 1];
				derivative[i] = (sN - s) * factor;
			}
			if(i == size) {
				sP = yScaleChromatogram[i - 1];
				s = yScaleChromatogram[i];
				derivative[i] = (s - sP) * factor;
			}
			if(i != 0 && i < size - 1) {
				sP = yScaleChromatogram[i - 1];
				sN = yScaleChromatogram[i + 1];
				derivative[i] = (sN - sP) * factor;
			}
		}
		return derivative;
	}
}
