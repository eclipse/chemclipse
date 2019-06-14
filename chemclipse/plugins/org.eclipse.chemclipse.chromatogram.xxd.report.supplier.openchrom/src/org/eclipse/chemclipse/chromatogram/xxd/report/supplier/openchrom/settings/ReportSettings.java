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

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.AbstractChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportSettings extends AbstractChromatogramReportSettings {

	@IntSettingsProperty(maxValue = PreferenceSupplier.MAX_DELTA_RETENTION_TIME, minValue = PreferenceSupplier.MIN_DELTA_RETENTION_TIME)
	@JsonProperty(value = "Delta Retention Time (Minutes)", defaultValue = "" + PreferenceSupplier.DEF_DELTA_RETENTION_TIME)
	private int deltaRetentionTime = -1;
	@JsonProperty(value = "Use Best Match", defaultValue = "" + PreferenceSupplier.DEF_USE_BEST_MATCH)
	private boolean useBestMatch;

	@Override
	protected String getDefaultFolder() {

		return org.eclipse.chemclipse.xxd.process.preferences.PreferenceSupplier.getReportExportFolder();
	}

	public int getDeltaRetentionTime() {

		if(deltaRetentionTime < PreferenceSupplier.MIN_DELTA_RETENTION_TIME) {
			return PreferenceSupplier.getDeltaRetentionTime();
		}
		return deltaRetentionTime;
	}

	public boolean isUseBestMatch() {

		return useBestMatch;
	}
}
