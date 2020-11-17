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
import java.util.Map;

import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataTableComparator;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class HeaderDataListUI extends ExtendedTableViewer {

	private static final String[] TITLES = HeaderDataLabelProvider.TITLES;
	private static final int[] BOUNDS = HeaderDataLabelProvider.BOUNDS;
	//
	private HeaderDataLabelProvider labelProvider = new HeaderDataLabelProvider();
	private HeaderDataTableComparator tableComparator = new HeaderDataTableComparator();
	private HeaderDataListFilter listFilter = new HeaderDataListFilter();
	//
	private IMeasurementInfo measurementInfo;

	public HeaderDataListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
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
		tableComparator.setColumn(column);
		tableComparator.setDirection(sortOrder);
		refresh();
	}

	public IMeasurementInfo getMeasurementInfo() {

		return measurementInfo;
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setFilters(new ViewerFilter[]{listFilter});
		setCellColorProvider();
		setEditingSupport();
	}

	private void setCellColorProvider() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		TableViewerColumn tableViewerColumn = tableViewerColumns.get(HeaderDataLabelProvider.INDEX_COLUMN_NAME);
		if(tableViewerColumn != null) {
			tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {

				@SuppressWarnings("rawtypes")
				@Override
				public void update(ViewerCell cell) {

					if(cell != null && measurementInfo != null) {
						Object object = cell.getElement();
						if(object instanceof Map.Entry) {
							Map.Entry entry = (Map.Entry)object;
							String key = entry.getKey().toString();
							if(measurementInfo.isKeyProtected(key)) {
								cell.setForeground(Colors.DARK_GRAY);
							}
							cell.setText(key);
						}
						super.update(cell);
					}
				}
			});
		}
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
