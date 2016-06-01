/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ExtendedTableViewer extends TableViewer {

	private Clipboard clipboard;
	private final String DELIMITER = "\t";
	private final String COPY_TO_CLIPBOARD = "Copy selection to clipboard";
	private final String POPUP_MENU_ID = "org.eclipse.chemclipse.swt.ui.viewers.extendedTableViewer.popup";
	private List<TableViewerColumn> tableViewerColumns;

	public ExtendedTableViewer(Composite parent) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		clipboard = new Clipboard(Display.getDefault());
		tableViewerColumns = new ArrayList<TableViewerColumn>();
	}

	public ExtendedTableViewer(Composite parent, int style) {
		super(parent, style);
		clipboard = new Clipboard(Display.getDefault());
		tableViewerColumns = new ArrayList<TableViewerColumn>();
	}

	public void addCopyToClipboardListener(final String[] titles) {

		this.getTable().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.stateMask == SWT.CTRL && e.keyCode == 'c') {
					copyToClipboard(titles);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		initContextMenu(titles);
	}

	private void initContextMenu(final String[] titles) {

		MenuManager menuManager = new MenuManager("#PopUpMenu", POPUP_MENU_ID);
		final Table table = this.getTable();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						copyToClipboard(titles);
					}
				};
				action.setText(COPY_TO_CLIPBOARD);
				manager.add(action);
			}
		});
		Menu menu = menuManager.createContextMenu(table);
		table.setMenu(menu);
	}

	/**
	 * Copies the actual selection to the clipboard.
	 */
	public void copyToClipboard(final String[] titles) {

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
		Table table = this.getTable();
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

	/**
	 * Creates the columns for the peak table.
	 * 
	 * @param tableViewer
	 */
	public void createColumns(final String[] titles, final int[] bounds) {

		final Table table = this.getTable();
		/*
		 * Clear the table and all existing columns.
		 */
		table.setRedraw(false);
		tableViewerColumns.clear();
		table.clearAll();
		while(table.getColumnCount() > 0) {
			table.getColumns()[0].dispose();
		}
		table.setRedraw(true);
		refresh();
		/*
		 * Set the columns.
		 */
		for(int i = 0; i < titles.length; i++) {
			/*
			 * Column sort.
			 */
			final int index = i;
			final TableViewerColumn tableViewerColumn = new TableViewerColumn(this, SWT.NONE);
			tableViewerColumns.add(tableViewerColumn);
			final TableColumn tableColumn = tableViewerColumn.getColumn();
			tableColumn.setText(titles[i]);
			tableColumn.setWidth(bounds[i]);
			tableColumn.setResizable(true);
			tableColumn.setMoveable(true);
			tableColumn.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					ViewerComparator viewerComparator = getComparator();
					if(viewerComparator instanceof IRecordTableComparator) {
						/*
						 * Only sort if a record table sorter has been set.
						 */
						IRecordTableComparator recordTableComparator = (IRecordTableComparator)viewerComparator;
						recordTableComparator.setColumn(index);
						int direction = table.getSortDirection();
						if(table.getSortColumn() == tableColumn) {
							/*
							 * Toggle the sort direction
							 */
							direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
						} else {
							direction = SWT.UP;
						}
						table.setSortDirection(direction);
						table.setSortColumn(tableColumn);
						refresh();
					}
				}
			});
		}
		/*
		 * Set header and lines visible.
		 */
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	public List<TableViewerColumn> getTableViewerColumns() {

		return tableViewerColumns;
	}
}
