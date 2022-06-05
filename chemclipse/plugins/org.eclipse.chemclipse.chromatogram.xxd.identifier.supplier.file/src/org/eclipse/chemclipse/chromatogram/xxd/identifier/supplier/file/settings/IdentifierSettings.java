/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparisonSupplier;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.MassSpectrumComparatorDynamicSettingProperty;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.ChromatogramIdentifierAdapterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.support.settings.ComboSettingsProperty;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class IdentifierSettings extends ChromatogramIdentifierAdapterSettings implements ILibraryIdentifierSettings {

	@JsonProperty(value = "Use Normalized Scan", defaultValue = "true")
	@JsonPropertyDescription(value = "When merging scan, normalize the intensities.")
	private boolean useNormalizedScan = true;
	@JsonProperty(value = "Calculation Type", defaultValue = "SUM")
	@JsonPropertyDescription(value = "Defines how to create combined scans.")
	private CalculationType calculationType = CalculationType.SUM;
	@JsonProperty(value = "Use Peaks Instead Of Scans", defaultValue = "false")
	@JsonPropertyDescription(value = "Use peaks instead of scans to calculate the combined spectrum.")
	private boolean usePeaksInsteadOfScans = false;
	/*
	 * File Identifier Settings
	 */
	@JsonProperty(value = "Limit Match Factor", defaultValue = "80.0")
	@JsonPropertyDescription(value = "Run an identification if no target exists with a Match Factor >= the given limit.")
	@FloatSettingsProperty(minValue = IIdentifierSettings.MIN_LIMIT_MATCH_FACTOR, maxValue = IIdentifierSettings.MAX_LIMIT_MATCH_FACTOR)
	private float limitMatchFactor = IIdentifierSettings.DEF_LIMIT_MATCH_FACTOR;
	@JsonProperty(value = "Library File", defaultValue = "")
	@JsonPropertyDescription("Select the library file.")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, extensionNames = {"AMDIS (*.msl)", "AMDIS (*.MSL)"}, validExtensions = {"*.msl", "*.MSL"}, onlyDirectory = false)
	private File libraryFile;
	@JsonProperty(value = "Mass Spectrum Comparator", defaultValue = DEFAULT_COMPARATOR_ID)
	@JsonPropertyDescription(value = "Select the algorithm used for mass spectrum comparison calculation.")
	@ComboSettingsProperty(MassSpectrumComparatorDynamicSettingProperty.class)
	private String massSpectrumComparatorId = DEFAULT_COMPARATOR_ID;
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
	@JsonIgnore
	private IMassSpectrumComparator comparator = null; // The comparator will be created dynamically.

	public boolean isUseNormalizedScan() {

		return useNormalizedScan;
	}

	public void setUseNormalizedScan(boolean useNormalizedScan) {

		this.useNormalizedScan = useNormalizedScan;
	}

	public CalculationType getCalculationType() {

		return calculationType;
	}

	public void setCalculationType(CalculationType calculationType) {

		this.calculationType = calculationType;
	}

	public boolean isUsePeaksInsteadOfScans() {

		return usePeaksInsteadOfScans;
	}

	public void setUsePeaksInsteadOfScans(boolean usePeaksInsteadOfScans) {

		this.usePeaksInsteadOfScans = usePeaksInsteadOfScans;
	}

	@Override
	public float getLimitMatchFactor() {

		return limitMatchFactor;
	}

	@Override
	public void setLimitMatchFactor(float limitMatchFactor) {

		this.limitMatchFactor = limitMatchFactor;
	}

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

	@Override
	public String getMassSpectrumComparatorId() {

		return massSpectrumComparatorId;
	}

	@Override
	public void setMassSpectrumComparatorId(String massSpectrumComparatorId) {

		comparator = null;
		this.massSpectrumComparatorId = massSpectrumComparatorId;
	}

	@JsonIgnore
	public IMassSpectrumComparator getMassSpectrumComparator() {

		if(comparator == null) {
			comparator = MassSpectrumComparator.getMassSpectrumComparator(getMassSpectrumComparatorId());
		}
		return comparator;
	}

	public void setMassSpectrumComparator(IMassSpectrumComparator comparator) {

		IMassSpectrumComparisonSupplier supplier = comparator.getMassSpectrumComparisonSupplier();
		if(supplier != null) {
			this.massSpectrumComparatorId = supplier.getId();
		}
		this.comparator = comparator;
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