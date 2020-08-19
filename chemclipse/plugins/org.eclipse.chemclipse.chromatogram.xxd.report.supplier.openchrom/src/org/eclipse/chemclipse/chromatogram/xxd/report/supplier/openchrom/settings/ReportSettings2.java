/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend configuration
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.DefaultChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.preferences.PreferenceSupplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ReportSettings2 extends DefaultChromatogramReportSettings {

	@JsonProperty(value = "Delta Retention Time Left [ms]", defaultValue = "0")
	@JsonPropertyDescription(value = "This is the left delta retention time in milliseconds.")
	private int deltaRetentionTimeLeft = 0;
	//
	@JsonProperty(value = "Delta Retention Time Right [ms]", defaultValue = "0")
	@JsonPropertyDescription(value = "This is the right delta retention time in milliseconds.")
	private int deltaRetentionTimeRight = 0;
	//
	@JsonProperty(value = "Use Best Match", defaultValue = "" + PreferenceSupplier.DEF_USE_BEST_MATCH)
	private boolean useBestMatch;

	public int getDeltaRetentionTimeLeft() {

		return deltaRetentionTimeLeft;
	}

	public void setDeltaRetentionTimeLeft(int deltaRetentionTimeLeft) {

		this.deltaRetentionTimeLeft = deltaRetentionTimeLeft;
	}

	public int getDeltaRetentionTimeRight() {

		return deltaRetentionTimeRight;
	}

	public void setDeltaRetentionTimeRight(int deltaRetentionTimeRight) {

		this.deltaRetentionTimeRight = deltaRetentionTimeRight;
	}

	public boolean isUseBestMatch() {

		return useBestMatch;
	}

	public void setUseBestMatch(boolean useBestMatch) {

		this.useBestMatch = useBestMatch;
	}
}
