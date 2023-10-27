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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model;

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
@XmlType(name = "dataType", propOrder = {"tar", "cq", "excl", "adp", "mdp", "endPt", "bgFluor", "bgFluorSlp", "quantFluor"})
public class DataType {

	@XmlElement(required = true)
	protected IdReferencesType tar;
	protected float cq;
	protected String excl;
	protected List<DpAmpCurveType> adp;
	protected List<DpMeltingCurveType> mdp;
	protected float endPt;
	protected float bgFluor;
	protected float bgFluorSlp;
	protected float quantFluor;

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

	public String getExcl() {

		return excl;
	}

	public void setExcl(String value) {

		this.excl = value;
	}

	public List<DpAmpCurveType> getAdp() {

		if(adp == null) {
			adp = new ArrayList<>();
		}
		return this.adp;
	}

	public List<DpMeltingCurveType> getMdp() {

		if(mdp == null) {
			mdp = new ArrayList<>();
		}
		return this.mdp;
	}

	public float getEndPt() {

		return endPt;
	}

	public void setEndPt(float value) {

		this.endPt = value;
	}

	public float getBgFluor() {

		return bgFluor;
	}

	public void setBgFluor(float value) {

		this.bgFluor = value;
	}

	public float getBgFluorSlp() {

		return bgFluorSlp;
	}

	public void setBgFluorSlp(float value) {

		this.bgFluorSlp = value;
	}

	public float getQuantFluor() {

		return quantFluor;
	}

	public void setQuantFluor(float value) {

		this.quantFluor = value;
	}
}
