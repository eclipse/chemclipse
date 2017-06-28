/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class InputFilesTable {

	private List<IDataInputEntry> dataInputEntries = new ArrayList<>();
	private List<Text> groupNames = new ArrayList<>();
	private Table table;
	private List<TableEditor> tableEditors = new ArrayList<>();

	public InputFilesTable(Composite composite) {
		createTable(composite);
	}

	private void createTable(Composite client) {

		GridData gridData;
		table = new Table(client, SWT.MULTI | SWT.BORDER);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		// gridData.widthHint = 150;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		// gridData.verticalSpan = 3;
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputEntries;
	}

	/**
	 * Reload the table.
	 */
	public void reload() {

		if(table != null) {
			/*
			 * Remove all entries.
			 */
			table.removeAll();
			table.clearAll();
			/*
			 * dispose editors and text
			 */
			for(TableEditor editor : tableEditors) {
				editor.dispose();
			}
			tableEditors.clear();
			for(Text text : groupNames) {
				text.dispose();
			}
			groupNames.clear();
			/*
			 * Header
			 */
			String[] titles = {"Filename", "Group", "Path"};
			for(int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText(titles[i]);
			}
			/*
			 * Data
			 */
			for(int i = 1; i < dataInputEntries.size(); i++) {
				IDataInputEntry entry = dataInputEntries.get(i);
				/*
				 * set file name column
				 */
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, entry.getFileName());
				/*
				 * set group name column
				 */
				TableEditor editor = new TableEditor(table);
				editor.horizontalAlignment = SWT.LEFT;
				editor.grabHorizontal = true;
				final Text text = new Text(table, SWT.NONE);
				String groupName = entry.getGroupName();
				if(groupName == null) {
					text.setText("");
				} else {
					text.setText(groupName);
				}
				text.setEnabled(true);
				text.addModifyListener((ModifyEvent e) -> {
					String gN = text.getText();
					if(gN != null) {
						gN = gN.trim();
						if(gN.isEmpty()) {
							entry.setGroupName(null);
						} else {
							entry.setGroupName(gN);
						}
					}
				});
				editor.setEditor(text, item, 1);
				tableEditors.add(editor);
				groupNames.add(text);
				/*
				 * set file path
				 */
				item.setText(2, entry.getInputFile());
			}
			/*
			 * Pack to make the entries visible.
			 */
			for(int i = 0; i < titles.length; i++) {
				table.getColumn(i).pack();
			}
			table.layout(true);
		}
	}

	/**
	 * Remove the given entries, which are selected
	 * The table need not to be reloaded.
	 *
	 */
	public void removeSelection() {

		int[] indices = table.getSelectionIndices();
		if(indices == null || indices.length == 0) {
			return;
		}
		int counter = 0;
		for(int index : indices) {
			/*
			 * Decrease the index and increase the counter to remove the correct entries.
			 */
			index -= counter;
			dataInputEntries.remove(index);
			counter++;
		}
		reload();
	}
}
