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
 * What's the difference between {@link TotalScanSignal} and {@link ExtendedTotalScanSignal}?<br/>
 * The extended total ion signal allows also negative total signals.
 * 
 * @author eselmeister
 */
public class ExtendedTotalScanSignal extends AbstractTotalScanSignal implements ITotalScanSignal {

	/*
	 * No limits like in TotalIonSignal.
	 */
	public ExtendedTotalScanSignal(int retentionTime, float retentionIndex, float totalSignal) {
		setRetentionTime(retentionTime);
		setRetentionIndex(retentionIndex);
		setTotalSignal(totalSignal);
	}

	// ------------------------------ITotalIonSignal
	@Override
	public ITotalScanSignal makeDeepCopy() {

		return new ExtendedTotalScanSignal(getRetentionTime(), getRetentionIndex(), getTotalSignal());
	}
	// ------------------------------ITotalIonSignal
}
