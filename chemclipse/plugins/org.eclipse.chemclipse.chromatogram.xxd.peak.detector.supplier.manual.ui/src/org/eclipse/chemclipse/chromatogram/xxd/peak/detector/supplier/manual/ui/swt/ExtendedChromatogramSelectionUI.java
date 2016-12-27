/*******************************************************************************
 * Copyright (c) 2012, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
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
		 * Buttons
		 */
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(4, true));
		GridData gridDataComposite = new GridData();
		gridDataComposite.horizontalAlignment = SWT.RIGHT;
		compositeButtons.setLayoutData(gridDataComposite);
		//
		addButtonDetectionType(compositeButtons, "Reset", IApplicationImage.IMAGE_RESET, ChromatogramSelectionUI.DETECTION_TYPE_NONE);
		addButtonDetectionType(compositeButtons, "Detection Modus (Baseline) [Key:" + ChromatogramSelectionUI.KEY_BASELINE + "]", IApplicationImage.IMAGE_DETECTION_TYPE_BASELINE, ChromatogramSelectionUI.DETECTION_TYPE_BASELINE);
		addButtonDetectionType(compositeButtons, "Detection Modus (BB) [Key:" + ChromatogramSelectionUI.KEY_BB + "]", IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_BB, ChromatogramSelectionUI.DETECTION_TYPE_SCAN_BB);
		addButtonDetectionType(compositeButtons, "Detection Modus (VV) [Key:" + ChromatogramSelectionUI.KEY_VV + "]", IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_VV, ChromatogramSelectionUI.DETECTION_TYPE_SCAN_VV);
		/*
		 * Chromatogram selection view
		 */
		chromatogramSelectionUI = new ChromatogramSelectionUI(composite, SWT.BORDER);
		chromatogramSelectionUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private Button addButtonDetectionType(Composite parent, String tooltip, String image, String detectionType) {

		Button button = new Button(parent, SWT.NONE);
		button.setText("");
		button.setToolTipText(tooltip);
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramSelectionUI.setDetectionType(detectionType);
			}
		});
		return button;
	}
}
