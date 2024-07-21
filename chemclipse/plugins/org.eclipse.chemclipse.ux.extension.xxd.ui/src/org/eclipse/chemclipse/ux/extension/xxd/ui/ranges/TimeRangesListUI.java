/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TimeRangesComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TimeRangesEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TimeRangesFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TimeRangesLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class TimeRangesListUI extends ExtendedTableViewer {

	private static final String[] TITLES = TimeRangesLabelProvider.TITLES;
	private static final int[] BOUNDS = TimeRangesLabelProvider.BOUNDS;
	//
	private TimeRangesLabelProvider labelProvider = new TimeRangesLabelProvider();
	private TimeRangesComparator tableComparator = new TimeRangesComparator();
	private TimeRangesFilter listFilter = new TimeRangesFilter();
	//
	private IUpdateListener updateListener;

	public TimeRangesListUI(Composite parent, int style) {

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

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateContent() {

		if(updateListener != null) {
			updateListener.update();
		}
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
			if(!label.equals(TimeRangesLabelProvider.IDENTIFIER)) {
				tableViewerColumn.setEditingSupport(new TimeRangesEditingSupport(this, label));
			}
		}
	}
}