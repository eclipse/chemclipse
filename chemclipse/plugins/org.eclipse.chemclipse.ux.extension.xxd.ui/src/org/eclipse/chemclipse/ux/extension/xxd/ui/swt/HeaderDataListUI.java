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

import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class HeaderDataListUI extends ExtendedTableViewer {

	private HeaderDataTableComparator headerDataTableComparator;
	private HeaderDataListFilter headerDataListFilter;
	private IMeasurementInfo measurementInfo;

	public HeaderDataListUI(Composite parent, int style) {
		super(parent, style);
		headerDataTableComparator = new HeaderDataTableComparator();
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		headerDataListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void setInput(IMeasurementInfo measurementInfo) {

		this.measurementInfo = measurementInfo;
		if(measurementInfo != null) {
			super.setInput(measurementInfo.getHeaderDataMap());
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
		headerDataTableComparator.setColumn(column);
		headerDataTableComparator.setDirection(sortOrder);
		refresh();
	}

	public IMeasurementInfo getMeasurementInfo() {

		return measurementInfo;
	}

	private void createColumns() {

		createColumns(HeaderDataLabelProvider.TITLES, HeaderDataLabelProvider.BOUNDS);
		setLabelProvider(new HeaderDataLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(headerDataTableComparator);
		headerDataListFilter = new HeaderDataListFilter();
		setFilters(new ViewerFilter[]{headerDataListFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(HeaderDataLabelProvider.VALUE)) {
				tableViewerColumn.setEditingSupport(new HeaderDataEditingSupport(this, label));
			}
		}
	}
}
