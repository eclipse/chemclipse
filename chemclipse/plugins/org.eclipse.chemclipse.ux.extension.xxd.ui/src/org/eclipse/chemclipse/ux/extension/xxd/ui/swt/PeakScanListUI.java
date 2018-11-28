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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListEditingSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakScanListTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("rawtypes")
public class PeakScanListUI extends ExtendedTableViewer {

	private String[] titles = PeakScanListLabelProvider.TITLES;
	private int[] bounds = PeakScanListLabelProvider.BOUNDS;
	private PeakScanListLabelProvider labelProvider = new PeakScanListLabelProvider();
	private PeakScanListTableComparator tableComparator = new PeakScanListTableComparator();
	private PeakScanListFilter listFilter = new PeakScanListFilter();
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();

	public PeakScanListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setInput(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			double chromatogramPeakArea = chromatogram.getPeakIntegratedArea();
			labelProvider.setChromatogramPeakArea(chromatogramPeakArea);
			tableComparator.setChromatogramPeakArea(chromatogramPeakArea);
			//
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			List<Object> input = new ArrayList<Object>();
			//
			if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAKS_IN_LIST)) {
				boolean showPeaksInSelectedRange = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAKS_IN_SELECTED_RANGE);
				input.addAll(chromatogramDataSupport.getPeaks(chromatogramSelection, showPeaksInSelectedRange));
			}
			//
			if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SCANS_IN_LIST)) {
				boolean showScansInSelectedRange = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SCANS_IN_SELECTED_RANGE);
				input.addAll(chromatogramDataSupport.getIdentifiedScans(chromatogramSelection, showScansInSelectedRange));
			}
			//
			super.setInput(input);
		} else {
			clear();
		}
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
			if(label.equals(PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS)) {
				tableViewerColumn.setEditingSupport(new PeakScanListEditingSupport(this, label));
			} else if(label.equals(PeakScanListLabelProvider.CLASSIFIER)) {
				tableViewerColumn.setEditingSupport(new PeakScanListEditingSupport(this, label));
			}
		}
	}
}
