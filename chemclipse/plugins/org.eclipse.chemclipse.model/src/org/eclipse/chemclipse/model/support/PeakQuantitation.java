/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.List;

public class PeakQuantitation {

	private int retentionTime = 0;
	private double integratedArea = 0;
	private String name = "";
	private String classifier = "";
	private String quantifier = "";
	private List<Double> concentrations;

	public PeakQuantitation() {
		concentrations = new ArrayList<Double>();
	}

	public int getRetentionTime() {

		return retentionTime;
	}

	public void setRetentionTime(int retentionTime) {

		this.retentionTime = retentionTime;
	}

	public double getIntegratedArea() {

		return integratedArea;
	}

	public void setIntegratedArea(double integratedArea) {

		this.integratedArea = integratedArea;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getClassifier() {

		return classifier;
	}

	public void setClassifier(String classifier) {

		this.classifier = classifier;
	}

	public String getQuantifier() {

		return quantifier;
	}

	public void setQuantifier(String quantifier) {

		this.quantifier = quantifier;
	}

	public List<Double> getConcentrations() {

		return concentrations;
	}
}
