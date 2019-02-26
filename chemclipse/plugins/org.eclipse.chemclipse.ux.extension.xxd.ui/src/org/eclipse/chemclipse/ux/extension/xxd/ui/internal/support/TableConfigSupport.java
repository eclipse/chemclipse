/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TableConfig;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.TableColumn;

public class TableConfigSupport implements TableConfig {

	private ExtendedTableViewer extendedTableViewer;

	public TableConfigSupport(ExtendedTableViewer extendedTableViewer) {
		this.extendedTableViewer = extendedTableViewer;
	}

	@Override
	public void setVisibleColumns(Set<String> visibleColumns) {

		List<TableViewerColumn> columns = extendedTableViewer.getTableViewerColumns();
		for(TableViewerColumn column : columns) {
			TableColumn tableColumn = column.getColumn();
			if(visibleColumns.contains(tableColumn.getText())) {
				Object oldWidth = tableColumn.getData("OLD_WITH");
				if(oldWidth instanceof Number) {
					tableColumn.setWidth(((Number)oldWidth).intValue());
				}
				Object oldResizable = tableColumn.getData("OLD_RESIZABLE");
				if(oldResizable instanceof Boolean) {
					tableColumn.setResizable(((Boolean)oldResizable).booleanValue());
				}
			} else {
				tableColumn.setData("OLD_WITH", tableColumn.getWidth());
				tableColumn.setData("OLD_RESIZABLE", tableColumn.getResizable());
				tableColumn.setWidth(0);
				tableColumn.setResizable(false);
			}
		}
	}

	@Override
	public Set<String> getColumns() {

		LinkedHashSet<String> set = new LinkedHashSet<>();
		List<TableViewerColumn> columns = extendedTableViewer.getTableViewerColumns();
		for(TableViewerColumn column : columns) {
			set.add(column.getColumn().getText());
		}
		return set;
	}
}
