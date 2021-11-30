/*******************************************************************************
 * Copyright (c) 2014, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons.IonMarkMode;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class MassSpectrumFilterSettings extends AbstractMassSpectrumFilterSettings {

	@JsonProperty(value = "Ions", defaultValue = "18 28 84 207")
	@JsonPropertyDescription(value = "List the ions, separated by a white space.")
	@StringSettingsProperty(regExp = "(\\d+[;|\\s]?)+", description = "must be space separated digits.", isMultiLine = false)
	private String ionsToRemove = "18 28 84 207";
	@JsonProperty(value = "Mode", defaultValue = "INCLUDE")
	@JsonPropertyDescription(value = "Gives the mode to use (include = remove all ions given in the list, exclude = remove all ions not in the list)")
	private IMarkedIons.IonMarkMode markMode = IonMarkMode.INCLUDE;

	public String getIonsToRemove() {

		return ionsToRemove;
	}

	public void setIonsToRemove(String ionsToRemove) {

		this.ionsToRemove = ionsToRemove;
	}

	public IMarkedIons.IonMarkMode getMarkMode() {

		return markMode;
	}

	public void setMarkMode(IMarkedIons.IonMarkMode markMode) {

		this.markMode = markMode;
	}
}
