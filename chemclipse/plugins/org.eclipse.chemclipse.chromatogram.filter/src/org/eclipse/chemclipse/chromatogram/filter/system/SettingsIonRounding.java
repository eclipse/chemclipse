/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.system;

import org.eclipse.chemclipse.model.math.IonRoundMethod;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.support.settings.LabelProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsIonRounding implements ISystemProcessSettings {

	@JsonProperty(value = "Ion Round Method", defaultValue = "DEFAULT")
	@LabelProperty(value = "%IonRoundMethod", tooltip = "%IonRoundMethodDescription")
	private IonRoundMethod ionRoundMethod = IonRoundMethod.DEFAULT;

	public IonRoundMethod getIonRoundMethod() {

		return ionRoundMethod;
	}

	public void setIonRoundMethod(IonRoundMethod ionRoundMethod) {

		this.ionRoundMethod = ionRoundMethod;
	}
}