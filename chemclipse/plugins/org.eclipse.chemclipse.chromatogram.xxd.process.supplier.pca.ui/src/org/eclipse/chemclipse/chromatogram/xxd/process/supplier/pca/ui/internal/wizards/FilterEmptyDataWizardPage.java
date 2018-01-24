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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.EmptyDataFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FilterEmptyDataWizardPage extends WizardPage implements IFilterWizardPage {

	protected FilterEmptyDataWizardPage(EmptyDataFilter emptyDataFilter) {
		super("Empty data filter");
		setTitle("Empty Data Filter");
		setDescription("Select rows, which contain just not-empty data");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		Label label = new Label(composite, SWT.None);
		label.setText("This filter is without parameters.");
		setControl(composite);
	}

	@Override
	public void update() {

	}
}
