/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.isd.filter.ui.swt;

import org.eclipse.chemclipse.chromatogram.isd.filter.ui.internal.provider.WavenumberSignalsComparator;
import org.eclipse.chemclipse.chromatogram.isd.filter.ui.internal.provider.WavenumberSignalsFilter;
import org.eclipse.chemclipse.chromatogram.isd.filter.ui.internal.provider.WavenumberSignalsLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class WavenumberSignalsUI extends ExtendedTableViewer {

	private static final String[] TITLES = WavenumberSignalsLabelProvider.TITLES;
	private static final int[] BOUNDS = WavenumberSignalsLabelProvider.BOUNDS;
	//
	private WavenumberSignalsLabelProvider labelProvider = new WavenumberSignalsLabelProvider();
	private WavenumberSignalsComparator tableComparator = new WavenumberSignalsComparator();
	private WavenumberSignalsFilter listFilter = new WavenumberSignalsFilter();
	//
	private IUpdateListener updateListener;

	public WavenumberSignalsUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateContent() {

		if(updateListener != null) {
			updateListener.update();
		}
	}

	public void clear() {

		setInput(null);
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setFilters(new ViewerFilter[]{listFilter});
	}
}