/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences.PreferenceSupplier;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

public class IonListEditor extends ListEditor {

	public IonListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected String createList(String[] items) {

		return PreferenceSupplier.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		InputDialog dialog = new InputDialog(getShell(), "Enter a ion.", "Standard values are 18 (water), 28 (nitrogen), 84 (solvent tailing), 207 (column bleed).", "", new IonInputValidator());
		dialog.create();
		dialog.open();
		String ion = dialog.getValue();
		return ion;
	}

	@Override
	protected String[] parseString(String stringList) {

		return PreferenceSupplier.parseString(stringList);
	}
}
