/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class PeakQuantitation {

	private int retentionTime = 0;
	private double integratedArea = 0;
	private String name = "";
	private String casNumber = "";
	private String referenceIdentifier = "";
	private String classifier = "";
	private String quantifier = "";
	private List<Double> concentrations = new ArrayList<>();
	/*
	 * Used as a reference
	 */
	private IChromatogramSelection<?, ?> chromatogramSelection;
	private IPeak peak;

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

	public String getCasNumber() {

		return casNumber;
	}

	public void setCasNumber(String casNumber) {

		this.casNumber = casNumber;
	}

	public String getReferenceIdentifier() {

		return referenceIdentifier;
	}

	public void setReferenceIdentifier(String referenceIdentifier) {

		this.referenceIdentifier = referenceIdentifier;
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

	public IChromatogramSelection<?, ?> getChromatogramSelection() {

		return chromatogramSelection;
	}

	public void setChromatogramSelection(IChromatogramSelection<?, ?> chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	public IPeak getPeak() {

		return peak;
	}

	public void setPeak(IPeak peak) {

		this.peak = peak;
	}
}