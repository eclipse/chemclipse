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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
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
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.AbstractHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class PeakListNatTable {

	private ColumnGroupHeaderLayer columnGroupHeaderLayer;
	private NatTable natTable;
	private SortModel sortModel;
	private TableData tableData;

	public PeakListNatTable(PcaEditor pcaEditor, Composite parent, FormToolkit formToolkit) {
		tableData = new TableData(pcaEditor);
		createPeakListIntensityTableSection(parent);
	}

	/**
	 * Create peak List intensity table
	 */
	private void createPeakListIntensityTableSection(Composite parent) {

		sortModel = new SortModel(tableData);
		final PcaResulDataProvider dataProvider = new PcaResulDataProvider(tableData, sortModel);
		final DataLayer bodyDataLayer = new DataLayer(dataProvider);
		bodyDataLayer.setConfigLabelAccumulator(new PcaResultLabelProvider());
		final RowHideShowLayer rowHideShowLayer = new RowHideShowLayer(bodyDataLayer);
		final ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(rowHideShowLayer);
		ColumnGroupModel columnGroupModel = new ColumnGroupModel();
		ColumnGroupExpandCollapseLayer columnGroupExpandCollapseLayer = new ColumnGroupExpandCollapseLayer(columnHideShowLayer, columnGroupModel, columnGroupModel);
		final SelectionLayer selectionLayer = new SelectionLayer(columnGroupExpandCollapseLayer);
		final ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
		final FreezeLayer freezeLayer = new FreezeLayer(selectionLayer);
		final CompositeFreezeLayer compositeFreezeLayer = new CompositeFreezeLayer(freezeLayer, viewportLayer, selectionLayer);
		/*
		 * build the column header layer
		 */
		IDataProvider columnHeaderDataProvider = new PcaResulHeaderProvider(tableData);
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, compositeFreezeLayer, selectionLayer);
		SortHeaderLayer<?> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer, sortModel);
		columnGroupHeaderLayer = new ColumnGroupHeaderLayer(sortHeaderLayer, selectionLayer, columnGroupModel);
		/*
		 * build the row header layer
		 */
		IDataProvider rowHeaderDataProvider = new PcaResultRowProvider(tableData);
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
		natTable.addConfiguration(new PcaResulRegistryConfiguration());
		natTable.configure();
		/*
		 * Freeze column dynamically
		 */
		columnHideShowLayer.addLayerListener(new ILayerListener() {

			@Override
			public void handleLayerEvent(ILayerEvent event) {

				/*
				 * freeze first column, this column contains retention times
				 */
				if(!columnHideShowLayer.isColumnIndexHidden(AbstractPcaResulDataProvider.COLUMN_INDEX_RETENTION_TIMES)) {
					compositeFreezeLayer.doCommand(new FreezeColumnCommand(compositeFreezeLayer, 0, false, true));
				}
			}
		});
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
					int[] ar = columnInOneGroup.apply(pos0 + AbstractPcaResulDataProvider.NUMER_OF_DESCRIPTION_COLUMN, pos1 + AbstractPcaResulDataProvider.NUMER_OF_DESCRIPTION_COLUMN);
					columnGroupHeaderLayer.addColumnsIndexesToGroup(name0, ar);
				}
				name0 = name1;
				pos0 = pos1;
			}
			if(name0 != null) {
				int[] ar = columnInOneGroup.apply(pos0 + AbstractPcaResulDataProvider.NUMER_OF_DESCRIPTION_COLUMN, tableData.getSamples().size() + AbstractPcaResulDataProvider.NUMER_OF_DESCRIPTION_COLUMN);
				columnGroupHeaderLayer.addColumnsIndexesToGroup(name0, ar);
			}
		}
	}

	public NatTable getNatTable() {

		return natTable;
	}

	public void update() {

		tableData.update();
		sortModel.update();
		generateGroup();
		natTable.refresh();
	}
}
