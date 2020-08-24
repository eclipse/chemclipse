/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.editors;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.StandardsReader;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.ExtendedRetentionIndexListUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PageCalibration {

	private Composite control;
	private ExtendedRetentionIndexListUI extendedTableViewer;

	public PageCalibration(Composite container) {

		createControl(container);
	}

	public Composite getControl() {

		return control;
	}

	public void showData(File file, ISeparationColumnIndices separationColumnIndices) {

		extendedTableViewer.setFile(file);
		extendedTableViewer.setInput(separationColumnIndices);
	}

	private void createControl(Composite container) {

		control = new Composite(container, SWT.NONE);
		control.setLayout(new FillLayout());
		control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		//
		extendedTableViewer = new ExtendedRetentionIndexListUI(control, SWT.NONE);
		extendedTableViewer.setInput(new StandardsReader().getStandardsList());
	}
}
