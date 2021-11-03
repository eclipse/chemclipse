/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

public abstract class AbstractSeparationColumn implements ISeparationColumn {

	private String value = "";
	private String name = "";
	private String length = "";
	private String diameter = "";
	private String phase = "";

	@Override
	public String getValue() {

		return value;
	}

	@Override
	public void setValue(String value) {

		this.value = value;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getLength() {

		return length;
	}

	@Override
	public void setLength(String length) {

		this.length = length;
	}

	@Override
	public String getDiameter() {

		return diameter;
	}

	@Override
	public void setDiameter(String diameter) {

		this.diameter = diameter;
	}

	@Override
	public String getPhase() {

		return phase;
	}

	@Override
	public void setPhase(String phase) {

		this.phase = phase;
	}

	@Override
	public String toString() {

		return "SeparationColumn [name=" + name + ", length=" + length + ", diameter=" + diameter + ", phase=" + phase + "]";
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((diameter == null) ? 0 : diameter.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phase == null) ? 0 : phase.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		AbstractSeparationColumn other = (AbstractSeparationColumn)obj;
		if(diameter == null) {
			if(other.diameter != null) {
				return false;
			}
		} else if(!diameter.equals(other.diameter)) {
			return false;
		}
		if(length == null) {
			if(other.length != null) {
				return false;
			}
		} else if(!length.equals(other.length)) {
			return false;
		}
		if(name == null) {
			if(other.name != null) {
				return false;
			}
		} else if(!name.equals(other.name)) {
			return false;
		}
		if(value == null) {
			if(other.value != null) {
				return false;
			}
		} else if(!value.equals(other.value)) {
			return false;
		}
		if(phase == null) {
			if(other.phase != null) {
				return false;
			}
		} else if(!phase.equals(other.phase)) {
			return false;
		}
		return true;
	}
}
