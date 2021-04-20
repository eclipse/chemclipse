/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.model.math.IonRoundMethod;
import org.eclipse.chemclipse.support.settings.EnumSelectionSettingProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsIonRounding extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Ion Round Method", defaultValue = "DEFAULT")
	@JsonPropertyDescription(value = "Set the used m/z round method on a system level.")
	@EnumSelectionSettingProperty
	private IonRoundMethod ionRoundMethod = IonRoundMethod.DEFAULT;

	public IonRoundMethod getIonRoundMethod() {

		return ionRoundMethod;
	}

	public void setIonRoundMethod(IonRoundMethod ionRoundMethod) {

		this.ionRoundMethod = ionRoundMethod;
	}
}