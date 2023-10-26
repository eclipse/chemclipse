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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Due to the frequent occurrence of this element, names are kept short.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "partitionDataType", propOrder = {"tar", "excluded", "note", "pos", "neg", "undef", "excl", "conc"})
public class PartitionDataType {

	@XmlElement(required = true)
	protected IdReferencesType tar;
	protected String excluded;
	protected String note;
	protected int pos;
	protected int neg;
	protected Integer undef;
	protected Integer excl;
	protected Float conc;

	public IdReferencesType getTar() {

		return tar;
	}

	public void setTar(IdReferencesType value) {

		this.tar = value;
	}

	public String getExcluded() {

		return excluded;
	}

	public void setExcluded(String value) {

		this.excluded = value;
	}

	public String getNote() {

		return note;
	}

	public void setNote(String value) {

		this.note = value;
	}

	public int getPos() {

		return pos;
	}

	public void setPos(int value) {

		this.pos = value;
	}

	public int getNeg() {

		return neg;
	}

	public void setNeg(int value) {

		this.neg = value;
	}

	public Integer getUndef() {

		return undef;
	}

	public void setUndef(Integer value) {

		this.undef = value;
	}

	public Integer getExcl() {

		return excl;
	}

	public void setExcl(Integer value) {

		this.excl = value;
	}

	public Float getConc() {

		return conc;
	}

	public void setConc(Float value) {

		this.conc = value;
	}
}
