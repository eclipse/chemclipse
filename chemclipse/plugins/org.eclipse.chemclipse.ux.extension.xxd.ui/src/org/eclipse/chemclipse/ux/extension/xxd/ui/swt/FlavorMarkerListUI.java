/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.FlavorMarkerComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.FlavorMarkerEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.FlavorMarkerLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.FlavorMarkerListFilter;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class FlavorMarkerListUI extends ExtendedTableViewer {

	private static final String[] TITLES = FlavorMarkerLabelProvider.TITLES;
	private static final int[] BOUNDS = FlavorMarkerLabelProvider.BOUNDS;
	//
	private final FlavorMarkerLabelProvider labelProvider = new FlavorMarkerLabelProvider();
	private final FlavorMarkerComparator comparator = new FlavorMarkerComparator();
	private final FlavorMarkerListFilter listFilter = new FlavorMarkerListFilter();
	//

	public FlavorMarkerListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
		setFilters(new ViewerFilter[]{listFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(FlavorMarkerLabelProvider.VERIFIED_MANUALLY)) {
				tableViewerColumn.setEditingSupport(new FlavorMarkerEditingSupport(this, label));
			}
		}
	}
}