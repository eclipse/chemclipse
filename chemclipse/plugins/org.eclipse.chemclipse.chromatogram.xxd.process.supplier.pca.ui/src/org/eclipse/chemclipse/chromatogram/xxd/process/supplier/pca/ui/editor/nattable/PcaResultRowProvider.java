/*******************************************************************************
 * Copyright (c) 2017 PC.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * PC - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

public class PcaResultRowProvider extends AbstractPcaResulDataProvider {

	public PcaResultRowProvider(TableData dataTable) {
		super(dataTable);
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
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {

	}
}
