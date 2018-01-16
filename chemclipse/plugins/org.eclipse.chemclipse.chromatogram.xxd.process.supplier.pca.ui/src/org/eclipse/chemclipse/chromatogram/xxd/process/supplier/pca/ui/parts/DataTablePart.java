/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.PeakListNatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.collections.ListChangeListener;

public class DataTablePart {

	private PeakListNatTable peakListIntensityTable;

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new FillLayout());
		peakListIntensityTable = new PeakListNatTable(composite, null);
		SelectionManagerSamples.getInstance().getSelection().addListener((ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>>)e -> {
			if(!e.getList().isEmpty()) {
				peakListIntensityTable.update(e.getList().get(0));
			}
		});
		if(!SelectionManagerSamples.getInstance().getSelection().isEmpty()) {
			peakListIntensityTable.update(SelectionManagerSamples.getInstance().getSelection().get(0));
		}
	}
}
