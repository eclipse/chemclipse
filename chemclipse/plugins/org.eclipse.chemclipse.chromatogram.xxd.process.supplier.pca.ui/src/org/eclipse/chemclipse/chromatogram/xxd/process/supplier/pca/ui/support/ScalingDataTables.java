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

import java.net.URL;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaScalingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaScalingData.Centering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaScalingData.Scaling;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaScalingData.Transformation;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ScalingDataTables {

	private final int COLUMNS_WIDTH = 500;
	private Button enableScaling;
	private PcaScalingData pcaScalingData;
	private Table tableCentering;
	private Table tableScaling;
	private Table tableTransformation;

	public ScalingDataTables(Composite composite, Object layoutData) {
		this(composite, layoutData, new PcaScalingData(false));
	}

	public ScalingDataTables(Composite composite, Object layoutData, PcaScalingData pcaNormalizationData) {
		this.pcaScalingData = pcaNormalizationData;
		initialize(composite, layoutData);
	}

	private Table createColumnCentering(Table table, Centering selectedCentering) {

		table.clearAll();
		table.removeAll();
		String[] description = new String[]{};
		setTableRow(table, "Mean centering", "center_mean.jpg", //
				description, Centering.MEAN, Centering.MEAN.equals(selectedCentering));
		description = new String[]{};
		setTableRow(table, "Median centering", "center_median.jpg", //
				description, Centering.MEDIAN, Centering.MEDIAN.equals(selectedCentering));
		table.getColumn(0).setWidth(COLUMNS_WIDTH);
		return table;
	}

	private void createColumnsDataNormalization(Table table, Scaling selectedNormalization) {

		table.clearAll();
		table.removeAll();
		String[] description = new String[]{};
		description = new String[]{};
		setTableRow(table, "Autoscaling", "norm_scal_auto.jpg", //
				description, Scaling.SCALING_AUTO, Scaling.SCALING_AUTO.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Range scaling", "norm_scal_range.jpg", //
				description, Scaling.SCALING_RANGE, Scaling.SCALING_RANGE.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Pareto scaling", "norm_scal_pareto.jpg", //
				description, Scaling.SCALING_PARETO, Scaling.SCALING_PARETO.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Vast scaling", "norm_scal_vast.jpg", //
				description, Scaling.SCALING_VAST, Scaling.SCALING_VAST.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Level scaling", "norm_scal_level.jpg", //
				description, Scaling.SCALING_LEVEL, Scaling.SCALING_LEVEL.equals(selectedNormalization));
		table.getColumn(0).setWidth(COLUMNS_WIDTH);
	}

	private Table createColumnsDataTransformation(Table table, Transformation selecetedTransformation) {

		table.clearAll();
		table.removeAll();
		String[] description = new String[]{};
		setTableRow(table, "Without transformation", "trans_none.jpg", //
				description, Transformation.NONE, Transformation.NONE.equals(selecetedTransformation));
		description = new String[]{};
		setTableRow(table, "Log transformation", "trans_log.jpg", //
				description, Transformation.LOG10, Transformation.LOG10.equals(selecetedTransformation));
		description = new String[]{};
		setTableRow(table, "Power transformation", "trans_power.jpg", //
				description, Transformation.POWER, Transformation.POWER.equals(selecetedTransformation));
		table.getColumn(0).setWidth(COLUMNS_WIDTH);
		return table;
	}

	public PcaScalingData getPcaScalingData() {

		return pcaScalingData;
	}

	void initialize(Composite parent, Object layoutData) {

		Group group = new Group(parent, SWT.None);
		group.setText("Scaling");
		group.setLayout(new FillLayout());
		group.setLayoutData(layoutData);
		ScrolledComposite scrolledComposite = new ScrolledComposite(group, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comoposite = new Composite(scrolledComposite, SWT.NONE);
		comoposite.setLayout(new GridLayout(1, true));
		enableScaling = new Button(comoposite, SWT.CHECK);
		enableScaling.setText("Scaling data");
		enableScaling.addListener(SWT.Selection, e -> {
			boolean selection = ((Button)e.widget).getSelection();
			pcaScalingData.setEnableModificationData(selection);
			setEnableTable(selection);
		});
		enableScaling.setSelection(pcaScalingData.isEnableModificationData());
		tableTransformation = tableDataTransformation(comoposite);
		tableTransformation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableCentering = tableDataCentering(comoposite);
		tableCentering.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableScaling = tableDataNormalization(comoposite);
		tableScaling.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scrolledComposite.setContent(comoposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(comoposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setEnableTable(pcaScalingData.isEnableModificationData());
	}

	public void setEnable(boolean enabled) {

		setEnableTable(enabled);
		enableScaling.setEnabled(enabled);
	}

	private void setEnableTable(boolean enabled) {

		if(enableScaling.getSelection()) {
			tableCentering.setEnabled(enabled);
			tableScaling.setEnabled(enabled);
			tableTransformation.setEnabled(enabled);
		} else {
			tableCentering.setEnabled(false);
			tableScaling.setEnabled(false);
			tableTransformation.setEnabled(false);
		}
	}

	public void setPcaNormalizationData(PcaScalingData pcaScalingData) {

		this.pcaScalingData = pcaScalingData;
	}

	private TableItem setTableRow(Table table, String name, String mathFormula, String[] description, Object data, boolean isSelected) {

		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, name);
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		final URL fullPathString = FileLocator.find(bundle, new Path("img/norm_tab/" + mathFormula), null);
		ImageDescriptor imageDesc = ImageDescriptor.createFromURL(fullPathString);
		Image image = imageDesc.createImage();
		item.setImage(0, image);
		for(int i = 0; i < description.length; i++) {
			item.setText(i + 1, description[i]);
		}
		item.setData(data);
		if(isSelected) {
			table.setData(data);
			item.setChecked(true);
		}
		return item;
	}

	private Table tableDataCentering(Composite parent) {

		Table table = new Table(parent, SWT.BORDER | SWT.CHECK);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {"Select Centering Type"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		createColumnCentering(table, pcaScalingData.getCentering());
		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				if(event.detail != SWT.CHECK) {
					return;
				}
				for(int i = 0; i < table.getItemCount(); i++) {
					table.getItem(i).setChecked(false);
				}
				TableItem item = (TableItem)event.item;
				item.setChecked(true);
				pcaScalingData.setCentering((Centering)item.getData());
			}
		});
		return table;
	}

	private Table tableDataNormalization(Composite parent) {

		Table table = new Table(parent, SWT.BORDER | SWT.CHECK);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {"Select Normalization type"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		createColumnsDataNormalization(table, pcaScalingData.getScalingType());
		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				if(event.detail != SWT.CHECK) {
					return;
				}
				for(int i = 0; i < table.getItemCount(); i++) {
					table.getItem(i).setChecked(false);
				}
				TableItem item = (TableItem)event.item;
				item.setChecked(true);
				pcaScalingData.setScalingType((Scaling)item.getData());
			}
		});
		return table;
	}

	private Table tableDataTransformation(Composite parent) {

		Table table = new Table(parent, SWT.BORDER | SWT.CHECK);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {"Select Transformation Type"};
		for(int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		createColumnsDataTransformation(table, pcaScalingData.getTransformationType());
		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				if(event.detail != SWT.CHECK) {
					return;
				}
				for(int i = 0; i < table.getItemCount(); i++) {
					table.getItem(i).setChecked(false);
				}
				TableItem item = (TableItem)event.item;
				item.setChecked(true);
				pcaScalingData.setTransformationType((Transformation)item.getData());
			}
		});
		return table;
	}

	public void update() {

		boolean modificationData = pcaScalingData.isEnableModificationData();
		enableScaling.setSelection(modificationData);
		setEnableTable(modificationData);
		createColumnCentering(tableCentering, pcaScalingData.getCentering());
		createColumnsDataNormalization(tableScaling, pcaScalingData.getScalingType());
		createColumnsDataTransformation(tableTransformation, pcaScalingData.getTransformationType());
	}
}
