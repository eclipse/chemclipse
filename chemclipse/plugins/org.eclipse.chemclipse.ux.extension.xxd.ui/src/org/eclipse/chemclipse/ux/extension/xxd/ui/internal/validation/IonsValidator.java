/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class IonsValidator implements IValidator {

	private static final String ERROR = "Please enter an ion list, e.g.: 18 28 32 84 207";
	private static final String ERROR_VALUE = "The ion value can't be parsed: ";
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
	//
	private Set<Integer> ions = new TreeSet<Integer>();

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String) {
				String text = (String)value;
				String[] values = text.split(OverlayChartSupport.SELECTED_IONS_CONCATENATOR);
				exitloop:
				for(String val : values) {
					try {
						ions.add(AbstractIon.getIon(decimalFormat.parse(val).doubleValue()));
					} catch(ParseException e) {
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

	public List<Integer> getIons() {

		return new ArrayList<Integer>(ions);
	}

	public String getIonsAsString() {

		StringBuilder builder = new StringBuilder();
		Iterator<Integer> iterator = ions.iterator();
		while(iterator.hasNext()) {
			builder.append(iterator.next());
			if(iterator.hasNext()) {
				builder.append(OverlayChartSupport.SELECTED_IONS_CONCATENATOR);
			}
		}
		return builder.toString();
	}
}
