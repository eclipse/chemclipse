/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TableConfig;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

public class TableConfigSupport implements TableConfig {

	private Supplier<Collection<? extends ViewerColumn>> columns;

	public TableConfigSupport(Collection<? extends ViewerColumn> columns) {

		this((Supplier<Collection<? extends ViewerColumn>>)() -> columns);
	}

	public TableConfigSupport(Supplier<Collection<? extends ViewerColumn>> columns) {

		this.columns = columns;
	}

	@Override
	public void setVisibleColumns(Set<String> visibleColumns) {

		for(ViewerColumn column : columns.get()) {
			Item item = getItem(column);
			if(visibleColumns.contains(item.getText())) {
				restoreItem(item);
			} else {
				hideItem(item);
			}
		}
	}

	private void restoreItem(Item item) {

		Object oldWidth = item.getData("OLD_WIDTH");
		Object oldResizable = item.getData("OLD_RESIZABLE");
		if(item instanceof TreeColumn column) {
			if(oldWidth instanceof Number oldWidthNumber) {
				column.setWidth(oldWidthNumber.intValue());
			}
			if(oldResizable instanceof Boolean oldResizableBoolean) {
				column.setResizable(oldResizableBoolean.booleanValue());
			}
		} else if(item instanceof TableColumn column) {
			if(oldWidth instanceof Number oldWidthNumber) {
				column.setWidth(oldWidthNumber.intValue());
			}
			if(oldResizable instanceof Boolean oldResizableBoolean) {
				column.setResizable(oldResizableBoolean.booleanValue());
			}
		}
	}

	@Override
	public Set<String> getColumns() {

		LinkedHashSet<String> set = new LinkedHashSet<>();
		for(ViewerColumn column : columns.get()) {
			Item item = getItem(column);
			if(item != null) {
				set.add(item.getText());
			}
		}
		return set;
	}

	private void hideItem(Item item) {

		if(item instanceof TreeColumn column) {
			item.setData("OLD_WIDTH", column.getWidth());
			item.setData("OLD_RESIZABLE", column.getResizable());
			column.setWidth(0);
			column.setResizable(false);
		} else if(item instanceof TableColumn column) {
			item.setData("OLD_WIDTH", column.getWidth());
			item.setData("OLD_RESIZABLE", column.getResizable());
			column.setWidth(0);
			column.setResizable(false);
		}
	}

	private Item getItem(ViewerColumn column) {

		if(column instanceof TreeViewerColumn treeViewerColumn) {
			return treeViewerColumn.getColumn();
		} else if(column instanceof TableViewerColumn tableViewerColumn) {
			return tableViewerColumn.getColumn();
		}
		return null;
	}

	@Override
	public int getColumWidth(String columnName) {

		for(ViewerColumn column : columns.get()) {
			Item item = getItem(column);
			if(item != null && item.getText().equals(columnName)) {
				if(item instanceof TreeColumn treeColumn) {
					return treeColumn.getWidth();
				} else if(item instanceof TableColumn tableColumn) {
					return tableColumn.getWidth();
				}
			}
		}
		return -1;
	}

	@Override
	public void setColumWidth(String columnName, int width) {

		for(ViewerColumn column : columns.get()) {
			Item item = getItem(column);
			if(item != null && item.getText().equals(columnName)) {
				if(item instanceof TreeColumn treeColumn) {
					treeColumn.setWidth(width);
					return;
				} else if(item instanceof TableColumn tableColumn) {
					tableColumn.setWidth(width);
					return;
				}
			}
		}
	}
}
