/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SampleAttribute {

	@XmlElement(name = "molecularWeight")
	private MolecularWeight molecularWeight;
	@XmlElement(name = "meltingPoint")
	private MeltingPoint meltingPoint;
	@XmlElement(name = "boilingPoint")
	private BoilingPoint boilingPoint;
	@XmlElement(name = "density")
	private Density density;
	@XmlElement(name = "refractiveIndex")
	private RefractiveIndex refractiveIndex;
	@XmlElement(name = "comment")
	private String comment;

	public MolecularWeight getMolecularWeight() {

		return molecularWeight;
	}

	public void setMolecularWeight(MolecularWeight molecularWeight) {

		this.molecularWeight = molecularWeight;
	}

	public MeltingPoint getMeltingPoint() {

		return meltingPoint;
	}

	public void setMeltingPoint(MeltingPoint meltingPoint) {

		this.meltingPoint = meltingPoint;
	}

	public BoilingPoint getBoilingPoint() {

		return boilingPoint;
	}

	public void setBoilingPoint(BoilingPoint boilingPoint) {

		this.boilingPoint = boilingPoint;
	}

	public Density getDensity() {

		return density;
	}

	public void setDensity(Density density) {

		this.density = density;
	}

	public RefractiveIndex getRefractiveIndex() {

		return refractiveIndex;
	}

	public void setRefractiveIndex(RefractiveIndex refractiveIndex) {

		this.refractiveIndex = refractiveIndex;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}
