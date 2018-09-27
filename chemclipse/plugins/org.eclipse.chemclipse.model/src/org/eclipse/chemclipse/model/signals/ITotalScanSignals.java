/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.signals;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

public interface ITotalScanSignals extends Iterable<Integer> {

	float NORMALIZATION_BASE = 1000.0f;

	/**
	 * Returns the chromatogram where these signals derive from.
	 * 
	 * @return {@link IChromatogram}
	 */
	@SuppressWarnings("rawtypes")
	IChromatogram getChromatogram();

	/**
	 * Adds an {@link ITotalScanSignal} instance at the end of the stored
	 * signals.
	 * 
	 * @param totalScanSignal
	 */
	void add(ITotalScanSignal totalScanSignal);

	/**
	 * Returns an {@link ITotalScanSignal} object.<br/>
	 * If no object is available, null will be returned.
	 * 
	 * @param scan
	 * @return ITotalIonSignal
	 */
	ITotalScanSignal getTotalScanSignal(int scan);

	/**
	 * Returns the next scan relative to the given scan.<br/>
	 * If no scan is available, null will be returned.
	 * 
	 * @param scan
	 * @return ITotalIonSignal
	 */
	default ITotalScanSignal getNextTotalScanSignal(int scan) {

		ITotalScanSignal signal = getTotalScanSignal(++scan);
		return signal;
	}

	/**
	 * Returns the previous scan relative to the given scan.<br/>
	 * If no scan is available, null will be returned.
	 * 
	 * @param scan
	 * @return IPoint
	 */
	default ITotalScanSignal getPreviousTotalScanSignal(int scan) {

		ITotalScanSignal signal = getTotalScanSignal(--scan);
		return signal;
	}

	/**
	 * Returns the size.
	 * 
	 * @return int
	 */
	int size();

	/**
	 * Returns the start scan number.
	 * 
	 * @return int
	 */
	int getStartScan();

	/**
	 * Returns the stop scan number.
	 * 
	 * @return int
	 */
	int getStopScan();

	/**
	 * Returns the highest total signal from the stored total ion signals.
	 * 
	 * @return float
	 */
	default float getMaxSignal() {

		if(size() == 0) {
			return 0.0f;
		}
		/*
		 * Get the highest value.
		 */
		float[] values = getValues();
		float max = Calculations.getMax(values);
		return max;
	}

	/**
	 * Returns the lowest total signal from the stored total ion signals.
	 * 
	 * @return float
	 */
	default float getMinSignal() {

		if(size() == 0) {
			return 0.0f;
		}
		/*
		 * Get the lowest value.
		 */
		float[] values = getValues();
		float min = Calculations.getMin(values);
		return min;
	}

	/**
	 * Makes a deep copy of the actual total ion signals list.
	 * 
	 * @return ITotalIonSignals
	 */
	ITotalScanSignals makeDeepCopy();

	/**
	 * Returns a list of the stored total ion signals. The list is a copy.
	 * Remove or add total ion signals with the appropriate methods of this
	 * interface.
	 * 
	 * @return List<ITotalIonSignal>
	 */
	List<ITotalScanSignal> getTotalScanSignals();

	/**
	 * Returns a list of the stored total ion signals. The list is unmodifiable.
	 * 
	 * @return List<ITotalIonSignal>
	 */
	Collection<ITotalScanSignal> getTotalScanSignalCollection();

	/**
	 * Returns the highest total ion signal.
	 * 
	 * @return {@link ITotalScanSignal}
	 */
	default ITotalScanSignal getMaxTotalScanSignal() {

		return Collections.max(getTotalScanSignalCollection(), new TotalScanSignalComparator());
	}

	/**
	 * Returns the lowest total ion signal.
	 * 
	 * @return {@link ITotalScanSignal}
	 */
	default ITotalScanSignal getMinTotalScanSignal() {

		return Collections.min(getTotalScanSignalCollection(), new TotalScanSignalComparator());
	}

	/**
	 * Sets all negative total ion signals to 0.
	 */
	default void setNegativeTotalSignalsToZero() {

		for(ITotalScanSignal signal : getTotalScanSignalCollection()) {
			if(signal.getTotalSignal() < 0) {
				signal.setTotalSignal(0.0f);
			}
		}
	}

	/**
	 * Sets all positive total ion signals to 0.
	 */
	default void setPositiveTotalSignalsToZero() {

		for(ITotalScanSignal signal : getTotalScanSignalCollection()) {
			if(signal.getTotalSignal() > 0) {
				signal.setTotalSignal(0.0f);
			}
		}
	}

	/**
	 * Sets all total signals as its absolute value.
	 */
	default void setTotalSignalsAsAbsoluteValues() {

		float abundance = 0.0f;
		for(ITotalScanSignal signal : getTotalScanSignalCollection()) {
			abundance = Math.abs(signal.getTotalSignal());
			signal.setTotalSignal(abundance);
		}
	}

	/**
	 * Returns all total ion signals as an float array.
	 * 
	 * @return float[]
	 */
	default float[] getValues() {

		float[] values = new float[size()];
		int i = 0;
		for(ITotalScanSignal signal : getTotalScanSignalCollection()) {
			values[i++] = signal.getTotalSignal();
		}
		return values;
	}
}
