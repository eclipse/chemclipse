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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;

public class SortModel implements ISortModel {

	private boolean isSorted;
	private SortDirectionEnum sortDirection;
	private int sortedColumn;
	private List<Integer> sortedRow;
	private TableData tableData;

	public SortModel(TableData tableProvider) {

		this.sortedRow = new ArrayList<>();
		sortDirection = SortDirectionEnum.NONE;
		this.tableData = tableProvider;
	}

	@Override
	public void clear() {

		for(int i = 0; i < sortedRow.size(); i++) {
			sortedRow.set(i, i);
		}
		sortDirection = SortDirectionEnum.NONE;
		isSorted = false;
	}

	@Override
	public Comparator<?> getColumnComparator(int columnIndex) {

		return null;
	}

	@Override
	public List<Comparator> getComparatorsForColumnIndex(int columnIndex) {

		return null;
	}

	public List<Integer> getOrderRow() {

		return sortedRow;
	}

	@Override
	public SortDirectionEnum getSortDirection(int columnIndex) {

		if(isSorted && columnIndex == sortedColumn) {
			return sortDirection;
		}
		return SortDirectionEnum.NONE;
	}

	@Override
	public List<Integer> getSortedColumnIndexes() {

		List<Integer> sortedColumnIndexes = new ArrayList<>();
		if(isSorted) {
			sortedColumnIndexes.add(sortedColumn);
		}
		return sortedColumnIndexes;
	}

	@Override
	public int getSortOrder(int columnIndex) {

		return 0;
	}

	@Override
	public boolean isColumnIndexSorted(int columnIndex) {

		if(isSorted && columnIndex == sortedColumn) {
			return true;
		}
		return false;
	}

	@Override
	public void sort(int columnIndex, SortDirectionEnum sortDirection, boolean accumulate) {

		clear();
		if(!sortDirection.equals(SortDirectionEnum.NONE)) {
			/*
			 * Set direction of sorting
			 */
			int direction = 1;
			switch(sortDirection) {
				case ASC:
					direction = 1;
					break;
				case DESC:
					direction = -1;
			}
			final int setDirection = direction;
			if(columnIndex == TableProvider.COLUMN_INDEX_SELECTED) {
				List<? extends IVariable> variables = tableData.getVariables();
				boolean[] modifiableRowList = tableData.getModifiableRowList();
				sortedRow.sort((i, j) -> {
					if(!modifiableRowList[i] && !modifiableRowList[j]) {
						return 0;
					}
					if(!modifiableRowList[j]) {
						return 1 * setDirection;
					}
					if(!modifiableRowList[i]) {
						return -1 * setDirection;
					}
					return setDirection * Boolean.compare(variables.get(i).isSelected(), variables.get(j).isSelected());
				});
			} else if(columnIndex == TableProvider.COLUMN_INDEX_VARIABLES) {
				/*
				 * sort by retention time
				 */
				List<IVariableVisualization> variableVisualizations = tableData.getVariables();
				sortedRow.sort((i, j) -> {
					return setDirection * variableVisualizations.get(i).compareTo(variableVisualizations.get(j));
				});
			} else if(columnIndex == TableProvider.COLUMN_INDEX_PEAK_NAMES) {
				/*
				 * sort by variable description
				 */
				Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareTo);
				List<IVariableVisualization> variables = tableData.getVariables();
				sortedRow.sort((i, j) -> {
					return setDirection * nullSafeStringComparator.compare(variables.get(i).getDescription(), variables.get(j).getDescription());
				});
			} else if(columnIndex == TableProvider.COLUMN_INDEX_CLASSIFICATIONS) {
				/*
				 * sort by variable description
				 */
				Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareTo);
				List<IVariableVisualization> variables = tableData.getVariables();
				sortedRow.sort((i, j) -> {
					return setDirection * nullSafeStringComparator.compare(variables.get(i).getClassification(), variables.get(j).getClassification());
				});
			} else if(columnIndex == TableProvider.COLUMN_INDEX_COLOR) {
				sortedRow.sort((i, j) -> {
					List<IVariableVisualization> variables = tableData.getVariables();
					return setDirection * Integer.compare(variables.get(i).getColor(), variables.get(i).getColor());
				});
			} else {
				/*
				 * sort by abundance
				 */
				ISample sample = tableData.getSamples().get(columnIndex - TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
				List<? extends ISampleData> sampleData = sample.getSampleData();
				sortedRow.sort((i, j) -> {
					ISampleData sampleDataA = sampleData.get(i);
					ISampleData sampleDataB = sampleData.get(j);
					return setDirection * Double.compare(sampleDataA.getModifiedData(), sampleDataB.getModifiedData());
				});
			}
			this.isSorted = true;
		}
		this.sortDirection = sortDirection;
		this.sortedColumn = columnIndex;
	}

	/**
	 * call this method after data in object tableData has been changed
	 */
	public void update() {

		int numberOfRow = tableData.getVariables().size();
		sortedRow.clear();
		for(int i = 0; i < numberOfRow; i++) {
			sortedRow.add(i);
		}
		sortDirection = SortDirectionEnum.NONE;
		isSorted = false;
	}
}
