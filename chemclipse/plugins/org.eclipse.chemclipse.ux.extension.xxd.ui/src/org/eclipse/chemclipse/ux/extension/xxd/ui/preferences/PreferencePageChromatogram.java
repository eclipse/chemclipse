/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.eavp.service.swtchart.preferences.PreferenceSupport;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageChromatogram extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageChromatogram() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Chromatogram");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, "Display RI without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS, "Display Area without decimals", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_PEAKS_IN_SELECTED_RANGE, "Show peaks in selected range", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE, "Compression Type:", PreferenceConstants.COMPRESSION_TYPES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL, "Display Color Scheme Normal", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SIC, "Display Color Scheme SIC", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_TIC, "Line Style TIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_BPC, "Line Style BPC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_XIC, "Line Style XIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_SIC, "Line Style SIC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_TSC, "Line Style TSC:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.P_LINE_STYLE_DISPLAY_DEFAULT, "Line Style Default:", PreferenceSupport.LINE_STYLES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM, "Color Chromatogram:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA, "Enable Chromatogram Area", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK, "Color Chromatogram Selected Peak:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, "Show Chromatogram Peak Labels", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME, "Font Name:", getFieldEditorParent()));
		IntegerFieldEditor chromatogramPeakLabelFontSizeEditor = new IntegerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE, "Chromatogram Peak Label Font Size:", getFieldEditorParent());
		chromatogramPeakLabelFontSizeEditor.setValidRange(PreferenceConstants.MIN_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE, PreferenceConstants.MAX_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		addField(chromatogramPeakLabelFontSizeEditor);
		addField(new ComboFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE, "Font Style:", PreferenceConstants.FONT_STYLES, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE, "Show Chromatogram Baseline", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE, "Color Chromatogram Baseline:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ENABLE_BASELINE_AREA, "Enable Baseline Area", getFieldEditorParent()));
		//
		IntegerFieldEditor chromatogramPeakLabelSymbolSizeEditor = new IntegerFieldEditor(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE, "Chromatogram Peak Label Symbol Size:", getFieldEditorParent());
		chromatogramPeakLabelSymbolSizeEditor.setValidRange(PreferenceConstants.MIN_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE, PreferenceConstants.MAX_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
		addField(chromatogramPeakLabelSymbolSizeEditor);
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_AXIS, "Show Chromatogram Scan Axis", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
