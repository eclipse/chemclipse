/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider.IndexAssignerEditingSupport;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider.IndexAssignerLabelProvider;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider.IndexAssignerListFilter;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider.IndexAssignerTableComparator;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class IndexAssignerListUI extends ExtendedTableViewer {

	private static final String[] TITLES = IndexAssignerLabelProvider.TITLES;
	private static final int[] BOUNDS = IndexAssignerLabelProvider.BOUNDS;
	//
	private LabelProvider labelProvider = new IndexAssignerLabelProvider();
	private IContentProvider contentProvider = new ListContentProvider();
	private IndexAssignerListFilter listFilter = new IndexAssignerListFilter();
	private IUpdateListener updateListener;

	public IndexAssignerListUI(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	private void initialize() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(contentProvider);
		setComparator(new IndexAssignerTableComparator());
		setFilters(new ViewerFilter[]{listFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			String columnLabel = tableViewerColumn.getColumn().getText();
			if(columnLabel.equals(IndexAssignerLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new IndexAssignerEditingSupport(this));
			}
		}
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateContent() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}