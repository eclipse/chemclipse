/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.converter.ui.internal.provider.ColumExtractorComparator;
import org.eclipse.chemclipse.converter.ui.internal.provider.ColumExtractorEditingSupport;
import org.eclipse.chemclipse.converter.ui.internal.provider.ColumExtractorLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;

public class ColumExtractorTable extends ExtendedTableViewer {

	private static final String[] TITLES = ColumExtractorLabelProvider.TITLES;
	private static final int[] BOUNDS = ColumExtractorLabelProvider.BOUNDS;
	//
	private ColumExtractorLabelProvider labelProvider = new ColumExtractorLabelProvider();
	private ColumExtractorComparator tableComparator = new ColumExtractorComparator();

	public ColumExtractorTable(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(ColumExtractorLabelProvider.SEPRATION_COLUMN)) {
				tableViewerColumn.setEditingSupport(new ColumExtractorEditingSupport(this, label));
			}
		}
	}
}
