/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model;

public class SpectrumMSMS extends AbstractSpectrum {

	public static final int version = 1;
	private SpectrumMS parentMS;
	private Peak precursorPeak;

	@Override
	public String toString() {

		return "MSMS precursor='" + precursorPeak + "', parentMS='" + getParentMSName() + "', num. of peaks: '" + getNumberOfPeak() + "'";
	}

	public SpectrumMSMS() {
		super();
	}

	public SpectrumMSMS(long id, String name) {
		super(id, name);
	}

	private String getParentMSName() {

		if(parentMS != null) {
			return parentMS.getName();
		}
		return null;
	}

	public SpectrumMS getParentMS() {

		return parentMS;
	}

	public void setParentMS(SpectrumMS parentMS1) {

		this.parentMS = parentMS1;
	}

	public Peak getPrecursorPeak() {

		return precursorPeak;
	}

	public void setPrecursorPeak(Peak precursorPeak) {

		this.precursorPeak = precursorPeak;
	}
}
