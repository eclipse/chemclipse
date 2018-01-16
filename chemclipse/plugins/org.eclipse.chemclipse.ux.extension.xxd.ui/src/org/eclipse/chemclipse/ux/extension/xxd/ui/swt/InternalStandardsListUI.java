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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.InternalStandardEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.InternalStandardsLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.InternalStandardsTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;

public class InternalStandardsListUI extends ExtendedTableViewer {

	public InternalStandardsListUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public InternalStandardsListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void update(List<IInternalStandard> internalStandards) {

		setInput(internalStandards);
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(InternalStandardsLabelProvider.TITLES, InternalStandardsLabelProvider.BOUNDS);
		//
		setLabelProvider(new InternalStandardsLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new InternalStandardsTableComparator());
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(InternalStandardsLabelProvider.NAME)) {
				tableViewerColumn.setEditingSupport(new InternalStandardEditingSupport(this, label));
			} else if(label.equals(InternalStandardsLabelProvider.CHEMICAL_CLASS)) {
				tableViewerColumn.setEditingSupport(new InternalStandardEditingSupport(this, label));
			}
		}
	}
}
