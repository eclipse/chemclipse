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

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;

public interface ITotalScanSignals {

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
	ITotalScanSignal getNextTotalScanSignal(int scan);

	/**
	 * Returns the previous scan relative to the given scan.<br/>
	 * If no scan is available, null will be returned.
	 * 
	 * @param scan
	 * @return IPoint
	 */
	ITotalScanSignal getPreviousTotalScanSignal(int scan);

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
	float getMaxSignal();

	/**
	 * Returns the lowest total signal from the stored total ion signals.
	 * 
	 * @return float
	 */
	float getMinSignal();

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
	 * Returns the highest total ion signal.
	 * 
	 * @return {@link ITotalScanSignal}
	 */
	ITotalScanSignal getMaxTotalScanSignal();

	/**
	 * Returns the lowest total ion signal.
	 * 
	 * @return {@link ITotalScanSignal}
	 */
	ITotalScanSignal getMinTotalScanSignal();

	/**
	 * Sets all negative total ion signals to 0.
	 */
	void setNegativeTotalSignalsToZero();

	/**
	 * Sets all positive total ion signals to 0.
	 */
	void setPositiveTotalSignalsToZero();

	/**
	 * Sets all total signals as its absolute value.
	 */
	void setTotalSignalsAsAbsoluteValues();
}
