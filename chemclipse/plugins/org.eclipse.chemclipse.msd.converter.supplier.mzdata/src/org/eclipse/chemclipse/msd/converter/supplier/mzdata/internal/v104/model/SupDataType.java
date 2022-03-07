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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "supDataType", propOrder = {"arrayName", "_float", "_double", "_int", "_boolean", "string", "time", "uri"})
public class SupDataType {

	@XmlElement(required = true)
	protected String arrayName;
	@XmlElement(name = "float", type = Float.class)
	protected List<Float> _float;
	@XmlElement(name = "double", type = Double.class)
	protected List<Double> _double;
	@XmlElement(name = "int", type = Integer.class)
	protected List<Integer> _int;
	@XmlElement(name = "boolean", type = Boolean.class)
	protected List<Boolean> _boolean;
	protected List<String> string;
	@XmlElement(type = Float.class)
	protected List<Float> time;
	@XmlElement(name = "URI")
	@XmlSchemaType(name = "anyURI")
	protected List<String> uri;
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "length", required = true)
	protected int length;
	@XmlAttribute(name = "indexed", required = true)
	protected boolean indexed;
	@XmlAttribute(name = "offset")
	protected Integer offset;

	public String getArrayName() {

		return arrayName;
	}

	public void setArrayName(String value) {

		this.arrayName = value;
	}

	public List<Float> getFloat() {

		if(_float == null) {
			_float = new ArrayList<Float>();
		}
		return this._float;
	}

	public List<Double> getDouble() {

		if(_double == null) {
			_double = new ArrayList<Double>();
		}
		return this._double;
	}

	public List<Integer> getInt() {

		if(_int == null) {
			_int = new ArrayList<Integer>();
		}
		return this._int;
	}

	public List<Boolean> getBoolean() {

		if(_boolean == null) {
			_boolean = new ArrayList<Boolean>();
		}
		return this._boolean;
	}

	public List<String> getString() {

		if(string == null) {
			string = new ArrayList<String>();
		}
		return this.string;
	}

	public List<Float> getTime() {

		if(time == null) {
			time = new ArrayList<Float>();
		}
		return this.time;
	}

	public List<String> getURI() {

		if(uri == null) {
			uri = new ArrayList<String>();
		}
		return this.uri;
	}

	public int getId() {

		return id;
	}

	public void setId(int value) {

		this.id = value;
	}

	public int getLength() {

		return length;
	}

	public void setLength(int value) {

		this.length = value;
	}

	public boolean isIndexed() {

		return indexed;
	}

	public void setIndexed(boolean value) {

		this.indexed = value;
	}

	public int getOffset() {

		if(offset == null) {
			return 0;
		} else {
			return offset;
		}
	}

	public void setOffset(Integer value) {

		this.offset = value;
	}
}
