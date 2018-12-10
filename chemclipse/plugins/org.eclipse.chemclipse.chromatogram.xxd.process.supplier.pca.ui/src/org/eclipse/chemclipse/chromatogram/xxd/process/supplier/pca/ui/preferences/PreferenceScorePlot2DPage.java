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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swtchart.extensions.preferences.PreferenceSupport;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferenceScorePlot2DPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferenceScorePlot2DPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Score Plot 2D Preferences");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_SCORE_PLOT_2D_SYMBOL_TYPE, "Symbol type:", PreferenceSupport.SYMBOL_TYPES, getFieldEditorParent()));
		addIntegerEditor(PreferenceSupplier.P_SCORE_PLOT_2D_SYMBOL_SIZE, "Symbol size", PreferenceSupplier.MIN_SCORE_PLOT_2D_SYMBOL_SIZE, PreferenceSupplier.MAX_SCORE_PLOT_2D_SYMBOL_SIZE);
	}

	private void addIntegerEditor(String name, String labelText, int min, int max) {

		IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(name, labelText, getFieldEditorParent());
		integerFieldEditor.setValidRange(min, max);
		addField(integerFieldEditor);
	}
}