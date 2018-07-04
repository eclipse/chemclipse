/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.util.IonSettingUtil;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

public class IonTableEditor extends TableViewerFieldEditor<String> {

	private IonSettingUtil settinngUtils;

	public IonTableEditor(String name, String labelText, Composite parent) {
		super(name, labelText, new String[]{"Ions"}, new int[]{200}, parent);
		settinngUtils = new IonSettingUtil();
	}

	@Override
	protected String createSavePreferences(List<String> values) {

		return settinngUtils.serialize(values);
	}

	@Override
	protected List<String> parseSavePreferences(String string) {

		return settinngUtils.deserialize(string);
	}

	@Override
	protected String convertColumnValue(String value, int numberColumn) {

		return value;
	}

	@Override
	protected List<String> getNewInputObject() {

		InputDialog dialog = new InputDialog(getShell(), "Enter a ion.", "Standard values are 18 (water), 28 (nitrogen), 84 (solvent tailing), 207 (column bleed).", "", new IonInputValidator());
		dialog.create();
		if(Window.OK == dialog.open()) {
			String ion = dialog.getValue();
			return settinngUtils.parseInput(ion);
		}
		return new ArrayList<>();
	}

	@Override
	protected int compareValue(String value1, String value2, int columnNumber) {

		return settinngUtils.compare(value1, value2);
	}
}
