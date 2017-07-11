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

import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
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
			List<ISample> samples = tableProvider.getDataTable().getSamples();
			ISample sample = samples.get(columnIndex - TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
			double[] sampleData = sample.getPcaResult().getSampleData();
			String normalization = tableProvider.getNormalizationData();
			switch(normalization) {
				case TableProvider.NORMALIZATION_NONE:
					return sampleData[sortRowIndex];
				case TableProvider.NORMALIZATION_ROW:
					if(sample instanceof IGroup) {
						double totalGroup = samples.stream().filter(s -> sample.getGroupName().equals(s.getGroupName())).filter(s -> (!(s instanceof IGroup))).mapToDouble(s -> s.getPcaResult().getSampleData()[sortRowIndex]).map(d -> Math.abs(d)).sum();
						return totalGroup / samples.stream().filter(s -> (!(s instanceof IGroup))).mapToDouble(s -> s.getPcaResult().getSampleData()[sortRowIndex]).map(d -> Math.abs(d)).sum();
					} else {
						return sampleData[sortRowIndex] / samples.stream().filter(s -> (!(s instanceof IGroup))) //
								.mapToDouble(s -> s.getPcaResult().getSampleData()[sortRowIndex]).map(d -> Math.abs(d)).sum();
					}
				case TableProvider.NORMALIZATION_COLUMN:
					return sampleData[sortRowIndex] / Arrays.stream(sampleData).map(d -> Math.abs(d)).sum();
				default:
					throw new RuntimeException("Undefine format cell");
			}
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
