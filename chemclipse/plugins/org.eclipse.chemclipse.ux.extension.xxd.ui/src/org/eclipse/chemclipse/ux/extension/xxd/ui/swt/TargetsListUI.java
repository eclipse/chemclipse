/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class TargetsListUI extends ExtendedTableViewer {

	private static final String[] TITLES = TargetsLabelProvider.TITLES;
	private static final int[] BOUNDS = TargetsLabelProvider.BOUNDS;
	private TargetsLabelProvider labelProvider = new TargetsLabelProvider();
	private TargetsComparator targetsTableComparator = new TargetsComparator();
	private TargetListFilter targetListFilter = new TargetListFilter();

	public TargetsListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		targetListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

		int column = 0;
		int sortOrder = TargetsComparator.DESCENDING;
		//
		targetsTableComparator.setColumn(column);
		targetsTableComparator.setDirection(sortOrder);
		refresh();
		targetsTableComparator.setDirection(1 - sortOrder);
		targetsTableComparator.setColumn(column);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(targetsTableComparator);
		setFilters(new ViewerFilter[]{targetListFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(TargetsLabelProvider.VERIFIED_MANUALLY)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.CAS)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			} else if(label.equals(TargetsLabelProvider.COMMENTS)) {
				tableViewerColumn.setEditingSupport(new TargetsEditingSupport(this, label));
			}
		}
	}
}
