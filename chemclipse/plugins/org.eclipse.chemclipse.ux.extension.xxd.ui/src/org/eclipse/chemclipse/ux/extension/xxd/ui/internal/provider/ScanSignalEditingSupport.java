/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ScanTableUI;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class ScanSignalEditingSupport extends EditingSupport {

	private static final Logger logger = Logger.getLogger(ScanSignalEditingSupport.class);
	//
	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;

	public ScanSignalEditingSupport(ExtendedTableViewer tableViewer, String column) {

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

		if(column.equals(ScanLabelProvider.INTENSITY)) {
			return tableViewer.isEditEnabled();
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {

		boolean editIsEnabled = tableViewer.isEditEnabled();
		if(editIsEnabled && column.equals(ScanLabelProvider.INTENSITY)) {
			if(element instanceof IIon ion) {
				return Float.toString(ion.getAbundance());
			} else if(element instanceof IScanSignalWSD scanSignalWSD) {
				return Float.toString(scanSignalWSD.getAbsorbance());
			} else if(element instanceof IScanCSD scanCSD) {
				return Float.toString(scanCSD.getTotalSignal());
			}
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(column.equals(ScanLabelProvider.INTENSITY)) {
			float abundance = parseValue(value);
			if(abundance > 0.0f) {
				if(element instanceof IIon ion) {
					ion.setAbundance(abundance);
					updateTable();
				} else if(element instanceof IScanSignalWSD scanSignalWSD) {
					scanSignalWSD.setAbsorbance(abundance);
					updateTable();
				} else if(element instanceof IScanCSD) {
					/*
					 * IScanCSD scanCSD = (IScanCSD)element;
					 */
					logger.info("It's not possible to edit the CSD scan total signal at the moment.");
				}
			}
		}
	}

	private void updateTable() {

		if(tableViewer instanceof ScanTableUI scanTableUI) {
			scanTableUI.updateScan();
		}
	}

	private float parseValue(Object value) {

		float result = 0.0f;
		try {
			result = Float.parseFloat(value.toString().trim());
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return result;
	}
}
