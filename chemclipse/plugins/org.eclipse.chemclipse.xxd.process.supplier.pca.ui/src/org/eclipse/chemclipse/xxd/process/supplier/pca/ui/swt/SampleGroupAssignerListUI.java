/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.GroupNamingEditingSupport;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.SampleGroupAssignerLabelProvider;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.SamplesComparator;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.SamplesLabelProvider;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.SamplesListFilter;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class SampleGroupAssignerListUI extends ExtendedTableViewer {

	private static final String[] TITLES = SampleGroupAssignerLabelProvider.TITLES;
	private static final int[] BOUNDS = SampleGroupAssignerLabelProvider.BOUNDS;
	//
	private final ITableLabelProvider labelProvider = new SampleGroupAssignerLabelProvider();
	private final ViewerComparator comparator = new SamplesComparator();
	private final SamplesListFilter listFilter = new SamplesListFilter();

	public SampleGroupAssignerListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void updateInput(List<ISample> sampleList) {

		super.setInput(sampleList);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
		setFilters(listFilter);
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(isEditable(label)) {
				tableViewerColumn.setEditingSupport(new GroupNamingEditingSupport(this, label));
			}
		}
	}

	private boolean isEditable(String label) {

		return !SamplesLabelProvider.SAMPLE_NAME.equals(label);
	}
}