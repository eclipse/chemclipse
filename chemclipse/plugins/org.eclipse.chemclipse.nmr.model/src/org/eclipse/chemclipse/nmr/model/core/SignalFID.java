/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.Objects;

import org.apache.commons.math3.complex.Complex;

public class SignalFID implements ISignalFID {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2563457000126174921L;
	private long time;
	private Complex intensityUnprocessedFID;
	private Complex intensityProcessedFID;
	private Complex intensityPreprocessedFID;

	public SignalFID() {

		super();
	}

	public SignalFID(long time, Complex intesityFID) {

		this();
		this.time = time;
		this.intensityUnprocessedFID = intesityFID;
		this.intensityProcessedFID = intesityFID;
		this.intensityPreprocessedFID = intesityFID;
	}

	@Override
	public double getX() {

		return time;
	}

	@Override
	public double getY() {

		return intensityProcessedFID.getReal();
	}

	@Override
	public Complex getIntensityProcessedFID() {

		return intensityProcessedFID;
	}

	@Override
	public void setIntensityProcessedFID(Complex processedIntensity) {

		this.intensityProcessedFID = processedIntensity;
	}

	@Override
	public void resetIntensityProcessedFID() {

		intensityProcessedFID = intensityPreprocessedFID;
	}

	@Override
	public void setIntensityUnprocessedFID(Complex unprocessedIntensity) {

		this.intensityUnprocessedFID = unprocessedIntensity;
	}

	@Override
	public Complex getIntensityUnprocessedFID() {

		return intensityUnprocessedFID;
	}

	@Override
	public long getAcquisitionTime() {

		return time;
	}

	@Override
	public void setAcquisitionTime(long nanoseconds) {

		this.time = nanoseconds;
	}

	@Override
	public int compareTo(ISignalFID o) {

		if(o != null) {
			return Long.compare(time, o.getAcquisitionTime());
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {

		return Objects.hash(time);
	}

	@Override
	public Complex getIntensityPreprocessedFID() {

		return intensityPreprocessedFID;
	}

	@Override
	public void setIntensityPreprocessedFID(Complex processedIntensity) {

		this.intensityPreprocessedFID = processedIntensity;
	}
}
