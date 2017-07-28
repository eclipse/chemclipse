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
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class InputFilesTable {

	private List<IDataInputEntry> dataInputEntries = new ArrayList<>();
	private List<Text> groupNames = new ArrayList<>();
	private Table table;
	private List<TableEditor> tableEditors = new ArrayList<>();

	public InputFilesTable(Composite composite, Object layoutData) {
		createTable(composite, layoutData);
	}

	private void createTable(Composite client, Object layoutData) {

		table = new Table(client, SWT.MULTI | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(layoutData);
	}

	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputEntries;
	}

	public Table getTable() {

		return table;
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
			String[] titles = {"Filename", "Color", "Group", "Path"};
			for(int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText(titles[i]);
			}
			/*
			 * Data
			 */
			Color backgroundNormal = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			Color backgroundGray = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			//
			Map<String, Color> groupColorMap = PcaColorGroup.getColorSWT(PcaUtils.getGroupNamesFromEntry(dataInputEntries));
			for(int i = 0; i < dataInputEntries.size(); i++) {
				IDataInputEntry entry = dataInputEntries.get(i);
				TableItem item = new TableItem(table, SWT.NONE);
				item.setData(entry);
				/*
				 * set file name column
				 */
				item.setText(0, entry.getFileName());
				/*
				 * set group color
				 */
				setGroupColor(item, entry.getGroupName(), groupColorMap);
				/*
				 * set group name column
				 */
				TableEditor editor = new TableEditor(table);
				editor.horizontalAlignment = SWT.LEFT;
				editor.grabHorizontal = true;
				Text text = new Text(table, SWT.BORDER);
				text.setBackground((i % 2 == 0) ? backgroundGray : backgroundNormal);
				String groupName = entry.getGroupName();
				if(groupName == null) {
					text.setText("");
				} else {
					text.setText(groupName);
				}
				text.setEnabled(true);
				text.addFocusListener(new FocusListener() {

					@Override
					public void focusGained(FocusEvent e) {

					}

					@Override
					public void focusLost(FocusEvent e) {

						String gN = text.getText().trim();
						String setGroupName = (gN.isEmpty() ? null : gN);
						entry.setGroupName(setGroupName);
						setGroupColor();
					}
				});
				editor.setEditor(text, item, 2);
				tableEditors.add(editor);
				groupNames.add(text);
				/*
				 * set file path
				 */
				item.setText(3, entry.getInputFile());
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

	private void setGroupColor() {

		Map<String, Color> groupColorMap = PcaColorGroup.getColorSWT(PcaUtils.getGroupNamesFromEntry(dataInputEntries));
		TableItem[] items = table.getItems();
		for(TableItem item : items) {
			IDataInputEntry entry = (IDataInputEntry)item.getData();
			setGroupColor(item, entry.getGroupName(), groupColorMap);
		}
	}

	private void setGroupColor(TableItem item, String name, Map<String, Color> mapGroupColor) {

		Color color = mapGroupColor.get(name);
		int len = 16;
		Image image = new Image(Display.getCurrent(), len, len);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(0, 0, len, len);
		gc.dispose();
		item.setImage(1, image);
	}
}
