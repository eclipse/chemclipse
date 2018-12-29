/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantCompoundLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantCompoundListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantCompoundTableComparator;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class QuantCompoundListUI extends ExtendedTableViewer {

	private String[] titles = QuantCompoundLabelProvider.TITLES;
	private int[] bounds = QuantCompoundLabelProvider.BOUNDS;
	private IBaseLabelProvider labelProvider = new QuantCompoundLabelProvider();
	private ViewerComparator tableComparator = new QuantCompoundTableComparator();
	private QuantCompoundListFilter viewerFilter = new QuantCompoundListFilter();

	public QuantCompoundListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		viewerFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setFilters(new ViewerFilter[]{viewerFilter});
	}
}
