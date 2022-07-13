/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMapping;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;

public class ChannelMappingInputValidator implements IInputValidator {

	private ChannelMappingValidator validator = new ChannelMappingValidator();
	private ChannelMappings channelMappings;

	public ChannelMappingInputValidator(ChannelMappings channelMappings) {

		this.channelMappings = channelMappings;
	}

	@Override
	public String isValid(String target) {

		IStatus status = validator.validate(target);
		if(status.isOK()) {
			ChannelMapping mapping = validator.getMapping();
			if(channelMappings.contains(mapping)) {
				return "The element already exists.";
			}
		} else {
			return status.getMessage();
		}
		return null;
	}
}
