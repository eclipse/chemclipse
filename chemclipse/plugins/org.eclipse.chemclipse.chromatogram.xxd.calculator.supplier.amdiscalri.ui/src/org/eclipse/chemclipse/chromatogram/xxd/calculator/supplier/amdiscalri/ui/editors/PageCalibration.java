/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.ExtendedRetentionIndexTableViewerUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PageCalibration {

	private Composite control;
	private ExtendedRetentionIndexTableViewerUI extendedTableViewer;

	public PageCalibration(Composite container) {
		createControl(container);
	}

	public Composite getControl() {

		return control;
	}

	public void showData(List<IRetentionIndexEntry> retentionIndexEntries) {

		extendedTableViewer.setInput(retentionIndexEntries);
	}

	public List<IRetentionIndexEntry> getRetentionIndexEntries() {

		return extendedTableViewer.getRetentionIndexEntries();
	}

	private void createControl(Composite container) {

		control = new Composite(container, SWT.NONE);
		control.setLayout(new FillLayout());
		control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		//
		extendedTableViewer = new ExtendedRetentionIndexTableViewerUI(control, SWT.NONE);
	}
}
