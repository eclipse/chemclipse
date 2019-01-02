/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.validators;

public abstract class AbstractValidator {

	protected boolean parseBoolean(String[] values, int index) {

		return parseBoolean(values, index, false);
	}

	protected boolean parseBoolean(String[] values, int index, boolean def) {

		boolean result = def;
		String value = parseString(values, index, "");
		result = Boolean.parseBoolean(value);
		return result;
	}

	protected String parseString(String[] values, int index) {

		return parseString(values, index, "");
	}

	protected String parseString(String[] values, int index, String def) {

		return (values.length > index) ? values[index].trim() : def;
	}

	protected float parseFloat(String[] values, int index) {

		return parseFloat(values, index, 0.0f);
	}

	protected float parseFloat(String[] values, int index, float def) {

		float result = def;
		String value = parseString(values, index, "");
		if(!"".equals(value)) {
			try {
				result = Float.parseFloat(value);
			} catch(NumberFormatException e) {
				//
			}
		}
		return result;
	}

	protected double parseDouble(String[] values, int index) {

		return parseDouble(values, index, 0.0d);
	}

	protected double parseDouble(String[] values, int index, double def) {

		double result = def;
		String value = parseString(values, index, "");
		if(!"".equals(value)) {
			try {
				result = Double.parseDouble(value);
			} catch(NumberFormatException e) {
				//
			}
		}
		return result;
	}
}
