/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring model
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class InputFilesTable {

	private TableViewer tableViewer;

	public InputFilesTable(Composite composite, Object layoutData) {

		createTable(composite, layoutData);
	}

	private void createColumns() {

		String[] titles = {"Name", "Group", "Filename", "Path"};
		int[] bounds = {100, 100, 100, 100};
		// first column is for the first name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IDataInputEntry inputData = (IDataInputEntry)element;
				return inputData.getSampleName();
			}
		});
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				IDataInputEntry input = (IDataInputEntry)cell.getElement();
				String text = input.getGroupName() != null ? input.getGroupName() : "";
				cell.setText(text);
			}
		});
		col.setEditingSupport(new EditingSupport(tableViewer) {

			private TextCellEditor editor = new TextCellEditor(tableViewer.getTable());

			@Override
			protected boolean canEdit(Object element) {

				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {

				return editor;
			}

			@Override
			protected Object getValue(Object element) {

				IDataInputEntry inputData = (IDataInputEntry)element;
				String groupName = inputData.getGroupName();
				if(groupName == null) {
					return "";
				} else {
					return groupName;
				}
			}

			@Override
			protected void setValue(Object element, Object value) {

				IDataInputEntry inputData = (IDataInputEntry)element;
				String groupName = (String)value;
				groupName = groupName.trim();
				if(!groupName.isEmpty()) {
					inputData.setGroupName(groupName);
				} else {
					inputData.setGroupName(null);
				}
				update();
			}
		});
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IDataInputEntry inputData = (IDataInputEntry)element;
				return inputData.getFileName();
			}
		});
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				IDataInputEntry inputData = (IDataInputEntry)element;
				return inputData.getInputFile();
			}
		});
	}

	private void createTable(Composite client, Object layoutData) {

		Table table = new Table(client, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(layoutData);
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new ArrayContentProvider());
		createColumns();
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Remove the given entries, which are selected
	 * The table need not to be reloaded.
	 *
	 */
	public void removeSelection() {

		Iterator<?> it = tableViewer.getStructuredSelection().iterator();
		Object object = tableViewer.getInput();
		if(object instanceof List<?>) {
			List<?> inputs = (List<?>)object;
			while(it.hasNext()) {
				inputs.remove(it.next());
			}
		}
		update();
	}

	public void setDataInputEntries(List<IDataInputEntry> dataInputEntries) {

		tableViewer.setInput(dataInputEntries);
	}

	public void update() {

		tableViewer.refresh();
		for(TableColumn column : tableViewer.getTable().getColumns()) {
			column.pack();
		}
	}
}