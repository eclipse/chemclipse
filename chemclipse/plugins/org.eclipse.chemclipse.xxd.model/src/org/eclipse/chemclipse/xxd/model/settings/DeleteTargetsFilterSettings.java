/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.settings;

import org.eclipse.chemclipse.support.settings.EnumSelectionSettingProperty;
import org.eclipse.chemclipse.xxd.model.support.TargetsDeleteOption;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteTargetsFilterSettings {

	@JsonProperty(value = "Target Delete Option:")
	@EnumSelectionSettingProperty
	private TargetsDeleteOption targetDeleteOption = TargetsDeleteOption.ALL_TARGETS;

	public TargetsDeleteOption getTargetDeleteOption() {

		return targetDeleteOption;
	}

	public void setTargetDeleteOption(TargetsDeleteOption targetDeleteOption) {

		this.targetDeleteOption = targetDeleteOption;
	}
}
