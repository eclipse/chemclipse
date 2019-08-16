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

public interface ITotalScanSignal {

	/**
	 * Returns the retention time.<br/>
	 * The retention time is stored in milliseconds.
	 * 
	 * @return int
	 */
	int getRetentionTime();

	/**
	 * Sets the retention time.
	 * 
	 * @param retentionTime
	 */
	void setRetentionTime(int retentionTime);

	/**
	 * Returns the retention index.
	 * 
	 * @return float
	 */
	float getRetentionIndex();

	/**
	 * Sets the retention index.
	 * 
	 * @param retentionIndex
	 */
	void setRetentionIndex(float retentionIndex);

	/**
	 * Returns the total ion signal
	 * 
	 * @return float
	 */
	float getTotalSignal();

	/**
	 * Sets a total signal.
	 * Validates automatically that only positive values are added.
	 * 
	 * @param totalSignal
	 */
	void setTotalSignal(float totalSignal);

	/**
	 * Sets a total signal.
	 * Set if only positive values shall be added.
	 * 
	 * @param totalSignal
	 * @param validatePositive
	 */
	void setTotalSignal(float totalSignal, boolean validatePositive);

	/**
	 * Returns a deep copy of the actual total ion signal.
	 * 
	 * @return {@link ITotalScanSignal}
	 */
	ITotalScanSignal makeDeepCopy();
}
