/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.Utils;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.WellMapping;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.WellMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.swt.WellMappingTable;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class WellMappingValidator extends ValueParserSupport implements IValidator<Object> {

	private static final String ERROR_ENTRY = "Please enter an item, e.g.: '" + WellMappingTable.EXAMPLE + "'";
	private static final String SEPARATOR_TOKEN = WellMappings.SEPARATOR_TOKEN;
	private static final String SEPARATOR_ENTRY = WellMappings.SEPARATOR_ENTRY;
	private static final String ERROR_TOKEN = "The item must not contain: " + SEPARATOR_TOKEN;
	//
	private String subset;
	private String sample;
	private int[] channels;
	private int cutoff;
	private String positive;
	private String negative;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = ERROR_ENTRY;
		} else if(value instanceof String text) {
			text = text.trim();
			if(text.contains(SEPARATOR_TOKEN)) {
				message = ERROR_TOKEN;
			} else if("".equals(text.trim())) {
				message = ERROR_ENTRY;
			} else {
				String[] values = text.trim().split("\\" + SEPARATOR_ENTRY); // The pipe needs to be escaped.
				if(values.length >= 6) {
					/*
					 * Evaluation
					 */
					subset = parseString(values, 0);
					sample = parseString(values, 1);
					channels = Utils.parseChannels(values, 2);
					cutoff = parseInteger(values, 3);
					positive = parseString(values, 4);
					negative = parseString(values, 5);
					/*
					 * Validations
					 */
					if(channels.length < 1) {
						message = "A channel has to be selected";
					}
					if("".equals(subset)) {
						message = "A subset needs to be set.";
					}
				} else {
					message = ERROR_ENTRY;
				}
			}
		} else {
			message = ERROR_ENTRY;
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public WellMapping getMapping() {

		return new WellMapping(subset, sample, channels, cutoff, positive, negative);
	}
}
