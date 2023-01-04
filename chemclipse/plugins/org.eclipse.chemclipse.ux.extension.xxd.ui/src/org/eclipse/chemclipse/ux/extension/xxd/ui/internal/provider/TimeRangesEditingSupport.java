/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class TimeRangesEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	public TimeRangesEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		this.cellEditor = new TextCellEditor(tableViewer.getTable());
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {

		return tableViewer.isEditEnabled();
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof TimeRange timeRange) {
			if(column.equals(TimeRangesLabelProvider.START)) {
				return formatValue(timeRange.getStart());
			}
			if(column.equals(TimeRangesLabelProvider.CENTER)) {
				return formatValue(timeRange.getCenter());
			}
			if(column.equals(TimeRangesLabelProvider.STOP)) {
				return formatValue(timeRange.getStop());
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof TimeRange timeRange) {
			if(column.equals(TimeRangesLabelProvider.START)) {
				timeRange.updateStart(parseValue(value));
			}
			if(column.equals(TimeRangesLabelProvider.CENTER)) {
				timeRange.updateCenter(parseValue(value));
			}
			if(column.equals(TimeRangesLabelProvider.STOP)) {
				timeRange.updateStop(parseValue(value));
			}
			tableViewer.refresh();
		}
	}

	private String formatValue(int retentionTime) {

		return decimalFormat.format(retentionTime / TimeRange.MINUTE_FACTOR);
	}

	private int parseValue(Object value) {

		try {
			double retentionTimeMinutes = Double.parseDouble(value.toString());
			return (int)(retentionTimeMinutes * TimeRange.MINUTE_FACTOR);
		} catch(NumberFormatException e) {
			return 0;
		}
	}
}
