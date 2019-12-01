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
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class TimeRangeValidator implements IValidator {

	private static final String ERROR = "Please enter a correct retention time in minutes, e.g.: 4.25.";
	/*
	 * Retention time in milliseconds.
	 */
	private TimeRange.Marker marker; // Initialized via constructor.
	private TimeRange timeRange = null;
	private int retentionTime = 0;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

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
				try {
					double retentionTimeMinutes = Double.parseDouble(((String)value).trim());
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
}
