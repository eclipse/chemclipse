/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class FiltersWizardPage extends WizardPage {

	private int filterType;

	protected FiltersWizardPage(String pageName) {

		super(pageName);
		setTitle("Filter selection");
		setDescription("Selec filter type");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;
		//
		Button button = new Button(composite, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("ANOVA Filter");
		button.setSelection(true);
		filterType = FiltersWizard.FITER_TYPE_ANOVA;
		button.addListener(SWT.Selection, (e) -> filterType = FiltersWizard.FITER_TYPE_ANOVA);
		button = new Button(composite, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("CV Filter");
		button.addListener(SWT.Selection, (e) -> filterType = FiltersWizard.FITER_TYPE_CV);
		button = new Button(composite, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("Abundance Filter");
		button.addListener(SWT.Selection, (e) -> filterType = FiltersWizard.FITER_TYPE_ABUNDANCE);
		button = new Button(composite, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("Retention Time Intervals Filter");
		button.addListener(SWT.Selection, (e) -> filterType = FiltersWizard.FITER_TYPE_RETENTION_TIME);
		button = new Button(composite, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("Empty Data Filter");
		button.addListener(SWT.Selection, (e) -> filterType = FiltersWizard.FITER_EMPTY_DATA);
		setControl(composite);
	}

	public int getFilterType() {

		return filterType;
	}

	@Override
	public boolean isPageComplete() {

		return true;
	}
}
