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
package org.eclipse.chemclipse.chromatogram.filter.impl.settings;

import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.model.RangeOption;
import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FilterSettingsReshape extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Header Field", defaultValue = "DATA_NAME")
	@JsonPropertyDescription(value = "Store the extracted transition in the selected header field.")
	private HeaderField headerField = HeaderField.DATA_NAME;
	@JsonProperty(value = "Range Option", defaultValue = "RETENTION_TIME_MIN")
	@JsonPropertyDescription(value = "Select whether to use minutes or milliseconds.")
	private RangeOption rangeOption = RangeOption.RETENTION_TIME_MIN;
	@JsonProperty(value = "Segment Width", defaultValue = "3.0")
	@JsonPropertyDescription(value = "Reshape the chromatogram and cut it into references given the segment width in minutes.")
	@DoubleSettingsProperty(minValue = PreferenceSupplier.MIN_RETENTION_TIME_MINUTES, maxValue = PreferenceSupplier.MAX_RETENTION_TIME_MINUTES)
	private double segmentWidth = 3.0d;
	@JsonProperty(value = "Reset Retention Times", defaultValue = "false")
	@JsonPropertyDescription(value = "The retention times are recalculated for each chromatogram with a scan delay of 0.")
	private boolean resetRetentionTimes = false;

	public HeaderField getHeaderField() {

		return headerField;
	}

	public void setHeaderField(HeaderField headerField) {

		this.headerField = headerField;
	}

	public RangeOption getRangeOption() {

		return rangeOption;
	}

	public void setRangeOption(RangeOption rangeOption) {

		this.rangeOption = rangeOption;
	}

	public double getSegmentWidth() {

		return segmentWidth;
	}

	public void setSegmentWidth(double segmentWidth) {

		this.segmentWidth = segmentWidth;
	}

	public boolean isResetRetentionTimes() {

		return resetRetentionTimes;
	}

	public void setResetRetentionTimes(boolean resetRetentionTimes) {

		this.resetRetentionTimes = resetRetentionTimes;
	}
}