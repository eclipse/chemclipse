/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class EnhancedTableViewer extends Composite {

	private static final Logger logger = Logger.getLogger(EnhancedTableViewer.class);
	//
	private static final String POPUP_MENU_ID = "#PopUpMenu"; // $NON-NLS-1$
	private static final String POPUP_MENU_POSTFIX = "PopUpMenu"; // $NON-NLS-1$
	//
	private TableViewer tableViewer;
	private List<TableViewerColumn> tableViewerColumns;
	private EnhancedViewerSorter sorter;
	//
	private Clipboard clipboard;

	public EnhancedTableViewer(Composite parent, int style) {

		super(parent, style);
		setLayout(new FillLayout());
		createControl();
	}

	// TODO in Interface auslagern! für AbstractReportColumnVerificationPage -> CalibrationProfile
	public void createColumns(Object object) {

	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}

	public void setContentProvider(IContentProvider provider) {

		tableViewer.setContentProvider(provider);
	}

	public void setLabelProvider(IBaseLabelProvider labelProvider) {

		tableViewer.setLabelProvider(labelProvider);
	}

	public void setSorter(EnhancedViewerSorter sorter) {

		this.sorter = sorter;
		tableViewer.setComparator(sorter);
	}

	public void setInput(Object input) {

		tableViewer.setInput(input);
	}

	public void setColumns(String[][] titlesAndBounds) {

		String[] titles = new String[titlesAndBounds.length];
		int[] bounds = new int[titlesAndBounds.length];
		for(int i = 0; i < titlesAndBounds.length; i++) {
			try {
				String[] titleAndBound = titlesAndBounds[i];
				titles[i] = titleAndBound[0];
				bounds[i] = Integer.parseInt(titleAndBound[1]);
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		createColumns(titles, bounds);
	}

	public void setColumns(String[] titles, int[] bounds) {

		createColumns(titles, bounds);
	}

	public List<TableViewerColumn> getTableViewerColumns() {

		return tableViewerColumns;
	}

	private void createControl() {

		tableViewerColumns = new ArrayList<TableViewerColumn>();
		//
		clipboard = new Clipboard(Display.getDefault());
		//
		tableViewer = new TableViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		table.setLayout(new FillLayout());
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					copyToClipboard();
				}
			}
		});
		initContextMenu(tableViewer);
	}

	private void copyToClipboard() {

		String DELIMITER = OperatingSystemUtils.TAB;
		String END_OF_LINE = OperatingSystemUtils.getLineDelimiter();
		StringBuilder builder = new StringBuilder();
		Table table = tableViewer.getTable();
		TableColumn[] tableColumns = table.getColumns();
		int size = tableColumns.length;
		/*
		 * Header
		 */
		for(TableColumn tableColumn : tableColumns) {
			builder.append(tableColumn.getText());
			builder.append(DELIMITER);
		}
		builder.append(END_OF_LINE);
		/*
		 * Copy the selected items.
		 */
		for(int index : table.getSelectionIndices()) {
			/*
			 * Get the nth selected item.
			 */
			TableItem selection = table.getItem(index);
			/*
			 * Dump all elements of the item, e.g. RT, Abundance, ... .
			 */
			for(int columnIndex = 0; columnIndex < size; columnIndex++) {
				builder.append(selection.getText(columnIndex));
				builder.append(DELIMITER);
			}
			builder.append(END_OF_LINE);
		}
		/*
		 * If the builder is empty, give a note that items needs to be selected.
		 */
		if(builder.length() == 0) {
			builder.append(SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_COPY_LINES_INFO));
			builder.append(END_OF_LINE);
		}
		/*
		 * Transfer the selected text (items) to the clipboard.
		 */
		TextTransfer textTransfer = TextTransfer.getInstance();
		Object[] data = new Object[]{builder.toString()};
		Transfer[] dataTypes = new Transfer[]{textTransfer};
		clipboard.setContents(data, dataTypes);
	}

	private void initContextMenu(TableViewer tableViewer) {

		MenuManager menuManager = new MenuManager(POPUP_MENU_ID, getClass().getName() + POPUP_MENU_POSTFIX);
		menuManager.setRemoveAllWhenShown(true);
		/*
		 * Copy to clipboard
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						copyToClipboard();
					}
				};
				action.setText(SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_COPY_SELECTION_CLIPBOARD));
				manager.add(action);
			}
		});
		//
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}

	private void createColumns(String[] titles, int[] bounds) {

		/*
		 * Clear existing columns and set the titles and bounds.
		 */
		clearTableViewerColumns(tableViewer);
		//
		for(int i = 0; i < titles.length; i++) {
			/*
			 * Column sort.
			 */
			final int index = i;
			final TableColumn tableColumn = getTableColumn(titles[i], bounds[i], tableViewer);
			tableColumn.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					handleSortSelectionListener(tableViewer, tableColumn, index);
				}
			});
		}
		/*
		 * Set header and lines visible.
		 */
		setTableProperties(tableViewer);
	}

	private void handleSortSelectionListener(TableViewer tableViewer, TableColumn tableColumn, int index) {

		if(sorter != null) {
			/*
			 * Sorting the table.
			 */
			Table table = tableViewer.getTable();
			sorter.setColumn(index);
			int direction = table.getSortDirection();
			if(table.getSortColumn() == tableColumn) {
				/*
				 * Toggle the direction
				 */
				direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
			} else {
				direction = SWT.UP;
			}
			/*
			 * Sort order detail.
			 */
			table.setSortDirection(direction);
			table.setSortColumn(tableColumn);
			tableViewer.refresh();
		}
	}

	private TableColumn getTableColumn(String title, int bound, TableViewer tableViewer) {

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumns.add(tableViewerColumn);
		//
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setText(title);
		tableColumn.setWidth(bound);
		tableColumn.setResizable(true);
		tableColumn.setMoveable(true);
		//
		return tableColumn;
	}

	private void setTableProperties(TableViewer tableViewer) {

		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	/**
	 * Removes all existing table viewer columns.
	 */
	private void clearTableViewerColumns(TableViewer tableViewer) {

		Table table = tableViewer.getTable();
		table.setRedraw(false);
		//
		tableViewerColumns.clear();
		table.clearAll();
		while(table.getColumnCount() > 0) {
			table.getColumns()[0].dispose();
		}
		//
		table.setRedraw(true);
		tableViewer.refresh();
	}
}
