/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SynonymsComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SynonymsLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SynonymsListFilter;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class SynonymsListUI extends ExtendedTableViewer {

	private static final String[] TITLES = SynonymsLabelProvider.TITLES;
	private static final int[] BOUNDS = SynonymsLabelProvider.BOUNDS;
	//
	private final SynonymsLabelProvider labelProvider = new SynonymsLabelProvider();
	private final SynonymsComparator comparator = new SynonymsComparator();
	private final SynonymsListFilter listFilter = new SynonymsListFilter();
	//

	public SynonymsListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
		setFilters(new ViewerFilter[]{listFilter});
	}
}