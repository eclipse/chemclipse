/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.eavp.service.swtchart.barcharts.BarSeriesData;
import org.eclipse.eavp.service.swtchart.barcharts.IBarSeriesData;
import org.eclipse.eavp.service.swtchart.barcharts.IBarSeriesSettings;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.customcharts.MassSpectrumChart;
import org.eclipse.eavp.service.swtchart.customcharts.MassSpectrumChart.LabelOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class SelectedScansPart extends AbstractScanUpdateSupport implements IScanUpdateSupport {

	private Composite toolbarSettings;
	private MassSpectrumChart massSpectrumChart;

	@Inject
	public SelectedScansPart(Composite parent, MPart part) {
		super(part);
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateScan(getScan());
		massSpectrumChart.setFocus();
	}

	@Override
	public void updateScan(IScan scan) {

		massSpectrumChart.deleteSeries();
		if(scan instanceof IScanMSD) {
			List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
			ISeriesData seriesData = getSeriesData((IScanMSD)scan);
			IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
			IBarSeriesSettings barSeriesSettings = barSeriesData.getBarSeriesSettings();
			barSeriesSettings.setDescription("");
			barSeriesDataList.add(barSeriesData);
			massSpectrumChart.addSeriesData(barSeriesDataList);
		}
	}

	private ISeriesData getSeriesData(IScanMSD scanMSD) {

		String id = "Scan MSD";
		List<IIon> ions = scanMSD.getIons();
		int size = ions.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(IIon ion : ions) {
			xSeries[index] = ion.getIon();
			ySeries[index] = ion.getAbundance();
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarSettings = createToolbarSettings(parent);
		createScanChart(parent);
		//
		PartSupport.setToolbarVisibility(toolbarSettings, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(1, false));
		//
		createButtonToggleToolbarSettings(composite);
	}

	private Composite createToolbarSettings(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
		//
		createButton(composite);
		//
		return composite;
	}

	private void createButtonToggleToolbarSettings(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle settings toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_INACTIVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleToolbarVisibility(toolbarSettings);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_INACTIVE, IApplicationImage.SIZE_16x16));
				}
			}
		});
	}

	private void createButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Tooltip");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
	}

	private void createScanChart(Composite parent) {

		massSpectrumChart = new MassSpectrumChart(parent, SWT.BORDER);
		massSpectrumChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = massSpectrumChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMaxY(0.2d);
		massSpectrumChart.applySettings(chartSettings);
		/*
		 * Additional settings
		 */
		massSpectrumChart.setNumberOfHighestIntensitiesToLabel(5);
		massSpectrumChart.setLabelOption(LabelOption.NOMIMAL);
		massSpectrumChart.setCustomLabels(null);
	}
}
