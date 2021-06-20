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
	protected String arrayName;
	@XmlElement(required = true)
	protected SupDataBinaryType.Data data;
	@XmlAttribute(name = "id", required = true)
	protected int id;

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
		protected byte[] value;
		@XmlAttribute(name = "precision", required = true)
		protected String precision;
		@XmlAttribute(name = "endian", required = true)
		protected String endian;
		@XmlAttribute(name = "length", required = true)
		protected int length;

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
