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

/**
 * If you need signals with a negative total signal range, use {@link ExtendedTotalScanSignal}.
 * 
 * @author eselmeister
 */
public class TotalScanSignal extends AbstractTotalScanSignal implements ITotalScanSignal {

	/**
	 * Add the values with a positive check.
	 * 
	 * @param retentionTime
	 * @param retentionIndex
	 * @param totalSignal
	 */
	public TotalScanSignal(int retentionTime, float retentionIndex, float totalSignal) {
		this(retentionTime, retentionIndex, totalSignal, true);
	}

	/**
	 * Validates that retention time and retention index are positive.
	 * If validatePositive is true, it will be checked that totalSignal is positive too.
	 * 
	 * @param retentionTime
	 * @param retentionIndex
	 * @param totalSignal
	 * @param validatePositive
	 */
	public TotalScanSignal(int retentionTime, float retentionIndex, float totalSignal, boolean validatePositive) {
		if(retentionTime >= 0) {
			setRetentionTime(retentionTime);
		}
		if(retentionIndex >= 0) {
			setRetentionIndex(retentionIndex);
		}
		setTotalSignal(totalSignal, validatePositive);
	}

	@Override
	public ITotalScanSignal makeDeepCopy() {

		return new TotalScanSignal(getRetentionTime(), getRetentionIndex(), getTotalSignal(), false);
	}
	// ------------------------------ITotalIonSignal
}
