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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData.Centering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData.Normalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData.Transformation;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class NormalizationDataTables {

	private final int COLUMNS_WIDTH = 500;
	private PcaNormalizationData pcaNormalizationData;
	private Table tableCentering;
	private Table tableScaling;
	private Table tableTransformation;

	public NormalizationDataTables(Composite composite, Object layoutData) {
		this(composite, layoutData, new PcaNormalizationData());
	}

	public NormalizationDataTables(Composite composite, Object layoutData, PcaNormalizationData pcaNormalizationData) {
		this.pcaNormalizationData = pcaNormalizationData;
		initialize(composite, layoutData);
	}

	private Table createColumnCentering(Table table, Centering selectedCentering) {

		table.clearAll();
		table.removeAll();
		String[] description = new String[]{};
		setTableRow(table, "Mean certering", "center_mean.jpg", //
				description, Centering.MEAN, Centering.MEAN.equals(selectedCentering));
		description = new String[]{};
		setTableRow(table, "Median cetering", "center_median.jpg", //
				description, Centering.MEDIAN, Centering.MEDIAN.equals(selectedCentering));
		table.getColumn(0).setWidth(COLUMNS_WIDTH);
		return table;
	}

	private void createColumnsDataNormalization(Table table, Normalization selectedNormalization) {

		table.clearAll();
		table.removeAll();
		String[] description = new String[]{};
		setTableRow(table, "Only Transformation or \n without transformation,\n if transformation is set as \n\"Without transformation\" ", "norm_trans.jpg", //
				description, Normalization.TRANSFORMING, Normalization.TRANSFORMING.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Only Centering", "norm_center.jpg", //
				description, Normalization.CENTERING, Normalization.CENTERING.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Autoscaling", "norm_scal_auto.jpg", //
				description, Normalization.SCALING_AUTO, Normalization.SCALING_AUTO.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Range scaling", "norm_scal_range.jpg", //
				description, Normalization.SCALING_RANGE, Normalization.SCALING_RANGE.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Pareto scaling", "norm_scal_pareto.jpg", //
				description, Normalization.SCALING_PARETO, Normalization.SCALING_PARETO.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Vast scaling", "norm_scal_vast.jpg", //
				description, Normalization.SCALING_VAST, Normalization.SCALING_VAST.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Level scaling", "norm_scal_level.jpg", //
				description, Normalization.SCALING_LEVEL, Normalization.SCALING_LEVEL.equals(selectedNormalization));
		description = new String[]{};
		setTableRow(table, "Maximum scaling", "norm_scal_max.jpg", //
				description, Normalization.SCALING_MAXIMUM, Normalization.SCALING_MAXIMUM.equals(selectedNormalization));
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

	public PcaNormalizationData getPcaNormalizationData() {

		return pcaNormalizationData;
	}

	void initialize(Composite parent, Object layoutData) {

		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(layoutData);
		Composite comoposite = new Composite(sc, SWT.NONE);
		comoposite.setLayout(new GridLayout(1, true));
		tableTransformation = tableDataTransformation(comoposite);
		tableTransformation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableCentering = tableDataCentering(comoposite);
		tableCentering.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableScaling = tableDataNormalization(comoposite);
		tableScaling.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sc.setContent(comoposite);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(comoposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void setPcaNormalizationData(PcaNormalizationData pcaNormalizationData) {

		this.pcaNormalizationData = pcaNormalizationData;
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
		createColumnCentering(table, pcaNormalizationData.getCentering());
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
				pcaNormalizationData.setCentering((Centering)item.getData());
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
		createColumnsDataNormalization(table, pcaNormalizationData.getNorlamalizationType());
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
				pcaNormalizationData.setNormalizationType((Normalization)item.getData());
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
		createColumnsDataTransformation(table, pcaNormalizationData.getTransformationType());
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
				pcaNormalizationData.setTransformationType((Transformation)item.getData());
			}
		});
		return table;
	}

	public void update() {

		createColumnCentering(tableCentering, pcaNormalizationData.getCentering());
		createColumnsDataNormalization(tableScaling, pcaNormalizationData.getNorlamalizationType());
		createColumnsDataTransformation(tableTransformation, pcaNormalizationData.getTransformationType());
	}
}
