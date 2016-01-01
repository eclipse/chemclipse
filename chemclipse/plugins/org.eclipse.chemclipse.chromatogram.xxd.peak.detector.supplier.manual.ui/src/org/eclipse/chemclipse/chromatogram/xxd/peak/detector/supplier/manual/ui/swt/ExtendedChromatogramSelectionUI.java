/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class ExtendedChromatogramSelectionUI extends Composite {

	private Button buttonDetect;
	private ChromatogramSelectionUI selectedChromatogramUI;

	public ExtendedChromatogramSelectionUI(Composite parent, int style) {
		super(parent, style);
		initialize(parent);
	}

	public void update(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		selectedChromatogramUI.updateSelection(chromatogramSelection, forceReload);
	}

	/**
	 * Initializes the widget.
	 */
	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout;
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		// ------------------------------------------------------------------------------------------Buttons
		Composite buttonbar = new Composite(composite, SWT.FILL);
		buttonbar.setLayout(new FillLayout());
		/*
		 * Add the "d" detect button
		 */
		buttonDetect = new Button(buttonbar, SWT.NONE);
		buttonDetect.setText("Start Peak Detection Mode (or press \"d\")");
		buttonDetect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectedChromatogramUI.startDetectionMode();
			}
		});
		/*
		 * Chromatogram selection view
		 */
		selectedChromatogramUI = new ChromatogramSelectionUI(composite, SWT.FILL | SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		selectedChromatogramUI.setLayoutData(gridData);
	}
}
