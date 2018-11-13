/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class MassSpectrumIdentifierSettings extends AbstractMassSpectrumIdentifierSettings implements INistSettings {

	@JsonProperty(value = "Path nistms$.exe", defaultValue = "nistms$.exe")
	@JsonPropertyDescription(value = "The path of the NIST executable.")
	@StringSettingsProperty
	private String nistApplication = "";
	@JsonProperty(value = "Number of Targets", defaultValue = "3")
	@JsonPropertyDescription(value = "The number of iterations to targets to store.")
	@IntSettingsProperty
	private int numberOfTargets = PreferenceSupplier.DEF_NUMBER_OF_TARGETS;
	@JsonProperty(value = "Store Targets", defaultValue = "true")
	@JsonPropertyDescription(value = "Shall the targets be stored.")
	private boolean storeTargets = true;
	@JsonProperty(value = "Timeout (Minutes)", defaultValue = "20")
	@JsonPropertyDescription(value = "The timeout in minutes to stop the action if something goes wrong.")
	@IntSettingsProperty
	private int timeoutInMinutes = 20;

	public MassSpectrumIdentifierSettings() {
		nistApplication = "";
	}

	@Override
	public String getNistApplication() {

		return nistApplication;
	}

	@Override
	public void setNistApplication(String nistApplication) {

		if(nistApplication != null) {
			this.nistApplication = nistApplication;
		}
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
