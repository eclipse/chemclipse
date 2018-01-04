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
package org.eclipse.chemclipse.model.support;

/**
 * @author eselmeister
 */
public class ScanRange implements IScanRange {

	private int startScan;
	private int stopScan;

	/**
	 * Creates a new ScanRange object. There are some limitations:<br/>
	 * The startScan may not be <= 1 and > MAX_SCAN. In such a case it will be
	 * set to MIN_SCAN. The stopScan should be <= MAX_SCAN and > 0 otherwise it
	 * will be set to MAX_SCAN.
	 * 
	 * @param startScan
	 * @param stopScan
	 */
	public ScanRange(int startScan, int stopScan) {
		if(startScan > stopScan) {
			int tmp = startScan;
			startScan = stopScan;
			stopScan = tmp;
		}
		if(startScan < MIN_SCAN || startScan > MAX_SCAN) {
			startScan = MIN_SCAN;
		}
		if(stopScan > MAX_SCAN || stopScan < MIN_SCAN) {
			stopScan = MAX_SCAN;
		}
		this.startScan = startScan;
		this.stopScan = stopScan;
	}

	@Override
	public int getStartScan() {

		return startScan;
	}

	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public int getWidth() {

		return stopScan - startScan + 1;
	}

	// -----------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(this == other) {
			return true;
		}
		if(other == null) {
			return false;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		ScanRange otherRange = (ScanRange)other;
		return getStartScan() == otherRange.getStartScan() && getStopScan() == otherRange.getStopScan();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(getStartScan()).hashCode() + 11 * Integer.valueOf(getStopScan()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("startScan=" + getStartScan());
		builder.append(",");
		builder.append("stopScan=" + getStopScan());
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
