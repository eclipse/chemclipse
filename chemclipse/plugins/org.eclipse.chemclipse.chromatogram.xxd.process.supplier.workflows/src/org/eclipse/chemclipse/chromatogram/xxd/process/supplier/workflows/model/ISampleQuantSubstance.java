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

public interface ISampleQuantSubstance {

	String TYPE_ISTD = "ISTD";
	String TYPE_SAMPLE = "SAMPLE";

	int getId();

	void setId(int id);

	String getCasNumber();

	void setCasNumber(String casNumber);

	String getName();

	void setName(String name);

	int getMaxScan();

	void setMaxScan(int maxScan);

	double getConcentration();

	void setConcentration(double concentration);

	String getUnit();

	void setUnit(String unit);

	String getMisc();

	void setMisc(String misc);

	String getType();

	double getMinMatchQuality();

	void setMinMatchQuality(double minMatchQuality);

	double getMatchQuality();

	void setMatchQuality(double matchQuality);

	boolean isValidated();

	void setValidated(boolean validated);
}
