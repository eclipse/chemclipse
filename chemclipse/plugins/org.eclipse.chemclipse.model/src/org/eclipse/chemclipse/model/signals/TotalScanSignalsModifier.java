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
 * Janos Binder - implementation of further filters
 *******************************************************************************/
package org.eclipse.chemclipse.model.signals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.model.exceptions.CalculationException;
import org.eclipse.chemclipse.model.exceptions.NoTotalSignalStoredException;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;

public class TotalScanSignalsModifier {

	/**
	 * This class has only static methods.
	 */
	private TotalScanSignalsModifier() {

	}

	/**
	 * Normalizes all stored abundance values with the given
	 * ITotalIonSignals.NORMALIZATION_BASE value.
	 */
	public static void normalize(ITotalScanSignals totalIonSignals) {

		normalize(totalIonSignals, ITotalScanSignals.NORMALIZATION_BASE);
	}

	/**
	 * Normalizes all stored abundance values to the given base.<br/>
	 * The given base must be >= 1.
	 * 
	 * @param base
	 */
	public static void normalize(ITotalScanSignals totalIonSignals, float base) {

		if(totalIonSignals == null) {
			return;
		}
		if(base < 1.0f) {
			return;
		}
		/*
		 * Use double for a better precision.
		 */
		double max = totalIonSignals.getMaxSignal();
		double factor = 0.0d;
		if(max != 0.0d) {
			factor = base / max;
		}
		/*
		 * Recalculate the total signals.
		 */
		float totalSignal;
		List<ITotalScanSignal> signals = totalIonSignals.getTotalScanSignals();
		for(ITotalScanSignal signal : signals) {
			totalSignal = (float)(factor * signal.getTotalSignal());
			signal.setTotalSignal(totalSignal);
		}
	}

	/**
	 * This method normalizes the total ion signals to mean.<br/>
	 * <br/>
	 * abundance(scan) = abundance(scan) / Abs(Mean(Sum(abundance(scan start to
	 * end))))
	 * 
	 * @param totalIonSignals
	 */
	public static void meanNormalize(ITotalScanSignals totalIonSignals) throws NoTotalSignalStoredException, CalculationException {

		if(totalIonSignals == null || totalIonSignals.size() == 0) {
			throw new NoTotalSignalStoredException("There are no total ion signals stored.");
		}
		/*
		 * Get all total abundance values.
		 */
		float[] allSignals = new float[totalIonSignals.size()];
		List<ITotalScanSignal> signals = totalIonSignals.getTotalScanSignals();
		int i = 0;
		for(ITotalScanSignal signal : signals) {
			allSignals[i++] = signal.getTotalSignal();
		}
		/*
		 * Calculate the absolute mean value.
		 */
		float meanSignal = Math.abs(Calculations.getMean(allSignals));
		if(meanSignal == 0) {
			throw new CalculationException("The mean abundance must not be 0.");
		}
		/*
		 * Create the mean normalized total ion signals instance.
		 */
		ITotalScanSignal totalIonSignal;
		float normalizedAbundance;
		int startScan = totalIonSignals.getStartScan();
		int stopScan = totalIonSignals.getStopScan();
		for(int scan = startScan; scan <= stopScan; scan++) {
			totalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			/*
			 * Calculate the new abundance.
			 */
			normalizedAbundance = totalIonSignal.getTotalSignal() / meanSignal;
			if(normalizedAbundance < 0.0f) {
				normalizedAbundance = 0.0f;
			}
			/*
			 * Set the new abundance.
			 */
			totalIonSignal.setTotalSignal(normalizedAbundance);
		}
	}

	public static void medianNormalize(ITotalScanSignals totalIonSignals) throws NoTotalSignalStoredException, CalculationException {

		if(totalIonSignals == null || totalIonSignals.size() == 0) {
			throw new NoTotalSignalStoredException("There are no total ion signals stored.");
		}
		/*
		 * Get all total abundance values.
		 */
		float[] allSignals = new float[totalIonSignals.size()];
		List<ITotalScanSignal> signals = totalIonSignals.getTotalScanSignals();
		int i = 0;
		for(ITotalScanSignal signal : signals) {
			allSignals[i++] = signal.getTotalSignal();
		}
		/*
		 * Calculate the median value.
		 */
		Arrays.sort(allSignals);
		int middle = allSignals.length / 2;
		float medianSignal = (allSignals.length % 2 == 1) ? allSignals[middle] : (allSignals[middle - 1] + allSignals[middle]) / 2.0f;
		if(medianSignal == 0) {
			throw new CalculationException("The median abundance must not be 0.");
		}
		/*
		 * Create the mean normalized total ion signals instance.
		 */
		ITotalScanSignal totalIonSignal;
		float normalizedAbundance;
		int startScan = totalIonSignals.getStartScan();
		int stopScan = totalIonSignals.getStopScan();
		for(int scan = startScan; scan <= stopScan; scan++) {
			totalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			/*
			 * Calculate the new abundance.
			 */
			normalizedAbundance = totalIonSignal.getTotalSignal() / medianSignal;
			if(normalizedAbundance < 0.0f) {
				normalizedAbundance = 0.0f;
			}
			/*
			 * Set the new abundance.
			 */
			totalIonSignal.setTotalSignal(normalizedAbundance);
		}
	}

