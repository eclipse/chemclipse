/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.InstrumentComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.InstrumentEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.InstrumentFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.InstrumentLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class InstrumentListUI extends ExtendedTableViewer {

	private static final String[] TITLES = InstrumentLabelProvider.TITLES;
	private static final int[] BOUNDS = InstrumentLabelProvider.BOUNDS;
	//
	private ITableLabelProvider labelProvider = new InstrumentLabelProvider();
	private ViewerComparator tableComparator = new InstrumentComparator();
	private InstrumentFilter listFilter = new InstrumentFilter();

	public InstrumentListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void setSortEnabled(boolean sortEnabled) {

		setComparator((sortEnabled) ? tableComparator : null);
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setFilters(new ViewerFilter[]{listFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(!label.equals(InstrumentLabelProvider.IDENTIFIER)) {
				tableViewerColumn.setEditingSupport(new InstrumentEditingSupport(this, label));
			}
		}
	}
}
