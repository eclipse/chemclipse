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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.export;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.TableProvider;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;

public class ExportDataSupplier {

	private ColumnGroupModel columnGroupModel;
	private IDataProvider data;
	private IDataProvider header;
	private TableProvider tableProvider;

	public ExportDataSupplier(TableProvider tableProvider, IDataProvider data, IDataProvider header, ColumnGroupModel columnGroupModel) {

		this.data = data;
		this.header = header;
		this.columnGroupModel = columnGroupModel;
		this.tableProvider = tableProvider;
	}

	public int getColumnCount() {

		return data.getColumnCount();
	}

	public Object getDataValue(int column, int row) {

		int headerRowCount = getHeaderRowCount();
		if(row < headerRowCount) {
			switch(row) {
				case 0:
					return header.getDataValue(column, 0);
				case 1:
					ColumnGroup columnGroup = columnGroupModel.getColumnGroupByIndex(column);
					if(columnGroup != null) {
						return columnGroup.getName();
					}
					return "";
				default:
					throw new RuntimeException("Undefided row");
			}
		} else {
			return data.getDataValue(column, row - headerRowCount);
		}
	}

	private int getHeaderRowCount() {

		if(!columnGroupModel.isEmpty()) {
			return 2;
		} else {
			return 1;
		}
	}

	public int getRowCount() {

		return data.getRowCount() + getHeaderRowCount();
	}
}
