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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.SamplesComparator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.SamplesLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class SamplesListUI extends ExtendedTableViewer {

	private static final String[] TITLES = SamplesLabelProvider.TITLES;
	private static final int[] BOUNDS = SamplesLabelProvider.BOUNDS;
	private final ITableLabelProvider labelProvider = new SamplesLabelProvider();
	private final ViewerComparator comparator = new SamplesComparator();

	public SamplesListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
	}
}
