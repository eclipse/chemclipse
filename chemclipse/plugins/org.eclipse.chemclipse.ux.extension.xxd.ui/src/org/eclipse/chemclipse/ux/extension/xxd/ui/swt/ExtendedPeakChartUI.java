/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.SolverException;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeaks;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ExtendedPeakChartUI {

	private Composite toolbarSettings;
	private LineChart peakChart;
	private IPeak peak;

	@Inject
	public ExtendedPeakChartUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updatePeak();
	}

	public void update(IPeak peak) {

		this.peak = peak;
		updatePeak();
	}

	private void updatePeak() {

		peakChart.deleteSeries();
		if(peak != null) {
			ISeriesData seriesData;
			ILineSeriesData lineSeriesData;
			ILineSeriesSettings lineSeriesSettings;
			ILineSeriesSettings lineSeriesSettingsHighlight;
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			/*
			 * Peak
			 */
			seriesData = getPeakSeriesData(peak);
			lineSeriesData = new LineSeriesData(seriesData);
			lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setEnableArea(true);
			lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
			lineSeriesSettingsHighlight.setLineWidth(2);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Increasing Tangent
			 */
			// seriesData = getIncreasingInflectionPoints(peak, false, Sign.POSITIVE);
			// lineSeriesData = new LineSeriesData(seriesData);
			// lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			// lineSeriesSettings.setEnableArea(false);
			// lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
			// lineSeriesSettingsHighlight.setLineWidth(2);
			// lineSeriesDataList.add(lineSeriesData);
			//
			peakChart.addSeriesData(lineSeriesDataList);
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarSettings = createToolbarSettings(parent);
		createPeakChart(parent);
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSettings);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
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

				peakChart.toggleSeriesLegendVisibility();
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

				reset();
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

				IPreferencePage preferencePage = new PreferencePagePeaks();
				preferencePage.setTitle("Peak Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
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

	private void createPeakChart(Composite parent) {

		peakChart = new LineChart(parent, SWT.BORDER);
		peakChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = peakChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		peakChart.applySettings(chartSettings);
	}

	private void reset() {

		updatePeak();
	}

	private void applySettings() {

		updatePeak();
	}

	private ISeriesData getPeakSeriesData(IPeak peak) {

		String id = "Peak";
		IPeakModel peakModel = peak.getPeakModel();
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		int size = retentionTimes.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(int retentionTime : retentionTimes) {
			xSeries[index] = retentionTime;
			ySeries[index] = peakModel.getIntensity(retentionTime);
			index++;
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private ISeriesData getIncreasingInflectionPoints(IPeak peak, boolean includeBackground, Sign sign) {

		String id = "Increasing Tangent";
		double[] xSeries = new double[2];
		double[] ySeries = new double[2];
		//
		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			try {
				IPoint intersection;
				LinearEquation increasing = peakModel.getIncreasingInflectionPointEquation();
				LinearEquation decreasing = peakModel.getDecreasingInflectionPointEquation();
				LinearEquation baseline = peakModel.getPercentageHeightBaselineEquation(0.0f);
				double x;
				/*
				 * Where does the increasing tangent crosses the baseline.
				 */
				intersection = Equations.calculateIntersection(increasing, baseline);
				/*
				 * Take a look if the retention time (X) is lower than the peaks
				 * retention time.<br/> If yes, take the peaks start retention
				 * time, otherwise the values would be 0 by default.
				 */
				double startRetentionTime = peakModel.getStartRetentionTime();
				x = intersection.getX() < startRetentionTime ? startRetentionTime : intersection.getX();
				xSeries[0] = intersection.getX();
				if(includeBackground) {
					ySeries[0] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[0] = intersection.getY();
				}
				/*
				 * This is the highest point of the peak, given by the tangents.
				 */
				intersection = Equations.calculateIntersection(increasing, decreasing);
				/*
				 * Take a look if the retention time (X) is greater than the
				 * peaks retention time.<br/> If yes, take the peaks stop
				 * retention time, otherwise the values would be 0 by default.
				 */
				double stopRetentionTime = peakModel.getStopRetentionTime();
				x = intersection.getX() > stopRetentionTime ? stopRetentionTime : intersection.getX();
				xSeries[1] = intersection.getX();
				if(includeBackground) {
					ySeries[1] = intersection.getY() + peakModel.getBackgroundAbundance((int)x);
				} else {
					ySeries[1] = intersection.getY();
				}
			} catch(SolverException e) {
				//
			}
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}
}
