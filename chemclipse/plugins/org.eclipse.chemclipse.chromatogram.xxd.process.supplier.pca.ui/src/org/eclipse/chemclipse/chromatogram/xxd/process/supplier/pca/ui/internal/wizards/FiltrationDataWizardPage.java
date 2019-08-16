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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.FiltersTable;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class FiltrationDataWizardPage extends WizardPage {

	private PcaFiltrationData pcaFiltrationData;

	protected FiltrationDataWizardPage(String pageName) {
		super(pageName);
		setTitle("Add Filters");
		setDescription("Filters can be also added later in the process in Data Filtration page");
		pcaFiltrationData = new PcaFiltrationData();
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		FiltersTable filtersTable = new FiltersTable(composite, new GridData(GridData.FILL_BOTH), pcaFiltrationData);
		Composite compositeButtons = new Composite(composite, SWT.None);
		compositeButtons.setLayout(new FillLayout());
		Button button = new Button(compositeButtons, SWT.PUSH);
		button.setText("Create New Filter");
		button.addListener(SWT.Selection, e -> filtersTable.createNewFilter());
		button = new Button(compositeButtons, SWT.PUSH);
		button.setText("Remove Selected Filters");
		button.addListener(SWT.Selection, e -> filtersTable.removeSelectedFilters());
		setControl(composite);
	}

	public PcaFiltrationData getPcaFiltrationData() {

		return pcaFiltrationData;
	}
}
