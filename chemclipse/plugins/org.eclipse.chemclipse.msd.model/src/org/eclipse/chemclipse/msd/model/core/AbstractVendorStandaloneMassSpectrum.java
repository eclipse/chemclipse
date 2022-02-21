/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.Date;

public abstract class AbstractVendorStandaloneMassSpectrum extends AbstractVendorMassSpectrum implements IVendorStandaloneMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 7180911179209208597L;
	//
	private String sample;
	private String description;
	private String operator;
	private Date acquisitionDate;
	private String instrument;

	@Override
	public String getSampleName() {

		return sample;
	}

	@Override
	public void setSampleName(String name) {

		this.sample = name;
	}

	@Override
	public String getOperator() {

		return operator;
	}

	@Override
	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public Date getDate() {

		return acquisitionDate;
	}

	@Override
	public void setDate(Date acquisitionDate) {

		this.acquisitionDate = acquisitionDate;
	}

	@Override
	public String getInstrument() {

		return instrument;
	}

	@Override
	public void setInstrument(String instrument) {

		this.instrument = instrument;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}
}
