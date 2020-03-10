/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - Optimize UI
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt.FilterSettingsUI;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class FilterWizardPage extends WizardPage {

	private FilterSettingsUI filterSettingsUI;

	protected FilterWizardPage() {
		super("Filter");
		setTitle("Add Filters");
		setDescription("Filters can be also added later in the PCA editor.");
	}

	@Override
	public void createControl(Composite parent) {

		filterSettingsUI = new FilterSettingsUI(parent, SWT.NONE);
		setControl(filterSettingsUI);
	}

	public FilterSettings getFilterSettings() {

		return filterSettingsUI.getFilterSettings();
	}
}
