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

import org.eclipse.chemclipse.support.model.WindowSize;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.swt.widgets.Composite;

public class WindowSizeFieldEditor extends ExtendedIntegerFieldEditor {

	public WindowSizeFieldEditor(String name, String labelText, Composite parent) {

		super(name, labelText, 0, 45, Validation.ODD_NUMBER_INCLUDING_ZERO, parent);
	}

	@Override
	public int getIntValue() throws NumberFormatException {

		return WindowSize.getAdjustedSetting(getStringValue());
	}
}
