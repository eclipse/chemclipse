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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetTemplateComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetTemplateEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetTemplateFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetTemplateLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class TargetTemplateListUI extends ExtendedTableViewer {

	private static final String[] TITLES = TargetTemplateLabelProvider.TITLES;
	private static final int[] BOUNDS = TargetTemplateLabelProvider.BOUNDS;
	private TargetTemplateLabelProvider labelProvider = new TargetTemplateLabelProvider();
	private TargetTemplateComparator tableComparator = new TargetTemplateComparator();
	private TargetTemplateFilter listFilter = new TargetTemplateFilter();

	public TargetTemplateListUI(Composite parent, int style) {
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
		setComparator(tableComparator);
		setFilters(new ViewerFilter[]{listFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(!label.equals(TargetTemplateLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new TargetTemplateEditingSupport(this, label));
			}
		}
	}
}
