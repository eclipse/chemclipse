/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.preferences;

import org.eclipse.chemclipse.swt.ui.Activator;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SWTPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public SWTPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the SWT settings.");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		addField(new ColorFieldEditor(PreferenceConstants.P_POSITION_MARKER_BACKGROUND_COLOR, "Position marker background color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_POSITION_MARKER_FOREGROUND_COLOR, "Position marker foreground color", getFieldEditorParent()));
		//
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_SELECTED_PEAK_IN_EDITOR, "Show the selected peak in the editor.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_SCANS_OF_SELECTED_PEAK, "Show the scans of the selected peak.", getFieldEditorParent()));
		addField(new ScaleFieldEditor(PreferenceConstants.P_SIZE_OF_PEAK_SCAN_MARKER, "Size of peak scan marker.", getFieldEditorParent(), 1, 10, 1, 1));
		//
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_BACKGROUND_IN_CHROMATOGRAM_EDITOR, "Show background in chromatogram editor.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_AREA, "Show chromatogram area.", getFieldEditorParent()));
		//
		addField(new IntegerFieldEditor(PreferenceConstants.P_SCAN_DISPLAY_NUMBER_OF_IONS, "Number of labeled m/z values.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_USE_MODULO_DISPLAY_OF_IONS, "Use modulo display of labeled m/z values.", getFieldEditorParent()));
		//
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM, "Chromatogram color", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_MASS_SPECTRUM, "Mass spectrum color", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_OVERLAY, "Overlay color scheme", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		//
		addField(new BooleanFieldEditor(PreferenceConstants.P_AUTO_ADJUST_EDITOR_INTENSITY_DISPLAY, "Auto adjust editor intensity display.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_AUTO_ADJUST_VIEW_INTENSITY_DISPLAY, "Auto adjust view intensity display.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION, "Alternate window move direction.", getFieldEditorParent()));
		//
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_AXIS_MILLISECONDS, "Show milliseconds axis", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_AXIS_RELATIVE_INTENSITY, "Show relative intensity axis", getFieldEditorParent()));
		//
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_HAIRLINE_DIVIDER, "Show chromatogram hairline divider", getFieldEditorParent()));
		//
		addField(new BooleanFieldEditor(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS, "Condense Scans with Cycle Number", getFieldEditorParent()));
		//
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, "Show retention index without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS, "Show area without decimals", getFieldEditorParent()));
	}
}
