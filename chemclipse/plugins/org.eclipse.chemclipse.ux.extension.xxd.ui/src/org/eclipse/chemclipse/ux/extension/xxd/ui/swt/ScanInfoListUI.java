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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanInfoLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanInfoTableComparator;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class ScanInfoListUI extends ExtendedTableViewer {

	private static final String[] TITLES = ScanInfoLabelProvider.TITLES;
	private static final int[] BOUNDS = ScanInfoLabelProvider.BOUNDS;
	//
	private ITableLabelProvider labelProvider = new ScanInfoLabelProvider();
	private ViewerComparator comparator = new ScanInfoTableComparator();

	public ScanInfoListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void update(IChromatogramSelectionMSD chromatogramSelection) {

		if(chromatogramSelection != null) {
			setInput(chromatogramSelection);
		} else {
			setInput(null);
		}
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		//
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
	}
}
