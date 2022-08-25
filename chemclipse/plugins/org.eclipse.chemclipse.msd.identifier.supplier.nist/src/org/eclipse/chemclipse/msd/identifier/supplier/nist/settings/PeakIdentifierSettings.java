/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove nist application configuration from config
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.PeakIdentifierAdapterSettingsMSD;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakIdentifierSettings extends PeakIdentifierAdapterSettingsMSD implements ISearchSettings {

	@JsonProperty(value = "NIST Folder (MSSEARCH)", defaultValue = "")
	@JsonPropertyDescription("Select the NIST-DB folder, called MSSEARCH.")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, onlyDirectory = true)
	private File nistFolder = null;
	@JsonProperty(value = "Limit Match Factor", defaultValue = "80.0")
	@JsonPropertyDescription(value = "Run an identification if no target exists with a Match Factor >= the given limit.")
	@FloatSettingsProperty(minValue = IIdentifierSettings.MIN_LIMIT_MATCH_FACTOR, maxValue = IIdentifierSettings.MAX_LIMIT_MATCH_FACTOR)
	private float limitMatchFactor = 80.0f;
	@JsonProperty(value = "Number of Targets", defaultValue = "3")
	@JsonPropertyDescription(value = "The max. number of targets that will be stored.")
	@IntSettingsProperty
	private int numberOfTargets = PreferenceSupplier.DEF_NUMBER_OF_TARGETS;
	@JsonProperty(value = "Use Optimized Mass Spectrum", defaultValue = "true")
	@JsonPropertyDescription(value = "If true, the optimized spectrum will be used if available.")
	private boolean useOptimizedMassSpectrum = true;
	@JsonProperty(value = "Min. Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minMatchFactor = 80.0f;
	@JsonProperty(value = "Min. Reverse Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minReverseMatchFactor = 80.0f;
	@JsonProperty(value = "Timeout [min]", defaultValue = "20")
	@JsonPropertyDescription(value = "The timeout in minutes to stop the action if something goes wrong.")
	@IntSettingsProperty
	private int timeoutInMinutes = 20;

	@Override
	public File getNistFolder() {

		return nistFolder;
	}

	public void setNistFolder(File nistFolder) {

		this.nistFolder = nistFolder;
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
	public int getNumberOfTargets() {

		return numberOfTargets;
	}

	public void setNumberOfTargets(int numberOfTargets) {

		if(numberOfTargets >= PreferenceSupplier.MIN_NUMBER_OF_TARGETS && numberOfTargets <= PreferenceSupplier.MAX_NUMBER_OF_TARGETS) {
			this.numberOfTargets = numberOfTargets;
		}
	}

	@Override
	public boolean isUseOptimizedMassSpectrum() {

		return useOptimizedMassSpectrum;
	}

	public void setUseOptimizedMassSpectrum(boolean useOptimizedMassSpectrum) {

		this.useOptimizedMassSpectrum = useOptimizedMassSpectrum;
	}

	@Override
	public float getMinMatchFactor() {

		return minMatchFactor;
	}

	public void setMinMatchFactor(float minMatchFactor) {

		this.minMatchFactor = minMatchFactor;
	}

	@Override
	public float getMinReverseMatchFactor() {

		return minReverseMatchFactor;
	}

	public void setMinReverseMatchFactor(float minReverseMatchFactor) {

		this.minReverseMatchFactor = minReverseMatchFactor;
	}

	@Override
	public int getTimeoutInMinutes() {

		return timeoutInMinutes;
	}

	public void setTimeoutInMinutes(int timeoutInMinutes) {

		this.timeoutInMinutes = timeoutInMinutes;
	}

	@Override
	public boolean isBatchModus() {

		return true; // Always true
	}

	@Override
	public int getWaitInSeconds() {

		return 3; // Only used if batch modus == false
	}
}
