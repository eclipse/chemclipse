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

import java.util.List;

import org.eclipse.chemclipse.support.util.StringSettingUtil;
import org.eclipse.swt.widgets.Composite;

public abstract class StringTableFieldEditor extends TableViewerFieldEditor<String[]> {

	private StringSettingUtil settingUtil;

	protected StringTableFieldEditor(String name, String labelText, String[] columnNames, int[] columnWidth, Composite parent) {
		super(name, labelText, columnNames, columnWidth, parent);
		settingUtil = new StringSettingUtil();
	}

	@Override
	protected String createSavePreferences(List<String[]> values) {

		return settingUtil.serialize(values);
	}

	@Override
	protected List<String[]> parseSavePreferences(String data) {

		return settingUtil.deserialize(data);
	}

	@Override
	protected String convertColumnValue(String[] value, int indexColumn) {

		return value[indexColumn];
	}

	@Override
	protected int compareValue(String[] value1, String[] value2, int indexColumn) {

		return value1[indexColumn].compareTo(value2[indexColumn]);
	}
}
