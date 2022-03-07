/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spectrumDescType", propOrder = {"spectrumSettings", "precursorList", "comments"})
public class SpectrumDescType {

	@XmlElement(required = true)
	private SpectrumSettingsType spectrumSettings;
	private SpectrumDescType.PrecursorList precursorList;
	private List<String> comments;

	public SpectrumSettingsType getSpectrumSettings() {

		return spectrumSettings;
	}

	public void setSpectrumSettings(SpectrumSettingsType value) {

		this.spectrumSettings = value;
	}

	public SpectrumDescType.PrecursorList getPrecursorList() {

		return precursorList;
	}

	public void setPrecursorList(SpectrumDescType.PrecursorList value) {

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
		private List<PrecursorType> precursor;
		@XmlAttribute(name = "count", required = true)
		private int count;

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
