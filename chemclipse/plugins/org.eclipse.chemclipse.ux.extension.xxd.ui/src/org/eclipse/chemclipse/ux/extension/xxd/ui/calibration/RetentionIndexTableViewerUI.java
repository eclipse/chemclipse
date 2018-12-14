/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.calibration;

import java.util.List;

import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class RetentionIndexTableViewerUI extends ExtendedTableViewer {

	public static final String NAME = "Name";
	private String[] titles = {"Retention Time (Minutes)", "Retention Index", NAME};
	private int[] bounds = {200, 150, 200};
	private RetentionIndexListFilter retentionIndexListFilter;

	public RetentionIndexTableViewerUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public RetentionIndexTableViewerUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		retentionIndexListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new RetentionIndexLabelProvider());
		setContentProvider(new RetentionIndexContentProvider());
		setComparator(new RetentionIndexTableComparator());
		retentionIndexListFilter = new RetentionIndexListFilter();
		setFilters(new ViewerFilter[]{retentionIndexListFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			String columnLabel = tableViewerColumn.getColumn().getText();
			if(columnLabel.equals(NAME)) {
				tableViewerColumn.setEditingSupport(new RetentionIndexEditingSupport(this));
			}
		}
	}
}
