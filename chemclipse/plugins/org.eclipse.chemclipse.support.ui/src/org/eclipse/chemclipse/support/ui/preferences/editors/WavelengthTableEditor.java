/*******************************************************************************
 * Copyright (c) 2023 Lablicate Gmbh.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.util.TraceSettingUtil;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

public class WavelengthTableEditor extends TableViewerFieldEditor<String> {

	private TraceSettingUtil settingUtils;

	public WavelengthTableEditor(String name, String labelText, Composite parent) {

		super(name, labelText, new String[]{SupportMessages.wavelengths}, new int[]{200}, parent);
		settingUtils = new TraceSettingUtil();
	}

	@Override
	protected String createSavePreferences(List<String> values) {

		return settingUtils.serialize(values);
	}

	@Override
	protected List<String> parseSavePreferences(String string) {

		return settingUtils.deserialize(string);
	}

	@Override
	protected String convertColumnValue(String value, int numberColumn) {

		return value;
	}

	@Override
	protected List<String> getNewInputObject() {

		InputDialog dialog = new InputDialog(getShell(), SupportMessages.enterWavelength, SupportMessages.standardWavelengthRanges, "", new WavelengthInputValidator());
		dialog.create();
		if(Window.OK == dialog.open()) {
			String wavelength = dialog.getValue();
			return settingUtils.parseInput(wavelength);
		}
		return new ArrayList<>();
	}

	@Override
	protected int compareValue(String value1, String value2, int indexNumber) {

		return settingUtils.compare(value1, value2);
	}
}
