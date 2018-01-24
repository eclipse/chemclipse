/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.DataPreprocessingSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ModificationDataWizardPage extends WizardPage {

	private PcaPreprocessingData pcaPreprocessingData;

	protected ModificationDataWizardPage(String pageName) {
		super(pageName);
		setTitle("Preprocess Data");
		setDescription("Data can be also preprocessed later in the process in Data Preprocessing page");
		pcaPreprocessingData = new PcaPreprocessingData();
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		new DataPreprocessingSelection(composite, gridData, pcaPreprocessingData);
		setControl(composite);
	}

	public PcaPreprocessingData getPcaPreprocessingData() {

		return pcaPreprocessingData;
	}
}
