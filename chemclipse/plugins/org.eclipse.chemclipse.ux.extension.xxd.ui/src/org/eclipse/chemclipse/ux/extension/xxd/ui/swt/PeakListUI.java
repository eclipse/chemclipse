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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakListEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakListTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class PeakListUI extends ExtendedTableViewer {

	private PeakListTableComparator peakListTableComparator;
	private PeakListFilter peakListFilter;

	public PeakListUI(Composite parent, int style) {
		super(parent, style);
		peakListTableComparator = new PeakListTableComparator();
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		peakListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

		int column = 0;
		int sortOrder = TargetsTableComparator.DESCENDING;
		//
		peakListTableComparator.setColumn(column);
		peakListTableComparator.setDirection(sortOrder);
		refresh();
		peakListTableComparator.setDirection(1 - sortOrder);
		peakListTableComparator.setColumn(column);
	}

	private void createColumns() {

		createColumns(PeakListLabelProvider.TITLES, PeakListLabelProvider.BOUNDS);
		setLabelProvider(new PeakListLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(peakListTableComparator);
		peakListFilter = new PeakListFilter();
		setFilters(new ViewerFilter[]{peakListFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(PeakListLabelProvider.ACTIVE_FOR_ANALYSIS)) {
				tableViewerColumn.setEditingSupport(new PeakListEditingSupport(this, label));
			}
		}
	}
}
