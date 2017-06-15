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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public abstract class AbstractPcaResulDataProvider implements IDataProvider {

	final public static int COLUMN_INDEX_RETENTION_TIMES = 0;
	/**
	 * number of column which is used to describe sample data
	 */
	final public static int NUMER_OF_DESCRIPTION_COLUMN = 1;
	private TableData dataTable;

	public AbstractPcaResulDataProvider(TableData dataTable) {
		this.dataTable = dataTable;
	}

	@Override
	public int getColumnCount() {

		return dataTable.getSamples().size() + NUMER_OF_DESCRIPTION_COLUMN;
	}

	protected TableData getDataTable() {

		return dataTable;
	}

	@Override
	public int getRowCount() {

		List<ISample> samples = dataTable.getSamples();
		if(!samples.isEmpty()) {
			return samples.get(0).getPcaResult().getSampleData().length;
		}
		return 0;
	}
}
