/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - support m/z filtering
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IBackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons.IonMarkMode;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public class PeakBuilderMSD {

	@Deprecated
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, boolean includedBackground, Set<Integer> includedIons) throws PeakException {

		return createPeak(chromatogram, scanRange, includedBackground, includedIons, IonMarkMode.INCLUDE);
	}

	/**
	 * EXPERIMENTAL!
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @param includedBackground
	 * @param includedIons
	 * @return IChromatogramPeakMSD
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, boolean includedBackground, Set<Integer> includedIons, IonMarkMode filterMode) throws PeakException {

		/*
		 * Validate the given objects.
		 */
		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		checkScanRange(chromatogram, scanRange);
		ITotalScanSignal totalIonSignal;
		IBackgroundAbundanceRange backgroundAbundanceRange;
		/*
		 * Get the total signals and determine the start and stop background
		 * abundance.
		 */
		ITotalScanSignals totalIonSignals = getTotalIonSignals(chromatogram, scanRange, new MarkedIons(includedIons, filterMode));
		/*
		 * Retrieve the start and stop signals of the peak to calculate its
		 * chromatogram and eventually peak internal background, if the start
		 * abundance is higher than the stop abundance or vice versa.
		 */
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStartScan());
		float startBackgroundAbundance = totalIonSignal.getTotalSignal();
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStopScan());
		float stopBackgroundAbundance = totalIonSignal.getTotalSignal();
		/*
		 * The abundance of base or startBackground/stopBackground (depends
		 * which is the lower value) is the chromatogram background.<br/> Then a
		 * peak included background could be calculated or not.<br/> This
		 * background is not the background of the chromatogram. It's the
		 * background of the peak.<br/> Think of, a peak could be skewed, means
		 * it starts with an abundance of zero and stops with a higher
		 * abundance.<br/> To include or exclude the background abundance in the
		 * IPeakModel affects the calculation of its width at different heights.
		 */
		if(includedBackground) {
			backgroundAbundanceRange = new BackgroundAbundanceRange(startBackgroundAbundance, stopBackgroundAbundance);
		} else {
			float base = Math.min(startBackgroundAbundance, stopBackgroundAbundance);
			backgroundAbundanceRange = new BackgroundAbundanceRange(base, base);
		}
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		/*
		 * Calculate the intensity values.
		 */
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, new MarkedIons(includedIons, filterMode));
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, float startIntensity, float stopIntensity, Set<Integer> includedIons, IonMarkMode filterMode) throws PeakException {

		/*
		 * Validate the given objects.
		 */
		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		checkScanRange(chromatogram, scanRange);
		/*
		 * Calculate the intensity values.
		 */
		ITotalScanSignals totalIonSignals = getTotalIonSignals(chromatogram, scanRange, new MarkedIons(includedIons, filterMode));
		float firstSignal = totalIonSignals.getFirstTotalScanSignal().getTotalSignal();
		float lastSignal = totalIonSignals.getLastTotalScanSignal().getTotalSignal();
		startIntensity = startIntensity <= firstSignal ? startIntensity : firstSignal;
		stopIntensity = stopIntensity <= lastSignal ? stopIntensity : lastSignal;
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, new BackgroundAbundanceRange(startIntensity, stopIntensity));
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, new MarkedIons(includedIons, filterMode));
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, startIntensity, stopIntensity);
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * EXPERIMENTAL!
	 */
	@SuppressWarnings("unused")
	private static IPeakMassSpectrum getPeakMassSpectrum(IChromatogramMSD chromatogram, IScanMSD massSpectrum, LinearEquation backgroundEquation) throws PeakException {

		if(chromatogram == null || massSpectrum == null || backgroundEquation == null) {
			throw new PeakException("The chromatogram, massSpectrum or backgroundEquation must not be null.");
		}
		/*
		 * Get the peak mass spectrum and subtract the background.
		 */
		IPeakMassSpectrum peakMassSpectrum = null;
		if(massSpectrum != null) {
			/*
			 * Adjust the peak mass spectrum.
			 */
			float actualSignal = massSpectrum.getTotalSignal();
			float backgroundSignal = (float)backgroundEquation.calculateY(massSpectrum.getRetentionTime());
			float correctedSignal = actualSignal - backgroundSignal;
			float percentage = (100.0f / correctedSignal) * actualSignal;
			peakMassSpectrum = new PeakMassSpectrum(massSpectrum, percentage);
		}
		return peakMassSpectrum;
	}

	/**
	 * Creates an instance of IPeak.<br/>
	 * The background abundance will be automatically set linear through the
	 * abundance of the first and the last signal of the given scan range.<br/>
	 * If you like to set the background abundance manually, choose the other
	 * method with background abundance parameters.<br/>
	 * When calculatePeakIncludedBackground is true, the background of the peak
	 * (not the chromatogram) will be handled separately.<br/>
	 * This affects the calculation of the peaks width.<br/>
	 * It has a higher effect if the peak is cutted e.g. by perpendicular or is
	 * detected by peak skimming.
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @param calculatePeakIncludedBackground
	 * @return IPeak
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, boolean calculatePeakIncludedBackground) throws PeakException {

		/*
		 * Validate the given objects.
		 */
		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		checkScanRange(chromatogram, scanRange);
		ITotalScanSignal totalIonSignal;
		IBackgroundAbundanceRange backgroundAbundanceRange;
		/*
		 * Get the total signals and determine the start and stop background
		 * abundance.
		 */
		ITotalScanSignals totalIonSignals = getTotalIonSignals(chromatogram, scanRange);
		/*
		 * Retrieve the start and stop signals of the peak to calculate its
		 * chromatogram and eventually peak internal background, if the start
		 * abundance is higher than the stop abundance or vice versa.
		 */
		LinearEquation backgroundEquation;
		float startBackgroundAbundance;
		float stopBackgroundAbundance;
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStartScan());
		startBackgroundAbundance = totalIonSignal.getTotalSignal();
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStopScan());
		stopBackgroundAbundance = totalIonSignal.getTotalSignal();
		/*
		 * The abundance of base or startBackground/stopBackground (depends
		 * which is the lower value) is the chromatogram background.<br/> Then a
		 * peak included background could be calculated or not.<br/> This
		 * background is not the background of the chromatogram. It's the
		 * background of the peak.<br/> Think of, a peak could be skewed, means
		 * it starts with an abundance of zero and stops with a higher
		 * abundance.<br/> To include or exclude the background abundance in the
		 * IPeakModel affects the calculation of its width at different heights.
		 */
		if(calculatePeakIncludedBackground) {
			backgroundAbundanceRange = new BackgroundAbundanceRange(startBackgroundAbundance, stopBackgroundAbundance);
		} else {
			float base = Math.min(startBackgroundAbundance, stopBackgroundAbundance);
			backgroundAbundanceRange = new BackgroundAbundanceRange(base, base);
		}
		backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		/*
		 * Calculate the intensity values.
		 */
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, null);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, float startIntensity, float stopIntensity) throws PeakException {

		/*
		 * Validate the given objects.
		 */
		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		checkScanRange(chromatogram, scanRange);
		/*
		 * Calculate the intensity values.
		 */
		ITotalScanSignals totalIonSignals = getTotalIonSignals(chromatogram, scanRange);
		float firstSignal = totalIonSignals.getFirstTotalScanSignal().getTotalSignal();
		float lastSignal = totalIonSignals.getLastTotalScanSignal().getTotalSignal();
		startIntensity = startIntensity <= firstSignal ? startIntensity : firstSignal;
		stopIntensity = stopIntensity <= lastSignal ? stopIntensity : lastSignal;
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, new BackgroundAbundanceRange(startIntensity, stopIntensity));
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, null);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, startIntensity, stopIntensity);
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * Creates an instance of IPeak.<br/>
	 * The background abundance will be used as given.<br/>
	 * If you like to set the background abundance automatically, choose the
	 * other method without background abundance parameters.<br/>
	 * You can choose also checkBackgroundAbundanceRange == false. In this case
	 * the background abundance could be higher than the total signals, in some
	 * case.
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @param backgroundAbundanceRange
	 * @param checkBackgroundAbundanceRange
	 * @return IPeak
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, IBackgroundAbundanceRange backgroundAbundanceRange, boolean checkBackgroundAbundanceRange) throws PeakException {

		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		validateBackgroundAbundanceRange(backgroundAbundanceRange);
		ITotalScanSignals totalIonSignals = getTotalIonSignals(chromatogram, scanRange);
		/*
		 * Adjust and correct the background abundance range, if needed.
		 */
		if(checkBackgroundAbundanceRange) {
			backgroundAbundanceRange = checkBackgroundAbundanceRange(totalIonSignals, scanRange, backgroundAbundanceRange);
		}
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, null);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * Creates an instance of IPeak.<br/>
	 * The background abundance will be automatically set linear through the
	 * abundance of the first and the last signal of the given scan range.<br/>
	 * If you like to set the background abundance manually, choose the other
	 * method with background abundance parameters.<br/>
	 * Only total ion signals without the given ions will be
	 * considered.
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @param excludedIons
	 * @return IPeak
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, IMarkedIons excludedIons) throws PeakException {

		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		validateExcludedIons(excludedIons);
		ITotalScanSignal totalIonSignal;
		IBackgroundAbundanceRange backgroundAbundanceRange;
		/*
		 * Get the total signals and determine the start and stop background
		 * abundance.
		 */
		ITotalScanSignals totalIonSignals = getTotalIonSignals(chromatogram, scanRange, excludedIons);
		/*
		 * BackgroundAbundanceRange
		 */
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStartScan());
		float startBackgroundAbundance = totalIonSignal.getTotalSignal();
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStopScan());
		float stopBackgroundAbundance = totalIonSignal.getTotalSignal();
		backgroundAbundanceRange = new BackgroundAbundanceRange(startBackgroundAbundance, stopBackgroundAbundance);
		//
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, excludedIons);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * Creates an instance of IPeak.<br/>
	 * The background abundance will be used as given.<br/>
	 * If you like to set the background abundance automatically, choose the
	 * other method without background abundance parameters.<br/>
	 * Only total ion signals without the given ions will be
	 * considered.
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @param backgroundAbundanceRange
	 * @param excludedIons
	 * @return IPeak
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, IScanRange scanRange, IBackgroundAbundanceRange backgroundAbundanceRange, IMarkedIons excludedIons) throws PeakException {

		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		validateExcludedIons(excludedIons);
		validateBackgroundAbundanceRange(backgroundAbundanceRange);
		/*
		 * Get the total signals and determine the start and stop background
		 * abundance.
		 */
		ITotalScanSignals totalIonSignals = getTotalIonSignals(chromatogram, scanRange, excludedIons);
		backgroundAbundanceRange = checkBackgroundAbundanceRange(totalIonSignals, scanRange, backgroundAbundanceRange);
		//
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, excludedIons);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * Creates a peak from the given parameters.<br/>
	 * The background will be detected automatically.
	 * 
	 * @param chromatogram
	 * @param totalIonSignals
	 * @param peakMassSpectrum
	 * @return IPeak
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, ITotalScanSignals totalIonSignals, IPeakMassSpectrum peakMassSpectrum) throws PeakException {

		validateChromatogram(chromatogram);
		validateTotalIonSignals(totalIonSignals);
		validatePeakMassSpectrum(peakMassSpectrum);
		ITotalScanSignal totalIonSignal;
		IBackgroundAbundanceRange backgroundAbundanceRange;
		IScanRange scanRange = new ScanRange(totalIonSignals.getStartScan(), totalIonSignals.getStopScan());
		/*
		 * BackgroundAbundanceRange
		 */
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStartScan());
		float startBackgroundAbundance = totalIonSignal.getTotalSignal();
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStopScan());
		float stopBackgroundAbundance = totalIonSignal.getTotalSignal();
		backgroundAbundanceRange = new BackgroundAbundanceRange(startBackgroundAbundance, stopBackgroundAbundance);
		//
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * Creates a peak from the given parameters.<br/>
	 * The background can be determined through start and stop background
	 * abundance.
	 * 
	 * @param chromatogram
	 * @param totalIonSignals
	 * @param peakMassSpectrum
	 * @param backgroundAbundanceRange
	 * @return IPeak
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IChromatogramMSD chromatogram, ITotalScanSignals totalIonSignals, IPeakMassSpectrum peakMassSpectrum, IBackgroundAbundanceRange backgroundAbundanceRange) throws PeakException {

		validateChromatogram(chromatogram);
		validateTotalIonSignals(totalIonSignals);
		validatePeakMassSpectrum(peakMassSpectrum);
		validateBackgroundAbundanceRange(backgroundAbundanceRange);
		IScanRange scanRange = new ScanRange(totalIonSignals.getStartScan(), totalIonSignals.getStopScan());
		/*
		 * BackgroundAbundanceRange
		 */
		backgroundAbundanceRange = checkBackgroundAbundanceRange(totalIonSignals, scanRange, backgroundAbundanceRange);
		//
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * Creates an instance of IPeak.<br/>
	 * The background abundance will be automatically set linear through the
	 * abundance of the first and the last signal of the given scan range.<br/>
	 * If you like to set the background abundance manually, choose the other
	 * method with background abundance parameters.
	 * 
	 * @param extractedIonSignals
	 * @param scanRange
	 * @return IPeak
	 * @throws PeakException
	 */
	public static IChromatogramPeakMSD createPeak(IExtractedIonSignals extractedIonSignals, IScanRange scanRange) throws PeakException {

		/*
		 * Validate the given objects.
		 */
		validateExtractedIonSignals(extractedIonSignals);
		validateScanRange(scanRange);
		checkScanRange(extractedIonSignals, scanRange);
		IChromatogramMSD chromatogram = extractedIonSignals.getChromatogram();
		validateChromatogram(chromatogram);
		ITotalScanSignal totalIonSignal;
		IBackgroundAbundanceRange backgroundAbundanceRange;
		/*
		 * Get the total signals and determine the start and stop background
		 * abundance.
		 */
		ITotalScanSignals totalIonSignals = extractedIonSignals.getTotalIonSignals(scanRange);
		/*
		 * BackgroundAbundanceRange
		 */
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStartScan());
		float startBackgroundAbundance = totalIonSignal.getTotalSignal();
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStopScan());
		float stopBackgroundAbundance = totalIonSignal.getTotalSignal();
		backgroundAbundanceRange = new BackgroundAbundanceRange(startBackgroundAbundance, stopBackgroundAbundance);
		//
		LinearEquation backgroundEquation = getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalIonSignals(totalIonSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		IPeakMassSpectrum peakMassSpectrum = getPeakMassSpectrum(extractedIonSignals, chromatogram, totalIonSignals, backgroundEquation, null);
		/*
		 * Create the peak.
		 */
		IPeakModelMSD peakModel = new PeakModelMSD(peakMassSpectrum, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		return peak;
	}

	/**
	 * Returns the peak mass spectrum from the given values.
	 */
	protected static IPeakMassSpectrum getPeakMassSpectrum(IExtractedIonSignals extractedIonSignals, IChromatogramMSD chromatogram, ITotalScanSignals totalIonSignals, LinearEquation backgroundEquation, IMarkedIons excludedIons) throws PeakException {

		if(extractedIonSignals == null || chromatogram == null || totalIonSignals == null || backgroundEquation == null) {
			throw new PeakException("The extractedIonSignals, chromatogram, totalIonSignals or backgroundEquation must not be null.");
		}
		/*
		 * Get the peak mass spectrum and subtract the background.
		 */
		IPeakMassSpectrum peakMassSpectrum = null;
		ITotalScanSignal totalIonSignal = totalIonSignals.getMaxTotalScanSignal();
		IScanMSD massSpectrum;
		if(totalIonSignal != null) {
			int scan = chromatogram.getScanNumber(totalIonSignal.getRetentionTime());
			/*
			 * Exclude ions?
			 */
			if(excludedIons == null) {
				massSpectrum = extractedIonSignals.getScan(scan);
			} else {
				massSpectrum = extractedIonSignals.getScan(scan, excludedIons);
			}
			float actualSignal = massSpectrum.getTotalSignal();
			float backgroundSignal = (float)backgroundEquation.calculateY(totalIonSignal.getRetentionTime());
			float correctedSignal = actualSignal - backgroundSignal;
			float percentage = (100.0f / correctedSignal) * actualSignal;
			/*
			 * Adjust the peak mass spectrum.
			 */
			peakMassSpectrum = new PeakMassSpectrum(massSpectrum, percentage);
		}
		return peakMassSpectrum;
	}

	/**
	 * Returns the peak mass spectrum from the given values.
	 * 
	 * @param chromatogram
	 * @param totalIonSignals
	 * @param backgroundEquation
	 * @param excludedIons
	 * @return {@link IPeakMassSpectrum}
	 * @throws PeakException
	 */
	protected static IPeakMassSpectrum getPeakMassSpectrum(IChromatogramMSD chromatogram, ITotalScanSignals totalIonSignals, LinearEquation backgroundEquation, IMarkedIons excludedIons) throws PeakException {

		if(chromatogram == null || totalIonSignals == null || backgroundEquation == null) {
			throw new PeakException("The chromatogram, totalIonSignals or backgroundEquation must not be null.");
		}
		/*
		 * Get the peak mass spectrum and subtract the background.
		 */
		IPeakMassSpectrum peakMassSpectrum = null;
		ITotalScanSignal totalIonSignal = totalIonSignals.getMaxTotalScanSignal();
		IScanMSD massSpectrum;
		if(totalIonSignal != null) {
			int scan = chromatogram.getScanNumber(totalIonSignal.getRetentionTime());
			/*
			 * Exclude ions?
			 */
			if(excludedIons == null) {
				massSpectrum = chromatogram.getSupplierScan(scan);
			} else {
				massSpectrum = chromatogram.getScan(scan, excludedIons);
			}
			float actualSignal = massSpectrum.getTotalSignal();
			float backgroundSignal = (float)backgroundEquation.calculateY(totalIonSignal.getRetentionTime());
			float correctedSignal = actualSignal - backgroundSignal;
			float percentage = (100.0f / correctedSignal) * actualSignal;
			/*
			 * Adjust the peak mass spectrum.
			 */
			peakMassSpectrum = new PeakMassSpectrum(massSpectrum, percentage);
		}
		return peakMassSpectrum;
	}

	/**
	 * Creates a {@link IPeakIntensityValues} instance from the given {@link ITotalScanSignals}.
	 * 
	 * @param peakIntensityTotalIonSignals
	 * @return {@link IPeakIntensityValues}
	 * @throws PeakException
	 */
	protected static IPeakIntensityValues getPeakIntensityValues(ITotalScanSignals peakIntensityTotalIonSignals) throws PeakException {

		if(peakIntensityTotalIonSignals == null) {
			throw new PeakException("The peakIntensityTotalIonSignals must not be null.");
		}
		/*
		 * Build a new peakIntensityValues object and add the normalized total
		 * ion signals.
		 */
		IPeakIntensityValues peakIntensityValues = new PeakIntensityValues();
		List<ITotalScanSignal> signals = peakIntensityTotalIonSignals.getTotalScanSignals();
		for(ITotalScanSignal signal : signals) {
			peakIntensityValues.addIntensityValue(signal.getRetentionTime(), signal.getTotalSignal());
		}
		return peakIntensityValues;
	}

	/**
	 * Makes a deep copy of total ion signals, and adjust each signal to the
	 * background signal at each signals retention time.<br/>
	 * Adjust means, that the background signal will be subtracted from the
	 * total signal.<br/>
	 * Afterwards, the signals will be normalized to
	 * IPeakIntensityValues.MAX_INTENSITY.
	 * 
	 * @param totalIonSignals
	 * @param backgroundEquation
	 * @return {@link ITotalScanSignals}
	 * @throws PeakException
	 */
	protected static ITotalScanSignals adjustTotalIonSignals(ITotalScanSignals totalIonSignals, LinearEquation backgroundEquation) throws PeakException {

		if(totalIonSignals == null || backgroundEquation == null) {
			throw new PeakException("The given totalIonSignals or backgroundEquation must not be null.");
		}
		/*
		 * Make a deep copy of totalIonSignals, normalize the values to
		 * IPeakIntensityValues.MAX_INTENSITY.
		 */
		ITotalScanSignals peakIntensityTotalIonSignals = totalIonSignals.makeDeepCopy();
		int start = peakIntensityTotalIonSignals.getStartScan();
		int stop = peakIntensityTotalIonSignals.getStopScan();
		float adjustedSignal;
		for(int scan = start; scan <= stop; scan++) {
			ITotalScanSignal totalIonSignal = peakIntensityTotalIonSignals.getTotalScanSignal(scan);
			adjustedSignal = (float)(totalIonSignal.getTotalSignal() - backgroundEquation.calculateY(totalIonSignal.getRetentionTime()));
			/*
			 * Check, that the total ion signal is >= 0!
			 */
			if(adjustedSignal < 0.0f) {
				adjustedSignal = 0.0f;
			}
			totalIonSignal.setTotalSignal(adjustedSignal);
		}
		TotalScanSignalsModifier.normalize(peakIntensityTotalIonSignals, IPeakIntensityValues.MAX_INTENSITY);
		return peakIntensityTotalIonSignals;
	}

	/**
	 * The background abundance range needs to be checked.
	 * 
	 * @param totalIonSignals
	 * @param scanRange
	 * @param backgroundAbundanceRange
	 * @throws PeakException
	 * @return {@link LinearEquation}
	 */
	protected static LinearEquation getBackgroundEquation(ITotalScanSignals totalIonSignals, IScanRange scanRange, IBackgroundAbundanceRange backgroundAbundanceRange) throws PeakException {

		if(totalIonSignals == null || scanRange == null || backgroundAbundanceRange == null) {
			throw new PeakException("The given totalIonSignals, scanRange or backgroundAbundanceRange must not be null.");
		}
		ITotalScanSignal totalIonSignal;
		// P1
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStartScan());
		IPoint p1 = new Point(totalIonSignal.getRetentionTime(), backgroundAbundanceRange.getStartBackgroundAbundance());
		// P2
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStopScan());
		IPoint p2 = new Point(totalIonSignal.getRetentionTime(), backgroundAbundanceRange.getStopBackgroundAbundance());
		/*
		 * Create the background abundance equation.
		 */
		LinearEquation backgroundEquation = Equations.createLinearEquation(p1, p2);
		return backgroundEquation;
	}

	/**
	 * Returns a {@link ITotalScanSignals} object from the given chromatogram and
	 * scan range.
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @param excludedIons
	 * @throws PeakException
	 * @return {@link ITotalScanSignals}
	 */
	protected static ITotalScanSignals getTotalIonSignals(IChromatogramMSD chromatogram, IScanRange scanRange, IMarkedIons excludedIons) throws PeakException {

		if(chromatogram == null || scanRange == null || excludedIons == null) {
			throw new PeakException("The given values must not be null.");
		}
		/*
		 * Try to get the signals.
		 */
		try {
			ITotalIonSignalExtractor totalIonSignalExtractor = new TotalIonSignalExtractor(chromatogram);
			return totalIonSignalExtractor.getTotalIonSignals(scanRange.getStartScan(), scanRange.getStopScan(), excludedIons);
		} catch(ChromatogramIsNullException e) {
			throw new PeakException("The chromatogram must not be null.");
		}
	}

	/**
	 * Returns a {@link ITotalScanSignals} object from the given chromatogram and
	 * scan range.
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @throws PeakException
	 * @return {@link ITotalScanSignals}
	 */
	protected static ITotalScanSignals getTotalIonSignals(IChromatogramMSD chromatogram, IScanRange scanRange) throws PeakException {

		if(chromatogram == null || scanRange == null) {
			throw new PeakException("The given values must not be null.");
		}
		/*
		 * Try to get the signals.
		 */
		try {
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			return totalIonSignalExtractor.getTotalScanSignals(scanRange.getStartScan(), scanRange.getStopScan());
		} catch(ChromatogramIsNullException e) {
			throw new PeakException("The chromatogram must not be null.");
		}
	}

	/**
	 * Checks the start and stop abundance.<br/>
	 * If the start or stop abundance are greater than the total ion signal, the
	 * abundance will be lowered to the abundance of the total ion signal.<br/>
	 * The method returns in all cases a valid BackgroundAbundanceRange.<br/>
	 * If one of the given values is null, a peak exception will be thrown.
	 * 
	 * @param totalIonSignals
	 * @param scanRange
	 * @param backgroundAbundanceRange
	 * @throws PeakException
	 * @return {@link IBackgroundAbundanceRange}
	 */
	protected static IBackgroundAbundanceRange checkBackgroundAbundanceRange(ITotalScanSignals totalIonSignals, IScanRange scanRange, IBackgroundAbundanceRange backgroundAbundanceRange) throws PeakException {

		ITotalScanSignal totalIonSignal;
		float background = 0.0f;
		float signal = 0.0f;
		float startBackgroundAbundance = 0.0f;
		float stopBackgroundAbundance = 0.0f;
		boolean adjustBackgroundAbundance = false;
		if(totalIonSignals == null || scanRange == null || backgroundAbundanceRange == null) {
			throw new PeakException("The given values must not be null.");
		}
		/*
		 * Check the start background abundance.
		 */
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStartScan());
		if(totalIonSignal != null) {
			background = backgroundAbundanceRange.getStartBackgroundAbundance();
			signal = totalIonSignal.getTotalSignal();
			if(background <= signal) {
				startBackgroundAbundance = background;
			} else {
				startBackgroundAbundance = signal;
				adjustBackgroundAbundance = true;
			}
		} else {
			adjustBackgroundAbundance = true;
		}
		/*
		 * Check the stop background abundance.
		 */
		totalIonSignal = totalIonSignals.getTotalScanSignal(scanRange.getStopScan());
		if(totalIonSignal != null) {
			background = backgroundAbundanceRange.getStopBackgroundAbundance();
			signal = totalIonSignal.getTotalSignal();
			if(background <= signal) {
				stopBackgroundAbundance = background;
			} else {
				stopBackgroundAbundance = signal;
				adjustBackgroundAbundance = true;
			}
		} else {
			adjustBackgroundAbundance = true;
		}
		/*
		 * If necessary correct the background abundance range.
		 */
		if(adjustBackgroundAbundance) {
			backgroundAbundanceRange = new BackgroundAbundanceRange(startBackgroundAbundance, stopBackgroundAbundance);
		}
		return backgroundAbundanceRange;
	}

	/**
	 * Checks that the scan range is in between the bounds of the given
	 * chromatogram.<br/>
	 * The method assumed that chromatogram and scan range are not null.
	 * 
	 * @param chromatogram
	 * @param scanRange
	 * @throws PeakException
	 */
	protected static void checkScanRange(IChromatogramMSD chromatogram, IScanRange scanRange) throws PeakException {

		assert (chromatogram != null) : "The chromatogram must not be null.";
		assert (scanRange != null) : "The scan range must not be null.";
		if(chromatogram == null || scanRange == null) {
			throw new PeakException("The given chromatogram or scanRange must not be null.");
		}
		/*
		 * Validate the ion range against the chromatogram.
		 */
		if(scanRange.getStartScan() < 1 || scanRange.getStopScan() > chromatogram.getNumberOfScans()) {
			throw new PeakException("The given scan range is out of chromatogram borders.");
		}
	}

	/**
	 * Checks that the scan range is in between the bounds of the given
	 * extracted ion signals.<br/>
	 * The method assumed that extracted ion signals and scan range are not
	 * null.
	 * 
	 * @param extractedIonSignals
	 * @param scanRange
	 * @throws PeakException
	 */
	protected static void checkScanRange(IExtractedIonSignals extractedIonSignals, IScanRange scanRange) throws PeakException {

		assert (extractedIonSignals != null) : "The extracted ion signals instance must not be null.";
		assert (scanRange != null) : "The scan range must not be null.";
		if(scanRange == null || extractedIonSignals == null) {
			throw new PeakException("The given scanRange or extractedIonSignals must not be null.");
		}
		/*
		 * Validate the ion range against the chromatogram.
		 */
		if(scanRange.getStartScan() < extractedIonSignals.getStartScan() || scanRange.getStopScan() > extractedIonSignals.getStopScan()) {
			throw new PeakException("The given scan range is out of chromatogram borders.");
		}
	}

	/**
	 * Checks the scan range and throws an exception is something is wrong.
	 * 
	 * @param scanRange
	 * @throws PeakException
	 */
	protected static void validateScanRange(IScanRange scanRange) throws PeakException {

		if(scanRange == null) {
			throw new PeakException("The scan range must not be null.");
		}
	}

	/**
	 * Checks the background abundance range and throws an exception is
	 * something is wrong.
	 * 
	 * @param backgroundAbundanceRange
	 * @throws PeakException
	 */
	protected static void validateBackgroundAbundanceRange(IBackgroundAbundanceRange backgroundAbundanceRange) throws PeakException {

		if(backgroundAbundanceRange == null) {
			throw new PeakException("The background abundance range must not be null.");
		}
	}

	/**
	 * Checks the peak mass spectrum and throws an exception is something is
	 * wrong.
	 * 
	 * @param peakMassSpectrum
	 * @throws PeakException
	 */
	protected static void validatePeakMassSpectrum(IPeakMassSpectrum peakMassSpectrum) throws PeakException {

		if(peakMassSpectrum == null) {
			throw new PeakException("The peak mass spectrum must not be null.");
		}
	}

	/**
	 * Checks the total ion signals and throws an exception is something is
	 * wrong.
	 * 
	 * @param totalIonSignals
	 * @throws PeakException
	 */
	protected static void validateTotalIonSignals(ITotalScanSignals totalIonSignals) throws PeakException {

		if(totalIonSignals == null) {
			throw new PeakException("The total ion signals must not be null.");
		}
	}

	/**
	 * Checks the excluded ions and throws an exception is something
	 * is wrong.
	 * 
	 * @param excludedIons
	 * @throws PeakException
	 */
	protected static void validateExcludedIons(IMarkedIons excludedIons) throws PeakException {

		if(excludedIons == null) {
			throw new PeakException("The excluded ions must not be null.");
		}
	}

	/**
	 * Checks the chromatogram and throws an exception is something is wrong.
	 * 
	 * @param chromatogram
	 * @throws PeakException
	 */
	protected static void validateChromatogram(IChromatogramMSD chromatogram) throws PeakException {

		if(chromatogram == null) {
			throw new PeakException("The chromatogram must not be null.");
		}
	}

	/**
	 * Checks the extractedIonSignals and throws an exception is something is
	 * wrong.
	 * 
	 * @param extractedIonSignals
	 * @throws PeakException
	 */
	protected static void validateExtractedIonSignals(IExtractedIonSignals extractedIonSignals) throws PeakException {

		if(extractedIonSignals == null) {
			throw new PeakException("The extracted ion signals instance must not be null.");
		}
	}
}
