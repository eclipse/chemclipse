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

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanListEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanListTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class ScanListUI extends ExtendedTableViewer {

	private ScanListTableComparator scanListTableComparator;
	private ScanListFilter scanListFilter;

	public ScanListUI(Composite parent, int style) {
		super(parent, style);
		scanListTableComparator = new ScanListTableComparator();
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		scanListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

		int column = 0;
		int sortOrder = TargetsTableComparator.DESCENDING;
		//
		scanListTableComparator.setColumn(column);
		scanListTableComparator.setDirection(sortOrder);
		refresh();
		scanListTableComparator.setDirection(1 - sortOrder);
		scanListTableComparator.setColumn(column);
	}

	private void createColumns() {

		createColumns(ScanListLabelProvider.TITLES, ScanListLabelProvider.BOUNDS);
		setLabelProvider(new ScanListLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(scanListTableComparator);
		scanListFilter = new ScanListFilter();
		setFilters(new ViewerFilter[]{scanListFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(ScanListLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new ScanListEditingSupport(this, label));
			}
		}
	}
}
