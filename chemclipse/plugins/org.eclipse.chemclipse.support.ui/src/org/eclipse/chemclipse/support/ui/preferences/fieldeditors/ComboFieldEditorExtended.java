/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.chemclipse.support.settings.StringSelectionSettingProperty;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class ComboFieldEditorExtended extends ComboFieldEditor {

	public ComboFieldEditorExtended(String name, String labelText, String[][] entryNamesAndValues, Composite parent) {

		super(name, labelText, entryNamesAndValues, parent);
	}

	public ComboFieldEditorExtended(String name, String labelText, Class<? extends Enum<?>> ecls, Composite parent) {

		super(name, labelText, enumToString(ecls), parent);
	}

	public ComboFieldEditorExtended(String name, String labelText, StringSelectionSettingProperty stringSelectionSettingProperty, Composite parent) {

		this(name, labelText, stringSelectionSettingProperty.ids(), stringSelectionSettingProperty.labels(), parent);
	}

	public ComboFieldEditorExtended(String name, String labelText, String[] ids, String[] labels, Composite parent) {

		super(name, labelText, toString(ids, labels), parent);
	}

	private static String[][] enumToString(Class<? extends Enum<?>> ecls) {

		Enum<?>[] enums = ecls.getEnumConstants();
		String[][] strings = new String[enums.length][2];
		for(int i = 0; i < enums.length; i++) {
			strings[i][0] = enums[i].toString();
			strings[i][1] = enums[i].name();
		}
		return strings;
	}

	private static String[][] toString(String[] ids, String[] labels) {

		String[][] strings = new String[ids.length][2];
		for(int i = 0; i < ids.length; i++) {
			strings[i][0] = labels[i];
			strings[i][1] = ids[i];
		}
		return strings;
	}
}
