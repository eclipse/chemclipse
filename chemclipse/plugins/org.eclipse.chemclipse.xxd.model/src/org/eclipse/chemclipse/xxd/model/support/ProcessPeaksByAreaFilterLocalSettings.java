/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.support;

import org.eclipse.chemclipse.xxd.model.filter.peaks.ProcessPeaksByIntegratedAreaFilterSettings;
import org.eclipse.chemclipse.xxd.model.filter.peaks.ProcessPeaksByPercentageAreaFilterSettings;

/**
 * Provides a container by means of which the settings for the area and area%
 * filters can be stored and processed uniformly.
 * 
 * @author Alexander Stark
 *
 */
public class ProcessPeaksByAreaFilterLocalSettings {

	private double lowerLimit = 0.0d;
	private double upperLimit = 0.0d;
	private ProcessPeaksByValueFilterSelectionCriterion selectionCriterion;
	private ProcessPeaksByValueFilterTreatmentOption treatmentOption;
	//
	private ProcessPeaksByIntegratedAreaFilterSettings integratedAreaFilterSettings;
	private ProcessPeaksByPercentageAreaFilterSettings percentageAreaFilterSettings;
	//
	private double areaSum = 0.0d;

	/**
	 * Gets the lower limit value from area or area% settings.
	 * 
	 * @return lowerLimit for filtering purposes
	 */
	public double getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * Sets the lower limit value from area or area% settings.
	 * 
	 * @param lowerLimit to be set for filtering
	 */
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	/**
	 * Gets the upper limit value from area or area% settings.
	 * 
	 * @return upperLimit for filtering purposes
	 */
	public double getUpperLimit() {
		return upperLimit;
	}

	/**
	 * Sets the upper limit value from area or area% settings.
	 * 
	 * @param upperLimit to be set for filtering
	 */
	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public ProcessPeaksByValueFilterSelectionCriterion getSelectionCriterion() {
		return selectionCriterion;
	}

	public void setSelectionCriterion(ProcessPeaksByValueFilterSelectionCriterion selectionCriterion) {
		this.selectionCriterion = selectionCriterion;
	}

	public ProcessPeaksByValueFilterTreatmentOption getTreatmentOption() {
		return treatmentOption;
	}

	public void setTreatmentOption(ProcessPeaksByValueFilterTreatmentOption treatmentOption) {
		this.treatmentOption = treatmentOption;
	}

	public ProcessPeaksByIntegratedAreaFilterSettings getIntegratedAreaFilterSettings() {
		return integratedAreaFilterSettings;
	}

	public void setIntegratedAreaFilterSettings(ProcessPeaksByIntegratedAreaFilterSettings integratedAreaFilterSettings) {
		this.integratedAreaFilterSettings = integratedAreaFilterSettings;
	}

	public ProcessPeaksByPercentageAreaFilterSettings getPercentageAreaFilterSettings() {
		return percentageAreaFilterSettings;
	}

	public void setPercentageAreaFilterSettings(ProcessPeaksByPercentageAreaFilterSettings percentageAreaFilterSettings) {
		this.percentageAreaFilterSettings = percentageAreaFilterSettings;
	}

	public double getAreaSum() {
		return areaSum;
	}

	public void setAreaSum(double areaSum) {
		this.areaSum = areaSum;
	}
}
