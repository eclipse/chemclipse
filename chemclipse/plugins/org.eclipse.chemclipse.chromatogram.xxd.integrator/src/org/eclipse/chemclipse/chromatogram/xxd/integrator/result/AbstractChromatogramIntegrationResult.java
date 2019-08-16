/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

public abstract class AbstractChromatogramIntegrationResult implements IChromatogramIntegrationResult {

	private double chromatogramArea = 0.0d;
	private double backgroundArea = 0.0d;
	private String integratorType = "";
	private double ion;

	public AbstractChromatogramIntegrationResult(double ion, double chromatogramArea, double backgroundArea) {
		this.ion = ion;
		this.chromatogramArea = chromatogramArea;
		this.backgroundArea = backgroundArea;
	}

	@Override
	public double getBackgroundArea() {

		return backgroundArea;
	}

	@Override
	public double getChromatogramArea() {

		return chromatogramArea;
	}

	@Override
	public String getIntegratorType() {

		return integratorType;
	}

	@Override
	public void setIntegratorType(String integratorType) {

		if(integratorType != null) {
			this.integratorType = integratorType;
		}
	}

	@Override
	public double getIon() {

		return ion;
	}

	// ----------------------------------------hashCode, equals, toString
	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		IChromatogramIntegrationResult otherResult = (IChromatogramIntegrationResult)other;
		return chromatogramArea == otherResult.getChromatogramArea() && backgroundArea == otherResult.getBackgroundArea() && integratorType == otherResult.getIntegratorType() && ion == otherResult.getIon();
	}

	@Override
	public int hashCode() {

		return Double.valueOf(chromatogramArea).hashCode() + Double.valueOf(chromatogramArea).hashCode() + integratorType.hashCode() + Double.valueOf(ion).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("chromatogramArea=" + chromatogramArea);
		builder.append(",");
		builder.append("backgroundArea=" + backgroundArea);
		builder.append(",");
		builder.append("integratorType=" + integratorType);
		builder.append(",");
		builder.append("ion=" + ion);
		builder.append("]");
		return builder.toString();
	}
	// ----------------------------------------hashCode, equals, toString
}
