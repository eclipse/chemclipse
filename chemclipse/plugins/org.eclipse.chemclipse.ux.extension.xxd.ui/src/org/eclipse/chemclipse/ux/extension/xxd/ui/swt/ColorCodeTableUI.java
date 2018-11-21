/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ColorCodeLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCode;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;

public class ColorCodeTableUI extends ExtendedTableViewer {

	public ColorCodeTableUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(ColorCodeLabelProvider.TITLES, ColorCodeLabelProvider.BOUNDS);
		setLabelProvider(new ColorCodeLabelProvider());
		setContentProvider(new ListContentProvider());
		setCellColorProvider();
	}

	private void setCellColorProvider() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		TableViewerColumn tableViewerColumn = tableViewerColumns.get(ColorCodeLabelProvider.INDEX_COLOR);
		if(tableViewerColumn != null) {
			tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {

				@Override
				public void update(ViewerCell cell) {

					if(cell != null) {
						ColorCode colorCode = (ColorCode)cell.getElement();
						cell.setBackground(colorCode.getColor());
						cell.setForeground(Colors.WHITE);
						cell.setText(colorCode.getColor().toString());
						super.update(cell);
					}
				}
			});
		}
	}
}
