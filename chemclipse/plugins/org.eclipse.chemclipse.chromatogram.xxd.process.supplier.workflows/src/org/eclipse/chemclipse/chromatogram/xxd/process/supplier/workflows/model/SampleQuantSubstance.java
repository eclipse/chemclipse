/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;

public class SampleQuantSubstance implements ISampleQuantSubstance {

	private int id = 0;
	private String casNumber = "";
	private String name = "";
	private int maxScan = 0;
	private double concentration = 0.0d;
	private String unit = "";
	private String misc = "";
	private double minMatchQuality = PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_DEF;
	private double matchQuality = 0.0d;
	private boolean validated = false;

	@Override
	public int getId() {

		return id;
	}

	@Override
	public void setId(int id) {

		this.id = id;
	}

	@Override
	public String getCasNumber() {

		return casNumber;
	}

	@Override
	public void setCasNumber(String casNumber) {

		this.casNumber = casNumber;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public int getMaxScan() {

		return maxScan;
	}

	@Override
	public void setMaxScan(int maxScan) {

		this.maxScan = maxScan;
	}

	@Override
	public double getConcentration() {

		return concentration;
	}

	@Override
	public void setConcentration(double concentration) {

		this.concentration = concentration;
	}

	@Override
	public String getUnit() {

		return unit;
	}

	@Override
	public void setUnit(String unit) {

		this.unit = unit;
	}

	@Override
	public String getMisc() {

		return misc;
	}

	@Override
	public void setMisc(String misc) {

		this.misc = misc;
	}

	@Override
	public String getType() {

		if(matchQuality > 0.0d) {
			return TYPE_SAMPLE;
		} else {
			return TYPE_ISTD;
		}
	}

	@Override
	public double getMatchQuality() {

		return matchQuality;
	}

	@Override
	public void setMatchQuality(double matchQuality) {

		this.matchQuality = matchQuality;
		validated = (matchQuality < minMatchQuality) ? false : true;
	}

	@Override
	public double getMinMatchQuality() {

		return minMatchQuality;
	}

	@Override
	public void setMinMatchQuality(double minMatchQuality) {

		this.minMatchQuality = minMatchQuality;
		validated = (matchQuality < minMatchQuality) ? false : true;
	}

	@Override
	public boolean isValidated() {

		return validated;
	}

	@Override
	public void setValidated(boolean validated) {

		this.validated = validated;
	}
}
