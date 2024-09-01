/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.chemclipse.model.core.IMassSpectrumPeak;

public abstract class AbstractVendorStandaloneMassSpectrum extends AbstractVendorMassSpectrum implements IVendorStandaloneMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 7180911179209208598L;
	//
	private File file;
	private String sample;
	private String description;
	private String operator;
	private Date acquisitionDate;
	private String instrument;
	private List<IMassSpectrumPeak> peaks = new ArrayList<>();

	@Override
	public File getFile() {

		return file;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	@Override
	public String getName() {

		String name = "No file has been set yet.";
		if(file != null) {
			name = file.getName();
		}
		return name;
	}

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

	@Override
	public List<IMassSpectrumPeak> getPeaks() {

		return peaks;
	}
}