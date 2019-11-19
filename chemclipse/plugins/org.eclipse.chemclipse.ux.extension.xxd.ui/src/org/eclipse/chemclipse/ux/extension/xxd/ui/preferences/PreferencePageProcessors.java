/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsPreferencesEditPage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PreferencePageProcessors extends PreferencePage {

	private final ProcessSupplierContext processSupplierContext;

	public PreferencePageProcessors(ProcessSupplierContext context) {
		this.processSupplierContext = context;
		setTitle("User Process Settings");
		setDescription("Below you find all currently stored processor Options, select one to manage or remove the stored state");
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(Composite parent) {

		SettingsPreferencesEditPage page = new SettingsPreferencesEditPage(() -> SettingsWizard.getAllPreferences(processSupplierContext));
		page.createControl(parent);
		return page.getControl();
	}
}
