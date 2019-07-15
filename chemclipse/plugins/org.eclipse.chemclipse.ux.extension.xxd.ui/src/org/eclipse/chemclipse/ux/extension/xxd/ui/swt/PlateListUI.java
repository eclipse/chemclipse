/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.pcr.model.core.IPlateTableEntry;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PlateListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PlateListTableComparator;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;

public class PlateListUI extends ExtendedTableViewer {

	private PlateListTableComparator tableComparator = new PlateListTableComparator();
	private PlateListLabelProvider labelProvider = new PlateListLabelProvider();
	private String[] titles = PlateListLabelProvider.TITLES;
	private int[] bounds = PlateListLabelProvider.BOUNDS;

	public PlateListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setCellColorProvider();
	}

	private void setCellColorProvider() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 1; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			if(tableViewerColumn != null) {
				setCellColorScheme(tableViewerColumn, i);
			}
		}
	}

	private void setCellColorScheme(TableViewerColumn tableViewerColumn, int column) {

		tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				if(cell != null) {
					IPlateTableEntry plateTableEntry = (IPlateTableEntry)cell.getElement();
					if(plateTableEntry != null) {
						IWell well = plateTableEntry.getWells().get(column);
						String text = PlateListLabelProvider.getCellText(well);
						if(well != null) {
							if(well.isEmptyMeasurement() || !well.isActiveSubset()) {
								cell.setBackground(Colors.GRAY);
								cell.setForeground(Colors.WHITE);
							} else {
								if(well.isPositiveMeasurement()) {
									cell.setBackground(Colors.RED);
									cell.setForeground(Colors.WHITE);
								} else {
									cell.setBackground(Colors.GREEN);
									cell.setForeground(Colors.BLACK);
								}
							}
						}
						cell.setText(text);
					}
					//
					super.update(cell);
				}
			}
		});
	}
}
