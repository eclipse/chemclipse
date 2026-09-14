/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.support.text.ILabel;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
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

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_EDITOR_LABEL, "Editor Label (Name in Part):", HeaderField.getOptions(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_REFERENCE_LABEL, "Reference Label (Name in Legend):", HeaderField.getOptions(), getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_NAME_TO_REFERENCES_HEADER_FIELD, "Transfer Name to References Header Field:", ILabel.getOptions(new HeaderField[]{HeaderField.DATA_NAME, HeaderField.SAMPLE_NAME, HeaderField.SAMPLE_GROUP, HeaderField.SHORT_INFO}), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_COLUMN_TYPE_TO_REFERENCES, "Transfer Column Type to References", getFieldEditorParent()));

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, "Transfer delta retention time [min]", PreferenceSupplier.MIN_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, PreferenceSupplier.MAX_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY, "Transfer best target only", getFieldEditorParent()));

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_SAVE_AS_FOLDER, "Save As... Folder", getFieldEditorParent()));
		addField(new FileFieldEditor(PreferenceSupplier.P_CHROMATOGRAM_LOAD_PROCESS_METHOD, "Load Process Method (*.ocm)", getFieldEditorParent()));
		addIntegerField(PreferenceSupplier.P_DELTA_MILLISECONDS_PEAK_SELECTION, "Delta Peak Selection [ms]", PreferenceSupplier.MIN_DELTA_MILLISECONDS_PEAK_SELECTION, PreferenceSupplier.MAX_DELTA_MILLISECONDS_PEAK_SELECTION);
		addField(new IntegerFieldEditor(PreferenceSupplier.P_ZOOMED_IN_BASES, "Initial Nucleobases", getFieldEditorParent())); // TODO: move
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