/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.SequenceListTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class SequenceListUI extends ExtendedTableViewer {

	private static final String[] LABELS = SequenceListLabelProvider.TITLES;
	private static final int[] BOUNDS = SequenceListLabelProvider.BOUNDS;
	//
	private SequenceListLabelProvider labelProvider = new SequenceListLabelProvider();
	private ListContentProvider contentProvider = new ListContentProvider();
	private SequenceListTableComparator comparator = new SequenceListTableComparator();
	private SequenceListFilter listFilter = new SequenceListFilter();

	public SequenceListUI(Composite parent, int style) {

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

	public void setComparator() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SEQUENCE_EXPLORER_SORT_DATA)) {
			setComparator(comparator);
		} else {
			setComparator(null);
		}
	}

	private void createColumns() {

		createColumns(LABELS, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(contentProvider);
		setComparator();
		setFilters(new ViewerFilter[]{listFilter});
	}
}
