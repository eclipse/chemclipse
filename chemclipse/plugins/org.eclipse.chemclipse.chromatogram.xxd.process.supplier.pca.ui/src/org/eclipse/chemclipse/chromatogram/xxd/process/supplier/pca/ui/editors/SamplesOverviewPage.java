/*******************************************************************************
 * Copyright (c) 2013, 2017 Jan Holy
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SamplesOverviewPage {

	private Label countSelectedSamples;
	private CheckboxTableViewer checkboxTableViewer;
	private Map<String, Color> mapGroupColor = new HashMap<>();
	private PcaEditor pcaEditor;
	private List<ISample> samples;

	public SamplesOverviewPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder);
	}

	private void createColumns() {

		String[] titles = {"Filename", "Group"};
		int[] bounds = {100, 100};
		// first column is for the first name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				ISample sample = (ISample)element;
				return sample.getName();
			}
		});
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				ISample sample = (ISample)cell.getElement();
				String text = sample.getGroupName() != null ? sample.getGroupName() : "";
				cell.setImage(getGroupColor(sample.getGroupName()));
				cell.setText(text);
			}
		});
		col.setEditingSupport(new EditingSupport(checkboxTableViewer) {

			private TextCellEditor editor = new TextCellEditor(checkboxTableViewer.getTable());

			@Override
			protected boolean canEdit(Object element) {

				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {

				return editor;
			}

			@Override
			protected Object getValue(Object element) {

				ISample sample = (ISample)element;
				String groupName = sample.getGroupName();
				if(groupName == null) {
					return "";
				} else {
					return groupName;
				}
			}

			@Override
			protected void setValue(Object element, Object value) {

				ISample sample = (ISample)element;
				String groupName = (String)value;
				groupName = groupName.trim();
				if(!groupName.isEmpty()) {
					sample.setGroupName(groupName);
				} else {
					sample.setGroupName(null);
				}
				update();
			}
		});
	}

	private void createInputFilesSection(Composite parent) {

		Composite client = new Composite(parent, SWT.None);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Creates the table and the action buttons.
		 */
		createTable(client);
		createLabels(client);
		client.pack();
	}

	/**
	 * Creates the file count labels.
	 *
	 * @param client
	 */
	private void createLabels(Composite client) {

		countSelectedSamples = new Label(client, SWT.NONE);
		countSelectedSamples.setText("Selected: 0 from 0 samples.");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		countSelectedSamples.setLayoutData(gridData);
	}

	private void createTable(Composite client) {

		GridData gridData;
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		Table table = new Table(client, SWT.CHECK | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gridData);
		checkboxTableViewer = new CheckboxTableViewer(table);
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		checkboxTableViewer.addCheckStateListener(event -> {
			((ISample)event.getElement()).setSelected(event.getChecked());
			redrawSamplesSelectedCount();
		});
		createColumns();
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(checkboxTableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private Image getGroupColor(String groupName) {

		Color color = mapGroupColor.get(groupName);
		int len = 16;
		Image image = new Image(Display.getCurrent(), len, len);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(0, 0, len, len);
		gc.dispose();
		return image;
	}

	private void initialize(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Samples Overview");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Create the section.
		 */
		createInputFilesSection(composite);
		//
		tabItem.setControl(composite);
	}

	private void redrawSamplesSelectedCount() {

		long selected = samples.stream().filter(ISample::isSelected).count();
		countSelectedSamples.setText("Selected: " + selected + " from " + samples.size() + " samples");
	}

	public void update() {

		Optional<IPcaResults> pcaResults = pcaEditor.getPcaResults();
		if(pcaResults.isPresent()) {
			samples = pcaResults.get().getSampleList();
			updateColorMap();
			checkboxTableViewer.setInput(samples);
			updateTable();
			pcaResults.get().getSampleList().forEach(s -> checkboxTableViewer.setChecked(s, s.isSelected()));
			redrawSamplesSelectedCount();
		}
	}

	private void updateColorMap() {

		mapGroupColor = PcaColorGroup.getColorSWT(PcaUtils.getGroupNames(samples));
	}

	private void updateTable() {

		updateColorMap();
		for(TableColumn column : checkboxTableViewer.getTable().getColumns()) {
			column.pack();
		}
	}
}
