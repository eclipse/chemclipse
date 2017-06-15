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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

public class PcaResulHeaderProvider extends AbstractPcaResulDataProvider {

	public PcaResulHeaderProvider(TableData dataTable) {
		super(dataTable);
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {

		if(columnIndex == COLUMN_INDEX_RETENTION_TIMES) {
			return "reten. time";
		} else {
			return getDataTable().getSamples().get(columnIndex - NUMER_OF_DESCRIPTION_COLUMN).getName();
		}
	}

	@Override
	public int getRowCount() {

		return 1;
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {

	}
}
