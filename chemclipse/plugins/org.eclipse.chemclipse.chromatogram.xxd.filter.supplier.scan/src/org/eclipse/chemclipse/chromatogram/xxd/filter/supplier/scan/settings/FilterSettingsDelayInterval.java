/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsDelayInterval extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Reset Retention Times", defaultValue = "false")
	@JsonPropertyDescription(value = "Reset the retention times after calculation of scan delay/interval.")
	private boolean resetRetentionTimes = false;

	public boolean isResetRetentionTimes() {

		return resetRetentionTimes;
	}

	public void setResetRetentionTimes(boolean resetRetentionTimes) {

		this.resetRetentionTimes = resetRetentionTimes;
	}
}