/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class PcaResultRowProvider implements IDataProvider {

	private TableProvider tableProvider;

	public PcaResultRowProvider(TableProvider tableProvider) {
		this.tableProvider = tableProvider;
	}

	@Override
	public int getColumnCount() {

		return 1;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {

		return rowIndex;
	}

	@Override
	public int getRowCount() {

		return tableProvider.getRowCount();
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {

	}
}
