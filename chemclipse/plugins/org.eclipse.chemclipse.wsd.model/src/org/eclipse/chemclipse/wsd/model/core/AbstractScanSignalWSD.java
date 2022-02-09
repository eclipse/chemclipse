/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

public abstract class AbstractScanSignalWSD implements IScanSignalWSD {

	private static final long serialVersionUID = -1057079378330160588L;
	private static final int MAX_PRECISION = 6;
	//
	private double wavelength;
	private float abundance;

	@Override
	public double getWavelength() {

		return wavelength;
	}

	@Override
	public void setWavelength(double wavelength) {

		this.wavelength = wavelength;
	}

	@Override
	public float getAbundance() {

		return abundance;
	}

	@Override
	public void setAbundance(float abundance) {

		this.abundance = abundance;
	}

	public static int getWavelength(double wavelength) {

		return (int)Math.round(wavelength);
	}

	// TODO JUnit
	/**
	 * Returns the given ion as an value rounded to the given precision.
	 * E.g.:
	 * ion = 28.78749204
	 * precision 1 => 28.8
	 * precision 2 => 28.79
	 * precision 3 => 28.787
	 * precision 4 => 28.7875
	 * precision 5 => 28.78749
	 * precision 6 => 28.787492
	 *
	 * The precision of 6 is the maximum. If the precious is outward of
	 * this bounds it will set to 1.
	 */
	public static double getWavelength(double wavelength, int precision) {

		/*
		 * Should another rounding range be used? E.g 0.7 - 1.7?
		 */
		if(precision <= 0 || precision > MAX_PRECISION) {
			precision = 1;
		}
		double factor = Math.pow(10, precision);
		return Math.round(wavelength * factor) / factor;
	}
}