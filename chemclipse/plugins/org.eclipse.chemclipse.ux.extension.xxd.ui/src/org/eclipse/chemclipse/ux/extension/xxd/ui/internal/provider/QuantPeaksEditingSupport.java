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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class QuantPeaksEditingSupport extends EditingSupport {

	private static final Logger logger = Logger.getLogger(QuantPeaksEditingSupport.class);
	//
	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;

	public QuantPeaksEditingSupport(ExtendedTableViewer tableViewer, String column) {

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

		if(element instanceof IQuantitationPeak peak) {
			if(column.equals(QuantPeaksLabelProvider.CONCENTRATION)) {
				return Double.toString(peak.getConcentration());
			}
			if(column.equals(QuantPeaksLabelProvider.CONCENTRATION_UNIT)) {
				return peak.getConcentrationUnit();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IQuantitationPeak peak) {
			if(column.equals(QuantPeaksLabelProvider.CONCENTRATION)) {
				double concentration = parseConcentration((String)value);
				if(!Double.isNaN(concentration)) {
					peak.setConcentration(concentration);
				}
			}
			if(column.equals(QuantPeaksLabelProvider.CONCENTRATION_UNIT)) {
				peak.setConcentrationUnit((String)value);
			}
		}
		tableViewer.refresh();
	}

	private double parseConcentration(String value) {

		double concentration = Double.NaN;
		try {
			double val = Double.parseDouble(value);
			if(val >= 0) {
				concentration = val;
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return concentration;
	}
}
