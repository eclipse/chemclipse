/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.util.List;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetReferenceEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetReferenceFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetReferenceLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetReferenceTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;

public class TargetReferenceListUI extends ExtendedTableViewer {

	private static final String[] TITLES = TargetReferenceLabelProvider.TITLES;
	private static final int[] BOUNDS = TargetReferenceLabelProvider.BOUNDS;
	//
	private TargetReferenceLabelProvider labelProvider = new TargetReferenceLabelProvider();
	private TargetReferenceTableComparator tableComparator = new TargetReferenceTableComparator();
	private TargetReferenceFilter targetReferenceFilter = new TargetReferenceFilter();
	private TargetReferenceEditingSupport editingSupport = null;

	public TargetReferenceListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setTargetDisplaySettings(ITargetDisplaySettings targetDisplaySettings) {

		labelProvider.setTargetDisplaySettings(targetDisplaySettings);
		tableComparator.setTargetDisplaySettings(targetDisplaySettings);
		targetReferenceFilter.setTargetDisplaySettings(targetDisplaySettings);
		//
		if(editingSupport != null) {
			editingSupport.setTargetDisplaySettings(targetDisplaySettings);
		}
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		targetReferenceFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		// setComparator(tableComparator);
		setFilters(targetReferenceFilter);
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.equals(TargetReferenceLabelProvider.VISIBLE)) {
				editingSupport = new TargetReferenceEditingSupport(this, label);
				tableViewerColumn.setEditingSupport(editingSupport);
			}
		}
	}
}
