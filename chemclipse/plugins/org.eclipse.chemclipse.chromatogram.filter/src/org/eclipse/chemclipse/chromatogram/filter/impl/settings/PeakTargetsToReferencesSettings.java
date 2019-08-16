/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class PeakTargetsToReferencesSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Transfer Best Target Only", defaultValue = "false")
	@JsonPropertyDescription(value = "If this value is true, only the best target will be transfered.")
	private boolean useBestTargetOnly = false;
	@JsonProperty(value = "Delta Retention Time (Minutes)", defaultValue = "0.1")
	@JsonPropertyDescription(value = "This is the delta retention time to map the peaks.")
	private double deltaRetentionTime = 0.1;

	public boolean isUseBestTargetOnly() {

		return useBestTargetOnly;
	}

	public void setUseBestTargetOnly(boolean useBestTargetOnly) {

		this.useBestTargetOnly = useBestTargetOnly;
	}

	public double getDeltaRetentionTime() {

		return deltaRetentionTime;
	}

	public void setDeltaRetentionTime(double deltaRetentionTime) {

		this.deltaRetentionTime = deltaRetentionTime;
	}
}
