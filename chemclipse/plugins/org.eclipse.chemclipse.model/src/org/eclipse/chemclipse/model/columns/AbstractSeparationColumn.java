/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

import java.util.Objects;

public abstract class AbstractSeparationColumn implements ISeparationColumn {

	private String name = "";
	private SeparationColumnType separationColumnType = SeparationColumnType.DEFAULT;
	private SeparationColumnPackaging separationColumnPackaging = SeparationColumnPackaging.CAPILLARY;
	private String calculationType = ""; // Kovats RI, Lee RI, ...
	private String length = "";
	private String diameter = "";
	private String phase = "";
	private String thickness = "";

	@Override
	public void copyFrom(ISeparationColumn separationColumn) {

		if(separationColumn != null) {
			this.name = separationColumn.getName();
			this.separationColumnType = separationColumn.getSeparationColumnType();
			this.separationColumnPackaging = separationColumn.getSeparationColumnPackaging();
			this.calculationType = separationColumn.getCalculationType();
			this.length = separationColumn.getLength();
			this.diameter = separationColumn.getDiameter();
			this.phase = separationColumn.getPhase();
			this.thickness = separationColumn.getThickness();
		}
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
	public SeparationColumnType getSeparationColumnType() {

		return separationColumnType;
	}

	@Override
	public void setSeparationColumnType(SeparationColumnType separationColumnType) {

		this.separationColumnType = separationColumnType;
	}

	@Override
	public SeparationColumnPackaging getSeparationColumnPackaging() {

		return separationColumnPackaging;
	}

	@Override
	public void setSeparationColumnPackaging(SeparationColumnPackaging separationColumnPackaging) {

		this.separationColumnPackaging = separationColumnPackaging;
	}

	@Override
	public String getCalculationType() {

		return calculationType;
	}

	@Override
	public void setCalculationType(String calculationType) {

		this.calculationType = calculationType;
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
	public String getThickness() {

		return thickness;
	}

	@Override
	public void setThickness(String thickness) {

		this.thickness = thickness;
	}

	@Override
	public int hashCode() {

		return Objects.hash(calculationType, diameter, length, name, phase, separationColumnPackaging, separationColumnType, thickness);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		AbstractSeparationColumn other = (AbstractSeparationColumn)obj;
		return Objects.equals(calculationType, other.calculationType) && Objects.equals(diameter, other.diameter) && Objects.equals(length, other.length) && Objects.equals(name, other.name) && Objects.equals(phase, other.phase) && separationColumnPackaging == other.separationColumnPackaging && separationColumnType == other.separationColumnType && Objects.equals(thickness, other.thickness);
	}

	@Override
	public String toString() {

		return "AbstractSeparationColumn [name=" + name + ", separationColumnType=" + separationColumnType + ", separationColumnPackaging=" + separationColumnPackaging + ", calculationType=" + calculationType + ", length=" + length + ", diameter=" + diameter + ", phase=" + phase + ", thickness=" + thickness + "]";
	}
}