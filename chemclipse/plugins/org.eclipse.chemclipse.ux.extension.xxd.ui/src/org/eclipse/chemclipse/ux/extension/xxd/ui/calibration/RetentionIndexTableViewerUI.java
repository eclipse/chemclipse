/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.RetentionIndexTableComparator;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class RetentionIndexTableViewerUI extends ExtendedTableViewer {

	private static final String[] TITLES = RetentionIndexLabelProvider.TITLES;
	private static final int[] BOUNDS = RetentionIndexLabelProvider.BOUNDS;
	//
	private LabelProvider labelProvider = new RetentionIndexLabelProvider();
	private IContentProvider contentProvider = new ListContentProvider();
	private RetentionIndexListFilter retentionIndexListFilter = new RetentionIndexListFilter();
	private IUpdateListener updateListener;

	public RetentionIndexTableViewerUI(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		retentionIndexListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	private void initialize() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(contentProvider);
		setComparator(new RetentionIndexTableComparator());
		setFilters(new ViewerFilter[]{retentionIndexListFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			String columnLabel = tableViewerColumn.getColumn().getText();
			if(columnLabel.equals(RetentionIndexLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new RetentionIndexEditingSupport(this));
			}
		}
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateContent() {

		if(updateListener != null) {
			updateListener.update(null);
		}
	}
}