/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DAD
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wavelengths;

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.NamedWavelengthsComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.NamedWavelengthsEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.NamedWavelengthsFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.NamedWavelengthsLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class NamedWavelengthsListUI extends ExtendedTableViewer {

	private static final String[] TITLES = NamedWavelengthsLabelProvider.TITLES;
	private static final int[] BOUNDS = NamedWavelengthsLabelProvider.BOUNDS;
	//
	private ITableLabelProvider labelProvider = new NamedWavelengthsLabelProvider();
	private ViewerComparator tableComparator = new NamedWavelengthsComparator();
	private NamedWavelengthsFilter listFilter = new NamedWavelengthsFilter();

	public NamedWavelengthsListUI(Composite parent, int style) {

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
			if(!label.equals(NamedWavelengthsLabelProvider.IDENTIFIER)) {
				tableViewerColumn.setEditingSupport(new NamedWavelengthsEditingSupport(this, label));
			}
		}
	}
}
