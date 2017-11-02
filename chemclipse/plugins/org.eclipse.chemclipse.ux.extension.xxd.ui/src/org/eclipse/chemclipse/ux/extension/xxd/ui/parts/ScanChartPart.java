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

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ScanChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IScanUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSelectedScan;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.eavp.service.swtchart.barcharts.BarSeriesData;
import org.eclipse.eavp.service.swtchart.barcharts.IBarSeriesData;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ScanChartPart extends AbstractScanUpdateSupport implements IScanUpdateSupport {

	private Composite toolbarSettings;
	private ScanChart scanChart;

	@Inject
	public ScanChartPart(Composite parent, MPart part) {
		super(part);
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateScan(getScan());
	}

	@Override
	public void updateScan(IScan scan) {

		if(scan != null) {
			List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
			ISeriesData seriesData = getSeriesData(scan);
			IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
			barSeriesDataList.add(barSeriesData);
			//
			if(scan instanceof IScanMSD) {
				scanChart.setDataType(DataType.MSD);
			} else if(scan instanceof IScanCSD) {
				scanChart.setDataType(DataType.CSD);
			} else if(scan instanceof IScanWSD) {
				scanChart.setDataType(DataType.WSD);
			}
			scanChart.addSeriesData(barSeriesDataList);
		} else {
			scanChart.deleteSeries();
		}
	}

	private ISeriesData getSeriesData(IScan scan) {

		double[] xSeries;
		double[] ySeries;
		String id = "Scan " + scan.getScanNumber();
		//
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			List<IIon> ions = scanMSD.getIons();
			int size = ions.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IIon ion : ions) {
				xSeries[index] = ion.getIon();
				ySeries[index] = ion.getAbundance();
				index++;
			}
		} else if(scan instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)scan;
			xSeries = new double[]{scanCSD.getRetentionTime()};
			ySeries = new double[]{scanCSD.getTotalSignal()};
		} else if(scan instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)scan;
			List<IScanSignalWSD> scanSignalsWSD = scanWSD.getScanSignals();
			int size = scanSignalsWSD.size();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IScanSignalWSD scanSignalWSD : scanSignalsWSD) {
				xSeries[index] = scanSignalWSD.getWavelength();
				ySeries[index] = scanSignalWSD.getAbundance();
				index++;
			}
		} else {
			xSeries = new double[0];
			ySeries = new double[0];
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
		PartSupport.setCompositeVisibility(toolbarSettings, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonToggleToolbarSettings(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
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

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSettings);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_INACTIVE, IApplicationImage.SIZE_16x16));
				}
			}
		});
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				scanChart.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				//
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageSelectedScan();
				preferencePage.setTitle("Scan Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						//
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
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

		scanChart = new ScanChart(parent, SWT.BORDER);
		scanChart.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
