/*******************************************************************************
 * Copyright (c) 2013, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

public abstract class AbstractQuantitationSignalMSD implements IQuantitationSignalMSD {

	private double ion;
	private float relativeResponse;
	private double uncertainty;
	private boolean use;

	public AbstractQuantitationSignalMSD(double ion, float relativeResponse) {
		this.ion = ion;
		this.relativeResponse = relativeResponse;
		this.use = true;
	}

	public AbstractQuantitationSignalMSD(double ion, float relativeResponse, double uncertainty) {
		this(ion, relativeResponse);
		this.uncertainty = uncertainty;
	}

	public AbstractQuantitationSignalMSD(double ion, float relativeResponse, double uncertainty, boolean use) {
		this(ion, relativeResponse);
		this.uncertainty = uncertainty;
		this.use = use;
	}

	@Override
	public double getIon() {

		return ion;
	}

	@Override
	public float getRelativeResponse() {

		return relativeResponse;
	}

	@Override
	public double getUncertainty() {

		return uncertainty;
	}

	@Override
	public void setUncertainty(double uncertainty) {

		this.uncertainty = uncertainty;
	}

	@Override
	public boolean isUse() {

		return use;
	}

	@Override
	public void setUse(boolean use) {

		this.use = use;
	}

	// -----------------------------equals, hashCode, toString
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
		/*
		 * Relative response, uncertainty and isUse shall be not part of the equals method.
		 */
		AbstractQuantitationSignalMSD other = (AbstractQuantitationSignalMSD)otherObject;
		return getIon() == other.getIon() && getRelativeResponse() == other.getRelativeResponse() && getUncertainty() == other.getUncertainty() && isUse() == other.isUse();
	}

	@Override
	public int hashCode() {

		return 7 * Double.valueOf(ion).hashCode() + 11 * Float.valueOf(relativeResponse).hashCode() + 13 * Double.valueOf(uncertainty).hashCode() + 11 * Boolean.valueOf(use).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("ion=" + ion);
		builder.append(",");
		builder.append("relativeResponse=" + relativeResponse);
		builder.append(",");
		builder.append("uncertainty=" + uncertainty);
		builder.append(",");
		builder.append("use=" + use);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------equals, hashCode, toString
}
