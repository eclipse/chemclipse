/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.support.text.ILabel;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swtchart.extensions.charts.ChartOptions;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageChromatogram extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageChromatogram() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle(ExtensionMessages.chromatogram);
		setDescription("");
	}

	@Override
	public void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE, ExtensionMessages.compressionType + ":", ChartOptions.COMPRESSION_TYPES, getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceSupplier.P_COLOR_CHROMATOGRAM, "Color Chromatogram:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceSupplier.P_COLOR_CHROMATOGRAM_INACTIVE, "Color Chromatogram (Inactive):", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ENABLE_CHROMATOGRAM_AREA, "Enable Chromatogram Area", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_CHROMATOGRAM_BASELINE, "Show Chromatogram Baseline", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceSupplier.P_COLOR_CHROMATOGRAM_BASELINE, "Color Chromatogram Baseline:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ENABLE_BASELINE_AREA, "Enable Baseline Area", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_ALTERNATE_WINDOW_MOVE_DIRECTION, "Use alternate window move direction", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CONDENSE_CYCLE_NUMBER_SCANS, "Condense cycle number scans", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SET_CHROMATOGRAM_INTENSITY_RANGE, "Set chromatogram intensity range", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_REFERENCE_LABEL, "Reference Label:", HeaderField.getOptions(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_NAME_TO_REFERENCES_HEADER_FIELD, "Transfer Name to References Header Field:", ILabel.getOptions(new HeaderField[]{HeaderField.DATA_NAME, HeaderField.SAMPLE_NAME, HeaderField.SAMPLE_GROUP, HeaderField.SHORT_INFO}), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_COLUMN_TYPE_TO_REFERENCES, "Transfer Column Type to References", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_SHOW_METHODS_TOOLBAR, "Show Methods Toolbar", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_SHOW_REFERENCES_COMBO, "Show References Combo", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, "Transfer delta retention time [min]", PreferenceSupplier.MIN_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, PreferenceSupplier.MAX_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY, "Transfer best target only", getFieldEditorParent()));
		addIntegerField(PreferenceSupplier.P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY, "Stretch Chromatogram Scan Delay [ms]:", PreferenceSupplier.MIN_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY, PreferenceSupplier.MAX_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY);
		addIntegerField(PreferenceSupplier.P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH, "Stretch Chromatogram Length [ms]:", PreferenceSupplier.MIN_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH, PreferenceSupplier.MAX_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH);
		addField(new DoubleFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_EXTEND_Y, "Extend Y (1.0 = 100%)", PreferenceSupplier.MIN_CHROMATOGRAM_EXTEND_Y, PreferenceSupplier.MAX_CHROMATOGRAM_EXTEND_Y, getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("The following settings define how the chart reacts on a user selection and zoom events.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_SELECT_X, "Restrict Select X", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_SELECT_Y, "Restrict Select Y", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_FORCE_ZERO_MIN_Y_MSD, "Force Zero Min Y (MSD)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_REFERENCE_ZOOM_ZERO_X, "Reference Zoom Zero X", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_REFERENCE_ZOOM_ZERO_Y, "Reference Zoom Zero Y", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_ZOOM_X, "Restrict Zoom X", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_ZOOM_Y, "Restrict Zoom Y", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_ACTION_ID, "Chromatogram Selected Action Id:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_SAVE_AS_FOLDER, "Save As... Folder", getFieldEditorParent()));
		addField(new FileFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_LOAD_PROCESS_METHOD, "Load Process Method (*.ocm)", getFieldEditorParent()));
		addIntegerField(PreferenceSupplier.P_DELTA_MILLISECONDS_PEAK_SELECTION, "Delta Peak Selection [ms]", PreferenceSupplier.MIN_DELTA_MILLISECONDS_PEAK_SELECTION, PreferenceSupplier.MAX_DELTA_MILLISECONDS_PEAK_SELECTION);
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_MARK_ANALYSIS_SEGMENTS, "Mark Analysis Segments", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_RESUME_METHOD_DIALOG, "Show Resume Method Dialog", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SHOW_RETENTION_INDEX_MARKER, "Show Retention Index Marker", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addIntegerField(PreferenceSupplier.P_LIMIT_SIM_TRACES, "If the scan contains less than ... it's SIM.", PreferenceSupplier.MIN_SIM_IONS, PreferenceSupplier.MAX_SIM_IONS);
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addIntegerField(PreferenceSupplier.P_HEATMAP_SCALE_INTENSITY_MIN_MSD, "Scale Intensity Min (MSD)", PreferenceSupplier.MIN_HEATMAP_SCALE_INTENSITY, PreferenceSupplier.MAX_HEATMAP_SCALE_INTENSITY);
		addIntegerField(PreferenceSupplier.P_HEATMAP_SCALE_INTENSITY_MAX_MSD, "Scale Intensity Max (MSD)", PreferenceSupplier.MIN_HEATMAP_SCALE_INTENSITY, PreferenceSupplier.MAX_HEATMAP_SCALE_INTENSITY);
		addIntegerField(PreferenceSupplier.P_HEATMAP_SCALE_INTENSITY_MIN_WSD, "Scale Intensity Min (WSD)", PreferenceSupplier.MIN_HEATMAP_SCALE_INTENSITY, PreferenceSupplier.MAX_HEATMAP_SCALE_INTENSITY);
		addIntegerField(PreferenceSupplier.P_HEATMAP_SCALE_INTENSITY_MAX_WSD, "Scale Intensity Max (WSD)", PreferenceSupplier.MIN_HEATMAP_SCALE_INTENSITY, PreferenceSupplier.MAX_HEATMAP_SCALE_INTENSITY);
		addIntegerField(PreferenceSupplier.P_HEATMAP_SCALE_INTENSITY_MIN_TSD, "Scale Intensity Min (TSD)", PreferenceSupplier.MIN_HEATMAP_SCALE_INTENSITY, PreferenceSupplier.MAX_HEATMAP_SCALE_INTENSITY);
		addIntegerField(PreferenceSupplier.P_HEATMAP_SCALE_INTENSITY_MAX_TSD, "Scale Intensity Max (TSD)", PreferenceSupplier.MIN_HEATMAP_SCALE_INTENSITY, PreferenceSupplier.MAX_HEATMAP_SCALE_INTENSITY);
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CREATE_METHOD_ENABLE_RESUME, "Create Methods (enable resume)", getFieldEditorParent()));
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferencesSupport.isDarkTheme() ? PreferenceSupplier.P_CHROMATOGRAM_ACTIVE_TARGET_LABEL_FONT_DARK_COLOR : PreferenceSupplier.P_CHROMATOGRAM_ACTIVE_TARGET_LABEL_FONT_COLOR, "Color Active Target Label:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferencesSupport.isDarkTheme() ? PreferenceSupplier.P_CHROMATOGRAM_INACTIVE_TARGET_LABEL_FONT_DARK_COLOR : PreferenceSupplier.P_CHROMATOGRAM_INACTIVE_TARGET_LABEL_FONT_COLOR, "Color Inactive Target Label:", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferencesSupport.isDarkTheme() ? PreferenceSupplier.P_CHROMATOGRAM_ID_TARGET_LABEL_FONT_DARK_COLOR : PreferenceSupplier.P_CHROMATOGRAM_ID_TARGET_LABEL_FONT_COLOR, "Color ID Target Label:", getFieldEditorParent()));
	}

	private void addIntegerField(String name, String labelText, int min, int max) {

		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(name, labelText, getFieldEditorParent());
		integerFieldEditor.setValidRange(min, max);
		addField(integerFieldEditor);
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}