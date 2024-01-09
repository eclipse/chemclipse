/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpinnerFieldEditor;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePagePeakTraces extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePagePeakTraces() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Peak Traces");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_COLOR_SCHEME_PEAK_TRACES, "Color Scheme", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new SpinnerFieldEditor(PreferenceSupplier.P_MAX_DISPLAY_PEAK_TRACES, "Display Traces", PreferenceSupplier.MIN_TRACES, PreferenceSupplier.MAX_TRACES, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_PEAK_TRACES_OFFSET_RETENTION_TIME, "Offset Time [ms]", PreferenceSupplier.MIN_OFFSET_RETENTION_TIME, PreferenceSupplier.MAX_OFFSET_RETENTION_TIME, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
