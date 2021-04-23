/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - change to ComboSettingsProperty(MassSpectrumComparatorDynamicSettingProperty.class)
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.identifier.GeneratedIdentifierSettings;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@GeneratedIdentifierSettings
public class PeakIdentifierSettings extends AbstractPeakIdentifierSettingsMSD implements IFileIdentifierSettings {

	@JsonProperty(value = "Library File", defaultValue = "")
	@JsonPropertyDescription("Select the library file.")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, extensionNames = {"AMDIS (*.msl)", "AMDIS (*.MSL)", "NIST (*.msp)", "NIST (*.MSP)", "MassBank (.zip)", "MassBank (.ZIP)"}, validExtensions = {"*.msl", "*.MSL", "*.msp", "*.MSP", "*.zip", "*.ZIP"}, onlyDirectory = false)
	private File libraryFile;
	@JsonProperty(value = "Limit Match Factor", defaultValue = "80.0")
	@JsonPropertyDescription(value = "Run an identification if no target exists with a Match Factor >= the given limit.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float limitMatchFactor = 80.0f;
	@JsonProperty(value = "Pre-Optimization", defaultValue = "false")
	private boolean usePreOptimization = false;
	@JsonProperty(value = "Threshold Pre-Optimization", defaultValue = "0.12")
	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_THRESHOLD_PRE_OPTIMIZATION, maxValue = PreferenceSupplier.MAX_THRESHOLD_PRE_OPTIMIZATION, step = 0.1)
	private double thresholdPreOptimization = 0.12;
	@JsonProperty(value = "Number of Targets", defaultValue = "15")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_NUMBER_OF_TARGETS, maxValue = PreferenceSupplier.MAX_NUMBER_OF_TARGETS)
	private int numberOfTargets = 15;
	@JsonProperty(value = "Min. Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minMatchFactor = 80.0f;
	@JsonProperty(value = "Min. Reverse Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minReverseMatchFactor = 80.0f;
	//
	@JsonIgnore
	private String alternateIdentifierId = "";
	@JsonIgnore
	private String massSpectraFiles = "";

	@Override
	public String getMassSpectraFiles() {

		if(libraryFile != null) {
			return libraryFile.getAbsolutePath();
		} else {
			return massSpectraFiles;
		}
	}

	@Override
	public void setMassSpectraFiles(String massSpectraFiles) {

		this.massSpectraFiles = massSpectraFiles;
	}

	public File getLibraryFile() {

		return libraryFile;
	}

	public void setLibraryFile(File libraryFile) {

		this.libraryFile = libraryFile;
	}

	public float getLimitMatchFactor() {

		return limitMatchFactor;
	}

	public void setLimitMatchFactor(float limitMatchFactor) {

		this.limitMatchFactor = limitMatchFactor;
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
