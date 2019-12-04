/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileIdentifierSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PeakIdentifierSettings extends AbstractPeakIdentifierSettingsMSD implements IFileIdentifierSettings {

	private String massSpectraFiles = ""; // Don't modify. The value will be set by the plugin.
	private boolean usePreOptimization = false; // Don't modify. The value will be set by the plugin.
	private double thresholdPreOptimization = 0.12; // Don't modify. The value will be set by the plugin.
	@JsonProperty(value = "Number of Targets", defaultValue = "15")
	private int numberOfTargets = 15;
	@JsonProperty(value = "Min Match Factor", defaultValue = "80.0")
	private float minMatchFactor = 80.0f;
	@JsonProperty(value = "Min Reverse Match Factor", defaultValue = "80.0")
	private float minReverseMatchFactor = 80.0f;
	//
	private String alternateIdentifierId = ""; // Don't modify. The value will be set by the plugin.

	@Override
	public String getMassSpectraFiles() {

		return massSpectraFiles;
	}

	@Override
	public void setMassSpectraFiles(String massSpectraFiles) {

		this.massSpectraFiles = massSpectraFiles;
	}

	@Override
	public boolean isUsePreOptimization() {

		return usePreOptimization;
	}

	@Override
	public void setUsePreOptimization(boolean usePreOptimization) {

		this.usePreOptimization = usePreOptimization;
	}

	@Override
	public double getThresholdPreOptimization() {

		return thresholdPreOptimization;
	}

	@Override
	public void setThresholdPreOptimization(double thresholdPreOptimization) {

		this.thresholdPreOptimization = thresholdPreOptimization;
	}

	@Override
	public int getNumberOfTargets() {

		return numberOfTargets;
	}

	@Override
	public void setNumberOfTargets(int numberOfTargets) {

		this.numberOfTargets = numberOfTargets;
	}

	@Override
	public float getMinMatchFactor() {

		return minMatchFactor;
	}

	@Override
	public void setMinMatchFactor(float minMatchFactor) {

		this.minMatchFactor = minMatchFactor;
	}

	@Override
	public float getMinReverseMatchFactor() {

		return minReverseMatchFactor;
	}

	@Override
	public void setMinReverseMatchFactor(float minReverseMatchFactor) {

		this.minReverseMatchFactor = minReverseMatchFactor;
	}

	@Override
	public String getAlternateIdentifierId() {

		return alternateIdentifierId;
	}

	@Override
	public void setAlternateIdentifierId(String alternateIdentifierId) {

		this.alternateIdentifierId = alternateIdentifierId;
	}
}