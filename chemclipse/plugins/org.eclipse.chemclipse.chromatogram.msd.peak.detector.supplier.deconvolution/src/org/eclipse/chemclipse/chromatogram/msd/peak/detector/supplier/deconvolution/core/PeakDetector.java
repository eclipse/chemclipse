/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate UG (haftungsbeschränkt).
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
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.AbstractPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.IPeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.PeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.DerivativesAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.FirstDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.IDerivativesAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.IFirstDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.ISecondDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.IThirdDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.SecondDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.ThirdDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeakDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeakRange;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeakRanges;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeaksDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeakDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeakRange;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeakRanges;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeaksDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.notifier.DeconvNotifier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.IDeconvolutionPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.ArrayView;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.ArraysViewDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.DeconvHelper;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArrayView;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IDeconvHelper;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.RawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.processor.DurbinWatsonProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.DurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IDurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.settings.IDurbinWatsonClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.calculator.SnipCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetector extends AbstractPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetector.class);
	IChromatogramMSD chromatogram;
	private IArraysViewDeconv arraysViewDeconv;
	/*
	 * Derivatives
	 */
	private int noDerivative = 0;
	private int firstDerivative = 1;
	// private int secondDerivative = 2;
	// private int thirdDerivative = 3;
	private double highestTicSignal = 0;
	private boolean validStartPoint;
	// Arrays for viewDeconv
	private double[] PeakRangesStartPoints;
	private double[] PeakRangesEndPoints;
	/*
	 * NoiseFactor by Vivo-Truyols
	 */
	private int factorNoiseOriginal = 5;
	private int factorNoiseDerivative = 3;
	// Internal setups
	private boolean useAverageNoiseForSmallNoise = false;
	private boolean seePeakRanges = false;
	private boolean checkWithSecondDerive = true;
	/*
	 * Set by User
	 */
	private int minPeakRising;
	private int minPeakWidth;
	private double minSignalToNoiseRatio;
	private int baselineIterations;
	// Noise
	private int quantityNoiseSegments;

	@Override
	public IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorMSDSettings peakDetectorSettings, IProgressMonitor monitor) {

		IPeakDetectorMSDProcessingInfo processingInfo = new PeakDetectorMSDProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, peakDetectorSettings, monitor));
		if(!processingInfo.hasErrorMessages()) {
			ISupplierFilterSettings supplierFilterSettings = new SupplierFilterSettings();
			IDurbinWatsonClassifierResult durbinWatsonClassifierResult = new DurbinWatsonClassifierResult(ResultStatus.OK, "Test");
			setMinimumSignalToNoise(peakDetectorSettings);
			setMinimumPeakWidth(peakDetectorSettings);
			setMinimumPeakRising(peakDetectorSettings);
			setBaselineIterations(peakDetectorSettings);
			setQuantityNoiseSegments(peakDetectorSettings);
			deconv(chromatogramSelection, durbinWatsonClassifierResult, supplierFilterSettings, monitor);
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Peak Detector Deconvolution", "Peaks have been detected successfully."));
		}
		return processingInfo;
	}

	@Override
	public IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		return null;
	}

	private void setMinimumSignalToNoise(IPeakDetectorMSDSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IDeconvolutionPeakDetectorSettings) {
			IDeconvolutionPeakDetectorSettings deconvolutionPeakDetectorSettings = (IDeconvolutionPeakDetectorSettings)peakDetectorSettings;
			this.minSignalToNoiseRatio = deconvolutionPeakDetectorSettings.getMinimumSignalToNoiseRatio();
		}
	}

	private void setMinimumPeakWidth(IPeakDetectorMSDSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IDeconvolutionPeakDetectorSettings) {
			IDeconvolutionPeakDetectorSettings deconvolutionPeakDetectorSettings = (IDeconvolutionPeakDetectorSettings)peakDetectorSettings;
			this.minPeakWidth = deconvolutionPeakDetectorSettings.getMinimumPeakWidth();
		}
	}

	private void setMinimumPeakRising(IPeakDetectorMSDSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IDeconvolutionPeakDetectorSettings) {
			IDeconvolutionPeakDetectorSettings deconvolutionPeakDetectorSettings = (IDeconvolutionPeakDetectorSettings)peakDetectorSettings;
			this.minPeakRising = deconvolutionPeakDetectorSettings.getMinimumPeakRising();
		}
	}

	private void setBaselineIterations(IPeakDetectorMSDSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IDeconvolutionPeakDetectorSettings) {
			IDeconvolutionPeakDetectorSettings deconvolutionPeakDetectorSettings = (IDeconvolutionPeakDetectorSettings)peakDetectorSettings;
			this.baselineIterations = deconvolutionPeakDetectorSettings.getBaselineIterations();
		}
	}

	private void setQuantityNoiseSegments(IPeakDetectorMSDSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof IDeconvolutionPeakDetectorSettings) {
			IDeconvolutionPeakDetectorSettings deconvolutionPeakDetectorSettings = (IDeconvolutionPeakDetectorSettings)peakDetectorSettings;
			this.quantityNoiseSegments = deconvolutionPeakDetectorSettings.getQuantityNoiseSegments();
		}
	}

	private void deconv(IChromatogramSelectionMSD chromatogramSelection, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, ISupplierFilterSettings supplierFilterSettings, IProgressMonitor monitor) {

		IDeconvHelper deconvHelper = new DeconvHelper();
		chromatogram = chromatogramSelection.getChromatogramMSD();
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
			// IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			// IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
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
			//
			chromatogram.removeAllPeaks();
			/*
			 * Durbin Watson
			 */
			DurbinWatsonRatings(yChromatogram, null, durbinWatsonClassifierResult, monitor);
			/*
			 * SNIP
			 */
			double[] baselineValues = deconvHelper.getDoubleArray(calculateSNIPBaseline(deconvHelper.getFloatArray(yChromatogram), baselineIterations, monitor));
			/*
			 * Noise by Vivo-Truyols
			 */
			double[] noiseValues = getNoiseOfTic(yChromatogram, false, false);
			/*
			 * Smoothed Values
			 */
			double[] smoothedValues = savitzkyGolaySmooth(noDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			IDerivativesAndNoise derivativesAndNoise = setDerivatives(yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			/*
			 * PeakRanges and set up for the view
			 */
			// Set peakRanges
			IPeakRanges peakRanges = new PeakRanges(totalIONsignals);
			// peakRanges = detectPeakRanges(derivativesAndNoise, peakRanges, yChromatogram, deconvHelper.getNoisePlusBaselin(baselineValues, noiseValues), baselineValues);
			peakRanges = detectPeakRanges(derivativesAndNoise, peakRanges, yChromatogram, noiseValues, baselineValues);
			peakRanges = checkPeakRangesWithUserParameter(peakRanges);
			setPeaksToChromatogram(peakRanges);
			fillArraysViewDeconv(xScales, smoothedValues, null, null, derivativesAndNoise.getSecondDerivativeAndNoise().getSecondDeriv(), derivativesAndNoise.getFirstDerivativeAndNoise().getFirstDeriv(), null, derivativesAndNoise.getThirdDerivativeAndNoise().getThirdDeriv(), PeakRangesStartPoints, PeakRangesEndPoints);
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

	/**
	 * 
	 * @param yChromatogram
	 * @param supplierFilterSettings
	 * @param durbinWatsonClassifierResult
	 * @param monitor
	 * @return
	 */
	private IDerivativesAndNoise setDerivatives(double[] yChromatogram, ISupplierFilterSettings supplierFilterSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		/*
		 * Savitzky Golay max second Derivative
		 */
		IDeconvHelper deconvHelper = new DeconvHelper();
		/*
		 * First deriv by Savitzky Golay with Noise (Factorised by 2 => now it's the same range like firstderiv by normal function)
		 */
		double[] firstDerivSmoothedFactorised = deconvHelper.factorisingValues(savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(firstDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor), supplierFilterSettings, durbinWatsonClassifierResult, monitor), 2);
		double[] noiseFirstDeriv = getNoiseOfTic(firstDerivSmoothedFactorised, false, false);
		double[] noiseNegativeFirstDeriv = deconvHelper.positivToNegativ(noiseFirstDeriv);
		IFirstDerivativeAndNoise firstDerivative = new FirstDerivativeAndNoise(firstDerivSmoothedFactorised, noiseFirstDeriv, noiseNegativeFirstDeriv);
		/*
		 * SecondDerivative is formed out of
		 * (original chromatogram ticvalues + SG(first derivative) => first derivative)
		 * (first derivative + SG(first derivative) => second derivative)
		 * (second derivative + SG(no derivative = smooth) => second derivative smoothed)
		 * (second derivative smoothed + factorising => better for output)
		 */
		double[] secondDerivSmoothedFactorised = deconvHelper.factorisingValues(savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(this.firstDerivative, savitzkyGolaySmooth(this.firstDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor), supplierFilterSettings, durbinWatsonClassifierResult, monitor), supplierFilterSettings, durbinWatsonClassifierResult, monitor), 9);
		double[] noiseSecondDeriv = getNoiseOfTic(secondDerivSmoothedFactorised, false, false);
		double[] noiseNegativeSecondDeriv = deconvHelper.positivToNegativ(noiseSecondDeriv);
		ISecondDerivativeAndNoise secondDerivative = new SecondDerivativeAndNoise(secondDerivSmoothedFactorised, noiseSecondDeriv, noiseNegativeSecondDeriv);
		/*
		 * Third Derivative is formed out of
		 * (original chromatogram ticvalues + SG(first derivative) => first derivative)
		 * (first derivative + SG(first derivative) => second derivative)
		 * (second derivative + SG(first derivative) => third derivative)
		 * (third derivative + SG(no derivative=smooth) => third derivative smoothed)
		 * (third derivative smoothed + factorising => better for output)
		 */
		double[] thirdDerivSmoothedFactorised = deconvHelper.factorisingValues(savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(this.firstDerivative, savitzkyGolaySmooth(this.firstDerivative, savitzkyGolaySmooth(this.firstDerivative, yChromatogram, supplierFilterSettings, durbinWatsonClassifierResult, monitor), supplierFilterSettings, durbinWatsonClassifierResult, monitor), supplierFilterSettings, durbinWatsonClassifierResult, monitor), supplierFilterSettings, durbinWatsonClassifierResult, monitor), 19);
		double[] noiseThirdDeriv = getNoiseOfTic(thirdDerivSmoothedFactorised, false, false);
		double[] noiseNegativeThirdDeriv = deconvHelper.positivToNegativ(noiseThirdDeriv);
		IThirdDerivativeAndNoise thirdDerivative = new ThirdDerivativeAndNoise(thirdDerivSmoothedFactorised, noiseThirdDeriv, noiseNegativeThirdDeriv);
		// All in one
		IDerivativesAndNoise derivativesAndNoise = new DerivativesAndNoise(firstDerivative, secondDerivative, thirdDerivative);
		return derivativesAndNoise;
	}

	/**
	 * 
	 * @param peakRanges
	 */
	private void setPeaksToChromatogram(IPeakRanges peakRanges) {

		IRawPeak rawPeak;
		List<IRawPeak> rawPeaks = new ArrayList<IRawPeak>();
		IChromatogramPeakMSD peak = null;
		IScanRange scanRange = null;
		int size = peakRanges.size();
		int offset = peakRanges.getStartScan();
		for(int i = 0; i < size; i++) {
			if(peakRanges.getPeakRange(i).getwidthPeakRange() >= IPeakModelMSD.MINIMUM_SCANS) {
				int peakStart = peakRanges.getPeakRange(i).getPeakStartPoint() + offset;
				int peakEnd = peakRanges.getPeakRange(i).getPeakEndPoint() + offset;
				int peakMax = peakRanges.getPeakRange(i).getPeakList(0).getPeakDeconv(0).getPeak() + offset;
				rawPeak = new RawPeak(peakStart, peakMax, peakEnd);
				rawPeaks.add(rawPeak);
			}
		}
		for(IRawPeak eachrawPeak : rawPeaks) {
			try {
				scanRange = new ScanRange(eachrawPeak.getStartScan(), eachrawPeak.getStopScan());
				peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, false);
				peak.setDetectorDescription("Peak detector deconvolution");
				chromatogram.addPeak(peak);
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * 
	 * @param peakRangesOriginal
	 * @return
	 */
	private IPeakRanges checkPeakRangesWithUserParameter(IPeakRanges peakRangesOriginal) {

		IPeakRanges newPeakRanges = peakRangesOriginal;
		// minimalSignalToNoiseRatio = 50.0;
		int size = peakRangesOriginal.size();
		for(int i = 0; i < size; i++) {
			if(newPeakRanges.getPeakRange(i).getwidthPeakRange() < minPeakWidth) {
				newPeakRanges.deletePeakRange(i);
				size = newPeakRanges.size();
				i--;
			}
		}
		for(int i = 0; i < size; i++) {
			if(Double.compare(newPeakRanges.getPeakRange(i).getSignalToNoise(), minSignalToNoiseRatio) < 0) {
				newPeakRanges.deletePeakRange(i);
				size = newPeakRanges.size();
				i--;
			}
		}
		/*
		 * Add values for deconv-view
		 */
		for(int i = 0; i < size; i++) {
			System.out.println("SNR: " + newPeakRanges.getPeakRange(i).getSignalToNoise());
			System.out.println("Width: " + newPeakRanges.getPeakRange(i).getwidthPeakRange());
			System.out.println("");
			if(seePeakRanges) {
				PeakRangesStartPoints[newPeakRanges.getPeakRange(i).getPeakStartPoint()] = highestTicSignal;
				PeakRangesEndPoints[newPeakRanges.getPeakRange(i).getPeakEndPoint()] = highestTicSignal;
			} else {
				PeakRangesStartPoints[newPeakRanges.getPeakRange(i).getPeakList(0).getPeakDeconv(0).getPeak()] = highestTicSignal;
				if(checkWithSecondDerive && (newPeakRanges.getPeakRange(i).sizePeakList() > 1)) {
					for(int z = 0; z < newPeakRanges.getPeakRange(i).getPeakList(1).sizePeakDeconv(); z++) {
						PeakRangesEndPoints[newPeakRanges.getPeakRange(i).getPeakList(1).getPeakDeconv(z).getPeak()] = highestTicSignal;
					}
				}
			}
		}
		return newPeakRanges;
	}

	/**
	 * 
	 * @param yChromatogram
	 * @param noiseChroma
	 * @param peakStart
	 * @param peakEnd
	 * @param baseline
	 * @return
	 */
	private double getSignaltoNoiseRatioforPeakRange(double[] yChromatogram, double[] noiseChroma, int peakStart, int peakEnd, double[] baseline) {

		double snr = 0.0;
		double maxSignal = 0.0;
		int bestSignal = 0;
		for(int i = peakStart; i < peakEnd; i++) {
			if(yChromatogram[i] > maxSignal) {
				maxSignal = yChromatogram[i];
				bestSignal = i;
			}
		}
		/*
		 * Default S/N => noisevalue by Dyson
		 */
		snr = (double)chromatogram.getSignalToNoiseRatio((float)yChromatogram[bestSignal]);
		return snr;
	}

	/**
	 * 
	 * @param derivativesAndNoise
	 * @param peakRanges
	 * @param yChromatogram
	 * @param noiseChroma
	 * @param baselineSnip
	 * @return
	 */
	private IPeakRanges detectPeakRanges(IDerivativesAndNoise derivativesAndNoise, IPeakRanges peakRanges, double[] yChromatogram, double[] noiseChroma, double[] baselineSnip) {

		int size = derivativesAndNoise.getFirstDerivativeAndNoise().getFirstDeriv().length;
		double[] firstDeriv = derivativesAndNoise.getFirstDerivativeAndNoise().getFirstDeriv();
		double[] noisePositivFirstDeriv = derivativesAndNoise.getFirstDerivativeAndNoise().getNoisePositiv();
		double[] noiseNegativSecondDeriv = derivativesAndNoise.getFirstDerivativeAndNoise().getNoiseNegative();
		PeakRangesStartPoints = new double[size];
		PeakRangesEndPoints = new double[size];
		validStartPoint = true;
		double maxValueFirstDeriv = 0.0;
		int[] peakStartEndMaxPeak = new int[4];
		boolean secDerivCheck = false;
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
			noisePositivAverage = noisePositivAverage + noisePositivFirstDeriv[i];
			noiseNegativAverage = noiseNegativAverage + noiseNegativSecondDeriv[i];
		}
		noisePositivAverage = noisePositivAverage / size;
		noiseNegativAverage = noiseNegativAverage / size;
		for(int i = 1; i < size; i++) {
			peakStartEndMaxPeak[0] = detectPeakStart(firstDeriv, i, noisePositivFirstDeriv, noisePositivAverage);
			if(validStartPoint) {
				peakStartEndMaxPeak = detectPeakEnd(firstDeriv, peakStartEndMaxPeak, noiseNegativSecondDeriv, noiseNegativAverage);
				if(peakStartEndMaxPeak[1] == 0) {
					i = peakStartEndMaxPeak[0] != 0 ? peakStartEndMaxPeak[0] : i++;
				} else {
					i = peakStartEndMaxPeak[1] - ((peakStartEndMaxPeak[1] - peakStartEndMaxPeak[0]) / 2);
					//
					if(checkWithSecondDerive) {
						secDerivCheck = false;
					}
					int peakStart = peakStartEndMaxPeak[3];
					int peakEnd = peakStartEndMaxPeak[1];
					// Signal to NoiseRatio
					double snr = getSignaltoNoiseRatioforPeakRange(yChromatogram, noiseChroma, peakStart, peakEnd, baselineSnip);
					// Add all to Object PeakRanges
					IPeakRange peakRange = new PeakRange(peakStart, peakEnd, snr);
					// First Deriv
					IPeaksDeconv peaksFirst = new PeaksDeconv("FristDerivative");
					IPeakDeconv peakFirst = new PeakDeconv(peakStartEndMaxPeak[2]);
					peaksFirst.addPeak(peakFirst);
					peakRange.addPeaks(peaksFirst);
					/*
					 * Check Range with Second Derivative
					 */
					int[] secondDerivativeCheck = checkPeakRangesWithSecondDerivative(derivativesAndNoise, peakStartEndMaxPeak);
					if(secondDerivativeCheck[0] == 1 && checkWithSecondDerive) {
						// Second Deriv
						IPeaksDeconv peaksSecond = new PeaksDeconv("SecondDerivative");
						for(int z = 1; z < secondDerivativeCheck.length; z++) {
							IPeakDeconv peakSecond = new PeakDeconv(secondDerivativeCheck[z]);
							peaksSecond.addPeak(peakSecond);
						}
						peakRange.addPeaks(peaksSecond);
						secDerivCheck = true;
					}
					// All together
					if(secDerivCheck || !checkWithSecondDerive) {
						peakRanges.addPeakRange(peakRange);
					}
				}
			} else {
				i = peakStartEndMaxPeak[0] + 1;
				validStartPoint = true;
			}
		}
		return peakRanges;
	}

	/**
	 * 
	 * @param firstDeriv
	 * @param startPoint
	 * @param noiseDerivPositiv
	 * @param noisePositivAverage
	 * @return
	 */
	private int detectPeakStart(double[] firstDeriv, int startPoint, double[] noiseDerivPositiv, double noisePositivAverage) {

		int startPeak = 0;
		int i;
		double noise = 0.0f;
		boolean foundStartPeak = false;
		outerloop:
		for(i = startPoint; i < firstDeriv.length - 4; i++) {
			noise = (useAverageNoiseForSmallNoise && Double.compare(noiseDerivPositiv[i], noisePositivAverage) < 0) ? noisePositivAverage : noiseDerivPositiv[i];
			if((Double.compare(firstDeriv[i], noise) > 0)) {
				if(checkRisingOfPeak(firstDeriv, i)) {
					for(int j = i; i > startPoint; j--) {
						if((Double.compare(firstDeriv[j], 0) > 0) && (Double.compare(firstDeriv[j - 1], 0) < 0)) {
							startPeak = j - 1;
							foundStartPeak = true;
							break outerloop;
						}
						if(j == 1) {
							startPeak = j;
							foundStartPeak = true;
							break outerloop;
						}
					}
				} else {
					validStartPoint = false;
					break;
				}
			}
		}
		if(!foundStartPeak) {
			startPeak = i;
		}
		return startPeak;
	}

	/**
	 * 
	 * @param firstDeriv
	 * @param peakStartEndMaxPeak
	 * @param noiseDerivNegativ
	 * @param noiseNegativAverage
	 * @return
	 */
	private int[] detectPeakEnd(double[] firstDeriv, int[] peakStartEndMaxPeak, double[] noiseDerivNegativ, double noiseNegativAverage) {

		int peakStart = peakStartEndMaxPeak[0];
		peakStartEndMaxPeak[1] = 0;
		peakStartEndMaxPeak[2] = 0;
		peakStartEndMaxPeak[3] = peakStart;
		double noise = 0.0f;
		int size = firstDeriv.length;
		int i;
		if(peakStart > 0) {
			outerloop:
			for(i = peakStart + 1; i < size - 1; i++) {
				if((Double.compare(firstDeriv[i], 0) > 0) && (Double.compare(firstDeriv[i + 1], 0) < 0)) {
					for(int x = i + 1; x < size - 4; x++) {
						noise = (useAverageNoiseForSmallNoise && Double.compare(noiseDerivNegativ[x], noiseNegativAverage) > 0) ? noiseNegativAverage : noiseDerivNegativ[x];
						if((Double.compare(firstDeriv[x], noise) < 0) && (Double.compare(firstDeriv[x + 1], noise) > 0)) {
							for(int y = x - 3; y < size - 1; y++) {
								if(((Double.compare(firstDeriv[y], 0) < 0) && (Double.compare(firstDeriv[y + 1], 0) > 0)) || y == size - 2) { // || ((Double.compare(firstDeriv[y], (double)(highestTicSignal / 76) * (-1)) < 0) && (Double.compare(firstDeriv[y + 1], (double)(highestTicSignal / 76) * (-1)) > 0)))) {
									peakStartEndMaxPeak[1] = y + 1;
									peakStartEndMaxPeak[2] = i;
									break outerloop;
								}
							}
							// When its only inside the NoiseIntervall
						} else if((Double.compare(firstDeriv[x], firstDeriv[x + 1]) < 0) && (Double.compare(firstDeriv[x + 1], noise) > 0)) {
							break outerloop;
						}
					}
				}
			}
			peakStartEndMaxPeak[0] = i;
		}
		return peakStartEndMaxPeak;
	}

	/**
	 * 
	 * @param derivativesAndNoise
	 * @param peakStartEndMaxPeak
	 * @return
	 */
	private int[] checkPeakRangesWithSecondDerivative(IDerivativesAndNoise derivativesAndNoise, int[] peakStartEndMaxPeak) {

		int peakRangeStart = peakStartEndMaxPeak[3];
		int peakRangeStop = peakStartEndMaxPeak[1];
		int peakCounter = 0;
		int[] peaksWithSecondDerivHelper = new int[peakRangeStop - peakRangeStart];
		boolean positivZoneFirst = false;
		boolean negativeZone = false;
		boolean positivZoneSecond = false;
		double[] secondDeriv = derivativesAndNoise.getSecondDerivativeAndNoise().getSecondDeriv();
		for(int i = peakRangeStart; i < peakRangeStop - 1; i++) {
			double noiseNegativSecondDeriv = derivativesAndNoise.getSecondDerivativeAndNoise().getNoiseNegative()[i];
			double noisePositivSecondDeriv = derivativesAndNoise.getSecondDerivativeAndNoise().getNoisePositiv()[i];
			// Above noise
			if((Double.compare(secondDeriv[i], noisePositivSecondDeriv) > 0) && !positivZoneFirst && !negativeZone && !positivZoneSecond) {
				positivZoneFirst = true;
			}
			if((Double.compare(secondDeriv[i], noiseNegativSecondDeriv) < 0) && !negativeZone && positivZoneFirst && !positivZoneSecond) {
				negativeZone = true;
			}
			if((Double.compare(secondDeriv[i], noisePositivSecondDeriv) > 0) && !positivZoneSecond && positivZoneFirst && negativeZone) {
				positivZoneSecond = true;
			}
			if(positivZoneFirst && negativeZone) {
				if((Double.compare(secondDeriv[i - 1], secondDeriv[i]) > 0) && (Double.compare(secondDeriv[i], secondDeriv[i + 1]) < 0)) {
					peaksWithSecondDerivHelper[peakCounter++] = i;
				}
			}
		}
		int[] peaksSecondDerivativ = new int[peakCounter + 1];
		peaksSecondDerivativ[0] = (positivZoneFirst && negativeZone && positivZoneSecond) ? 1 : 0;
		for(int i = 1; i < peakCounter + 1; i++) {
			peaksSecondDerivativ[i] = peaksWithSecondDerivHelper[i - 1];
		}
		return peaksSecondDerivativ;
	}

	/**
	 * 
	 * @param firstDeriv
	 * @param currentValue
	 * @return
	 */
	private boolean checkRisingOfPeak(double[] firstDeriv, int currentValue) {

		boolean validPeakRising = false;
		switch(minPeakRising) {
			case 5:
				if(!checkNextValueBiggerthanCurrent(firstDeriv, currentValue, 5)) {
					break;
				}
			case 4:
				if(!checkNextValueBiggerthanCurrent(firstDeriv, currentValue, 4)) {
					break;
				}
			case 3:
				if(!checkNextValueBiggerthanCurrent(firstDeriv, currentValue, 3)) {
					break;
				}
			case 2:
				if(!checkNextValueBiggerthanCurrent(firstDeriv, currentValue, 2)) {
					break;
				}
			case 1:
				if(checkNextValueBiggerthanCurrent(firstDeriv, currentValue, 1)) {
					validPeakRising = true;
				} else {
					break;
				}
			default:
				break;
		}
		return validPeakRising;
	}

	/**
	 * 
	 * @param firstDeriv
	 * @param currentValue
	 * @param checkValue
	 * @return
	 */
	private boolean checkNextValueBiggerthanCurrent(double[] firstDeriv, int currentValue, int checkValue) {

		if(Double.compare(firstDeriv[currentValue + checkValue - 1], firstDeriv[currentValue + checkValue]) < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param ticValues
	 * @return
	 */
	private double[] getNoiseOfTic(double[] ticValues, boolean derivActive, boolean aboveNullDeriv) {

		int size = ticValues.length;
		int segmentSizeFactor = size / quantityNoiseSegments;
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
				noise = segmentNoiseWasNull(segmentNoise) ? (howManySegments == 1) ? 0.0d : finalNoise[i - segmentsize - 1] : getNoiseTicValueByVivoTruyols(segmentNoise, derivActive);
				//
				counter = 0;
				/*
				 * Set the Noise Value by VivoTruyols to finalNoise
				 */
				for(int j = i - segmentsize * howManySegments + 1; j < segmentsize + 1; j++) {
					int index = i + counter - segmentsize;
					finalNoise[index] = derivActive && !aboveNullDeriv ? noise * -1 : noise;
					counter++;
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
				segmentNoise[counter] = derivActive ? getSegmentNoiseForDeriv(ticValues, aboveNullDeriv, i) : ticValues[i];
				counter++;
			}
		}
		/*
		 * Loop for the Rest of TicValues
		 */
		if((size - maxFor - 1) >= 0) {
			for(int x = size - maxFor - 1; x < size - maxFor; x++) {
				segmentNoise[x] = derivActive ? getSegmentNoiseForDeriv(ticValues, aboveNullDeriv, x) : ticValues[x];
			}
			/*
			 * Only for derivatives,
			 * => maybe segmentNoise = 0;
			 * => set Noise to latest Noise Value
			 */
			noise = segmentNoiseWasNull(segmentNoise) ? finalNoise[maxFor - 1] : getNoiseTicValueByVivoTruyols(segmentNoise, derivActive);
			/*
			 * Set the Noise Value by VivoTruyols to finalNoise
			 */
			for(int i = maxFor; i < size; i++) {
				finalNoise[i] = (derivActive && !aboveNullDeriv) ? noise * -1 : noise;
			}
		}
		return finalNoise;
	}

	/**
	 * 
	 * @param ticValues
	 * @param aboveNullDeriv
	 * @param i
	 * @return
	 */
	private double getSegmentNoiseForDeriv(double[] ticValues, boolean aboveNullDeriv, int i) {

		double segmentNoise;
		if(aboveNullDeriv) {
			segmentNoise = ticValues[i] > 0 ? ticValues[i] : 0.0;
		} else {
			segmentNoise = ticValues[i] < 0 ? ticValues[Math.abs(i)] : 0.0;
		}
		return segmentNoise;
	}

	/**
	 * 
	 * @param segmentNoise
	 * @return
	 */
	private boolean segmentNoiseWasNull(double[] segmentNoise) {

		for(int j = 0; j < segmentNoise.length; j++) {
			if(segmentNoise[j] > 0.0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param ticValues
	 * @return
	 */
	private double getNoiseTicValueByVivoTruyols(double[] ticValues, boolean derivative) {

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
		if(derivative) {
			return Noiseh1 * factorNoiseDerivative;
		} else {
			return Noiseh1 * factorNoiseOriginal;
		}
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
}
// private IMarkedIons CodaFilter(IExtractedIonSignals extractedIonSignals) {
//
// IMarkedIons excludedIons = new MarkedIons();
// List<Float> mcqs = new ArrayList<Float>();
// float mcq;
// int startIon = extractedIonSignals.getStartIon();
// int stopIon = extractedIonSignals.getStopIon();
// for(int ion = startIon; ion <= stopIon; ion++) {
// mcq = CodaCalculator.getMCQValue(extractedIonSignals, MOVING_AVERAGE_WINDOW, ion);
// /*
// * Add the ion value to the excluded ion list?
// */
// if(mcq < codaThreshold) {
// excludedIons.add(new MarkedIon(ion));
// }
// /*
// * Use mcqs to determine the data reduction value.
// */
// mcqs.add(mcq);
// }
// return excludedIons;
// }
