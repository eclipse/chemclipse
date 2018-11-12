/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.AbstractPeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.DerivativesAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.FirstDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.IDerivativesAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.IFirstDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.ISecondDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.IThirdDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.SecondDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives.ThirdDerivativeAndNoise;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.AllIonSignals;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IAllIonSignals;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IIonSignal;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IIonSignals;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IonSignal;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IonSignals;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakModelForDeconvolution.IPeakModelDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakModelForDeconvolution.IPeakModelDeconvIon;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakModelForDeconvolution.PeakModelDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakModelForDeconvolution.PeakModelDeconvIon;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeakDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeakRange;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeakRanges;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.IPeaksDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeakDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeakRange;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeakRanges;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakRanges.PeaksDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.notifier.DeconvNotifier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.ArrayView;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.ArraysViewDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.DeconvHelper;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArrayView;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IDeconvHelper;
import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.RawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.processor.DurbinWatsonProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.DurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IDurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.settings.ClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.calculator.SnipCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.AnalysisSupport;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakDetector extends AbstractPeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetector.class);
	IChromatogramMSD chromatogram;
	private IArraysViewDeconv arraysViewDeconv;
	IDeconvHelper deconvHelper = new DeconvHelper();
	IDerivativesAndNoise derivativesAndNoise;
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
	 * NoiseFactor by Vivo-Truyols modified for each Derivative other Factor
	 */
	private int factorNoiseOriginal = 5;
	private int factorNoiseFirstDerive = 2;
	private int factorNoiseSecondDerive = 2;
	private int factorNoiseThirdDerive = 10;
	// Internal setups
	private boolean giveOutput = false;
	private boolean useAverageNoiseForSmallNoise = false;
	private boolean seePeakRanges = true;
	private boolean checkWithSecondDerive = true;
	private boolean checkWithThirdDerivative = true;
	/*
	 * Set by User
	 */
	private int minPeakRising;
	private int minPeakWidth;
	private double minSignalToNoiseRatio;
	private int baselineIterations;
	private int sensitivityOfDeconvolution;
	// Noise
	private int quantityNoiseSegments;

	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorSettingsMSD peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addMessages(validate(chromatogramSelection, peakDetectorSettings, monitor));
		if(!processingInfo.hasErrorMessages()) {
			ChromatogramFilterSettings filterSettings = new ChromatogramFilterSettings();
			IDurbinWatsonClassifierResult durbinWatsonClassifierResult = new DurbinWatsonClassifierResult(ResultStatus.OK, "Test");
			setMinimumSignalToNoise(peakDetectorSettings);
			setMinimumPeakWidth(peakDetectorSettings);
			setMinimumPeakRising(peakDetectorSettings);
			setBaselineIterations(peakDetectorSettings);
			setQuantityNoiseSegments(peakDetectorSettings);
			setSensitivityOfDeconvolution(peakDetectorSettings);
			deconv(chromatogramSelection, durbinWatsonClassifierResult, filterSettings, monitor);
			processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, "Peak Detector Deconvolution", "Peaks have been detected successfully."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		return null;
	}

	private void setMinimumSignalToNoise(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof PeakDetectorSettings) {
			PeakDetectorSettings deconvolutionPeakDetectorSettings = (PeakDetectorSettings)peakDetectorSettings;
			this.minSignalToNoiseRatio = deconvolutionPeakDetectorSettings.getMinimumSignalToNoiseRatio();
		}
	}

	private void setSensitivityOfDeconvolution(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof PeakDetectorSettings) {
			PeakDetectorSettings deconvolutionPeakDetectorSettings = (PeakDetectorSettings)peakDetectorSettings;
			this.sensitivityOfDeconvolution = deconvolutionPeakDetectorSettings.getSensitivityOfDeconvolution();
		}
	}

	private void setMinimumPeakWidth(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof PeakDetectorSettings) {
			PeakDetectorSettings deconvolutionPeakDetectorSettings = (PeakDetectorSettings)peakDetectorSettings;
			this.minPeakWidth = deconvolutionPeakDetectorSettings.getMinimumPeakWidth();
		}
	}

	private void setMinimumPeakRising(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof PeakDetectorSettings) {
			PeakDetectorSettings deconvolutionPeakDetectorSettings = (PeakDetectorSettings)peakDetectorSettings;
			this.minPeakRising = deconvolutionPeakDetectorSettings.getMinimumPeakRising();
		}
	}

	private void setBaselineIterations(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof PeakDetectorSettings) {
			PeakDetectorSettings deconvolutionPeakDetectorSettings = (PeakDetectorSettings)peakDetectorSettings;
			this.baselineIterations = deconvolutionPeakDetectorSettings.getBaselineIterations();
		}
	}

	private void setQuantityNoiseSegments(IPeakDetectorSettings peakDetectorSettings) {

		if(peakDetectorSettings instanceof PeakDetectorSettings) {
			PeakDetectorSettings deconvolutionPeakDetectorSettings = (PeakDetectorSettings)peakDetectorSettings;
			this.quantityNoiseSegments = deconvolutionPeakDetectorSettings.getQuantityNoiseSegments();
		}
	}

	private void deconv(IChromatogramSelectionMSD chromatogramSelection, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, ChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		chromatogram = chromatogramSelection.getChromatogramMSD();
		try {
			/*
			 * Send Output to File
			 */
			if(giveOutput) {
				File file = new File("/Users/fe22st/Desktop/java.log"); // "/Users/fe22st/Desktop/java.log"
				try {
					System.setOut(new PrintStream(new FileOutputStream(file, true)));
				} catch(FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			/*
			 * Set AllIonSignals with ExtractedIonSignal and TotalIonSignal
			 */
			IAllIonSignals allIonSignals = getAllIonSignals(chromatogramSelection, monitor);
			//
			chromatogram.removeAllPeaks();
			/*
			 * Durbin Watson
			 */
			DurbinWatsonRatings(allIonSignals.getIonSignals(0).getIonSignals(), null, durbinWatsonClassifierResult, monitor);
			/*
			 * Smoothed Values
			 */
			double[] smoothedValues = savitzkyGolaySmooth(noDerivative, allIonSignals.getIonSignals(0).getIonSignals(), filterSettings, durbinWatsonClassifierResult, monitor);
			double[] smoothedValues2 = savitzkyGolaySmooth(firstDerivative, allIonSignals.getIonSignals(0).getIonSignals(), filterSettings, durbinWatsonClassifierResult, monitor);
			/*
			 * PeakRanges and set up for the view
			 */
			IPeakRanges peakRanges = getPeakRangesTicSignal(allIonSignals, filterSettings, durbinWatsonClassifierResult, monitor);
			/*
			 * Get excludedIons, but its for the ionsignal output, which segment are accepted from stein
			 */
			IMarkedIons excludedIons = getSystemOutputAndExcludedIons(allIonSignals);
			// removeIonsFromChromatogram(excludedIons, chromatogramSelection);
			/*
			 * Set Peaks
			 */
			setPeaksToChromatogram(peakRanges);
			/*
			 * ueber alle Ionen welchen im PeakRange liegen, druebergehen, und anschauen ob diese Peaks haben koennen
			 */
			// IPeakRanges peaksDeconv = getPeaksFromDeconvolution(allIonSignals, peakRanges, supplierFilterSettings, durbinWatsonClassifierResult, monitor);
			setPeaksFromDeconvolution(allIonSignals, peakRanges, filterSettings, durbinWatsonClassifierResult, monitor);
			/*
			 * Output
			 */
			;
			ITotalIonSignalExtractor totalIonSignalExtractor = new TotalIonSignalExtractor(chromatogram);
			ITotalScanSignals totalIONsignals = totalIonSignalExtractor.getTotalIonSignals(chromatogramSelection);
			// fillArraysViewDeconv(xScales, smoothedValues, null, null, derivativesAndNoise.getSecondDerivativeAndNoise().getSecondDeriv(), derivativesAndNoise.getFirstDerivativeAndNoise().getFirstDeriv(), derivativesAndNoise.getSecondDerivativeAndNoise().getNoisePositiv(), derivativesAndNoise.getSecondDerivativeAndNoise().getNoiseNegative(), PeakRangesStartPoints, PeakRangesEndPoints);
			// fillArraysViewDeconv(xScales, smoothedValues, null, null, derivativesAndNoise.getFirstDerivativeAndNoise().getNoisePositiv(), derivativesAndNoise.getFirstDerivativeAndNoise().getFirstDeriv(), derivativesAndNoise.getFirstDerivativeAndNoise().getNoiseNegative(), null, PeakRangesStartPoints, PeakRangesEndPoints);
			// fillArraysViewDeconv(xScales, smoothedValues, null, null, null, null, null, null, null, null);
			int numberOfIon = 0;
			boolean ionNumber = false;
			//
			int factorSizeNormal = 1;
			int factorSizeFirstDeriv = 1;
			int factorSizeSecondDeriv = 1;
			//
			int numberOfIonInList = 0;
			double[] ionSignal = null;
			if(ionNumber) {
				for(int i = 1; i < allIonSignals.getIonSignals().size(); i++) {
					if(allIonSignals.getIonSignals(i).getIon() == numberOfIon) {
						ionSignal = allIonSignals.getIonSignals(i).getIonSignals();
						numberOfIonInList = i;
						break;
					}
				}
			} else {
				numberOfIonInList = numberOfIon;
				ionSignal = allIonSignals.getIonSignals(numberOfIonInList).getIonSignals();
			}
			DurbinWatsonRatings(ionSignal, null, durbinWatsonClassifierResult, monitor);
			double[] smoothedIonSignal = savitzkyGolaySmooth(noDerivative, ionSignal, filterSettings, durbinWatsonClassifierResult, monitor);
			double[] signal = allIonSignals.getIonSignals(numberOfIonInList).getIonSignals();
			double[] firstDerivIon2 = savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(firstDerivative, signal, filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor);
			double[] firstDerivIon2smoother = savitzkyGolaySmooth(noDerivative, firstDerivIon2, filterSettings, durbinWatsonClassifierResult, monitor);
			double[] secondDerivIon2 = savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(this.firstDerivative, savitzkyGolaySmooth(this.firstDerivative, signal, filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor);
			double[] secondDerivIon2smoother = savitzkyGolaySmooth(noDerivative, secondDerivIon2, filterSettings, durbinWatsonClassifierResult, monitor);
			double[] noiseFirstDeriv = getNoiseOfTic(signal, 0, false);
			//
			int numberOfIon2 = 168;
			boolean ionNumber2 = true;
			//
			int factorSizeNormal2 = 1;
			//
			int numberOfIonInList2 = 0;
			double[] ionSignal2 = null;
			if(ionNumber2) {
				for(int i = 1; i < allIonSignals.getIonSignals().size(); i++) {
					if(allIonSignals.getIonSignals(i).getIon() == numberOfIon2) {
						ionSignal2 = allIonSignals.getIonSignals(i).getIonSignals();
						numberOfIonInList2 = i;
						break;
					}
				}
			} else {
				numberOfIonInList = numberOfIon2;
				ionSignal2 = allIonSignals.getIonSignals(numberOfIonInList).getIonSignals();
			}
			DurbinWatsonRatings(ionSignal2, null, durbinWatsonClassifierResult, monitor);
			double[] smoothedIonSignal2 = savitzkyGolaySmooth(noDerivative, ionSignal, filterSettings, durbinWatsonClassifierResult, monitor);
			double[] signal2 = allIonSignals.getIonSignals(numberOfIonInList2).getIonSignals();
			fillArraysViewDeconv(deconvHelper.setXValueforPrint(totalIONsignals), smoothedValues, deconvHelper.factorisingValues(signal2, factorSizeNormal2), null, null, null, deconvHelper.factorisingValues(signal, factorSizeNormal), null, null, null);
			/*
			 * fillArraysViewDeconv(deconvHelper.setXValueforPrint(totalIONsignals), smoothedValues, deconvHelper.factorisingValues(signal2, factorSizeNormal2), null, deconvHelper.factorisingValues(firstDerivIon2, factorSizeFirstDeriv), null, deconvHelper.factorisingValues(signal, factorSizeNormal), deconvHelper.factorisingValues(smoothedIonSignal, factorSizeNormal), PeakRangesStartPoints, PeakRangesEndPoints);
			 */
			System.out.println("Deconv ist durchgelaufen");
			System.out.println("--------------------------------------------");
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
	}

	private IPeakRanges getPeakRangesTicSignal(IAllIonSignals allIonSignals, ChromatogramFilterSettings filterSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		IPeakRanges peakRanges = new PeakRanges(allIonSignals);
		double[] yChromatogram = allIonSignals.getIonSignals(0).getIonSignals();
		derivativesAndNoise = setDerivatives(yChromatogram, filterSettings, durbinWatsonClassifierResult, monitor);
		/*
		 * SNIP
		 */
		double[] baselineValues = deconvHelper.getDoubleArray(calculateSNIPBaseline(deconvHelper.getFloatArray(yChromatogram), baselineIterations, monitor));
		peakRanges = detectPeakRanges(derivativesAndNoise, peakRanges, yChromatogram, getNoiseOfTic(yChromatogram, 0, false), baselineValues);
		peakRanges = checkPeakRangesWithUserParameter(peakRanges);
		return peakRanges;
	}

	/*
	 * 
	 */
	private IMarkedIons getSystemOutputAndExcludedIons(IAllIonSignals allIonSignals) {

		IMarkedIons excludedIons = new MarkedIons();
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(giveOutput) {
				System.out.print("Ion " + ionSignal.getIon() + " SteinYes: " + ionSignal.getCounterSteinAccepted() + " SteinNo: " + ionSignal.getCounterSteinDenied() + " ");
			}
			for(IIonSignal segmentSignal : ionSignal.getSegmentSignals()) {
				if(Double.compare(segmentSignal.getSignal(), 0.0) > 0.0) {
					excludedIons.add(new MarkedIon(ionSignal.getIon()));
				}
				if(giveOutput) {
					System.out.print((int)segmentSignal.getSignal() + " ");
				}
			}
			if(giveOutput) {
				System.out.println("\n");
			}
		}
		return excludedIons;
	}

	/*
	 * Remove excluded Ions from Chromatogram
	 */
	private void removeIonsFromChromatogram(IMarkedIons excludedIons, IChromatogramSelectionMSD chromatogramSelection) {

		IVendorMassSpectrum supplierMassSpectrum;
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		for(int scan = startScan; scan <= stopScan; scan++) {
			supplierMassSpectrum = chromatogram.getSupplierScan(scan);
			supplierMassSpectrum.removeIons(excludedIons);
		}
	}

	private void setPeaksFromDeconvolution(IAllIonSignals allIonSignals, IPeakRanges peakRanges, ChromatogramFilterSettings filterSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		boolean doingAll = false;
		boolean foundPeakModel = false;
		boolean ionInModel = false;
		@SuppressWarnings("unused")
		int rTOfPeak = 0;
		//
		//
		if(giveOutput) {
			System.out.println("------------");
			System.out.println("deconv part");
			System.out.println("");
		}
		int counterPeaksInPeak = 0, counterPeak = 0, counterIonInList = 0;
		List<IChromatogramPeakMSD> listAllPeaksFromPeakDetection = chromatogram.getPeaks();
		List<IChromatogramPeakMSD> listPeaksDeconvolution = new ArrayList<IChromatogramPeakMSD>();
		List<List<IPeakModelDeconv>> allModelPeaks = new ArrayList<List<IPeakModelDeconv>>();
		//
		for(IChromatogramPeakMSD peakFromPeakDetection : listAllPeaksFromPeakDetection) {
			List<IPeakModelDeconv> listModelPeaks = new ArrayList<IPeakModelDeconv>();
			if(giveOutput) {
				System.out.println("----------------------------------------------");
				System.out.println("Peak: " + counterPeak + " TIC-Signal peak: ScanMax: " + peakFromPeakDetection.getScanMax() + " Peak Abundance: " + peakFromPeakDetection.getPeakModel().getPeakAbundance());
			}
			IMarkedIons excludedIons = new MarkedIons();
			IScanRange scanRange = new ScanRange(peakRanges.getPeakRange(counterPeak).getPeakStartPoint() + peakRanges.getStartScan(), peakRanges.getPeakRange(counterPeak).getPeakEndPoint() + peakRanges.getStartScan());
			List<IIon> listIons = getCopyOfIonListFromPeak(peakFromPeakDetection);
			excludedIons = getExcludedIons(scanRange);
			if(excludedIons != null) {
				for(int i = 0; i < listIons.size(); i++) {
					excludedIons.remove(new MarkedIon(listIons.get(i).getIon()));
					IChromatogramPeakMSD peak = getModel(scanRange, excludedIons);
					if(peak != null) {
						rTOfPeak = peak.getExtractedMassSpectrum().getRetentionTime();
						if(giveOutput) {
							System.out.println("Ion: " + peak.getExtractedMassSpectrum().getHighestIon().getIon() + " RetentionTime: " + rTOfPeak + " : HighestAbundance: " + peak.getExtractedMassSpectrum().getHighestAbundance().getAbundance() + "  : ScanMax: " + peak.getScanMax() + " ::Peak Abundance: " + peak.getPeakModel().getPeakAbundance() + " ::RT at Peak Max: " + peak.getPeakModel().getRetentionTimeAtPeakMaximum() + " ::RT by InflectionPoint: " + peak.getPeakModel().getRetentionTimeAtPeakMaximumByInflectionPoints());
							peak.getExtractedMassSpectrum().getHighestAbundance().getAbundance();
						}
						if(listModelPeaks.size() == 0) {
							float ticSignalAbundanceMax = listAllPeaksFromPeakDetection.get(counterPeak).getPeakModel().getPeakAbundance();
							IPeakModelDeconv peakModel = new PeakModelDeconv(peak, scanRange, ticSignalAbundanceMax);
							listModelPeaks.add(peakModel);
						} else if(listModelPeaks.size() > 0) {
							int rT = peak.getExtractedMassSpectrum().getRetentionTime();
							for(int counterModelPeak = 0; counterModelPeak < listModelPeaks.size(); counterModelPeak++) {
								if(rT == listModelPeaks.get(counterModelPeak).getModelRetentionTime()) {
									IPeakModelDeconvIon deconvIon = new PeakModelDeconvIon(peak);
									listModelPeaks.get(counterModelPeak).addIonToDeconvModel(deconvIon);
									ionInModel = true;
									break;
								}
							}
							if(!ionInModel) {
								float ticSignalAbundanceMax = listAllPeaksFromPeakDetection.get(counterPeak).getPeakModel().getPeakAbundance();
								IPeakModelDeconv peakModel = new PeakModelDeconv(peak, scanRange, ticSignalAbundanceMax);
								listModelPeaks.add(peakModel);
							}
						}
					}
					/*
					 * 
					 */
					ionInModel = false;
					excludedIons.add(new MarkedIon(listIons.get(i).getIon()));
					counterIonInList++;
				}
				if(counterPeaksInPeak == 1) {
					listPeaksDeconvolution.add(peakFromPeakDetection);
				}
			}
			allModelPeaks.add(listModelPeaks);
			counterIonInList = 0;
			counterPeak++;
			if(giveOutput) {
				System.out.println("");
			}
		}
		setPeaksToChromatogramDeconvolution(allModelPeaks, listAllPeaksFromPeakDetection);
	}

	private void setPeaksToChromatogramDeconvolution(List<List<IPeakModelDeconv>> allModelPeaks, List<IChromatogramPeakMSD> listAllPeaksFromPeakDetection) {

		List<IChromatogramPeakMSD> listOldPeaks = new ArrayList<IChromatogramPeakMSD>();
		for(IChromatogramPeakMSD peakMSD : listAllPeaksFromPeakDetection) {
			listOldPeaks.add(peakMSD);
		}
		for(List<IPeakModelDeconv> listModelPeaks : allModelPeaks) {
			for(IPeakModelDeconv modelPeaks : listModelPeaks) {
				IMarkedIons excludedIons = new MarkedIons();
				excludedIons = getExcludedIons(modelPeaks.getScanRange());
				for(IPeakModelDeconvIon ionsInModelPeak : modelPeaks.getAllIonsInModel()) {
					excludedIons.remove(new MarkedIon(ionsInModelPeak.getIon()));
				}
				excludedIons.remove(new MarkedIon(modelPeaks.getModelIon()));
				//
				//
				IChromatogramPeakMSD peak;
				try {
					peak = PeakBuilderMSD.createPeak(chromatogram, modelPeaks.getScanRange(), excludedIons);
					double prozentOfModelPeakToTic = 1 / (modelPeaks.getTicAbundanceMax() / peak.getPeakModel().getPeakAbundance());
					if(Double.compare(prozentOfModelPeakToTic, 0.2) > 0) {
						chromatogram.addPeak(peak);
					}
				} catch(PeakException e) {
					logger.warn(e);
				}
				//
			}
		}
		chromatogram.removePeaks(listOldPeaks);
	}

	private IMarkedIons getExcludedIons(IScanRange scanRange) {

		IMarkedIons excludedIons = new MarkedIons();
		try {
			IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(scanRange.getStartScan(), scanRange.getStopScan());
			for(int i = extractedIonSignals.getStartIon(); i <= extractedIonSignals.getStopIon(); i++) {
				excludedIons.add(new MarkedIon(i));
			}
			return excludedIons;
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		return null;
	}

	private IChromatogramPeakMSD getModel(IScanRange scanRange, IMarkedIons excludedIons) {

		try {
			IChromatogramPeakMSD peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, excludedIons);
			return peak;
		} catch(PeakException e) {
			// logger.warn(e);
		}
		return null;
	}

	private List<IIon> getCopyOfIonListFromPeak(IChromatogramPeakMSD peakFromPeakDetection) {

		List<IIon> listIons = peakFromPeakDetection.getExtractedMassSpectrum().getIons();
		List<IIon> listIonsCopy = new ArrayList<IIon>();
		for(int i = 0; i < listIons.size(); i++) {
			listIonsCopy.add(listIons.get(i));
		}
		// Sort of a list of IIon
		// listIons.sort(new IonValueComparator());
		listIonsCopy.sort(new IonAbundanceComparator(SortOrder.DESC));
		return listIonsCopy;
	}

	/*
	 * Testing different peak models
	 */
	private IPeakRanges getPeaksFromDeconvolution(IAllIonSignals allIonSignals, IPeakRanges peakRanges, ChromatogramFilterSettings filterSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		List<IChromatogramPeakMSD> peakList = chromatogram.getPeaks();
		IChromatogramPeakMSD peak, peak2, peak3, peak4, peak5, peak6 = null;
		int signalSegmentWidth = quantityNoiseSegments;
		IPeakRanges peakRangesDeconv = peakRanges;
		IScanRange scanRange, scanRange137, scanRange167, scanRangePeak2_1,
				scanRangePeak2_2 = null;
		scanRange = new ScanRange(peakRanges.getPeakRange(1).getPeakStartPoint() + peakRanges.getStartScan(), peakRanges.getPeakRange(1).getPeakEndPoint() + peakRanges.getStartScan());
		scanRangePeak2_1 = new ScanRange(peakRanges.getPeakRange(2).getPeakStartPoint() + peakRanges.getStartScan() + 2, peakRanges.getPeakRange(2).getPeakEndPoint() + peakRanges.getStartScan() - 7);
		scanRangePeak2_2 = new ScanRange(peakRanges.getPeakRange(2).getPeakStartPoint() + peakRanges.getStartScan(), peakRanges.getPeakRange(2).getPeakEndPoint() + peakRanges.getStartScan() + 4);
		peak = peakList.get(1);
		float tailing = peak.getPeakModel().getTailing();
		float leading = peak.getPeakModel().getLeading();
		List<IIon> listIons = peak.getExtractedMassSpectrum().getIons();
		IIon highestIon = peak.getExtractedMassSpectrum().getHighestAbundance();
		// peak.getExtractedMassSpectrum().removeIon(highestIon);
		IIon secondhighestIon = peak.getExtractedMassSpectrum().getHighestAbundance();
		float test = peak.getExtractedMassSpectrum().getTotalSignal();
		float tailing2 = peak.getPeakModel().getTailing();
		float leading2 = peak.getPeakModel().getLeading();
		if(listIons.size() > 71) {
			IIon Iontest = listIons.get(71);
			peak.getExtractedMassSpectrum().removeAllIons();
			peak.getExtractedMassSpectrum().addIon(Iontest);
		}
		//
		IMarkedIons excludedIons = new MarkedIons();
		IMarkedIons excludedIons2 = new MarkedIons();
		IMarkedIons excludedIons3 = new MarkedIons();
		IMarkedIons excludedIons4 = new MarkedIons();
		IMarkedIons excludedIons5 = new MarkedIons();
		IMarkedIons excludedIonsPeak2_1 = new MarkedIons();
		IMarkedIons excludedIonsPeak2_2 = new MarkedIons();
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(ionSignal.getIon() == 137 || ionSignal.getIon() == 15 || ionSignal.getIon() == 26 || ionSignal.getIon() == 27 || ionSignal.getIon() == 29 || ionSignal.getIon() == 31 || ionSignal.getIon() == 37 || ionSignal.getIon() == 38 || ionSignal.getIon() == 39 || ionSignal.getIon() == 40 || ionSignal.getIon() == 41 || ionSignal.getIon() == 42 || ionSignal.getIon() == 49 || ionSignal.getIon() == 50 || ionSignal.getIon() == 51 || ionSignal.getIon() == 52 || ionSignal.getIon() == 53 || ionSignal.getIon() == 55 || ionSignal.getIon() == 62 || ionSignal.getIon() == 63 || ionSignal.getIon() == 64 || ionSignal.getIon() == 65 || ionSignal.getIon() == 66 || ionSignal.getIon() == 67 || ionSignal.getIon() == 68 || ionSignal.getIon() == 69 || ionSignal.getIon() == 75 || ionSignal.getIon() == 76 || ionSignal.getIon() == 77 || ionSignal.getIon() == 78 || ionSignal.getIon() == 79 || ionSignal.getIon() == 80 || ionSignal.getIon() == 81 || ionSignal.getIon() == 82 || ionSignal.getIon() == 86 || ionSignal.getIon() == 91 || ionSignal.getIon() == 92 || ionSignal.getIon() == 93 || ionSignal.getIon() == 94 || ionSignal.getIon() == 95 || ionSignal.getIon() == 105 || ionSignal.getIon() == 106 || ionSignal.getIon() == 107 || ionSignal.getIon() == 108 || ionSignal.getIon() == 109 || ionSignal.getIon() == 119 || ionSignal.getIon() == 121 || ionSignal.getIon() == 123 || ionSignal.getIon() == 124 || ionSignal.getIon() == 125 || ionSignal.getIon() == 134 || ionSignal.getIon() == 135 || ionSignal.getIon() == 138 || ionSignal.getIon() == 139 || ionSignal.getIon() == 140 || ionSignal.getIon() == 145 || ionSignal.getIon() == 147 || ionSignal.getIon() == 166 || ionSignal.getIon() == 168 || ionSignal.getIon() == 181 || ionSignal.getIon() == 183) {
			} else {
				excludedIons.add(new MarkedIon(ionSignal.getIon()));
			}
		}
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(ionSignal.getIon() == 137 || ionSignal.getIon() == 167 || ionSignal.getIon() == 15 || ionSignal.getIon() == 26 || ionSignal.getIon() == 27 || ionSignal.getIon() == 29 || ionSignal.getIon() == 31 || ionSignal.getIon() == 37 || ionSignal.getIon() == 38 || ionSignal.getIon() == 39 || ionSignal.getIon() == 40 || ionSignal.getIon() == 41 || ionSignal.getIon() == 42 || ionSignal.getIon() == 49 || ionSignal.getIon() == 50 || ionSignal.getIon() == 51 || ionSignal.getIon() == 52 || ionSignal.getIon() == 53 || ionSignal.getIon() == 55 || ionSignal.getIon() == 62 || ionSignal.getIon() == 63 || ionSignal.getIon() == 64 || ionSignal.getIon() == 65 || ionSignal.getIon() == 66 || ionSignal.getIon() == 67 || ionSignal.getIon() == 68 || ionSignal.getIon() == 69 || ionSignal.getIon() == 75 || ionSignal.getIon() == 76 || ionSignal.getIon() == 77 || ionSignal.getIon() == 78 || ionSignal.getIon() == 79 || ionSignal.getIon() == 80 || ionSignal.getIon() == 81 || ionSignal.getIon() == 82 || ionSignal.getIon() == 86 || ionSignal.getIon() == 91 || ionSignal.getIon() == 92 || ionSignal.getIon() == 93 || ionSignal.getIon() == 94 || ionSignal.getIon() == 95 || ionSignal.getIon() == 105 || ionSignal.getIon() == 106 || ionSignal.getIon() == 107 || ionSignal.getIon() == 108 || ionSignal.getIon() == 109 || ionSignal.getIon() == 119 || ionSignal.getIon() == 121 || ionSignal.getIon() == 123 || ionSignal.getIon() == 124 || ionSignal.getIon() == 125 || ionSignal.getIon() == 134 || ionSignal.getIon() == 135 || ionSignal.getIon() == 138 || ionSignal.getIon() == 139 || ionSignal.getIon() == 140 || ionSignal.getIon() == 145 || ionSignal.getIon() == 147 || ionSignal.getIon() == 166 || ionSignal.getIon() == 168 || ionSignal.getIon() == 181 || ionSignal.getIon() == 183) {
			} else {
				excludedIons5.add(new MarkedIon(ionSignal.getIon()));
			}
		}
		double[] signal137 = allIonSignals.getIonSignals(85).getIonSignals();
		double[] noise137 = getNoiseOfTic(signal137, 0, false);
		@SuppressWarnings("unused")
		int start137 = 0, end137 = 0;
		boolean start = false;
		for(int i = peakRanges.getPeakRange(1).getPeakStartPoint(); i <= peakRanges.getPeakRange(1).getPeakEndPoint(); i++) {
			if(Double.compare(signal137[i], noise137[i]) > 0 && !start) {
				start137 = i;
				start = true;
			}
			if(Double.compare(signal137[i], noise137[i]) < 0 && start) {
				end137 = i;
				start = false;
			}
		}
		scanRange137 = new ScanRange(peakRanges.getPeakRange(1).getPeakStartPoint() + peakRanges.getStartScan() + 1, peakRanges.getPeakRange(1).getPeakEndPoint() + peakRanges.getStartScan());
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(ionSignal.getIon() == 167 || ionSignal.getIon() == 15) {
			} else {
				excludedIons2.add(new MarkedIon(ionSignal.getIon()));
			}
		}
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(ionSignal.getIon() == 168) {
			} else {
				excludedIons3.add(new MarkedIon(ionSignal.getIon()));
			}
		}
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(ionSignal.getIon() == 167 || ionSignal.getIon() == 15 || ionSignal.getIon() == 27 || ionSignal.getIon() == 31 || ionSignal.getIon() == 37 || ionSignal.getIon() == 38 || ionSignal.getIon() == 39 || ionSignal.getIon() == 41 || ionSignal.getIon() == 50 || ionSignal.getIon() == 51 || ionSignal.getIon() == 52 || ionSignal.getIon() == 53 || ionSignal.getIon() == 55 || ionSignal.getIon() == 58 || ionSignal.getIon() == 63 || ionSignal.getIon() == 64 || ionSignal.getIon() == 65 || ionSignal.getIon() == 66 || ionSignal.getIon() == 67 || ionSignal.getIon() == 68 || ionSignal.getIon() == 69 || ionSignal.getIon() == 70 || ionSignal.getIon() == 75 || ionSignal.getIon() == 76 || ionSignal.getIon() == 77 || ionSignal.getIon() == 78 || ionSignal.getIon() == 79 || ionSignal.getIon() == 80 || ionSignal.getIon() == 81 || ionSignal.getIon() == 87 || ionSignal.getIon() == 91 || ionSignal.getIon() == 92 || ionSignal.getIon() == 93 || ionSignal.getIon() == 95 || ionSignal.getIon() == 105 || ionSignal.getIon() == 106 || ionSignal.getIon() == 107 || ionSignal.getIon() == 108 || ionSignal.getIon() == 109 || ionSignal.getIon() == 117 || ionSignal.getIon() == 119 || ionSignal.getIon() == 121 || ionSignal.getIon() == 123 || ionSignal.getIon() == 124 || ionSignal.getIon() == 125 || ionSignal.getIon() == 131 || ionSignal.getIon() == 134 || ionSignal.getIon() == 135 || ionSignal.getIon() == 139 || ionSignal.getIon() == 140 || ionSignal.getIon() == 154 || ionSignal.getIon() == 164 || ionSignal.getIon() == 165 || ionSignal.getIon() == 168 || ionSignal.getIon() == 169 || ionSignal.getIon() == 181 || ionSignal.getIon() == 183 || ionSignal.getIon() == 184 || ionSignal.getIon() == 194) {
			} else {
				excludedIons4.add(new MarkedIon(ionSignal.getIon()));
			}
		}
		double[] signal167 = allIonSignals.getIonSignals(106).getIonSignals();
		double[] noise167 = getNoiseOfTic(signal167, 0, false);
		@SuppressWarnings("unused")
		int start167 = 0, end167 = 0;
		for(int i = peakRanges.getPeakRange(1).getPeakStartPoint(); i <= peakRanges.getPeakRange(1).getPeakEndPoint(); i++) {
			if(Double.compare(signal167[i], noise167[i]) > 0 && !start) {
				start167 = i;
				start = true;
			}
			if(Double.compare(signal167[i], noise167[i]) < 0 && start) {
				end167 = i;
				start = false;
			}
		}
		scanRange167 = new ScanRange(peakRanges.getPeakRange(1).getPeakStartPoint() + peakRanges.getStartScan() + 3, peakRanges.getPeakRange(1).getPeakEndPoint() + peakRanges.getStartScan() - 2);
		/*
		 * 
		 * 
		 */
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(ionSignal.getIon() == 37 || ionSignal.getIon() == 50 || ionSignal.getIon() == 51 || ionSignal.getIon() == 62 || ionSignal.getIon() == 64 || ionSignal.getIon() == 65 || ionSignal.getIon() == 68 || ionSignal.getIon() == 76 || ionSignal.getIon() == 77 || ionSignal.getIon() == 78 || ionSignal.getIon() == 80 || ionSignal.getIon() == 91 || ionSignal.getIon() == 92 || ionSignal.getIon() == 93 || ionSignal.getIon() == 103 || ionSignal.getIon() == 105 || ionSignal.getIon() == 106 || ionSignal.getIon() == 107 || ionSignal.getIon() == 108 || ionSignal.getIon() == 115 || ionSignal.getIon() == 117 || ionSignal.getIon() == 119 || ionSignal.getIon() == 121 || ionSignal.getIon() == 124 || ionSignal.getIon() == 125 || ionSignal.getIon() == 130 || ionSignal.getIon() == 131 || ionSignal.getIon() == 132 || ionSignal.getIon() == 133 || ionSignal.getIon() == 135 || ionSignal.getIon() == 137 || ionSignal.getIon() == 138 || ionSignal.getIon() == 144 || ionSignal.getIon() == 145 || ionSignal.getIon() == 147 || ionSignal.getIon() == 161 || ionSignal.getIon() == 163 || ionSignal.getIon() == 164 || ionSignal.getIon() == 176 || ionSignal.getIon() == 177 || ionSignal.getIon() == 178 || ionSignal.getIon() == 179 || ionSignal.getIon() == 180 || ionSignal.getIon() == 263) {
			} else {
				excludedIonsPeak2_1.add(new MarkedIon(ionSignal.getIon()));
			}
		}
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			if(ionSignal.getIon() == 19 || ionSignal.getIon() == 26 || ionSignal.getIon() == 27 || ionSignal.getIon() == 29 || ionSignal.getIon() == 30 || ionSignal.getIon() == 31 || ionSignal.getIon() == 41 || ionSignal.getIon() == 42 || ionSignal.getIon() == 43 || ionSignal.getIon() == 45 || ionSignal.getIon() == 46 || ionSignal.getIon() == 47 || ionSignal.getIon() == 57 || ionSignal.getIon() == 58 || ionSignal.getIon() == 69 || ionSignal.getIon() == 70 || ionSignal.getIon() == 72 || ionSignal.getIon() == 73 || ionSignal.getIon() == 86 || ionSignal.getIon() == 101 || ionSignal.getIon() == 102 || ionSignal.getIon() == 162) {
			} else {
				excludedIonsPeak2_2.add(new MarkedIon(ionSignal.getIon()));
			}
		}
		try {
			peak2 = PeakBuilderMSD.createPeak(chromatogram, scanRangePeak2_1, excludedIonsPeak2_1);
			peak2.getExtractedMassSpectrum().removeIon(167);
			peak3 = PeakBuilderMSD.createPeak(chromatogram, scanRange137, excludedIons);
			peak4 = PeakBuilderMSD.createPeak(chromatogram, scanRangePeak2_2, excludedIonsPeak2_2);
			peak5 = PeakBuilderMSD.createPeak(chromatogram, scanRange137, excludedIons5);
			peak6 = PeakBuilderMSD.createPeak(chromatogram, scanRange167, excludedIons4);
			//
			chromatogram.removeAllPeaks();
			// chromatogram.addPeak(peak);
			chromatogram.addPeak(peak3);
			// chromatogram.addPeak(peak5);
			chromatogram.addPeak(peak6);
			chromatogram.addPeak(peak4);
			chromatogram.addPeak(peak2);
			/*
			 * List<IChromatogramPeakMSD> peakList2 = chromatogram.getPeaks();
			 * peakList2.remove(0);
			 * peakList2.remove(0);
			 */
		} catch(PeakException e) {
			logger.warn(e);
		}
		peak.setDetectorDescription("Peak detector deconvolution");
		// 89 = Ion 137
		return peakRangesDeconv;
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

	private int[] firstDerivZeroCrossing(double[] signal) {

		int[] zeroCrossings = new int[signal.length];
		boolean aboveNull = false;
		if(signal[0] > 0.0) {
			aboveNull = true;
		}
		int length = (signal.length - 1);
		int counter = 0;
		for(int i = 0; i < length; i++) {
			if(aboveNull) {
				if(signal[i + 1] < 0.0) {
					zeroCrossings[counter] = i;
					counter++;
					aboveNull = false;
				}
			} else {
				if(signal[i + 1] > 0.0) {
					zeroCrossings[counter] = i;
					counter++;
					aboveNull = true;
				}
			}
		}
		return zeroCrossings;
	}

	private IAllIonSignals calcNoiseSegments(IAllIonSignals allIonSignals, IProgressMonitor monitor) {

		/*
		 * noise Segment hat die bereiche welche deklariert wurden.
		 */
		int segmentWidth = quantityNoiseSegments;
		IAllIonSignals allIonNewSignals = null;
		ScanRange scanRange = new ScanRange(allIonSignals.getStartScan(), allIonSignals.getStopScan());
		if(scanRange == null || scanRange.getWidth() <= segmentWidth) {
		} else {
			try {
				AnalysisSupport analysisSupport = new AnalysisSupport(scanRange, segmentWidth);
				List<IAnalysisSegment> analysisSegments = analysisSupport.getAnalysisSegments();
				allIonNewSignals = calculateIonSignalSegments(analysisSegments, allIonSignals, monitor);
			} catch(AnalysisSupportException e) {
				logger.warn(e);
			}
		}
		setHighestTicSignal(allIonSignals.getIonSignals(0).getIonSignals());
		return allIonNewSignals;
	}

	private IAllIonSignals calculateIonSignalSegments(List<IAnalysisSegment> analysisSegments, IAllIonSignals allIonSignals, IProgressMonitor monitor) {

		int counterSteinAccepted = 0;
		int counterSteinDenied = 0;
		@SuppressWarnings("unused")
		int counter = 0;
		@SuppressWarnings("unused")
		int counterIon = 0;
		IIonSignal segmentSignal;
		double meanSegment;
		int startallIonSignals = allIonSignals.getStartScan();
		for(IIonSignals ionSignal : allIonSignals.getIonSignals()) {
			for(IAnalysisSegment analysisSegment : analysisSegments) {
				meanSegment = calculateAcceptedSegment(analysisSegment, ionSignal, startallIonSignals);
				if(Double.compare(meanSegment, 0) > 0.0) {
					counterSteinAccepted++;
				} else {
					counterSteinDenied++;
				}
				segmentSignal = new IonSignal(meanSegment);
				ionSignal.addSegmentValue(segmentSignal);
				counter++;
			}
			ionSignal.setCounterSteinAccepted(counterSteinAccepted);
			ionSignal.setCounterSteinDenied(counterSteinDenied);
			counterSteinAccepted = 0;
			counterSteinDenied = 0;
			counter = 0;
			counterIon++;
		}
		return allIonSignals;
	}

	private double calculateAcceptedSegment(IAnalysisSegment analysisSegment, IIonSignals ionSignal, int start) {

		double signal;
		int size = analysisSegment.getSegmentWidth();
		double result = 1;
		int startScanCorrection = start;
		double[] valuesSignal = ionSignal.getIonSignals();
		if(size > 0) {
			double[] values = new double[size];
			int counter = 0;
			for(int scan = analysisSegment.getStartScan(); scan <= analysisSegment.getStopScan(); scan++) {
				try {
					signal = valuesSignal[scan - startScanCorrection];
					values[counter] = signal;
				} catch(Exception e) {
					logger.warn(e);
				} finally {
					counter++;
				}
			}
			double mean = getMittel(values, 1);
			if(!checkSegmentStein(values, mean)) {
				result = 0;
			} else {
				result = mean;
			}
		} else {
			result = -1.0;
		}
		return result;
	}

	/*
	 * Give back MeanValue
	 */
	private double getMittel(double[] signalValues, int whichCalc) {

		/*
		 * 1= arithmetisches Mittel
		 * 2= geometrisches Mittel
		 * 3= harmonisches Mittel
		 * 4= Median
		 */
		double[] newSignalValues = signalValues.clone();
		int whichResult = whichCalc;
		double numbers[] = new double[newSignalValues.length];
		int size = newSignalValues.length;
		int counter = 0;
		double am = 0, gm = 1.0, hm = 0, median = 0;
		for(double value : newSignalValues) {
			am += value;
			gm *= value;
			hm += 1.0 / value;
			numbers[counter] = value;
			counter++;
		}
		Arrays.sort(newSignalValues);
		if(newSignalValues.length % 2 == 0) {
			median = 0.5 * (numbers[numbers.length / 2 - 1] + numbers[numbers.length / 2]);
		} else if(newSignalValues.length > 2) {
			median = numbers[numbers.length / 2];
		} else {
			median = numbers[0];
		}
		if(whichResult == 1) {
			return am / size;
		} else if(whichResult == 2) {
			return Math.pow(gm, (1.0 / size));
		} else if(whichResult == 3) {
			return hm * size;
		} else {
			return median;
		}
	}

	private boolean checkSegmentStein(double[] values, double mean) {

		boolean isAbove = false;
		if(values[0] >= mean) {
			isAbove = true;
		}
		int crossings = 0;
		int length = (values.length - 1);
		for(int i = 0; i < length; i++) {
			if(isAbove) {
				if(values[i + 1] < mean) {
					crossings++;
					isAbove = false;
				}
			} else {
				if(values[i + 1] > mean) {
					crossings++;
					isAbove = true;
				}
			}
		}
		boolean accept = false;
		if(crossings <= ((length + 1) / 2)) {
			accept = false;
		} else {
			accept = true;
		}
		return accept;
	}

	/*
	 * Gives class back with Ion and Signal of that Ion
	 * allIonSignals= Ion, Signals over all Scans of that Ion
	 * For all Ions which exist
	 * TicSignal => Ion 0
	 */
	private IAllIonSignals getAllIonSignals(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		/*
		 * TotalScanSignal
		 */
		// TotalScanSignalExtractor signalExtractor = new TotalScanSignalExtractor(chromatogram);
		// ITotalScanSignals totalScanSignals = signalExtractor.getTotalScanSignals(chromatogramSelection, false);
		/*
		 * TotalIonSignal
		 * MSD
		 */
		chromatogram = chromatogramSelection.getChromatogramMSD();
		ITotalIonSignalExtractor totalIonSignalExtractor;
		ITotalScanSignals totalIONsignals = null;
		IExtractedIonSignalExtractor extractedIonSignalExtractor;
		IExtractedIonSignals extractedIonSignals = null;
		try {
			totalIonSignalExtractor = new TotalIonSignalExtractor(chromatogram);
			totalIONsignals = totalIonSignalExtractor.getTotalIonSignals(chromatogramSelection);
			// ITotalScanSignals totalIONsignals2 = totalIONsignals.makeDeepCopy();
			extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
		} catch(ChromatogramIsNullException e1) {
			logger.warn(e1);
			/*
			 * TODO
			 * Abbruch und Message, dass die totalSignals nicht gezogen werden konnten
			 */
		}
		double[] ticSignal = deconvHelper.getSignalAsArrayDoubleDeconv(totalIONsignals);
		/*
		 * Set Size of ArraysViewDeconv
		 */
		arraysViewDeconv = new ArraysViewDeconv(totalIONsignals);
		IExtractedIonSignal extractedIonSignal;
		IAllIonSignals allIonSignals = new AllIonSignals(extractedIonSignals);
		int startIon = extractedIonSignals.getStartIon();
		int stopIon = extractedIonSignals.getStopIon();
		int startScan = extractedIonSignals.getStartScan();
		int stopScan = extractedIonSignals.getStopScan();
		double meanSignal = 0.0;
		int nullcounter = 0;
		int divider = 0;
		double ionSignal;
		// ticSignal hinzufügen
		IIonSignals ticSignaltoAllIon = new IonSignals(0, ticSignal, 0.0);
		allIonSignals.addIonDeconv(ticSignaltoAllIon);
		//
		for(int ion = startIon; ion <= stopIon; ion++) {
			//
			//
			double[] signals = new double[stopScan - startScan + 1];
			for(int scan = startScan, counter = 0; scan <= stopScan; scan++, counter++) {
				//
				try {
					extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
					ionSignal = extractedIonSignal.getAbundance(ion);
					signals[counter] = ionSignal;
					if(Double.compare(ionSignal, 0) > 0.0) {
					} else {
						nullcounter++;
					}
					meanSignal += extractedIonSignal.getAbundance(ion);
					divider = counter;
				} catch(NoExtractedIonSignalStoredException e) {
					logger.warn(e);
				}
			}
			if(nullcounter != signals.length) {
				IIonSignals ionSignals = new IonSignals(ion, signals, meanSignal / divider);
				allIonSignals.addIonDeconv(ionSignals);
			}
			nullcounter = 0;
		}
		allIonSignals = deconvHelper.setXValueToAllIonSignals(allIonSignals, totalIONsignals);
		allIonSignals = calcNoiseSegments(allIonSignals, monitor);
		return allIonSignals;
	}

	/**
	 * 
	 * @param yChromatogram
	 * @param filterSettings
	 * @param durbinWatsonClassifierResult
	 * @param monitor
	 * @return
	 */
	private IDerivativesAndNoise setDerivatives(double[] yChromatogram, ChromatogramFilterSettings filterSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		/*
		 * Savitzky Golay max second Derivative
		 */
		IDeconvHelper deconvHelper = new DeconvHelper();
		/*
		 * First deriv by Savitzky Golay with Noise (Factorised by 2 => now it's the same range like firstderiv by normal function)
		 */
		double[] firstDerivSmoothedFactorised = deconvHelper.factorisingValues(savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(firstDerivative, yChromatogram, filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor), 2);
		double[] noiseFirstDeriv = getNoiseOfTic(firstDerivSmoothedFactorised, 1, true);
		double[] noiseNegativeFirstDeriv = deconvHelper.positivToNegativ(noiseFirstDeriv);
		IFirstDerivativeAndNoise firstDerivative = new FirstDerivativeAndNoise(firstDerivSmoothedFactorised, noiseFirstDeriv, noiseNegativeFirstDeriv);
		/*
		 * SecondDerivative is formed out of
		 * (original chromatogram ticvalues + SG(first derivative) => first derivative)
		 * (first derivative + SG(first derivative) => second derivative)
		 * (second derivative + SG(no derivative = smooth) => second derivative smoothed)
		 * (second derivative smoothed + factorising => better for output)
		 */
		double[] secondDerivSmoothedFactorised = deconvHelper.factorisingValues(savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(this.firstDerivative, savitzkyGolaySmooth(this.firstDerivative, yChromatogram, filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor), 9);
		double[] noiseSecondDeriv = getNoiseOfTic(secondDerivSmoothedFactorised, 2, true);
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
		double[] thirdDerivSmoothedFactorised = deconvHelper.factorisingValues(savitzkyGolaySmooth(noDerivative, savitzkyGolaySmooth(this.firstDerivative, savitzkyGolaySmooth(this.firstDerivative, savitzkyGolaySmooth(this.firstDerivative, yChromatogram, filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor), filterSettings, durbinWatsonClassifierResult, monitor), 19);
		double[] noiseThirdDeriv = getNoiseOfTic(thirdDerivSmoothedFactorised, 3, false);
		double[] noiseNegativeThirdDeriv = deconvHelper.positivToNegativ(noiseThirdDeriv);
		IThirdDerivativeAndNoise thirdDerivative = new ThirdDerivativeAndNoise(thirdDerivSmoothedFactorised, noiseThirdDeriv, noiseNegativeThirdDeriv);
		// All in one
		IDerivativesAndNoise derivativesAndNoise = new DerivativesAndNoise(firstDerivative, secondDerivative, thirdDerivative);
		return derivativesAndNoise;
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
			// System.out.println("SNR: " + newPeakRanges.getPeakRange(i).getSignalToNoise());
			// System.out.println("Width: " + newPeakRanges.getPeakRange(i).getwidthPeakRange());
			// System.out.println("");
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
					if(giveOutput) {
						System.out.println("");
						System.out.println("First Peak Detect: " + peakStartEndMaxPeak[2]);
					}
					/*
					 * Check Range with Second Derivative
					 */
					int[] secondDerivativeCheck = checkPeakRangesWithSecondDerivative(derivativesAndNoise, peakStartEndMaxPeak);
					if(secondDerivativeCheck[0] == 1 && checkWithSecondDerive) {
						if(giveOutput) {
							System.out.print(" Second Derivative News: || Negative Zone: " + secondDerivativeCheck[1] + " || Positive Zone: " + secondDerivativeCheck[2] + " Peaks: " + (secondDerivativeCheck.length - 3));
						}
						// Second Deriv
						if(giveOutput) {
							if((secondDerivativeCheck.length - 3) >= 1) {
								System.out.print(" --- ");
								for(int l = 3; l < secondDerivativeCheck.length; l++) {
									System.out.print(secondDerivativeCheck[l] + "  ");
								}
							}
							System.out.print("\n");
						}
						IPeaksDeconv peaksSecond = new PeaksDeconv("SecondDerivative");
						for(int z = 3; z < secondDerivativeCheck.length; z++) {
							IPeakDeconv peakSecond = new PeakDeconv(secondDerivativeCheck[z]);
							peaksSecond.addPeak(peakSecond);
						}
						peakRange.addPeaks(peaksSecond);
						secDerivCheck = true;
					} else {
						if(giveOutput) {
							System.out.println("	SecondDerivative Check :: False");
						}
					}
					/*
					 * Check Range with Third Derivative
					 */
					int[] thirdDerivativeCheck = checkPeakRangesWithThirdDerivative(derivativesAndNoise, peakStartEndMaxPeak);
					if(giveOutput) {
						if(thirdDerivativeCheck[0] == 1 && checkWithThirdDerivative) {
							System.out.println(" Third  Derivative News: || Change Signs: " + thirdDerivativeCheck[1]);
							System.out.println("--------------------------------------------");
							System.out.println("");
						} else {
							System.out.println(" 	ThirdDerivative Check :: False");
							System.out.println("--------------------------------------------");
							System.out.println("");
						}
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
						if((Double.compare(firstDeriv[x], 0) < 0) && (Double.compare(firstDeriv[x + 1], noise) > 0)) {
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
		int negativeZoneCounter = 0;
		int positiveZoneCounter = 0;
		int[] peaksWithSecondDerivHelper = new int[peakRangeStop - peakRangeStart];
		boolean positivZoneFirst = false;
		boolean negativeZone = false;
		boolean positivZoneSecond = false;
		double[] secondDeriv = derivativesAndNoise.getSecondDerivativeAndNoise().getSecondDeriv();
		for(int i = peakRangeStart; i < peakRangeStop - 1; i++) {
			double noiseNegativSecondDeriv = derivativesAndNoise.getSecondDerivativeAndNoise().getNoiseNegative()[i];
			double noisePositivSecondDeriv = derivativesAndNoise.getSecondDerivativeAndNoise().getNoisePositiv()[i];
			if((Double.compare(secondDeriv[i], noisePositivSecondDeriv) > 0) && !positivZoneFirst && !negativeZone && !positivZoneSecond) {
				positivZoneFirst = true;
				positiveZoneCounter++;
			}
			if((Double.compare(secondDeriv[i], noiseNegativSecondDeriv) < 0) && !negativeZone && positivZoneFirst && !positivZoneSecond) {
				negativeZone = true;
				negativeZoneCounter++;
			}
			if((Double.compare(secondDeriv[i], noisePositivSecondDeriv) > 0) && !positivZoneSecond && positivZoneFirst && negativeZone) {
				positivZoneSecond = true;
				positiveZoneCounter++;
			}
			if(positivZoneFirst && negativeZone) {
				if((Double.compare(secondDeriv[i - 1], secondDeriv[i]) > 0) && (Double.compare(secondDeriv[i], secondDeriv[i + 1]) < 0)) {
					peaksWithSecondDerivHelper[peakCounter++] = i;
				}
			}
		}
		int[] peaksSecondDerivativ = new int[peakCounter + 3];
		peaksSecondDerivativ[0] = (positivZoneFirst && negativeZone && positivZoneSecond) ? 1 : 0;
		peaksSecondDerivativ[1] = positiveZoneCounter;
		peaksSecondDerivativ[2] = negativeZoneCounter;
		for(int i = 3; i < peakCounter + 3; i++) {
			peaksSecondDerivativ[i] = peaksWithSecondDerivHelper[i - 3];
		}
		return peaksSecondDerivativ;
	}

	private int[] checkPeakRangesWithThirdDerivative(IDerivativesAndNoise derivativesAndNoise, int[] peakStartEndMaxPeak) {

		int peakRangeStart = peakStartEndMaxPeak[3];
		int peakRangeStop = peakStartEndMaxPeak[1];
		int peakCounter = 0;
		int changeSignsCounter = 0;
		boolean negativeZone = true;
		boolean positivZone = false;
		double[] thirdDeriv = derivativesAndNoise.getThirdDerivativeAndNoise().getThirdDeriv();
		for(int i = peakRangeStart; i < peakRangeStop - 1; i++) {
			double noiseNegativThirdDeriv = derivativesAndNoise.getThirdDerivativeAndNoise().getNoiseNegative()[i];
			double noisePositivThirdDeriv = derivativesAndNoise.getThirdDerivativeAndNoise().getNoisePositiv()[i];
			if((Double.compare(thirdDeriv[i], noisePositivThirdDeriv) > 0) && negativeZone) {
				positivZone = true;
				negativeZone = false;
				if(changeSignsCounter != 0) {
					changeSignsCounter++;
				}
			}
			if((Double.compare(thirdDeriv[i], noiseNegativThirdDeriv) < 0) && positivZone) {
				negativeZone = true;
				positivZone = false;
				changeSignsCounter++;
			}
		}
		int[] peaksThirdDerivativ = new int[peakCounter + 2];
		peaksThirdDerivativ[0] = (changeSignsCounter > 2) ? 1 : 0;
		peaksThirdDerivativ[1] = changeSignsCounter;
		return peaksThirdDerivativ;
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
	private double[] getNoiseOfTic(double[] ticValues, int whichSignal, boolean aboveNullDeriv) {

		int size = ticValues.length;
		int segmentSizeFactor = size / quantityNoiseSegments;
		int segmentsize = quantityNoiseSegments;
		int maxFor = 0;
		boolean setSegmentsize = false;
		if(segmentsize < 3) {
			maxFor = segmentsize * segmentSizeFactor;
			segmentsize = 3;
			setSegmentsize = true;
		} else {
			maxFor = segmentsize * segmentSizeFactor;
		}
		boolean derivActive = false;
		if(whichSignal > 0) {
			derivActive = true;
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
				noise = segmentNoiseWasNull(segmentNoise) ? (howManySegments == 1) ? 0.0d : finalNoise[i - segmentsize - 1] : getNoiseTicValueByVivoTruyols(segmentNoise, whichSignal);
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
			noise = segmentNoiseWasNull(segmentNoise) ? finalNoise[maxFor - 1] : getNoiseTicValueByVivoTruyols(segmentNoise, whichSignal);
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
	private double getNoiseTicValueByVivoTruyols(double[] ticValues, int whichSignal) {

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
		if(whichSignal == 0) {
			return Noiseh1 * factorNoiseOriginal;
		} else if(whichSignal == 1) {
			return Noiseh1 * factorNoiseFirstDerive;
		} else if(whichSignal == 2) {
			return Noiseh1 * factorNoiseSecondDerive;
		} else {
			return Noiseh1 * factorNoiseThirdDerive;
		}
	}

	/*
	 * 
	 */
	private void setHighestTicSignal(double[] yChromatogram) {

		for(int i = 0; i < yChromatogram.length; i++) {
			if(Double.compare(highestTicSignal, yChromatogram[i]) < 0) {
				highestTicSignal = yChromatogram[i];
			}
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
	private double[] savitzkyGolaySmooth(int whichDerivative, double[] ticValues, ChromatogramFilterSettings supplierFilterSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

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
	private void DurbinWatsonRatings(double[] ticValues, ClassifierSettings classifierSettings, IDurbinWatsonClassifierResult durbinWatsonClassifierResult, IProgressMonitor monitor) {

		DurbinWatsonProcessor durbinWatsonProcessor = new DurbinWatsonProcessor();
		durbinWatsonProcessor.durbinWatsonMain(ticValues, classifierSettings, durbinWatsonClassifierResult, monitor);
	}

	/**
	 * 
	 * @param durbinWatsonClassifierResult
	 * @param whichderivative
	 * @param filterSettings
	 * @return supplierFilterSettings
	 */
	private ChromatogramFilterSettings DurbinWatsonSetBestValuesForSavitzkyGolay(IDurbinWatsonClassifierResult durbinWatsonClassifierResult, ChromatogramFilterSettings filterSettings, int whichderivative) {

		if(durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings() != null) {
			int sizeRating = durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().size() - 1;
			double bestRating = 100.0f;
			double newRating = 0.0f;
			int bestDurbinWatson = 0;
			boolean setbestDB = false;
			for(int i = 1; i < sizeRating; i++) {
				if(durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().get(i).getFilterSettings().getDerivative() == whichderivative) {
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
			int order = durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().get(bestDurbinWatson).getFilterSettings().getOrder();
			int width = durbinWatsonClassifierResult.getSavitzkyGolayFilterRatings().get(bestDurbinWatson).getFilterSettings().getWidth();
			filterSettings.setDerivative(whichderivative);
			filterSettings.setOrder(order);
			filterSettings.setWidth(width);
		}
		return filterSettings;
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