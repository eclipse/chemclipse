/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 * Janos Binder - cleanup
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.internal.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.internal.provider.ProcessingInfoContentProvider;
import org.eclipse.chemclipse.processing.ui.internal.provider.ProcessingInfoLabelProvider;
import org.eclipse.chemclipse.processing.ui.internal.provider.ProcessingInfoTableComparator;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ProcessingInfoUI {

	private TableViewer tableViewer;
	private ProcessingInfoTableComparator processingInfoTableComparator;
	private Clipboard clipboard;
	private String[] titles = {"Type", "Description", "Message", "Date"};
	private int bounds[] = {100, 100, 100, 100};
	//
	private static final String DELIMITER = "\t";

	public ProcessingInfoUI(Composite parent, int style) {
		/*
		 * Clipboard / OS utils
		 */
		parent.setLayout(new FillLayout());
		clipboard = new Clipboard(Display.getDefault());
		Map<Long, String> substances = new HashMap<Long, String>();
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		createColumns(tableViewer);
		setTableProvider(substances);
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void update(IProcessingInfo processingInfo) {

		tableViewer.setInput(processingInfo);
	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}

	/**
	 * Copies the actual selection to the clipboard.
	 */
	public void copyToClipboard() {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelSorter PeakListView
		 */
		StringBuilder builder = new StringBuilder();
		int size = titles.length;
		/*
		 * Header
		 */
		for(String title : titles) {
			builder.append(title);
			builder.append(DELIMITER);
		}
		builder.append(OperatingSystemUtils.getLineDelimiter());
		/*
		 * Copy the selected items.
		 */
		TableItem selection;
		Table table = tableViewer.getTable();
		for(int index : table.getSelectionIndices()) {
			/*
			 * Get the nth selected item.
			 */
			selection = table.getItem(index);
			/*
			 * Dump all elements of the item, e.g. RT, Abundance, ... .
			 */
			for(int columnIndex = 0; columnIndex < size; columnIndex++) {
				builder.append(selection.getText(columnIndex));
				builder.append(DELIMITER);
			}
			builder.append(OperatingSystemUtils.getLineDelimiter());
		}
		/*
		 * If the builder is empty, give a note that items needs to be selected.
		 */
		if(builder.length() == 0) {
			builder.append("Please select one or more entries in the list.");
			builder.append(OperatingSystemUtils.getLineDelimiter());
		}
		/*
		 * Transfer the selected text (items) to the clipboard.
		 */
		TextTransfer textTransfer = TextTransfer.getInstance();
		Object[] data = new Object[]{builder.toString()};
		Transfer[] dataTypes = new Transfer[]{textTransfer};
		clipboard.setContents(data, dataTypes);
	}

	// -----------------------------------------private methods
	private void setTableProvider(Map<Long, String> substances) {

		/*
		 * Set the provider.
		 */
		tableViewer.setContentProvider(new ProcessingInfoContentProvider());
		tableViewer.setLabelProvider(new ProcessingInfoLabelProvider());
		processingInfoTableComparator = new ProcessingInfoTableComparator();
		tableViewer.setComparator(processingInfoTableComparator);
	}

	/**
	 * Creates the columns for the peak viewer table.
	 * 
	 * @param tableViewer
	 */
	private void createColumns(final TableViewer tableViewer) {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelSorter PeakListView
		 */
		/*
		 * Set the titles and bounds.
		 */
		for(int i = 0; i < titles.length; i++) {
			/*
			 * Column sort.
			 */
			final int index = i;
			final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			final TableColumn tableColumn = tableViewerColumn.getColumn();
			tableColumn.setText(titles[i]);
			tableColumn.setWidth(bounds[i]);
			tableColumn.setResizable(true);
			tableColumn.setMoveable(true);
			tableColumn.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					processingInfoTableComparator.setColumn(index);
					int direction = tableViewer.getTable().getSortDirection();
					if(tableViewer.getTable().getSortColumn() == tableColumn) {
						/*
						 * Toggle the direction
						 */
						direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
					} else {
						direction = SWT.UP;
					}
					tableViewer.getTable().setSortDirection(direction);
					tableViewer.getTable().setSortColumn(tableColumn);
					tableViewer.refresh();
				}
			});
		}
		/*
		 * Set header and lines visible.
		 */
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
}
