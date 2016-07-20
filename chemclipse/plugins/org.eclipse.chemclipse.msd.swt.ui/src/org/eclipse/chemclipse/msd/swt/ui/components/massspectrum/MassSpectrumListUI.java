/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.msd.swt.ui.internal.editingsupport.LibraryTextEditingSupport;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListFilter;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumListUI extends ExtendedTableViewer {

	private String[] titles = {"Name", "Retention Time", "Retention Index", "Base Peak", "Base Peak Abundance", "Number of Ions", "CAS", "MW", "Formula", "SMILES", "InChI", "Reference Identifier", "Comments"};
	private int bounds[] = {300, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
	//
	private MassSpectrumListFilter massSpectrumListFilter;

	public MassSpectrumListUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public MassSpectrumListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		massSpectrumListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new MassSpectrumListLabelProvider());
		setContentProvider(new MassSpectrumListContentProvider());
		setComparator(new MassSpectrumListTableComparator());
		massSpectrumListFilter = new MassSpectrumListFilter();
		setFilters(new ViewerFilter[]{massSpectrumListFilter});
		setEditingSupport();
	}

	private void setEditingSupport() {

		Set<String> excludeFromEditing = new HashSet<String>();
		excludeFromEditing.add("Base Peak");
		excludeFromEditing.add("Base Peak Abundance");
		excludeFromEditing.add("Number of Ions");
		//
		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			String columnLabel = tableViewerColumn.getColumn().getText();
			if(!excludeFromEditing.contains(columnLabel)) {
				tableViewerColumn.setEditingSupport(new LibraryTextEditingSupport(this, columnLabel));
			}
		}
	}
}
