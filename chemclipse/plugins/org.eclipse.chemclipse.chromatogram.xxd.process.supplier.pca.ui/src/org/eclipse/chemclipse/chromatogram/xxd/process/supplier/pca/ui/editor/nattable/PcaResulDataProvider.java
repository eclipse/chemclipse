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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class PcaResulDataProvider implements IDataProvider {

	private SortModel sortModel;
	private TableProvider tableProvider;

	public PcaResulDataProvider(TableProvider tableProvider, SortModel sortModel) {
		this.sortModel = sortModel;
		this.tableProvider = tableProvider;
	}

	@Override
	public int getColumnCount() {

		return tableProvider.getColumnCount();
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {

		int sortRowIndex = sortModel.getOrderRow().get(rowIndex);
		if(columnIndex == TableProvider.COLUMN_INDEX_RETENTION_TIMES) {
			int retentionTime = tableProvider.getDataTable().getRetentionTimes().get(sortRowIndex);
			return retentionTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		} else {
			ISample sample = tableProvider.getDataTable().getSamples().get(columnIndex - TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
			return sample.getPcaResult().getSampleData()[sortRowIndex];
		}
	}

	@Override
	public int getRowCount() {

		return tableProvider.getRowCount();
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {

	}
}
