/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TargetsListUI;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ScanTargetsPart extends AbstractScanUpdateSupport implements IScanUpdateSupport {

	private Label labelScan;
	private TargetsListUI targetsListUI;

	@Inject
	public ScanTargetsPart(Composite parent, MPart part) {
		super(part);
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateScan(getScan());
	}

	@Override
	public void updateScan(IScan scan) {

		labelScan.setText(ScanSupport.getScanLabel(scan));
		if(scan instanceof IScanMSD) {
			targetsListUI.setInput(((IScanMSD)scan).getTargets());
		} else if(scan instanceof IScanCSD) {
			targetsListUI.setInput(((IScanCSD)scan).getTargets());
		} else if(scan instanceof IScanWSD) {
			// targetsListUI.setInput(((IScanWSD)scan).getTargets()); // TODO
		} else {
			targetsListUI.clear();
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarInfo(parent);
		createTable(parent);
	}

	private void createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelScan = new Label(composite, SWT.NONE);
		labelScan.setText("");
		labelScan.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createTable(Composite parent) {

		targetsListUI = new TargetsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		targetsListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
