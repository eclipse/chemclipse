/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.support;

import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.implementation.PeakModelCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
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
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public class PeakBuilderCSD {

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
	public static IChromatogramPeakCSD createPeak(IChromatogramCSD chromatogram, IScanRange scanRange, boolean calculatePeakIncludedBackground) throws PeakException {

		/*
		 * Validate the given objects.
		 */
		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		checkScanRange(chromatogram, scanRange);
		ITotalScanSignal totalScanSignal;
		IBackgroundAbundanceRange backgroundAbundanceRange;
		/*
		 * Get the total signals and determine the start and stop background
		 * abundance.
		 */
		ITotalScanSignals totalScanSignals = getTotalScanSignals(chromatogram, scanRange);
		/*
		 * Retrieve the start and stop signals of the peak to calculate its
		 * chromatogram and eventually peak internal background, if the start
		 * abundance is higher than the stop abundance or vice versa.
		 */
		totalScanSignal = totalScanSignals.getTotalScanSignal(scanRange.getStartScan());
		float startBackgroundAbundance = totalScanSignal.getTotalSignal();
		totalScanSignal = totalScanSignals.getTotalScanSignal(scanRange.getStopScan());
		float stopBackgroundAbundance = totalScanSignal.getTotalSignal();
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
		/*
		 * Calculate the intensity values.
		 */
		LinearEquation backgroundEquation = getBackgroundEquation(totalScanSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalScanSignals = adjustTotalScanSignals(totalScanSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalScanSignals);
		/*
		 * Create the peak.
		 */
		IScanCSD supplierScanCSD = getPeakScan(totalScanSignals, backgroundEquation);
		IPeakModelCSD peakModel = new PeakModelCSD(supplierScanCSD, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakCSD peak = new ChromatogramPeakCSD(peakModel, chromatogram);
		return peak;
	}

	public static IChromatogramPeakCSD createPeak(IChromatogramCSD chromatogram, IScanRange scanRange, float startIntensity, float stopIntensity) throws PeakException {

		/*
		 * Validate the given objects.
		 */
		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		checkScanRange(chromatogram, scanRange);
		/*
		 * Get the total signals and determine the start and stop background
		 * abundance.
		 */
		ITotalScanSignals totalScanSignals = getTotalScanSignals(chromatogram, scanRange);
		float firstSignal = totalScanSignals.getFirstTotalScanSignal().getTotalSignal();
		float lastSignal = totalScanSignals.getLastTotalScanSignal().getTotalSignal();
		startIntensity = startIntensity <= firstSignal ? startIntensity : firstSignal;
		stopIntensity = stopIntensity <= lastSignal ? stopIntensity : lastSignal;
		LinearEquation backgroundEquation = getBackgroundEquation(totalScanSignals, scanRange, new BackgroundAbundanceRange(startIntensity, stopIntensity));
		ITotalScanSignals peakIntensityTotalScanSignals = adjustTotalScanSignals(totalScanSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalScanSignals);
		/*
		 * Create the peak.
		 */
		IScanCSD supplierScanCSD = getPeakScan(totalScanSignals, backgroundEquation);
		IPeakModelCSD peakModel = new PeakModelCSD(supplierScanCSD, peakIntensityValues, startIntensity, stopIntensity);
		IChromatogramPeakCSD peak = new ChromatogramPeakCSD(peakModel, chromatogram);
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
	public static IChromatogramPeakCSD createPeak(IChromatogramCSD chromatogram, IScanRange scanRange, IBackgroundAbundanceRange backgroundAbundanceRange, boolean checkBackgroundAbundanceRange) throws PeakException {

		validateChromatogram(chromatogram);
		validateScanRange(scanRange);
		validateBackgroundAbundanceRange(backgroundAbundanceRange);
		ITotalScanSignals totalScanSignals = getTotalScanSignals(chromatogram, scanRange);
		/*
		 * Adjust and correct the background abundance range, if needed.
		 */
		if(checkBackgroundAbundanceRange) {
			backgroundAbundanceRange = checkBackgroundAbundanceRange(totalScanSignals, scanRange, backgroundAbundanceRange);
		}
		LinearEquation backgroundEquation = getBackgroundEquation(totalScanSignals, scanRange, backgroundAbundanceRange);
		ITotalScanSignals peakIntensityTotalIonSignals = adjustTotalScanSignals(totalScanSignals, backgroundEquation);
		IPeakIntensityValues peakIntensityValues = getPeakIntensityValues(peakIntensityTotalIonSignals);
		/*
		 * Create the peak.
		 */
		IScanCSD supplierScanCSD = getPeakScan(totalScanSignals, backgroundEquation);
		IPeakModelCSD peakModel = new PeakModelCSD(supplierScanCSD, peakIntensityValues, backgroundAbundanceRange.getStartBackgroundAbundance(), backgroundAbundanceRange.getStopBackgroundAbundance());
		IChromatogramPeakCSD peak = new ChromatogramPeakCSD(peakModel, chromatogram);
		return peak;
	}

	private static IScanCSD getPeakScan(ITotalScanSignals totalScanSignals, LinearEquation backgroundEquation) {

		ITotalScanSignal totalScanSignal = totalScanSignals.getMaxTotalScanSignal();
		int retentionTime = totalScanSignal.getRetentionTime();
		float adjustedSignal = (float)(totalScanSignal.getTotalSignal() - backgroundEquation.calculateY(retentionTime));
		IScanCSD supplierScanCSD = new ScanCSD(retentionTime, adjustedSignal);
		return supplierScanCSD;
	}

	/**
	 * Creates a {@link IPeakIntensityValues} instance from the given {@link ITotalScanSignals}.
	 * 
	 * @param peakIntensityTotalIonSignals
	 * @return {@link IPeakIntensityValues}
	 * @throws PeakException
	 */
	protected static IPeakIntensityValues getPeakIntensityValues(ITotalScanSignals peakIntensityTotalIonSignals) throws PeakException {

		assert (peakIntensityTotalIonSignals != null) : "The peak intensity total ion signals must not be null.";
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
	 * @param totalScanSignals
	 * @param backgroundEquation
	 * @return {@link ITotalScanSignals}
	 * @throws PeakException
	 */
	protected static ITotalScanSignals adjustTotalScanSignals(ITotalScanSignals totalScanSignals, LinearEquation backgroundEquation) throws PeakException {

		assert (totalScanSignals != null) : "The total ion signals must not be null.";
		assert (backgroundEquation != null) : "The background equation must not be null.";
		if(totalScanSignals == null || backgroundEquation == null) {
			throw new PeakException("The given totalIonSignals or backgroundEquation must not be null.");
		}
		ITotalScanSignal totalScanSignal;
		/*
		 * Make a deep copy of totalIonSignals, normalize the values to
		 * IPeakIntensityValues.MAX_INTENSITY.
		 */
		ITotalScanSignals peakIntensityTotalScanSignals = totalScanSignals.makeDeepCopy();
		int start = peakIntensityTotalScanSignals.getStartScan();
		int stop = peakIntensityTotalScanSignals.getStopScan();
		float adjustedSignal;
		for(int scan = start; scan <= stop; scan++) {
			totalScanSignal = peakIntensityTotalScanSignals.getTotalScanSignal(scan);
			adjustedSignal = (float)(totalScanSignal.getTotalSignal() - backgroundEquation.calculateY(totalScanSignal.getRetentionTime()));
			/*
			 * Check, that the total ion signal is >= 0!
			 */
			if(adjustedSignal < 0.0f) {
				adjustedSignal = 0.0f;
			}
			totalScanSignal.setTotalSignal(adjustedSignal);
		}
		TotalScanSignalsModifier.normalize(peakIntensityTotalScanSignals, IPeakIntensityValues.MAX_INTENSITY);
		return peakIntensityTotalScanSignals;
	}

	/**
	 * The background abundance range needs to be checked.
	 * 
	 * @param totalScanSignals
	 * @param scanRange
	 * @param backgroundAbundanceRange
	 * @throws PeakException
	 * @return {@link LinearEquation}
	 */
	protected static LinearEquation getBackgroundEquation(ITotalScanSignals totalScanSignals, IScanRange scanRange, IBackgroundAbundanceRange backgroundAbundanceRange) throws PeakException {

		assert (totalScanSignals != null) : "The total ion signals must not be null.";
		assert (scanRange != null) : "The scan range must not be null.";
		assert (backgroundAbundanceRange != null) : "The background abundance range must not be null.";
		if(totalScanSignals == null || scanRange == null || backgroundAbundanceRange == null) {
			throw new PeakException("The given totalIonSignals, scanRange or backgroundAbundanceRange must not be null.");
		}
		ITotalScanSignal totalScanSignal;
		// P1
		totalScanSignal = totalScanSignals.getTotalScanSignal(scanRange.getStartScan());
		IPoint p1 = new Point(totalScanSignal.getRetentionTime(), backgroundAbundanceRange.getStartBackgroundAbundance());
		// P2
		totalScanSignal = totalScanSignals.getTotalScanSignal(scanRange.getStopScan());
		IPoint p2 = new Point(totalScanSignal.getRetentionTime(), backgroundAbundanceRange.getStopBackgroundAbundance());
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
	 * @throws PeakException
	 * @return {@link ITotalScanSignals}
	 */
	protected static ITotalScanSignals getTotalScanSignals(IChromatogramCSD chromatogram, IScanRange scanRange) throws PeakException {

		assert (chromatogram != null) : "The chromatogram must not be null.";
		assert (scanRange != null) : "The scan range must not be null.";
		if(chromatogram == null || scanRange == null) {
			throw new PeakException("The given values must not be null.");
		}
		/*
		 * Try to get the signals.
		 */
		try {
			ITotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			return totalScanSignalExtractor.getTotalScanSignals(scanRange.getStartScan(), scanRange.getStopScan());
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
	 * @param totalScanSignals
	 * @param scanRange
	 * @param backgroundAbundanceRange
	 * @throws PeakException
	 * @return {@link IBackgroundAbundanceRange}
	 */
	protected static IBackgroundAbundanceRange checkBackgroundAbundanceRange(ITotalScanSignals totalScanSignals, IScanRange scanRange, IBackgroundAbundanceRange backgroundAbundanceRange) throws PeakException {

		ITotalScanSignal totalScanSignal;
		float background = 0.0f;
		float signal = 0.0f;
		float startBackgroundAbundance = 0.0f;
		float stopBackgroundAbundance = 0.0f;
		boolean adjustBackgroundAbundance = false;
		if(totalScanSignals == null || scanRange == null || backgroundAbundanceRange == null) {
			throw new PeakException("The given values must not be null.");
		}
		/*
		 * Check the start background abundance.
		 */
		totalScanSignal = totalScanSignals.getTotalScanSignal(scanRange.getStartScan());
		if(totalScanSignal != null) {
			background = backgroundAbundanceRange.getStartBackgroundAbundance();
			signal = totalScanSignal.getTotalSignal();
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
		totalScanSignal = totalScanSignals.getTotalScanSignal(scanRange.getStopScan());
		if(totalScanSignal != null) {
			background = backgroundAbundanceRange.getStopBackgroundAbundance();
			signal = totalScanSignal.getTotalSignal();
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
	protected static void checkScanRange(IChromatogramCSD chromatogram, IScanRange scanRange) throws PeakException {

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
	 * Checks the total ion signals and throws an exception is something is
	 * wrong.
	 * 
	 * @param totalScanSignals
	 * @throws PeakException
	 */
	protected static void validateTotalIonSignals(ITotalScanSignals totalScanSignals) throws PeakException {

		if(totalScanSignals == null) {
			throw new PeakException("The total scan signals must not be null.");
		}
	}

	/**
	 * Checks the chromatogram and throws an exception is something is wrong.
	 * 
	 * @param chromatogram
	 * @throws PeakException
	 */
	protected static void validateChromatogram(IChromatogramCSD chromatogram) throws PeakException {

		if(chromatogram == null) {
			throw new PeakException("The chromatogram must not be null.");
		}
	}
	// -------------------------------------------------protected methods
}
