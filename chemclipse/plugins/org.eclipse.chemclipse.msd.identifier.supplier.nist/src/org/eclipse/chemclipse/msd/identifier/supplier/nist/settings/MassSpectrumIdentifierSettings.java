/*******************************************************************************
 * Copyright (c) 2010, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove nist application path settings from config
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.MassSpectrumIdentifierAdapterSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class MassSpectrumIdentifierSettings extends MassSpectrumIdentifierAdapterSettings implements INistSettings {

	@JsonProperty(value = "NIST Folder (MSSEARCH)", defaultValue = "")
	@JsonPropertyDescription("Select the NIST-DB folder, called MSSEARCH.")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, onlyDirectory = true)
	private File nistFolder = PreferenceSupplier.getNistInstallationFolder();
	@JsonProperty(value = "Number of Targets", defaultValue = "3")
	@JsonPropertyDescription(value = "The number of iterations to targets to store.")
	@IntSettingsProperty
	private int numberOfTargets = PreferenceSupplier.DEF_NUMBER_OF_TARGETS;
	@JsonProperty(value = "Min Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minMatchFactor = PreferenceSupplier.getMinMatchFactor();
	@JsonProperty(value = "Min Reverse Match Factor", defaultValue = "80.0")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float minReverseMatchFactor = PreferenceSupplier.getMinReverseMatchFactor();
	@JsonProperty(value = "Store Targets", defaultValue = "true")
	@JsonPropertyDescription(value = "Shall the targets be stored.")
	private boolean storeTargets = true;
	@JsonProperty(value = "Timeout [min]", defaultValue = "20")
	@JsonPropertyDescription(value = "The timeout in minutes to stop the action if something goes wrong.")
	@IntSettingsProperty
	private int timeoutInMinutes = 20;

	@Override
	public File getNistFolder() {

		return nistFolder;
	}

	@Override
	public void setNistFolder(File nistFolder) {

		this.nistFolder = nistFolder;
	}

	@Override
	public int getNumberOfTargets() {

		return numberOfTargets;
	}

	@Override
	public void setNumberOfTargets(int numberOfTargets) {

		if(numberOfTargets >= PreferenceSupplier.MIN_NUMBER_OF_TARGETS && numberOfTargets <= PreferenceSupplier.MAX_NUMBER_OF_TARGETS) {
			this.numberOfTargets = numberOfTargets;
		}
	}

	public float getMinMatchFactor() {

		return minMatchFactor;
	}

	public void setMinMatchFactor(float minMatchFactor) {

		this.minMatchFactor = minMatchFactor;
	}

	public float getMinReverseMatchFactor() {

		return minReverseMatchFactor;
	}

	public void setMinReverseMatchFactor(float minReverseMatchFactor) {

		this.minReverseMatchFactor = minReverseMatchFactor;
	}

	@Override
	public boolean getStoreTargets() {

		return storeTargets;
	}

	@Override
	public void setStoreTargets(boolean storeTargets) {

		this.storeTargets = storeTargets;
	}

	@Override
	public int getTimeoutInMinutes() {

		return timeoutInMinutes;
	}

	@Override
	public void setTimeoutInMinutes(int timeoutInMinutes) {

		this.timeoutInMinutes = timeoutInMinutes;
	}
}
