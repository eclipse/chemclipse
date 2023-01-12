/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import java.util.Arrays;

import org.eclipse.chemclipse.support.text.ILabel;

public enum CalibrationMethod implements ILabel {

	/*
	 * ISTD is used for internal standards only.
	 * All other are used for external calibration.
	 */
	LINEAR("Linear"), //
	QUADRATIC("Quadratic"), //
	AVERAGE("Average"), //
	ISTD("Internal Standard");

	private String label = "";

	private CalibrationMethod(String label) {

		this.label = label;
	}

	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}

	public static final CalibrationMethod[] getInternalCalibrationOptions() {

		return new CalibrationMethod[]{ //
				CalibrationMethod.ISTD //
		};
	}

	public static final String[] getInternalCalibrationOptionsArray() {

		return getOptionsArray(getInternalCalibrationOptions());
	}

	public static final CalibrationMethod[] getExternalCalibrationOptions() {

		return new CalibrationMethod[]{ //
				CalibrationMethod.AVERAGE, //
				CalibrationMethod.LINEAR, //
				CalibrationMethod.QUADRATIC //
		};
	}

	public static final String[] getExternalCalibrationOptionsArray() {

		return getOptionsArray(getExternalCalibrationOptions());
	}

	private static final String[] getOptionsArray(CalibrationMethod[] calibrationMethods) {

		return Arrays.asList(calibrationMethods).toArray(new String[calibrationMethods.length]);
	}
}