/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MassSpectrumSearchListUI extends Composite {

	private Label labelInfo;
	private MassSpectrumListUI massSpectrumListUI;

	public MassSpectrumSearchListUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	@Override
	public boolean setFocus() {

		return massSpectrumListUI.getTable().setFocus();
	}

	public void update(IMassSpectra massSpectra) {

		if(massSpectra != null) {
			massSpectrumListUI.setInput(massSpectra);
			updateLabel();
		} else {
			clear();
		}
	}

	public void clear() {

		massSpectrumListUI.setInput(null);
		updateLabel();
	}

	public TableViewer getTableViewer() {

		return massSpectrumListUI;
	}

	private void initialize() {

		this.setLayout(new GridLayout(1, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createMassSpectrumTable(this);
		createLabelInfo(this);
	}

	private void createMassSpectrumTable(Composite parent) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		//
		massSpectrumListUI = new MassSpectrumListUI(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		massSpectrumListUI.getTable().setLayoutData(gridData);
	}

	private void createLabelInfo(Composite parent) {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		//
		labelInfo = new Label(parent, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(gridData);
	}

	private void updateLabel() {

		labelInfo.setText("Stored Mass Spectra: " + getItemSize());
	}

	private int getItemSize() {

		return massSpectrumListUI.getTable().getItemCount();
	}
}