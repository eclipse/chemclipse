/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add contains method
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IScan;

/**
 * @author eselmeister
 */
public interface IScanRange {

	int MIN_SCAN = 1;
	int MAX_SCAN = Integer.MAX_VALUE;

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
	 * Returns the width of the scan range.
	 * 
	 * @return int
	 */
	default int getWidth() {

		int stopScan = getStopScan();
		int startScan = getStartScan();
		if(startScan == stopScan) {
			return 0;
		}
		return stopScan - startScan + 1;
	}

	/**
	 * Compares this objects content to the other objects content, the default implementation compares {@link #getStartScan()}, {@link #getStopScan()}
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare different instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1}
	 * 
	 * @param other
	 * @return
	 */
	default boolean contentEquals(IScanRange other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		return getStopScan() == other.getStopScan() && getStartScan() == other.getStartScan();
	}

	default boolean containsScan(IScan scan) {

		return containsScan(scan.getScanNumber());
	}

	default boolean containsScan(int scan) {

		return scan >= getStartScan() && scan <= getStopScan();
	}
}
