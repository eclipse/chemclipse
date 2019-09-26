/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportSettings2 extends DefaultChromatogramReportSettings {

	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_DELTA_RETENTION_TIME_MINUTES, maxValue = PreferenceSupplier.MAX_DELTA_RETENTION_TIME_MINUTES)
	@JsonProperty(value = "Delta Retention Time Left (Minutes)", defaultValue = "" + PreferenceSupplier.DEF_DELTA_RETENTION_TIME_MINUTES_LEFT)
	private double deltaRetentionTimeMinutesLeft = 0.0d;
	//
	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_DELTA_RETENTION_TIME_MINUTES, maxValue = PreferenceSupplier.MAX_DELTA_RETENTION_TIME_MINUTES)
	@JsonProperty(value = "Delta Retention Time Right (Minutes)", defaultValue = "" + PreferenceSupplier.DEF_DELTA_RETENTION_TIME_MINUTES_RIGHT)
	private double deltaRetentionTimeMinutesRight = 0.0d;
	//
	@JsonProperty(value = "Use Best Match", defaultValue = "" + PreferenceSupplier.DEF_USE_BEST_MATCH)
	private boolean useBestMatch;

	public double getDeltaRetentionTimeMinutesLeft() {

		return deltaRetentionTimeMinutesLeft;
	}

	public void setDeltaRetentionTimeMinutesLeft(double deltaRetentionTimeMinutesLeft) {

		this.deltaRetentionTimeMinutesLeft = deltaRetentionTimeMinutesLeft;
	}

	public double getDeltaRetentionTimeMinutesRight() {

		return deltaRetentionTimeMinutesRight;
	}

	public void setDeltaRetentionTimeMinutesRight(double deltaRetentionTimeMinutesRight) {

		this.deltaRetentionTimeMinutesRight = deltaRetentionTimeMinutesRight;
	}

	public boolean isUseBestMatch() {

		return useBestMatch;
	}

	public void setUseBestMatch(boolean useBestMatch) {

		this.useBestMatch = useBestMatch;
	}
}
