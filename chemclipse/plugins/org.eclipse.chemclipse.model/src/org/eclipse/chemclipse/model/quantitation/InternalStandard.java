/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

public class InternalStandard implements IInternalStandard {

	private double content;
	private double responseFactor;
	private String unit;

	public InternalStandard(double content, double responseFactor, String unit) {
		this.content = content;
		this.responseFactor = responseFactor;
		this.unit = unit;
	}

	@Override
	public double getContent() {

		return content;
	}

	@Override
	public double getResponseFactor() {

		return responseFactor;
	}

	@Override
	public String getUnit() {

		return unit;
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		IInternalStandard other = (IInternalStandard)otherObject;
		return content == other.getContent() && //
				responseFactor == other.getResponseFactor() && //
				unit == other.getUnit();
	}

	@Override
	public int hashCode() {

		return Double.valueOf(content).hashCode() + //
				Double.valueOf(responseFactor).hashCode() + //
				unit.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("content=" + content);
		builder.append(",");
		builder.append("responseFactor=" + responseFactor);
		builder.append(",");
		builder.append("unit=" + unit);
		builder.append("]");
		return builder.toString();
	}
}
