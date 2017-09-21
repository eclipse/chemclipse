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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.ISeries;
import org.swtchart.LineStyle;

public class ChromatogramOverlayPart extends AbstractMeasurementEditorPartSupport {

	@Inject
	private EPartService partService;
	private ChromatogramChart chromatogramChart;
	private IColorScheme colorScheme;
	//
	private static final String OVERLAY_TYPE_TIC = "TIC";
	private static final String OVERLAY_TYPE_BPC = "BPC";
	private static final String OVERLAY_TYPE_CONCATENATOR = "_";
	//
	private static final String SELECTED_SERIES_NONE = "None";
	//
	private String[] overlayTypes;
	private Combo comboOverlayType;
	private Combo comboSelectedSeries;

	public ChromatogramOverlayPart() {
		colorScheme = Colors.getColorScheme(Colors.COLOR_SCHEME_PUBLICATION);
		overlayTypes = new String[]{//
				OVERLAY_TYPE_TIC, //
				OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_BPC//
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		createButtonsToolbar(parent);
		createChromatogramChart(parent);
	}

	private void createButtonsToolbar(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, false));
		//
		Composite compositeLeft = new Composite(composite, SWT.NONE);
		GridData gridDataLeft = new GridData(GridData.FILL_HORIZONTAL);
		gridDataLeft.horizontalAlignment = SWT.BEGINNING;
		gridDataLeft.grabExcessHorizontalSpace = true;
		compositeLeft.setLayoutData(gridDataLeft);
		compositeLeft.setLayout(new GridLayout(2, false));
		//
		Composite compositeRight = new Composite(composite, SWT.NONE);
		GridData gridDataRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataRight.horizontalAlignment = SWT.END;
		compositeRight.setLayoutData(gridDataRight);
		compositeRight.setLayout(new GridLayout(2, false));
		//
		createDisplayTypeCombo(compositeLeft);
		createHighlightSeriesCombo(compositeLeft);
		//
		createResetButton(compositeRight);
		createSettingsButton(compositeRight);
	}

	private void createDisplayTypeCombo(Composite parent) {

		comboOverlayType = new Combo(parent, SWT.PUSH);
		comboOverlayType.setToolTipText("Seelect the overlay type");
		comboOverlayType.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 350;
		gridData.grabExcessHorizontalSpace = true;
		comboOverlayType.setLayoutData(gridData);
		comboOverlayType.setItems(overlayTypes);
		comboOverlayType.select(0);
		comboOverlayType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
	}

	private void createHighlightSeriesCombo(Composite parent) {

		comboSelectedSeries = new Combo(parent, SWT.PUSH);
		comboSelectedSeries.setToolTipText("Highlight the selected series");
		comboSelectedSeries.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 350;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedSeries.setLayoutData(gridData);
		comboSelectedSeries.setItems(new String[]{SELECTED_SERIES_NONE});
		comboSelectedSeries.select(0);
		comboSelectedSeries.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

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

				colorScheme.reset();
				chromatogramChart.deleteSeries();
				refreshUpdateOverlayChart();
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

				IPreferencePage preferencePage = new PreferencePage();
				preferencePage.setTitle("Overlay Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applyOverlaySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void applyOverlaySettings() {

		comboOverlayType.select(0);
		comboSelectedSeries.select(0);
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.NONE);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chromatogramChart.applySettings(chartSettings);
	}

	@Focus
	public void setFocus() {

		refreshUpdateOverlayChart();
	}

	private void refreshUpdateOverlayChart() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(partService);
		Set<String> selectedSeriesIds = new HashSet<String>();
		//
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.suspendUpdate(true);
		//
		for(int i = 0; i < chromatogramSelections.size(); i++) {
			IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramName = chromatogram.getName() + "-" + i;
			/*
			 * Which series shall be displayed?
			 */
			Color color = colorScheme.getColor();
			colorScheme.incrementColor();
			//
			String[] overlayTypes = comboOverlayType.getText().trim().split(OVERLAY_TYPE_CONCATENATOR);
			for(String overlayType : overlayTypes) {
				String seriesId = chromatogramName + "_(" + overlayType + ")";
				if(!baseChart.isSeriesContained(seriesId)) {
					//
					addDataSeries(chromatogram, seriesId, overlayType, color);
					selectedSeriesIds.add(seriesId);
				}
			}
		}
		/*
		 * Delete non-selected series.
		 */
		for(ISeries series : baseChart.getSeriesSet().getSeries()) {
			String seriesId = series.getId();
			if(!selectedSeriesIds.contains(seriesId)) {
				baseChart.deleteSeries(seriesId);
			}
		}
		//
		String[] items = new String[selectedSeriesIds.size() + 1];
		items[0] = SELECTED_SERIES_NONE;
		int index = 1;
		for(String seriesId : selectedSeriesIds) {
			items[index++] = seriesId;
		}
		comboSelectedSeries.setItems(items);
		comboSelectedSeries.select(0);
		//
		baseChart.suspendUpdate(true);
		chromatogramChart.redraw();
	}

	private void addDataSeries(IChromatogram chromatogram, String seriesId, String overlayType, Color color) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		double[] xSeries = new double[chromatogram.getNumberOfScans()];
		double[] ySeries = new double[chromatogram.getNumberOfScans()];
		/*
		 * Get the line style.
		 */
		LineStyle lineStyle;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			lineStyle = LineStyle.SOLID;
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
			lineStyle = LineStyle.DASH;
		} else {
			lineStyle = LineStyle.DOT;
		}
		//
		int index = 0;
		for(IScan scan : chromatogram.getScans()) {
			xSeries[index] = scan.getRetentionTime();
			/*
			 * Get the intensity.
			 */
			if(overlayType.equals(OVERLAY_TYPE_TIC)) {
				/*
				 * TIC
				 */
				ySeries[index] = scan.getTotalSignal();
			} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
				/*
				 * BPC
				 */
				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					IIon ion = scanMSD.getHighestAbundance();
					if(ion != null) {
						ySeries[index] = ion.getAbundance();
					}
				}
			}
			index++;
		}
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setLineColor(color);
		lineSerieSettings.setLineStyle(lineStyle);
		lineSerieSettings.setEnableArea(false);
		lineSeriesDataList.add(lineSeriesData);
		chromatogramChart.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
	}
}
