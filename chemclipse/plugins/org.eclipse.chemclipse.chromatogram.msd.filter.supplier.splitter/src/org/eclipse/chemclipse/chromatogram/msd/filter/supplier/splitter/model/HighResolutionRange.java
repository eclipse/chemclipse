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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model;

import java.util.Objects;

public class HighResolutionRange {

	private double mz = 0.0d;
	private int binning = 0;
	private double traceStart = 0.0d;
	private double traceStop = 0.0d;

	/*
	 * 400.01627±10ppm => ±0.004000163
	 * 400.01627 * 10 / 1000000 = 0.004000163
	 */
	public HighResolutionRange(double mz, int binning) {

		this.mz = mz;
		this.binning = binning;
		//
		if(binning > 0) {
			double delta = mz * binning / 1000000.0d;
			traceStart = mz - delta;
			traceStop = mz + delta;
		} else {
			traceStart = mz;
			traceStop = mz;
		}
	}

	public boolean matches(double mz) {

		return mz >= traceStart && mz <= traceStop;
	}

	public double getMZ() {

		return mz;
	}

	public void setMz(double mz) {

		this.mz = mz;
	}

	public double getTraceStart() {

		return traceStart;
	}

	public double getTraceStop() {

		return traceStop;
	}

	@Override
	public int hashCode() {

		return Objects.hash(binning, mz);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		HighResolutionRange other = (HighResolutionRange)obj;
		return binning == other.binning && Double.doubleToLongBits(mz) == Double.doubleToLongBits(other.mz);
	}

	@Override
	public String toString() {

		return "HighResolutionRange [mz=" + mz + ", binning=" + binning + ", traceStart=" + traceStart + ", traceStop=" + traceStop + "]";
	}
}