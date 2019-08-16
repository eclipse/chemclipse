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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantReferencesComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantReferencesEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantReferencesFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantReferencesLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class QuantReferencesListUI extends ExtendedTableViewer {

	private QuantReferencesComparator comparator = new QuantReferencesComparator();
	private QuantReferencesFilter filter = new QuantReferencesFilter();

	public QuantReferencesListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		filter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(QuantReferencesLabelProvider.TITLES, QuantReferencesLabelProvider.BOUNDS);
		setLabelProvider(new QuantReferencesLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
		setFilters(new ViewerFilter[]{filter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(QuantReferencesLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new QuantReferencesEditingSupport(this, label));
			}
		}
	}
}
