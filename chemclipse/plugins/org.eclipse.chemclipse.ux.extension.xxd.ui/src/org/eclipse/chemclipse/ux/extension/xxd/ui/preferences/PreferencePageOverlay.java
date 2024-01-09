/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.ExtendedIntegerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swtchart.extensions.charts.ChartOptions;
import org.eclipse.swtchart.extensions.preferences.PreferenceSupport;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageOverlay extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageOverlay() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Overlay");
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_OVERLAY_CHART_COMPRESSION_TYPE, "Compression Type:", ChartOptions.COMPRESSION_TYPES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_COLOR_SCHEME_DISPLAY_OVERLAY, "Display Color Scheme", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_LINE_STYLE_DISPLAY_OVERLAY, "Line Style:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS, "Show Referenced Chromatograms", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_OVERLAY_ADD_TYPE_INFO, "Overlay Add Type Info [MSD, ...]", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_OVERLAY_SHOW_AREA, "Overlay Show Area", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS, "Overlay Autofocus Profile Settings", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS, "Overlay Autofocus Shift Settings", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_OPTIMIZED_CHROMATOGRAM_XWC, "Show Optimized Chromatogram XWC", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_OVERLAY_LOCK_ZOOM, "Overlay Lock Zoom", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_OVERLAY_FOCUS_SELECTION, "Overlay Focus Selection", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_OVERLAY_SHIFT_X, "Overlay Shift X:", PreferenceSupplier.MIN_OVERLAY_SHIFT_X, PreferenceSupplier.MAX_OVERLAY_SHIFT_X, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_INDEX_SHIFT_X, "Index Shift X", PreferenceSupplier.MIN_INDEX_SHIFT_X, PreferenceSupplier.MAX_INDEX_SHIFT_X, getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_OVERLAY_SHIFT_Y, "Overlay Shift Y:", PreferenceSupplier.MIN_OVERLAY_SHIFT_Y, PreferenceSupplier.MAX_OVERLAY_SHIFT_Y, getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_INDEX_SHIFT_Y, "Index Shift Y", PreferenceSupplier.MIN_INDEX_SHIFT_Y, PreferenceSupplier.MAX_INDEX_SHIFT_Y, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ExtendedIntegerFieldEditor(PreferenceSupplier.P_MODULO_AUTO_MIRROR_CHROMATOGRAMS, "Auto Mirror every nth chromatogram", PreferenceSupplier.MIN_MODULO_AUTO_MIRROR, PreferenceSupplier.MAX_MODULO_AUTO_MIRROR, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
