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

public class PcaResulDataProvider extends AbstractPcaResulDataProvider {

	private SortModel sortModel;

	public PcaResulDataProvider(TableData dataTable, SortModel sortModel) {
		super(dataTable);
		this.sortModel = sortModel;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {

		int sortRowIndex = sortModel.getOrderRow().get(rowIndex);
		if(columnIndex == COLUMN_INDEX_RETENTION_TIMES) {
			int retentionTime = getDataTable().getRetentionTimes().get(sortRowIndex);
			return retentionTime / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		} else {
			ISample sample = getDataTable().getSamples().get(columnIndex - NUMER_OF_DESCRIPTION_COLUMN);
			return sample.getPcaResult().getSampleData()[sortRowIndex];
		}
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {

	}
}
