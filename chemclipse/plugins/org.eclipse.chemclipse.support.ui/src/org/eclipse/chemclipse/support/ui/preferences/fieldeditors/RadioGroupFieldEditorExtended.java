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
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.chemclipse.support.settings.StringSelectionRadioButtonsSettingProperty;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class RadioGroupFieldEditorExtended extends RadioGroupFieldEditor {

	public RadioGroupFieldEditorExtended() {
		super();
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, Class<? extends Enum<?>> ecls, Composite parent) {
		super(name, labelText, numColumns, enumToString(ecls), parent);
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, Class<? extends Enum<?>> ecls, Composite parent, boolean useGroup) {
		super(name, labelText, numColumns, enumToString(ecls), parent, useGroup);
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, StringSelectionRadioButtonsSettingProperty stringSelectionRadioButtonsSettingProperty, Composite parent) {
		this(name, labelText, numColumns, stringSelectionRadioButtonsSettingProperty.ids(), stringSelectionRadioButtonsSettingProperty.labels(), parent);
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, StringSelectionRadioButtonsSettingProperty stringSelectionRadioButtonsSettingProperty, Composite parent, boolean useGroup) {
		this(name, labelText, numColumns, stringSelectionRadioButtonsSettingProperty.ids(), stringSelectionRadioButtonsSettingProperty.labels(), parent, useGroup);
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, String[][] labelAndValues, Composite parent, boolean useGroup) {
		super(name, labelText, numColumns, labelAndValues, parent, useGroup);
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, String[][] labelAndValues, Composite parent) {
		super(name, labelText, numColumns, labelAndValues, parent);
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, String[] ids, String[] labels, Composite parent) {
		super(name, labelText, numColumns, toString(ids, labels), parent);
	}

	public RadioGroupFieldEditorExtended(String name, String labelText, int numColumns, String[] ids, String[] labels, Composite parent, boolean useGroup) {
		super(name, labelText, numColumns, toString(ids, labels), parent, useGroup);
	}

	private static String[][] enumToString(Class<? extends Enum<?>> ecls) {

		@SuppressWarnings("rawtypes")
		Enum[] enums = ecls.getEnumConstants();
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
