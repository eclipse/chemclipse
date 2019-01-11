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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.export.ExportData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.export.ExportDataSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeColumnCommand;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.RowHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeCollapseAllCommand;
import org.eclipse.nebula.widgets.nattable.ui.menu.AbstractHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public class PeakListNatTable {

	private ColumnGroupHeaderLayer columnGroupHeaderLayer;
	private PcaResulHeaderProvider columnHeaderDataProvider;
	private ColumnHideShowLayer columnHideShowLayer;
	private PcaResulDataProvider dataProvider;
	private ExportData exportData;
	NatTable natTable;
	SortModel sortModel;
	TableData tableData;
	TableProvider tableProvider;
	SelectionLayer selectionLayer;

	public PeakListNatTable(Composite parent, Object layoutData) {

		tableData = new TableData();
		tableProvider = new TableProvider(tableData);
		createPeakListIntensityTableSection(parent, layoutData);
	}

	private void attachToolTip() {

		DefaultToolTip toolTip = new DefaultToolTip(natTable) {

			@Override
			protected String getText(Event event) {

				int col = natTable.getColumnPositionByX(event.x);
				int row = natTable.getRowPositionByY(event.y);
				if(col > 0) {
					ILayerCell cell = natTable.getCellByPosition(col, row);
					if(cell == null) {
						return "";
					}
					final int rowIndex = cell.getRowIndex();
					final int columnIndex = cell.getColumnIndex();
					if(TableProvider.COLUMN_INDEX_SELECTED == columnIndex) {
						long selectedVariables = tableData.getVariables().stream().filter(IVariable::isSelected).count();
						int total = tableData.getVariables().size();
						return selectedVariables + " variables from " + total + " is selected";
					}
					if(row < 2) {
						return columnHeaderDataProvider.getDataValue(columnIndex, 0).toString();
					} else {
						return dataProvider.getDataValue(columnIndex, rowIndex).toString();
					}
				}
				return null;
			}

			@Override
			protected Object getToolTipArea(Event event) {

				int col = natTable.getColumnPositionByX(event.x);
				int row = natTable.getRowPositionByY(event.y);
				return new Point(col, row);
			}
		};
		toolTip.setPopupDelay(500);
		toolTip.activate();
		toolTip.setShift(new Point(10, 10));
	}

	public void clearTable() {

		tableData.clear();
		natTable.update();
		natTable.refresh();
	}

	public void collapseAllColumns() {

		natTable.doCommand(new TreeCollapseAllCommand());
	}

	/**
	 * Create peak List intensity table
	 */
	private void createPeakListIntensityTableSection(Composite parent, Object layoutData) {

		sortModel = new SortModel(tableProvider);
		dataProvider = new PcaResulDataProvider(tableProvider, sortModel);
		DataLayer bodyDataLayer = new DataLayer(dataProvider);
		bodyDataLayer.setConfigLabelAccumulator(new PcaResultLabelProvider(tableProvider));
		final RowHideShowLayer rowHideShowLayer = new RowHideShowLayer(bodyDataLayer);
		columnHideShowLayer = new ColumnHideShowLayer(rowHideShowLayer);
		ColumnGroupModel columnGroupModel = new ColumnGroupModel();
		ColumnGroupExpandCollapseLayer columnGroupExpandCollapseLayer = new ColumnGroupExpandCollapseLayer(columnHideShowLayer, columnGroupModel, columnGroupModel);
		selectionLayer = new SelectionLayer(columnGroupExpandCollapseLayer);
		final ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
		final FreezeLayer freezeLayer = new FreezeLayer(selectionLayer);
		final CompositeFreezeLayer compositeFreezeLayer = new CompositeFreezeLayer(freezeLayer, viewportLayer, selectionLayer);
		/*
		 * build the column header layer
		 */
		columnHeaderDataProvider = new PcaResulHeaderProvider(tableProvider);
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, compositeFreezeLayer, selectionLayer);
		SortHeaderLayer<?> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer, sortModel);
		columnGroupHeaderLayer = new ColumnGroupHeaderLayer(sortHeaderLayer, selectionLayer, columnGroupModel);
		/*
		 * build the row header layer
		 */
		IDataProvider rowHeaderDataProvider = new PcaResultRowProvider(tableProvider);
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, compositeFreezeLayer, selectionLayer);
		/*
		 * build the corner layer
		 */
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnGroupHeaderLayer);
		/*
		 * build the grid layer
		 */
		GridLayer gridLayer = new GridLayer(compositeFreezeLayer, columnGroupHeaderLayer, rowHeaderLayer, cornerLayer);
		natTable = new NatTable(parent, gridLayer, false);
		natTable.setLayoutData(layoutData);
		final ConfigRegistry configRegistry = new ConfigRegistry();
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		DisplayColumnChooserCommandHandler columnChooserCommandHandler = new DisplayColumnChooserCommandHandler(selectionLayer, columnHideShowLayer, columnHeaderLayer, columnHeaderDataLayer, columnGroupHeaderLayer, columnGroupModel);
		viewportLayer.registerCommandHandler(columnChooserCommandHandler);
		compositeFreezeLayer.registerCommandHandler(columnChooserCommandHandler);
		/*
		 * add menu
		 */
		natTable.addConfiguration(new AbstractHeaderMenuConfiguration(natTable) {

			@Override
			protected PopupMenuBuilder createColumnHeaderMenu(NatTable natTable) {

				return super.createColumnHeaderMenu(natTable).withHideColumnMenuItem() //
						.withShowAllColumnsMenuItem() //
						.withColumnChooserMenuItem();
			}

			@Override
			protected PopupMenuBuilder createCornerMenu(NatTable natTable) {

				return super.createCornerMenu(natTable).withShowAllColumnsMenuItem();
			}
		});
		natTable.addConfiguration(new SingleClickSortConfiguration());
		natTable.addConfiguration(new PcaResulRegistryConfiguration(tableProvider));
		natTable.addConfiguration(new BodyMenuConfiguration(this));
		natTable.configure();
		/*
		 * Freeze column dynamically
		 */
		columnHideShowLayer.addLayerListener(new ILayerListener() {

			int freezeColumns = -1;

			@Override
			public void handleLayerEvent(ILayerEvent event) {

				int num = 0;
				/*
				 * freeze first column, this column contains retention times
				 */
				if(!columnHideShowLayer.isColumnIndexHidden(TableProvider.COLUMN_INDEX_VARIABLES)) {
					num++;
				}
				/*
				 * freeze first column, this column contains names of peaks
				 */
				if(!columnHideShowLayer.isColumnIndexHidden(TableProvider.COLUMN_INDEX_PEAK_NAMES)) {
					num++;
				}
				if(!columnHideShowLayer.isColumnIndexHidden(TableProvider.COLUMN_INDEX_SELECTED)) {
					num++;
				}
				if((event instanceof ColumnStructuralChangeEvent) || freezeColumns != num) {
					if(num > 0) {
						compositeFreezeLayer.doCommand(new FreezeColumnCommand(compositeFreezeLayer, num - 1, false, true));
						freezeColumns = num;
					}
				}
			}
		});
		exportData = new ExportData(new ExportDataSupplier(tableProvider, dataProvider, columnHeaderDataProvider, columnGroupModel));
		attachToolTip();
	}

	public void exportTableDialog(Display display) {

		exportData.exportTableDialog(display);
	}

	/**
	 * create column group according to names group
	 */
	private void generateGroup() {

		/*
		 * clear all groups
		 */
		columnGroupHeaderLayer.clearAllGroups();
		/*
		 * function return array of integer which contains integer in range from first parameter to second parameter
		 */
		final BiFunction<Integer, Integer, int[]> columnInOneGroup = (s, e) -> {
			int[] ar = new int[e - s];
			for(int i = 0; i < ar.length; i++) {
				ar[i] = i + s;
			}
			return ar;
		};
		/*
		 * create column group
		 */
		SortedMap<Integer, String> groups = PcaUtils.getIndexsFirstOccurrence(tableData.getSamples());
		Iterator<Entry<Integer, String>> iterator = groups.entrySet().iterator();
		if(iterator.hasNext()) {
			Entry<Integer, String> entry = iterator.next();
			int pos0 = entry.getKey();
			String name0 = entry.getValue();
			while(iterator.hasNext()) {
				entry = iterator.next();
				int pos1 = entry.getKey();
				String name1 = entry.getValue();
				if(name0 != null) {
					int[] ar = columnInOneGroup.apply(pos0 + TableProvider.NUMER_OF_DESCRIPTION_COLUMN, pos1 + TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
					columnGroupHeaderLayer.addColumnsIndexesToGroup(name0, ar);
				}
				name0 = name1;
				pos0 = pos1;
			}
			if(name0 != null) {
				int[] ar = columnInOneGroup.apply(pos0 + TableProvider.NUMER_OF_DESCRIPTION_COLUMN, tableData.getSamples().size() + TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
				columnGroupHeaderLayer.addColumnsIndexesToGroup(name0, ar);
			}
		}
	}

	private void hideCompoundColumn() {

		boolean isEmpty = !tableData.getVariables().stream().map(r -> r.getDescription()).anyMatch(s -> s != null && !s.isEmpty());
		boolean isHidden = columnHideShowLayer.isColumnIndexHidden(TableProvider.COLUMN_INDEX_PEAK_NAMES);
		List<Integer> peakNamesColumn = new ArrayList<>();
		peakNamesColumn.add(TableProvider.COLUMN_INDEX_PEAK_NAMES);
		if(isEmpty) {
			if(!isHidden) {
				columnHideShowLayer.hideColumnPositions(peakNamesColumn);
			}
		} else {
			if(isHidden) {
				columnHideShowLayer.showColumnIndexes(peakNamesColumn);
			}
		}
	}

	public void refreshTable() {

		natTable.refresh();
	}

	public void update(ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples) {

		tableData.update(samples);
		sortModel.update();
		natTable.update();
		natTable.refresh();
		generateGroup();
		hideCompoundColumn();
	}
}
