/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DAD
 *******************************************************************************/
package org.eclipse.chemclipse.support.validators;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.util.NamedWavelengthListUtil;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class WavelengthValidator implements IValidator {

	private static final String ERROR = "Please enter valid wavelength in nm, e.g.: 253.7";
	private static final String ERROR_VALUE = "The wavelength value can't be parsed: ";
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.#");
	private Set<Double> wavelengths = new TreeSet<Double>();

	@Override
	public IStatus validate(Object value) {

		wavelengths.clear();
		String message = null;
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String) {
				String text = (String)value;
				String[] values = text.split(NamedWavelengthListUtil.SEPARATOR_WAVELENGTH);
				exitloop:
				for(String val : values) {
					try {
						wavelengths.add(Double.parseDouble(val));
					} catch(NumberFormatException e) {
						message = ERROR_VALUE + val;
						break exitloop;
					}
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

	public List<Double> getWavelengthsAsDouble() {

		return new ArrayList<Double>(wavelengths);
	}

	public List<Integer> getWavelengthsAsInteger() {

		Set<Integer> trazes = new HashSet<>();
		for(double wavelength : wavelengths) {
			trazes.add(Math.round((float)wavelength));
		}
		return new ArrayList<>(trazes);
	}

	public String getWavelengthsAsString() {

		StringBuilder builder = new StringBuilder();
		Iterator<Double> iterator = wavelengths.iterator();
		while(iterator.hasNext()) {
			builder.append(decimalFormat.format(iterator.next()));
			if(iterator.hasNext()) {
				builder.append(NamedWavelengthListUtil.SEPARATOR_WAVELENGTH);
			}
		}
		return builder.toString();
	}
}
