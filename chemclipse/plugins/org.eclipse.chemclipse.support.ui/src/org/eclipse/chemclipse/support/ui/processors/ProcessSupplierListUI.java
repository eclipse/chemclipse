/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import org.eclipse.chemclipse.support.ui.internal.provider.ProcessorLabelProvider;
import org.eclipse.chemclipse.support.ui.internal.provider.ProcessorTableComparator;
import org.eclipse.chemclipse.support.ui.internal.provider.ProcessorListFilter;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class ProcessSupplierListUI extends ExtendedTableViewer {

	private static final String[] TITLES = ProcessorLabelProvider.TITLES;
	private static final int[] BOUNDS = ProcessorLabelProvider.BOUNDS;
	//
	private IBaseLabelProvider labelProvider = new ProcessorLabelProvider();
	private ViewerComparator tableComparator = new ProcessorTableComparator();
	private ProcessorListFilter viewerFilter = new ProcessorListFilter();

	public ProcessSupplierListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		viewerFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void enableSorting(boolean enableSorting) {

		if(enableSorting) {
			setComparator(tableComparator);
		} else {
			setComparator(null);
		}
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setFilters(new ViewerFilter[]{viewerFilter});
	}
}