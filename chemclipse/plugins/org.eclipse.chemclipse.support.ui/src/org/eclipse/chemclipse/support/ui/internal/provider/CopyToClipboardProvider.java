/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.chemclipse.support.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.ui.support.CopyColumnsSupport;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CopyToClipboardProvider {

	private static final String LINE_BREAK = "\n"; //$NON-NLS-1$

	public void copyToClipboard(Clipboard clipboard, ExtendedTableViewer extendedTableViewer) {

		String valueDelimiter = extendedTableViewer.getCopyValueDelimiterClipboard().delimiter();
		String lineDelimiter = OperatingSystemUtils.getLineDelimiter();
		StringBuilder builder = new StringBuilder();
		int[] columns = getColumns(extendedTableViewer);
		/*
		 * Header
		 */
		if(extendedTableViewer.isCopyHeaderToClipboard()) {
			addHeader(extendedTableViewer, builder, columns, valueDelimiter, lineDelimiter);
		}
		/*
		 * Data
		 */
		addContent(extendedTableViewer, builder, columns, valueDelimiter, lineDelimiter);
		addNoContentMessageOnDemand(builder);
		//
		transferToClipboard(clipboard, builder.toString());
	}

	private int[] addHeader(ExtendedTableViewer extendedTableViewer, StringBuilder builder, int[] columns, String valueDelimiter, String lineDelimiter) {

		String[] titles = getTitles(extendedTableViewer);
		int size = titles.length;
		//
		List<String> elements = new ArrayList<>();
		for(int column : columns) {
			if(column >= 0 && column < size) {
				elements.add(optimizeText(titles[column]));
			}
		}
		print(builder, elements, valueDelimiter, lineDelimiter);
		//
		return columns;
	}

	private void addContent(ExtendedTableViewer extendedTableViewer, StringBuilder builder, int[] columns, String valueDelimiter, String lineDelimiter) {

		Table table = extendedTableViewer.getTable();
		int size = table.getColumnCount();
		int[] indices = table.getSelectionIndices();
		lineDelimiter = indices.length > 1 ? lineDelimiter : null;
		//
		TableItem selection;
		for(int index : table.getSelectionIndices()) {
			/*
			 * Dump all elements of the item.
			 */
			List<String> elements = new ArrayList<>();
			selection = table.getItem(index);
			for(int column : columns) {
				if(column >= 0 && column < size) {
					elements.add(optimizeText(selection.getText(column)));
				}
			}
			print(builder, elements, valueDelimiter, lineDelimiter);
		}
	}

	private String optimizeText(String text) {

		return text.replace(LINE_BREAK, " "); //$NON-NLS-1$
	}

	private void addNoContentMessageOnDemand(StringBuilder builder) {

		if(builder.length() == 0) {
			builder.append(SupportMessages.selectEntriesinList);
		}
	}

	private void transferToClipboard(Clipboard clipboard, String content) {

		/*
		 * Transfer the selected text (items) to the clipboard.
		 */
		TextTransfer textTransfer = TextTransfer.getInstance();
		Object[] data = new Object[]{content};
		Transfer[] dataTypes = new Transfer[]{textTransfer};
		clipboard.setContents(data, dataTypes);
	}

	private int[] getColumns(ExtendedTableViewer extendedTableViewer) {

		Table table = extendedTableViewer.getTable();
		//
		int[] columns;
		String copyColumnsToClipBoard = extendedTableViewer.getCopyColumnsToClipboard();
		if(copyColumnsToClipBoard.isEmpty()) {
			if(PreferenceSupplier.isClipboardDefaultSorting()) {
				int size = extendedTableViewer.getTableViewerColumns().size();
				columns = IntStream.range(0, size).toArray();
			} else {
				columns = table.getColumnOrder();
			}
		} else {
			columns = CopyColumnsSupport.getColumns(copyColumnsToClipBoard);
		}
		//
		return columns;
	}

	private String[] getTitles(ExtendedTableViewer extendedTableViewer) {

		List<TableViewerColumn> tableViewerColumns = extendedTableViewer.getTableViewerColumns();
		String[] titles = new String[tableViewerColumns.size()];
		int i = 0;
		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			titles[i++] = tableViewerColumn.getColumn().getText();
		}
		//
		return titles;
	}

	private void print(StringBuilder builder, List<String> elements, String delimiter, String lineDelimiter) {

		Iterator<String> iterator = elements.iterator();
		while(iterator.hasNext()) {
			builder.append(iterator.next());
			if(iterator.hasNext()) {
				builder.append(delimiter);
			}
		}
		/*
		 * Line Break
		 */
		if(lineDelimiter != null) {
			builder.append(lineDelimiter);
		}
	}
}