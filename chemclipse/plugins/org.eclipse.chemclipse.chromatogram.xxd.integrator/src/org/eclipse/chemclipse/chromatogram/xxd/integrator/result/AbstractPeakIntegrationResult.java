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

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPeakIntegrationResult implements IPeakIntegrationResult {

	private double integratedArea;
	private float tailing;
	private int width;
	private String integratorType = "";
	private String peakType = "";
	private String modelDescription = "";
	private float sn;
	private int startRetentionTime;
	private int stopRetentionTime;
	private float purity;
	private Set<Integer> integratedIons;

	public AbstractPeakIntegrationResult() {
		integratedIons = new HashSet<Integer>();
	}

	@Override
	public double getIntegratedArea() {

		return integratedArea;
	}

	@Override
	public void setIntegratedArea(double integratedArea) {

		this.integratedArea = integratedArea;
	}

	@Override
	public float getTailing() {

		return tailing;
	}

	@Override
	public void setTailing(float tailing) {

		this.tailing = tailing;
	}

	@Override
	public int getWidth() {

		return width;
	}

	@Override
	public void setWidth(int width) {

		this.width = width;
	}

	@Override
	public String getIntegratorType() {

		return integratorType;
	}

	@Override
	public void setIntegratorType(String integratorType) {

		this.integratorType = integratorType;
	}

	@Override
	public String getPeakType() {

		return peakType;
	}

	@Override
	public void setPeakType(String peakType) {

		this.peakType = peakType;
	}

	@Override
	public String getModelDescription() {

		return modelDescription;
	}

	@Override
	public void setModelDescription(String modelDescription) {

		this.modelDescription = modelDescription;
	}

	@Override
	public float getSN() {

		return sn;
	}

	@Override
	public void setSN(float sn) {

		this.sn = sn;
	}

	@Override
	public int getStartRetentionTime() {

		return startRetentionTime;
	}

	@Override
	public void setStartRetentionTime(int startRetentionTime) {

		this.startRetentionTime = startRetentionTime;
	}

	@Override
	public int getStopRetentionTime() {

		return stopRetentionTime;
	}

	@Override
	public void setStopRetentionTime(int stopRetentionTime) {

		this.stopRetentionTime = stopRetentionTime;
	}

	@Override
	public float getPurity() {

		return purity;
	}

	@Override
	public void setPurity(float purity) {

		this.purity = purity;
	}

	@Override
	public Set<Integer> getIntegratedIons() {

		return integratedIons;
	}

	@Override
	public void addIntegratedIon(int ion) {

		integratedIons.add(ion);
	}

	@Override
	public void removeIntegratedIon(int ion) {

		integratedIons.remove(ion);
	}

	@Override
	public void addIntegratedIons(Set<Integer> ions) {

		integratedIons.addAll(ions);
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
		IPeakIntegrationResult otherResult = (IPeakIntegrationResult)other;
		/*
		 * integratedIons could be checked better.
		 */
		int size = 0;
		int otherSize = 0;
		if(integratedIons != null) {
			size = integratedIons.size();
		}
		if(otherResult.getIntegratedIons() != null) {
			otherSize = otherResult.getIntegratedIons().size();
		}
		return integratedArea == otherResult.getIntegratedArea() && tailing == otherResult.getTailing() && width == otherResult.getWidth() && integratorType == otherResult.getIntegratorType() && peakType == otherResult.getPeakType() && modelDescription == otherResult.getModelDescription() && sn == otherResult.getSN() && startRetentionTime == otherResult.getStartRetentionTime() && stopRetentionTime == otherResult.getStopRetentionTime() && purity == otherResult.getPurity() && size == otherSize;
	}

	@Override
	public int hashCode() {

		return Double.valueOf(integratedArea).hashCode() + Float.valueOf(tailing).hashCode() + Integer.valueOf(width).hashCode() + integratorType.hashCode() + peakType.hashCode() + modelDescription.hashCode() + Float.valueOf(sn).hashCode() + Integer.valueOf(startRetentionTime).hashCode() + Integer.valueOf(stopRetentionTime).hashCode() + Float.valueOf(purity).hashCode() + integratedIons.hashCode();
	}

	@Override
	public String toString() {

		int size = 0;
		if(integratedIons != null) {
			size = integratedIons.size();
		}
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("integratedArea=" + integratedArea);
		builder.append(",");
		builder.append("tailing=" + tailing);
		builder.append(",");
		builder.append("width=" + width);
		builder.append(",");
		builder.append("integratorType=" + integratorType);
		builder.append(",");
		builder.append("peakType=" + peakType);
		builder.append(",");
		builder.append("modelDescription=" + modelDescription);
		builder.append(",");
		builder.append("sn=" + sn);
		builder.append(",");
		builder.append("startRetentionTime=" + startRetentionTime);
		builder.append(",");
		builder.append("stopRetentionTime=" + stopRetentionTime);
		builder.append(",");
		builder.append("purity=" + purity);
		builder.append(",");
		builder.append("integratedArea=" + integratedArea);
		builder.append(",");
		builder.append("integratedIons=" + size);
		builder.append("]");
		return builder.toString();
	}
	// ----------------------------------------hashCode, equals, toString
}
