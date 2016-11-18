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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExtendedChromatogramSelectionUI extends Composite {

	private Button buttonDetect;
	private ChromatogramSelectionUI chromatogramSelectionUI;

	public ExtendedChromatogramSelectionUI(Composite parent, int style) {
		super(parent, style);
		initialize(parent);
	}

	public void update(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		chromatogramSelectionUI.updateSelection(chromatogramSelection, forceReload);
	}

	public ChromatogramSelectionUI getChromatogramSelectionUI() {

		return chromatogramSelectionUI;
	}

	/**
	 * Initializes the widget.
	 */
	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		/*
		 * Add the "d" detect button
		 */
		buttonDetect = new Button(composite, SWT.NONE);
		buttonDetect.setText("Start Peak Detection Mode (or press \"d\")");
		buttonDetect.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonDetect.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonDetect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramSelectionUI.startDetectionMode(ChromatogramSelectionUI.DETECTION_TYPE_BASELINE);
			}
		});
		/*
		 * Chromatogram selection view
		 */
		chromatogramSelectionUI = new ChromatogramSelectionUI(composite, SWT.BORDER);
		chromatogramSelectionUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
