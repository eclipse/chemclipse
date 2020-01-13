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

public class ProcessPeaksByAreaFilterLocalSettings {

	private double[] areaLimits = new double[] {0.0, 0.0};
	private ProcessPeaksByAreaFilterSelectionCriterion selectionCriterion;
	private ProcessPeaksByAreaFilterTreatmentOption treatmentOption;
	//
	private ProcessPeaksByIntegratedAreaFilterSettings integratedAreaFilterSettings;
	private ProcessPeaksByPercentageAreaFilterSettings percentageAreaFilterSettings;

	public double[] getAreaLimits() {
		return areaLimits;
	}

	public void setAreaLimits(double[] areaLimits) {
		this.areaLimits = areaLimits;
	}

	public ProcessPeaksByAreaFilterSelectionCriterion getSelectionCriterion() {
		return selectionCriterion;
	}

	public void setSelectionCriterion(ProcessPeaksByAreaFilterSelectionCriterion selectionCriterion) {
		this.selectionCriterion = selectionCriterion;
	}

	public ProcessPeaksByAreaFilterTreatmentOption getTreatmentOption() {
		return treatmentOption;
	}

	public void setTreatmentOption(ProcessPeaksByAreaFilterTreatmentOption treatmentOption) {
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
}
