/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakQuantitationEntriesContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakQuantitationEntriesLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakQuantitationEntriesTableComparator;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuCategories;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class PeakQuantitationEntriesUI {

	private ExtendedTableViewer tableViewer;
	private PeakQuantitationEntriesTableComparator peakQuantitationEntriesTableComparator;
	private String[] titles = {"Name", "Chemical Class", "Concentration", "Concentration Unit", "Area", "Ion (0 = TIC)", "Calibration Method", "Cross Zero", "Description"};
	private int bounds[] = {100, 100, 100, 100, 100, 100, 100, 100, 100};

	public PeakQuantitationEntriesUI(Composite parent, int style) {
		parent.setLayout(new FillLayout());
		/*
		 * E.g. Scan
		 */
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new PeakQuantitationEntriesContentProvider());
		tableViewer.setLabelProvider(new PeakQuantitationEntriesLabelProvider());
		ITableSettings tableSettings = tableViewer.getTableSettings();
		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					deleteSelectedQuantitationResults();
				}
			}
		});
		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Selected Quantitation Results";
			}

			@Override
			public String getCategory() {

				return ITableMenuCategories.STANDARD_OPERATION;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteSelectedQuantitationResults();
			}
		});
		tableViewer.applySettings(tableSettings);
		/*
		 * Sorting the table.
		 */
		peakQuantitationEntriesTableComparator = new PeakQuantitationEntriesTableComparator();
		tableViewer.setComparator(peakQuantitationEntriesTableComparator);
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void update(IPeakMSD peak, boolean forceReload) {

		if(peak != null) {
			tableViewer.setInput(peak);
		}
	}

	public void clear() {

		tableViewer.setInput(null);
	}

	public ExtendedTableViewer getTableViewer() {

		return tableViewer;
	}

	/*
	 * Delete the selected targets after confirming the message box.
	 */
	private void deleteSelectedQuantitationResults() {

		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setText("Delete Selected Quantitation Results");
		messageBox.setMessage("Do you really want to delete the selected quantitation results?");
		int decision = messageBox.open();
		if(SWT.YES == decision) {
			/*
			 * Delete the selected items.
			 */
			Table table = tableViewer.getTable();
			int[] indices = table.getSelectionIndices();
			/*
			 * Delete the selected targets. Make a distinction between: -
			 * IChromatogram - IChromatogramPeak Don't delete entries in cause
			 * they are temporary: - IMassSpectrumIdentificationResult
			 */
			Object input = tableViewer.getInput();
			if(input instanceof IPeakMSD) {
				List<IQuantitationEntry> quantitationEntriesToRemove = getQuantitationEntryList(table, indices);
				IPeakMSD peak = (IPeakMSD)input;
				peak.removeQuantitationEntries(quantitationEntriesToRemove);
			}
			/*
			 * Delete targets in table.
			 */
			table.remove(indices);
		}
	}

	private List<IQuantitationEntry> getQuantitationEntryList(Table table, int[] indices) {

		List<IQuantitationEntry> quantitationEntryList = new ArrayList<IQuantitationEntry>();
		for(int index : indices) {
			/*
			 * Get the selected item.
			 */
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IQuantitationEntry) {
				IQuantitationEntry quantitationEntry = (IQuantitationEntry)object;
				quantitationEntryList.add(quantitationEntry);
			}
		}
		return quantitationEntryList;
	}
}