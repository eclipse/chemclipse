/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components;

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.internal.provider.ColumMappingComparator;
import org.eclipse.chemclipse.swt.ui.internal.provider.ColumMappingEditingSupport;
import org.eclipse.chemclipse.swt.ui.internal.provider.ColumMappingFilter;
import org.eclipse.chemclipse.swt.ui.internal.provider.ColumMappingLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class ColumnMappingListUI extends ExtendedTableViewer {

	private static final String[] TITLES = ColumMappingLabelProvider.TITLES;
	private static final int[] BOUNDS = ColumMappingLabelProvider.BOUNDS;
	//
	private ColumMappingLabelProvider labelProvider = new ColumMappingLabelProvider();
	private ColumMappingComparator tableComparator = new ColumMappingComparator();
	private ColumMappingFilter listFilter = new ColumMappingFilter();

	public ColumnMappingListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
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
			if(label.equals(ColumMappingLabelProvider.SEPRATION_COLUMN)) {
				tableViewerColumn.setEditingSupport(new ColumMappingEditingSupport(this, label));
			}
		}
	}
}