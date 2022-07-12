/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMapping;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.swt.ChannelMappingTable;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ChannelMappingValidator extends ValueParserSupport implements IValidator<Object> {

	private static final String ERROR_ENTRY = "Please enter an item, e.g.: '" + ChannelMappingTable.EXAMPLE + "'";
	private static final String SEPARATOR_TOKEN = ChannelMappings.SEPARATOR_TOKEN;
	private static final String SEPARATOR_ENTRY = ChannelMappings.SEPARATOR_ENTRY;
	private static final String ERROR_TOKEN = "The item must not contain: " + SEPARATOR_TOKEN;
	//
	private int channel;
	private String subset;
	private String label;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = ERROR_ENTRY;
		} else if(value instanceof String) {
			String text = ((String)value).trim();
			if(text.contains(SEPARATOR_TOKEN)) {
				message = ERROR_TOKEN;
			} else if("".equals(text.trim())) {
				message = ERROR_ENTRY;
			} else {
				String[] values = text.trim().split("\\" + SEPARATOR_ENTRY); // The pipe needs to be escaped.
				if(values.length >= 3) {
					/*
					 * Evaluation
					 */
					subset = parseString(values, 0);
					channel = parseInteger(values, 1);
					label = parseString(values, 2);
					/*
					 * Validations
					 */
					if(channel < 1) {
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

	public ChannelMapping getMapping() {

		return new ChannelMapping(subset, channel, label);
	}
}