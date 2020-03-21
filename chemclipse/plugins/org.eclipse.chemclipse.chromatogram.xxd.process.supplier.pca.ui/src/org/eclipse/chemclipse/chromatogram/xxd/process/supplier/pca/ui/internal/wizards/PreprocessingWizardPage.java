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
 * Dr. Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt.PreprocessingSettingsUI;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PreprocessingWizardPage extends WizardPage {

	private PreprocessingSettingsUI preprocessingSettingsUI;

	public PreprocessingWizardPage() {
		super("Preprocess");
		setTitle("Preprocess Data");
		setDescription("Data can be also preprocessed later in the process in Data Preprocessing page");
	}

	@Override
	public void createControl(Composite parent) {

		preprocessingSettingsUI = new PreprocessingSettingsUI(parent, SWT.NONE);
		setControl(preprocessingSettingsUI);
	}

	public PreprocessingSettings getPreprocessingSettings() {

		return preprocessingSettingsUI.getPreprocessingSettings();
	}
}
