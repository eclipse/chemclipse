/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - add support for name editing, improve classifier support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("rawtypes")
public class PeakScanListUI extends ExtendedTableViewer {

	private final String[] titles = PeakScanListLabelProvider.TITLES;
	private final int[] bounds = PeakScanListLabelProvider.BOUNDS;
	//
	private final PeakScanListLabelProvider labelProvider = new PeakScanListLabelProvider();
	private final PeakScanListTableComparator tableComparator = new PeakScanListTableComparator();
	private final PeakScanListFilter listFilter = new PeakScanListFilter();

	public PeakScanListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setInput(IChromatogramSelection<?, ?> chromatogramSelection, boolean showPeaks, boolean showPeaksInRange, boolean showScans, boolean showScansInRange) {

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			double chromatogramPeakArea = chromatogram.getPeakIntegratedArea();
			labelProvider.setChromatogramPeakArea(chromatogramPeakArea);
			tableComparator.setChromatogramPeakArea(chromatogramPeakArea);
			//
			List<Object> input = new ArrayList<>();
			//
			if(showPeaks) {
				input.addAll(ChromatogramDataSupport.getPeaks(chromatogramSelection, showPeaksInRange));
			}
			//
			if(showScans) {
				input.addAll(ChromatogramDataSupport.getIdentifiedScans(chromatogramSelection, showScansInRange));
			}
			//
			super.setInput(input);
		} else {
			clear();
		}
	}

	public void setInput(IPeaks<?> peaks) {

		labelProvider.setChromatogramPeakArea(0);
		tableComparator.setChromatogramPeakArea(0);
		super.setInput(peaks.getPeaks());
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void clear() {

		super.setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
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
			if(PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS.equals(label)) {
				tableViewerColumn.setEditingSupport(new PeakScanListEditingSupport(this, label));
			} else if(PeakScanListLabelProvider.CLASSIFIER.equals(label)) {
				tableViewerColumn.setEditingSupport(new PeakScanListEditingSupport(this, label));
			} else if(PeakScanListLabelProvider.NAME.equals(label)) {
				tableViewerColumn.setEditingSupport(new PeakScanListEditingSupport(this, label));
			}
		}
	}
}
