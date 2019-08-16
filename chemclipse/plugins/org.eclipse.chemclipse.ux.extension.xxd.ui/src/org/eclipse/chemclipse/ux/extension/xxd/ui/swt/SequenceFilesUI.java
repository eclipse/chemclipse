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

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceFilesFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceFilesLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceFilesTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class SequenceFilesUI extends ExtendedTableViewer {

	private SequenceFilesTableComparator sequenceListTableComparator;
	private SequenceFilesFilter sequenceListFilter;

	public SequenceFilesUI(Composite parent, int style) {
		super(parent, style);
		sequenceListTableComparator = new SequenceFilesTableComparator();
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		sequenceListFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

		int column = 0;
		int sortOrder = TargetsComparator.DESCENDING;
		//
		sequenceListTableComparator.setColumn(column);
		sequenceListTableComparator.setDirection(sortOrder);
		refresh();
		sequenceListTableComparator.setDirection(1 - sortOrder);
		sequenceListTableComparator.setColumn(column);
	}

	private void createColumns() {

		createColumns(SequenceFilesLabelProvider.TITLES, SequenceFilesLabelProvider.BOUNDS);
		setLabelProvider(new SequenceFilesLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(sequenceListTableComparator);
		sequenceListFilter = new SequenceFilesFilter();
		setFilters(new ViewerFilter[]{sequenceListFilter});
	}
}
