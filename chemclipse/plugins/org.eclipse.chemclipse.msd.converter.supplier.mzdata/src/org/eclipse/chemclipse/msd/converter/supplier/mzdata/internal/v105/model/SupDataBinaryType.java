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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "supDataBinaryType", propOrder = {"arrayName", "data"})
public class SupDataBinaryType {

	@XmlElement(required = true)
	private String arrayName;
	@XmlElement(required = true)
	private SupDataBinaryType.Data data;
	@XmlAttribute(name = "id", required = true)
	private int id;

	public String getArrayName() {

		return arrayName;
	}

	public void setArrayName(String value) {

		this.arrayName = value;
	}

	public SupDataBinaryType.Data getData() {

		return data;
	}

	public void setData(SupDataBinaryType.Data value) {

		this.data = value;
	}

	public int getId() {

		return id;
	}

	public void setId(int value) {

		this.id = value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {"value"})
	public static class Data {

		@XmlValue
		private byte[] value;
		@XmlAttribute(name = "precision", required = true)
		private String precision;
		@XmlAttribute(name = "endian", required = true)
		private String endian;
		@XmlAttribute(name = "length", required = true)
		private int length;

		public byte[] getValue() {

			return value;
		}

		public void setValue(byte[] value) {

			this.value = value;
		}

		public String getPrecision() {

			return precision;
		}

		public void setPrecision(String value) {

			this.precision = value;
		}

		public String getEndian() {

			return endian;
		}

		public void setEndian(String value) {

			this.endian = value;
		}

		public int getLength() {

			return length;
		}

		public void setLength(int value) {

			this.length = value;
		}
	}
}
