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
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.util.TimeRangeListUtil;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class TimeRangeValidator implements IValidator {

	private static final String ERROR = "Please enter a correct retention time in minutes, e.g.: 4.25.";
	/*
	 * Retention time in milliseconds.
	 */
	private TimeRange.Marker marker = null;
	private TimeRange timeRange = null;
	private int retentionTime = 0;
	//
	private String identifier = "";
	private int retentionTimeStart = 0;
	private int retentionTimeCenter = 0;
	private int retentionTimeStop = 0;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public TimeRangeValidator() {
		this(null);
	}

	public TimeRangeValidator(TimeRange.Marker marker) {
		this.marker = marker;
	}

	public void setTimeRange(TimeRange timeRange) {

		this.timeRange = timeRange;
	}

	@Override
	public IStatus validate(Object value) {

		String message = null;
		retentionTime = 0;
		/*
		 * The value is the retention time in minutes.
		 * But this validator returns the retention time in milliseconds.
		 */
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String) {
				if(marker != null) {
					parseSpecificMarker((String)value);
				} else {
					parseAllMarker((String)value);
				}
			} else {
				message = ERROR;
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	private String parseSpecificMarker(String value) {

		String message = null;
		try {
			double retentionTimeMinutes = Double.parseDouble((value).trim());
			if(retentionTimeMinutes < 0.0d) {
				message = "The retention time must be > 0.0.";
			} else {
				retentionTime = (int)(retentionTimeMinutes * TimeRange.MINUTE_FACTOR);
				if(timeRange != null) {
					switch(marker) {
						case START:
							if(retentionTime > timeRange.getCenter()) {
								message = "The retention time must be <= center (" + getRetentionTimeMinutes(timeRange.getCenter()) + ").";
							}
							break;
						case CENTER:
							if(retentionTime < timeRange.getStart() || retentionTime > timeRange.getStop()) {
								message = "The retention time must be >= start (" + getRetentionTimeMinutes(timeRange.getStart()) + ") and <= stop (" + getRetentionTimeMinutes(timeRange.getStop()) + ").";
							}
							break;
						case STOP:
							if(retentionTime < timeRange.getCenter()) {
								message = "The retention time must be >= center (" + getRetentionTimeMinutes(timeRange.getCenter()) + ").";
							}
							break;
					}
				}
			}
		} catch(NumberFormatException e) {
			message = ERROR;
		}
		//
		return message;
	}

	private String parseAllMarker(String value) {

		String message = null;
		try {
			String[] values = value.split(TimeRangeListUtil.SEPARATOR_ENTRY);
			if(values.length == 4) {
				identifier = values[0].trim();
				retentionTimeStart = (int)(Double.parseDouble(values[1].trim()) * TimeRange.MINUTE_FACTOR);
				retentionTimeCenter = (int)(Double.parseDouble(values[2].trim()) * TimeRange.MINUTE_FACTOR);
				retentionTimeStop = (int)(Double.parseDouble(values[3].trim()) * TimeRange.MINUTE_FACTOR);
				//
				if("".equals(identifier)) {
					message = "Please specify an identifier.";
				} else if(retentionTimeStart > retentionTimeCenter) {
					message = "Start > Center";
				} else if(retentionTimeStart > retentionTimeCenter || retentionTimeStart > retentionTimeStop) {
					message = "Start > Stop";
				} else if(retentionTimeCenter > retentionTimeStop) {
					message = "Center > Stop";
				}
			}
		} catch(Exception e) {
			message = "Input is invalid.";
		}
		return message;
	}

	/**
	 * Return the validated retention time in milliseconds.
	 * 
	 * @return int
	 */
	public int getRetentionTime() {

		return retentionTime;
	}

	private String getRetentionTimeMinutes(int milliseconds) {

		return decimalFormat.format(milliseconds / TimeRange.MINUTE_FACTOR);
	}

	public String getIdentifier() {

		return identifier;
	}

	public int getRetentionTimeStart() {

		return retentionTimeStart;
	}

	public int getRetentionTimeCenter() {

		return retentionTimeCenter;
	}

	public int getRetentionTimeStop() {

		return retentionTimeStop;
	}
}
