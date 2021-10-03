/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.chemclipse.support.model.SegmentWidth;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.swt.widgets.Composite;

public class SegmentSizeFieldEditor extends IntegerFieldEditor {

	public SegmentSizeFieldEditor(String name, String labelText, Composite parent) {

		super(name, labelText, 5, 19, Validation.ODD_NUMBER, parent);
	}

	@Override
	public int getIntValue() throws NumberFormatException {

		return SegmentWidth.getAdjustedSetting(getStringValue());
	}
}
