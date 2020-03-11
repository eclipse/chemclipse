/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class FilterSettingsUI extends Composite {

	private FilterSettings filterSettings = new FilterSettings();

	public FilterSettingsUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void update(FilterSettings filterSettings) {

		this.filterSettings = filterSettings;
		updateWidgets();
	}

	public FilterSettings getFilterSettings() {

		return filterSettings;
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createCheckBoxRemoveVariables(this);
	}

	private void updateWidgets() {

		if(filterSettings != null) {
		}
	}

	private Button createCheckBoxRemoveVariables(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Remove");
		button.setToolTipText("Remove Filter");
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				//
			}
		});
		//
		return button;
	}
}
