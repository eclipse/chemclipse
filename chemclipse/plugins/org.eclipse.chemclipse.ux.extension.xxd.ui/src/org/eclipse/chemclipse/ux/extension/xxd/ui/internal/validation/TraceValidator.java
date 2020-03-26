/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.util.NamedTraceListUtil;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class TraceValidator implements IValidator {

	private static final String ERROR = "Please enter valid traces, e.g.: 320.1 400";
	private static final String ERROR_VALUE = "The trace value can't be parsed: ";
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.#");
	private Set<Double> traces = new TreeSet<Double>();

	@Override
	public IStatus validate(Object value) {

		traces.clear();
		String message = null;
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String) {
				String text = (String)value;
				String[] values = text.split(NamedTraceListUtil.SEPARATOR_TRACE);
				exitloop:
				for(String val : values) {
					try {
						traces.add(Double.parseDouble(val));
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

	public List<Double> getTraces() {

		return new ArrayList<Double>(traces);
	}

	public String getTracesAsString() {

		StringBuilder builder = new StringBuilder();
		Iterator<Double> iterator = traces.iterator();
		while(iterator.hasNext()) {
			builder.append(decimalFormat.format(iterator.next()));
			if(iterator.hasNext()) {
				builder.append(NamedTraceListUtil.SEPARATOR_TRACE);
			}
		}
		return builder.toString();
	}
}
