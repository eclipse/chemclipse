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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class PcaResulHeaderProvider implements IDataProvider {

	private TableProvider tableProvider;

	public PcaResulHeaderProvider(TableProvider tableProvider) {
		this.tableProvider = tableProvider;
	}

	@Override
	public int getColumnCount() {

		return tableProvider.getColumnCount();
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {

		if(columnIndex == TableProvider.COLUMN_INDEX_SELECTED) {
			return "";
		} else if(columnIndex == TableProvider.COLUMN_INDEX_RETENTION_TIMES) {
			return "reten. time";
		} else if(columnIndex == TableProvider.COLUMN_INDEX_PEAK_NAMES) {
			return "Compound";
		} else {
			ISample sample = tableProvider.getDataTable().getSamples().get(columnIndex - TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
			if(sample instanceof IGroup) {
				return "Mean";
			} else {
				return sample.getName();
			}
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