	public static void multiply(ITotalScanSignals totalIonSignals, float multiplier) throws NoTotalSignalStoredException, CalculationException {

		if(totalIonSignals == null || totalIonSignals.size() == 0) {
			throw new NoTotalSignalStoredException("There are no total ion signals stored.");
		}
		/*
		 * Create the multiplied total ion signals instance.
		 */
		int startScan = totalIonSignals.getStartScan();
		int stopScan = totalIonSignals.getStopScan();
		for(int scan = startScan; scan <= stopScan; scan++) {
			ITotalScanSignal totalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			/*
			 * Calculate the new abundance.
			 */
			float normalizedAbundance = totalIonSignal.getTotalSignal() * multiplier;
			if(normalizedAbundance < 0.0f) {
				normalizedAbundance = 0.0f;
			}
			/*
			 * Set the new abundance.
			 */
			totalIonSignal.setTotalSignal(normalizedAbundance);
		}
	}

	public static void divide(ITotalScanSignals totalIonSignals, float divisor) throws NoTotalSignalStoredException, CalculationException {

		if(totalIonSignals == null || totalIonSignals.size() == 0) {
			throw new NoTotalSignalStoredException("There are no total ion signals stored.");
		}
		/*
		 * Create the multiplied total ion signals instance.
		 */
		int startScan = totalIonSignals.getStartScan();
		int stopScan = totalIonSignals.getStopScan();
		for(int scan = startScan; scan <= stopScan; scan++) {
			ITotalScanSignal totalIonSignal = totalIonSignals.getTotalScanSignal(scan);
			/*
			 * Calculate the new abundance.
			 */
			float normalizedAbundance = totalIonSignal.getTotalSignal() / divisor;
			if(normalizedAbundance < 0.0f) {
				normalizedAbundance = 0.0f;
			}
			/*
			 * Set the new abundance.
			 */
			totalIonSignal.setTotalSignal(normalizedAbundance);
		}
	}

	public static void calculateMovingAverage(ITotalScanSignals totalIonSignals, WindowSize windowSize) {

		/*
		 * Return if the windowSize or totalIonSignals is null.
		 */
		if(windowSize == null || WindowSize.NONE.equals(windowSize) || totalIonSignals == null) {
			return;
		}
		/*
		 * Return if the available number of totalIonSignals are lower than the
		 * window size.
		 */
		if(totalIonSignals.size() < windowSize.getSize()) {
			return;
		}
		int diff = windowSize.getSize() / 2;
		int windowStop = windowSize.getSize() - diff;
		/*
		 * Moving average calculation.
		 */
		int size = totalIonSignals.size() - diff;
		float[] values = new float[windowSize.getSize()];
		for(int i = diff; i < size; i++) {
			for(int j = -diff, k = 0; j < windowStop; j++, k++) {
				/*
				 * Why i+j+1? The index of total ion signals is 1 and not 0.
				 */
				values[k] = totalIonSignals.getTotalScanSignal(i + j + 1).getTotalSignal();
			}
			/*
			 * Set the new total signal value. Why +1? The index of total ion
			 * signals is 1 and not 0.
			 */
			totalIonSignals.getTotalScanSignal(i + 1).setTotalSignal(Calculations.getMean(values));
		}
	}

	public static void unitSumNormalize(ITotalScanSignals totalIonSignals, double areaSumIntensity) throws NoTotalSignalStoredException {

		if(totalIonSignals == null || totalIonSignals.size() == 0) {
			throw new NoTotalSignalStoredException("There are no total ion signals stored.");
		}
		/*
		 * Recalculate the total signals.
		 */
		List<ITotalScanSignal> signals = totalIonSignals.getTotalScanSignals();
		for(ITotalScanSignal signal : signals) {
			float totalSignal = (float)(signal.getTotalSignal() / areaSumIntensity);
			signal.setTotalSignal(totalSignal);
		}
	}
}
