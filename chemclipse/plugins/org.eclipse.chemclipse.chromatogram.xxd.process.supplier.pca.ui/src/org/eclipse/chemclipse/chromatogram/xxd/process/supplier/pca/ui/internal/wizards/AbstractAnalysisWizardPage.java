/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractAnalysisWizardPage extends WizardPage {

	protected Algorithm[] algorithms = Algorithm.getAlgorithms();
	protected IAnalysisSettings analysisSettings = new AnalysisSettings();

	public AbstractAnalysisWizardPage(String pageName) {

		super(pageName);
	}

	public IAnalysisSettings getAnalysisSettings() {

		return analysisSettings;
	}

	protected Text createTextTitle(Composite parent, int horizontalSpan) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The title of the current analysis.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = horizontalSpan;
		text.setLayoutData(gridData);
		//
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				analysisSettings.setTitle(text.getText().trim());
			}
		});
		//
		return text;
	}
}