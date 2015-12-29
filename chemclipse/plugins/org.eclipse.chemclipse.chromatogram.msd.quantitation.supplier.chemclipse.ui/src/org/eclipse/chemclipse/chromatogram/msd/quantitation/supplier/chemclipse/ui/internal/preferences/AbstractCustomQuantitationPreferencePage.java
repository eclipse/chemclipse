/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;

public abstract class AbstractCustomQuantitationPreferencePage extends AbstractQuantitationTabFolderPreferencePage {

	private IPreferenceStore preferenceStore;

	public AbstractCustomQuantitationPreferencePage(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	@Override
	public void init(IWorkbench workbench) {

		setPreferenceStore(preferenceStore);
	}

	@Override
	protected void createFieldEditors() {

		createSettingPages();
	}
}
