/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.SamplesComparator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.SamplesEditingSupport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.SamplesLabelProvider;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.SamplesListFilter;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class SamplesListUI extends ExtendedTableViewer {

	private static final String[] TITLES = SamplesLabelProvider.TITLES;
	private static final int[] BOUNDS = SamplesLabelProvider.BOUNDS;
	//
	private final ITableLabelProvider labelProvider = new SamplesLabelProvider();
	private final ViewerComparator comparator = new SamplesComparator();
	private final SamplesListFilter listFilter = new SamplesListFilter();

	public SamplesListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void updateInput(List<ISample> sampleList) {

		super.setInput(sampleList);
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
		setFilters(new ViewerFilter[]{listFilter});
		setEditingSupport();
		setCellColorProvider();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(isEditable(label)) {
				tableViewerColumn.setEditingSupport(new SamplesEditingSupport(this, label));
			}
		}
	}

	private boolean isEditable(String label) {

		return !SamplesLabelProvider.SAMPLE_NAME.equals(label);
	}

	private void setCellColorProvider() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		TableViewerColumn tableViewerColumn = tableViewerColumns.get(SamplesLabelProvider.INDEX_COLOR);
		if(tableViewerColumn != null) {
			tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {

				@Override
				public void update(ViewerCell cell) {

					if(cell != null) {
						Object element = cell.getElement();
						if(element instanceof ISample sample) {
							cell.setBackground(Colors.getColor(sample.getRGB()));
							cell.setForeground(Colors.BLACK);
							cell.setText("");
						}
						super.update(cell);
					}
				}
			});
		}
	}
}