/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.traces;

/**
 * HPLC-DAD
 * ...
 */
public class TraceHighResWSD extends TraceGenericDelta {

	public double getWavelength() {

		return getValue();
	}

	public void setWavelength(double wavelength) {

		setValue(wavelength);
	}

	public double getStartWavelength() {

		return getWavelength() - getDelta();
	}

	public double getStopWavelength() {

		return getWavelength() + getDelta();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getWavelength());
		if(getDelta() > 0 && getWavelength() > 0) {
			builder.append(ITrace.INFIX_RANGE_STANDARD);
			builder.append(getDelta());
		}
		builder.append(getScaleFactorAsString());
		//
		return builder.toString();
	}
}