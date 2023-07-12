/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.misc;

import java.util.HashSet;
import java.util.Set;

/**
 * Example in *.CID file:
 * ---
 * |C106-50-3 |FC6H8N2
 * 1,4-Benzenediamine
 * ---
 * |C4128-17-0 |FC17H28O2 |RI1840 |RT12.8
 * 2,6,10-Dodecatrien-1-ol, 3,7,11-trimethyl-, acetate, (E,E)-
 */
public class CompoundInformation {

	private String casNumber = "";
	private String formula = "";
	private String retentionTime = "";
	private String retentionIndex = "";
	private String retentionWindow = "";
	private String signalToNoise = "";
	private String chemicalClass = "";
	private String referenceConcentration = "";
	private String compensationFactor = "";
	private String minMatchFactor = "";
	private String number = "";
	private String name = "";
	private Set<String> miscellaneous = new HashSet<>();
	private Set<String> synonyms = new HashSet<>();

	public String getCasNumber() {

		return casNumber;
	}

	public void setCasNumber(String casNumber) {

		this.casNumber = casNumber;
	}

	public String getFormula() {

		return formula;
	}

	public void setFormula(String formula) {

		this.formula = formula;
	}

	public String getRetentionTime() {

		return retentionTime;
	}

	public void setRetentionTime(String retentionTime) {

		this.retentionTime = retentionTime;
	}

	public String getRetentionIndex() {

		return retentionIndex;
	}

	public void setRetentionIndex(String retentionIndex) {

		this.retentionIndex = retentionIndex;
	}

	public String getRetentionWindow() {

		return retentionWindow;
	}

	public void setRetentionWindow(String retentionWindow) {

		this.retentionWindow = retentionWindow;
	}

	public String getSignalToNoise() {

		return signalToNoise;
	}

	public void setSignalToNoise(String signalToNoise) {

		this.signalToNoise = signalToNoise;
	}

	public String getChemicalClass() {

		return chemicalClass;
	}

	public void setChemicalClass(String chemicalClass) {

		this.chemicalClass = chemicalClass;
	}

	public String getReferenceConcentration() {

		return referenceConcentration;
	}

	public void setReferenceConcentration(String referenceConcentration) {

		this.referenceConcentration = referenceConcentration;
	}

	public String getCompensationFactor() {

		return compensationFactor;
	}

	public void setCompensationFactor(String compensationFactor) {

		this.compensationFactor = compensationFactor;
	}

	public String getMinMatchFactor() {

		return minMatchFactor;
	}

	public void setMinMatchFactor(String minMatchFactor) {

		this.minMatchFactor = minMatchFactor;
	}

	public String getNumber() {

		return number;
	}

	public void setNumber(String number) {

		this.number = number;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public Set<String> getMiscellaneous() {

		return miscellaneous;
	}

	public Set<String> getSynonyms() {

		return synonyms;
	}
}