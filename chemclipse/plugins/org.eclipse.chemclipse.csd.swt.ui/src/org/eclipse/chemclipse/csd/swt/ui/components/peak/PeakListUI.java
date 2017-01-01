/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.swt.ui.components.peak;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.swt.ui.internal.provider.PeakListContentProvider;
import org.eclipse.chemclipse.csd.swt.ui.internal.provider.PeakListLabelProvider;
import org.eclipse.chemclipse.csd.swt.ui.internal.provider.PeakListTableComparator;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;

public class PeakListUI {

	private ExtendedTableViewer tableViewer;
	private PeakListTableComparator peakListTableComparator;
	private String[] titles = {"RT (minutes)", "RI", "Area", "Start RT", "Stop RT", "Width", "Scan# at Peak Maximum", "S/N", "Leading", "Tailing", "Model Description", "Suggested Components"};
	private int bounds[] = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};

	public PeakListUI(Composite parent, int style) {
		parent.setLayout(new FillLayout());
		/*
		 * E.g. Scan
		 */
		// SWT.VIRTUAL | SWT.FULL_SELECTION
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new PeakListContentProvider());
		tableViewer.setLabelProvider(new PeakListLabelProvider());
		/*
		 * Sorting the table.
		 */
		peakListTableComparator = new PeakListTableComparator();
		tableViewer.setComparator(peakListTableComparator);
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					tableViewer.copyToClipboard(titles);
				}
			}
		});
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void update(IPeaks peaks, boolean forceReload) {

		if(peaks != null) {
			tableViewer.setInput(peaks);
		}
	}

	public void clear() {

		tableViewer.setInput(null);
	}

	public ExtendedTableViewer getTableViewer() {

		return tableViewer;
	}

	public String[] getTitles() {

		return titles;
	}

	/**
	 * Deletes the selected peaks
	 * 
	 */
	public void deleteSelectedPeaks(IChromatogramSelectionCSD chromatogramSelection) {

		/*
		 * Delete the selected items.
		 */
		Table table = tableViewer.getTable();
		int[] indices = table.getSelectionIndices();
		List<IChromatogramPeakCSD> peaksToDelete = getChromatogramPeakList(table, indices);
		/*
		 * Delete peaks in table.
		 */
		table.remove(indices);
		/*
		 * Delete peak in chromatogram.
		 */
		IChromatogramCSD chromatogram = chromatogramSelection.getChromatogramCSD();
		chromatogram.removePeaks(peaksToDelete);
		/*
		 * Is the chromatogram updatable? IChromatogramSelection
		 * at itself isn't.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
			ChromatogramSelectionCSD chromSelection = (ChromatogramSelectionCSD)chromatogramSelection;
			List<IChromatogramPeakCSD> peaks = chromatogram.getPeaks();
			if(peaks.size() > 0) {
				chromSelection.setSelectedPeak(peaks.get(0));
			}
			chromSelection.update(true); // true: forces the editor to update
		}
	}

	private List<IChromatogramPeakCSD> getChromatogramPeakList(Table table, int[] indices) {

		List<IChromatogramPeakCSD> peakList = new ArrayList<IChromatogramPeakCSD>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IChromatogramPeakCSD) {
				IChromatogramPeakCSD chromatogramPeak = (IChromatogramPeakCSD)object;
				peakList.add(chromatogramPeak);
			}
		}
		return peakList;
	}
}