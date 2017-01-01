/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.comparator.RetentionIndexTableComparator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider.RetentionIndexLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.widgets.Composite;

public class RetentionIndexTableViewerUI extends ExtendedTableViewer {

	private String[] titles = {"Retention Time (Minutes)", "Retention Index", "Name"};
	private int[] bounds = {200, 150, 200};

	public RetentionIndexTableViewerUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public RetentionIndexTableViewerUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new RetentionIndexLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new RetentionIndexTableComparator());
	}
}
