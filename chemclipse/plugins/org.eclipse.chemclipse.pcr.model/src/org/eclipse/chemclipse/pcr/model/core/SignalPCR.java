/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import org.eclipse.chemclipse.model.core.AbstractSignal;

public class SignalPCR extends AbstractSignal implements ISignalPCR, Comparable<ISignalPCR> {

	private static final long serialVersionUID = 4011351889173887912L;
	//
	private double cycle = 0.0d;
	private double intensity = 0.0d;

	public SignalPCR(double chemicalShift, double intensity) {
		this.cycle = chemicalShift;
		this.intensity = intensity;
	}

	@Override
	public double getX() {

		return cycle;
	}

	@Override
	public double getY() {

		return intensity;
	}

	@Override
	public double getCycle() {

		return cycle;
	}

	@Override
	public void setCycle(double cycle) {

		this.cycle = cycle;
	}

	@Override
	public double getIntensity() {

		return intensity;
	}

	@Override
	public void setIntensity(double intensity) {

		this.intensity = intensity;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cycle);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		SignalPCR other = (SignalPCR)obj;
		if(Double.doubleToLongBits(cycle) != Double.doubleToLongBits(other.cycle))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "SignalNMR [cycle=" + cycle + ", intensity=" + intensity + "]";
	}

	@Override
	public int compareTo(ISignalPCR signalNMR) {

		if(signalNMR != null) {
			return Double.compare(cycle, signalNMR.getCycle());
		} else {
			return 0;
		}
	}
}
