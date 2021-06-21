/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v104.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "acqDescType", propOrder = {"acqSettings", "precursorList", "comments"})
public class AcqDescType {

	@XmlElement(required = true)
	protected AcqSettingsType acqSettings;
	protected AcqDescType.PrecursorList precursorList;
	protected List<String> comments;

	public AcqSettingsType getAcqSettings() {

		return acqSettings;
	}

	public void setAcqSettings(AcqSettingsType value) {

		this.acqSettings = value;
	}

	public AcqDescType.PrecursorList getPrecursorList() {

		return precursorList;
	}

	public void setPrecursorList(AcqDescType.PrecursorList value) {

		this.precursorList = value;
	}

	public List<String> getComments() {

		if(comments == null) {
			comments = new ArrayList<String>();
		}
		return this.comments;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"precursor"})
	public static class PrecursorList {

		@XmlElement(required = true)
		protected List<PrecursorType> precursor;
		@XmlAttribute(name = "count", required = true)
		protected int count;

		public List<PrecursorType> getPrecursor() {

			if(precursor == null) {
				precursor = new ArrayList<PrecursorType>();
			}
			return this.precursor;
		}

		public int getCount() {

			return count;
		}

		public void setCount(int value) {

			this.count = value;
		}
	}
}
