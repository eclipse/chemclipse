/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Due to the frequent occurrence of this element, names are kept short.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataType", propOrder = {"tar", "cq", "n0", "ampEffMet", "ampEff", "ampEffSE", "corrF", "corrP", "corrCq", "meltTemp", "excl", "note", "adp", "mdp", "endPt", "bgFluor", "bgFluorSlp", "quantFluor"})
public class DataType {

	@XmlElement(required = true)
	protected IdReferencesType tar;
	protected Float cq;
	@XmlElement(name = "N0")
	protected Float n0;
	protected String ampEffMet;
	protected Float ampEff;
	protected Float ampEffSE;
	protected Float corrF;
	protected Float corrP;
	protected Float corrCq;
	protected Float meltTemp;
	protected String excl;
	protected String note;
	protected List<DpAmpCurveType> adp;
	protected List<DpMeltingCurveType> mdp;
	protected Float endPt;
	protected Float bgFluor;
	protected Float bgFluorSlp;
	protected Float quantFluor;

	public IdReferencesType getTar() {

		return tar;
	}

	public void setTar(IdReferencesType value) {

		this.tar = value;
	}

	public Float getCq() {

		return cq;
	}

	public void setCq(Float value) {

		this.cq = value;
	}

	public Float getN0() {

		return n0;
	}

	public void setN0(Float value) {

		this.n0 = value;
	}

	public String getAmpEffMet() {

		return ampEffMet;
	}

	public void setAmpEffMet(String value) {

		this.ampEffMet = value;
	}

	public Float getAmpEff() {

		return ampEff;
	}

	public void setAmpEff(Float value) {

		this.ampEff = value;
	}

	public Float getAmpEffSE() {

		return ampEffSE;
	}

	public void setAmpEffSE(Float value) {

		this.ampEffSE = value;
	}

	public Float getCorrF() {

		return corrF;
	}

	public void setCorrF(Float value) {

		this.corrF = value;
	}

	public Float getCorrP() {

		return corrP;
	}

	public void setCorrP(Float value) {

		this.corrP = value;
	}

	public Float getCorrCq() {

		return corrCq;
	}

	public void setCorrCq(Float value) {

		this.corrCq = value;
	}

	public Float getMeltTemp() {

		return meltTemp;
	}

	public void setMeltTemp(Float value) {

		this.meltTemp = value;
	}

	public String getExcl() {

		return excl;
	}

	public void setExcl(String value) {

		this.excl = value;
	}

	public String getNote() {

		return note;
	}

	public void setNote(String value) {

		this.note = value;
	}

	public List<DpAmpCurveType> getAdp() {

		if(adp == null) {
			adp = new ArrayList<DpAmpCurveType>();
		}
		return this.adp;
	}

	public List<DpMeltingCurveType> getMdp() {

		if(mdp == null) {
			mdp = new ArrayList<DpMeltingCurveType>();
		}
		return this.mdp;
	}

	public Float getEndPt() {

		return endPt;
	}

	public void setEndPt(Float value) {

		this.endPt = value;
	}

	public Float getBgFluor() {

		return bgFluor;
	}

	public void setBgFluor(Float value) {

		this.bgFluor = value;
	}

	public Float getBgFluorSlp() {

		return bgFluorSlp;
	}

	public void setBgFluorSlp(Float value) {

		this.bgFluorSlp = value;
	}

	public Float getQuantFluor() {

		return quantFluor;
	}

	public void setQuantFluor(Float value) {

		this.quantFluor = value;
	}
}
