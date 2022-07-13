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

import org.eclipse.chemclipse.pcr.report.supplier.tabular.Utils;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.LogicalOperator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannel;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannels;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.swt.VirtualChannelTable;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class VirtualChannelValidator extends ValueParserSupport implements IValidator<Object> {

	private static final String ERROR_ENTRY = "Please enter an item, e.g.: '" + VirtualChannelTable.EXAMPLE + "'";
	private static final String SEPARATOR_TOKEN = VirtualChannels.SEPARATOR_TOKEN;
	private static final String SEPARATOR_ENTRY = VirtualChannels.SEPARATOR_ENTRY;
	private static final String ERROR_TOKEN = "The item must not contain: " + SEPARATOR_TOKEN;
	//
	private String sample;
	private int[] sources;
	private LogicalOperator logicalOperator;
	private String subset;
	private int target;

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
				if(values.length >= 4) {
					/*
					 * Evaluation
					 */
					subset = parseString(values, 0);
					sample = parseString(values, 1);
					String channels = parseString(values, 2);
					sources = Utils.parseLogicalChannels(channels);
					logicalOperator = LogicalOperator.parse(channels);
					target = parseInteger(values, 3);
					/*
					 * Validations
					 */
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

	public VirtualChannel getVirtualChannel() {

		return new VirtualChannel(subset, sample, sources, logicalOperator, target);
	}
}