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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class NormalizationDataTables {

	private Table tableCentering;
	private Table tableScaling;
	private Table tableTransformation;

	public NormalizationDataTables(Composite composite, Object layoutData) {
		initialize(composite, layoutData);
	}

	public String getCentering() {

		return (String)tableCentering.getData();
	}

	public String getScaling() {

		return (String)tableScaling.getData();
	}

	public String getTransformation() {

		return (String)tableTransformation.getData();
	}

	void initialize(Composite composite, Object layoutData) {

		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(layoutData);
		parent.setLayout(new GridLayout(1, true));
		tableTransformation = tableDataTransformation(parent);
		tableTransformation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableCentering = tableDataCentering(parent);
		tableCentering.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableScaling = tableDataScaling(parent);
		tableScaling.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private TableItem setTableRow(Table table, String[] columnData, Object data) {

		return setTableRow(table, columnData, data, false);
	}

	private TableItem setTableRow(Table table, String[] columnData, Object data, boolean isSelected) {

		TableItem item = new TableItem(table, SWT.NONE);
		TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		Button button = new Button(table, SWT.RADIO);
		button.addListener(SWT.Selection, e -> {
			if(button.getSelection()) {
				table.setData(data);
			}
		});
		editor.setEditor(button, item, 0);
		for(int i = 0; i < columnData.length; i++) {
			item.setText(i + 1, columnData[i]);
		}
		item.setData(data);
		if(isSelected) {
			button.setSelection(isSelected);
			table.setData(data);
		}
		return item;
	}

	private Table tableDataCentering(Composite parent) {

		Table table = new Table(parent, SWT.BORDER | SWT.HIDE_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {"", "Type"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		String[] centering = new String[]{"Without certering"};
		setTableRow(table, centering, PcaNormalizationData.CENTERING_NONE);
		centering = new String[]{"Mean certering"};
		setTableRow(table, centering, PcaNormalizationData.CENTERING_MEAN, true);
		centering = new String[]{"Meadin cetering"};
		setTableRow(table, centering, PcaNormalizationData.CENTERING_MEDIAN);
		for(int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
		return table;
	}

	private Table tableDataScaling(Composite parent) {

		Table table = new Table(parent, SWT.BORDER | SWT.HIDE_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {"", "Type"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		String[] scaling = new String[]{"Without Scaling"};
		setTableRow(table, scaling, PcaNormalizationData.SCALING_NONE);
		scaling = new String[]{"Autoscaling"};
		setTableRow(table, scaling, PcaNormalizationData.SCALING_AUTO, true);
		scaling = new String[]{"Range scaling"};
		setTableRow(table, scaling, PcaNormalizationData.SCALING_RANGE);
		scaling = new String[]{"Pareto scaling"};
		setTableRow(table, scaling, PcaNormalizationData.SCALING_PARETO);
		scaling = new String[]{"Vast scaling"};
		setTableRow(table, scaling, PcaNormalizationData.SCALING_VAST);
		scaling = new String[]{"Level scaling"};
		setTableRow(table, scaling, PcaNormalizationData.SCALING_LEVEL);
		scaling = new String[]{"Maximum scaling"};
		setTableRow(table, scaling, PcaNormalizationData.SCALING_MAXIMUM);
		for(int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
		return table;
	}

	private Table tableDataTransformation(Composite parent) {

		Table table = new Table(parent, SWT.BORDER | SWT.HIDE_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {"", "Type"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		String[] transformation = new String[]{"Without transformation"};
		setTableRow(table, transformation, PcaNormalizationData.TRANSFORMATION_NONE, true);
		transformation = new String[]{"Log transformation"};
		setTableRow(table, transformation, PcaNormalizationData.TRANSFORMATION_LOG10);
		transformation = new String[]{"Power transformationn"};
		setTableRow(table, transformation, PcaNormalizationData.TRANSFORMATION_POWER);
		for(int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
		return table;
	}
}
