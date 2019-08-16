/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparatorDynamicSettingProperty;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.identifier.GeneratedIdentifierSettings;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.DynamicSettingsProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.MultiFileSettingProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@GeneratedIdentifierSettings
public class PeakIdentifierSettings extends AbstractPeakIdentifierSettingsMSD implements IFileIdentifierSettings {

	@JsonProperty(value = "Mass Spectra Files", defaultValue = "")
	@JsonPropertyDescription(value = "Use a semicolon to separate the path of several files.") // see FileListUtil()
	@MultiFileSettingProperty
	private String massSpectraFiles = "";
	@JsonProperty(value = "Pre-Optimization", defaultValue = "false")
	private boolean usePreOptimization = false;
	@JsonProperty(value = "Threshold Pre-Optimization", defaultValue = "0.12")
	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_THRESHOLD_PRE_OPTIMIZATION, maxValue = PreferenceSupplier.MAX_THRESHOLD_PRE_OPTIMIZATION, step = 0.1)
	private double thresholdPreOptimization = 0.12;
	@JsonProperty(value = "Number of Targets", defaultValue = "15")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_NUMBER_OF_TARGETS, maxValue = PreferenceSupplier.MAX_NUMBER_OF_TARGETS)
	private int numberOfTargets = 15;
	@JsonProperty(value = "Min Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_MIN_MATCH_FACTOR, maxValue = PreferenceSupplier.MAX_MIN_MATCH_FACTOR)
	private float minMatchFactor = 80.0f;
	@JsonProperty(value = "Min Reverse Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_MIN_REVERSE_MATCH_FACTOR, maxValue = PreferenceSupplier.MAX_MIN_REVERSE_MATCH_FACTOR)
	private float minReverseMatchFactor = 80.0f;
	@JsonProperty(value = "Add Unknown m/z List Target", defaultValue = "true")
	private boolean addUnknownMzListTarget = true;
	@JsonProperty(value = "Alternate Identifier Id", defaultValue = "")
	@DynamicSettingsProperty(dynamicSettingPropertyClass = MassSpectrumComparatorDynamicSettingProperty.class)
	private String alternateIdentifierId = "";

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
	public boolean isAddUnknownMzListTarget() {

		return addUnknownMzListTarget;
	}

	@Override
	public void setAddUnknownMzListTarget(boolean addUnknownMzListTarget) {

		this.addUnknownMzListTarget = addUnknownMzListTarget;
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
