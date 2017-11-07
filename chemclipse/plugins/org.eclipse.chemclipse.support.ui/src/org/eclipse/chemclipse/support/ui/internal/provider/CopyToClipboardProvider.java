/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

import java.util.List;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CopyToClipboardProvider {

	private final String DELIMITER = "\t";

	public void copyToClipboard(Clipboard clipboard, ExtendedTableViewer extendedTableViewer) {

		String[] titles = getTitles(extendedTableViewer);
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
		Table table = extendedTableViewer.getTable();
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

	private String[] getTitles(ExtendedTableViewer extendedTableViewer) {

		List<TableViewerColumn> tableViewerColumns = extendedTableViewer.getTableViewerColumns();
		String[] titles = new String[tableViewerColumns.size()];
		int i = 0;
		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			titles[i++] = tableViewerColumn.getColumn().getText();
		}
		return titles;
	}
}
