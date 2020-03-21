/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("PCA");
		setDescription("");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		addField(new ComboFieldEditor(PreferenceSupplier.P_ALGORITHM_TYPE, "Algorithm Type:", PreferenceSupplier.ALGORITHM_TYPES, getFieldEditorParent()));
		addField(new org.eclipse.chemclipse.support.ui.preferences.fieldeditors.IntegerFieldEditor(PreferenceSupplier.P_NUMBER_OF_COMPONENTS, "Number of Components", PreferenceSupplier.MIN_NUMBER_OF_COMPONENTS, PreferenceSupplier.MAX_NUMBER_OF_COMPONENTS, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_COLOR_SCHEME, "Color Scheme", Colors.getAvailableColorSchemes(), getFieldEditorParent()));
	}
}