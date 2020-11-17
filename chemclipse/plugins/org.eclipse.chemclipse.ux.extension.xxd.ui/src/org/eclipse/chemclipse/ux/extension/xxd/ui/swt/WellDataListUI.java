/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.WellDataEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.WellDataLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.WellDataListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.WellDataTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class WellDataListUI extends ExtendedTableViewer {

	private static final String[] TITLES = WellDataLabelProvider.TITLES;
	private static final int[] BOUNDS = WellDataLabelProvider.BOUNDS;
	//
	private WellDataTableComparator tableComparator = new WellDataTableComparator();
	private WellDataListFilter listFilter = new WellDataListFilter();
	private WellDataLabelProvider labelProvider = new WellDataLabelProvider();
	//
	private IWell well = null;

	public WellDataListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void setInput(IWell well) {

		this.well = well;
		if(well != null) {
			super.setInput(well.getData());
		} else {
			clear();
		}
	}

	public void clear() {

		super.setInput(null);
	}

	public void sortTable() {

		int column = 0;
		int sortOrder = HeaderDataTableComparator.DESCENDING;
		//
		tableComparator.setColumn(column);
		tableComparator.setDirection(sortOrder);
		refresh();
	}

	public IWell getWell() {

		return well;
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
			if(label.equals(WellDataLabelProvider.VALUE)) {
				tableViewerColumn.setEditingSupport(new WellDataEditingSupport(this, label));
			}
		}
	}
}
