/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

public class WavelengthRange implements IWavelengthRange {

	private int startWavelength;
	private int stopWavelength;

	public WavelengthRange(int startWavelength, int stopWavelength) {
		if(startWavelength > stopWavelength) {
			int tmp = startWavelength;
			startWavelength = stopWavelength;
			stopWavelength = tmp;
		}
		if(startWavelength < MIN_WAVELENGTH || startWavelength > MAX_WAVELENGTH) {
			startWavelength = MIN_WAVELENGTH;
		}
		if(stopWavelength > MAX_WAVELENGTH || stopWavelength < MIN_WAVELENGTH) {
			stopWavelength = MAX_WAVELENGTH;
		}
		this.startWavelength = startWavelength;
		this.stopWavelength = stopWavelength;
	}

	@Override
	public int getStartWavelength() {

		return startWavelength;
	}

	@Override
	public int getStopWavelength() {

		return stopWavelength;
	}

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
		WavelengthRange otherRange = (WavelengthRange)other;
		return getStartWavelength() == otherRange.getStartWavelength() && getStopWavelength() == otherRange.getStopWavelength();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(getStartWavelength()).hashCode() + 11 * Integer.valueOf(getStopWavelength()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("startWavelength=" + getStartWavelength());
		builder.append(",");
		builder.append("stopWavelength=" + getStopWavelength());
		builder.append("]");
		return builder.toString();
	}
}
