/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - restore initial implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.nmr.model.core.SpectrumMeasurement;
import org.eclipse.chemclipse.nmr.model.core.SpectrumSignal;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartNMR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedNMROverlayUI {

	private enum Mode {
		OVERLAY, STACKED
	}

	private ChartNMR chartNMR;
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	//
	private List<IDataNMRSelection> dataNMRSelections = new ArrayList<>();
	private EPartService partservice;
	private IColorScheme colorSchemeNormal;
	private IPreferenceStore preferenceStore;
	private Mode mode = Mode.OVERLAY;

	public ExtendedNMROverlayUI(Composite parent, EPartService partservice, IPreferenceStore preferenceStore) {
		this.partservice = partservice;
		this.preferenceStore = preferenceStore;
		if(preferenceStore != null) {
			colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY));
		} else {
			colorSchemeNormal = Colors.getColorScheme(Colors.COLOR_SCHEME_RED);
		}
		initialize(parent);
	}

	public void update() {

		dataNMRSelections = editorUpdateSupport.getDataNMRSelections(partservice);
		refreshUpdateOverlayChart();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createOverlayChart(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createModeButtons(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createModeButtons(Composite parent) {

		{
			Button button = new Button(parent, SWT.PUSH);
			button.setToolTipText("Stacked Mode");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY_MIRRORED, IApplicationImage.SIZE_16x16));
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					if(mode != Mode.STACKED) {
						mode = Mode.STACKED;
						refreshUpdateOverlayChart();
					}
				}
			});
		}
		{
			Button button = new Button(parent, SWT.PUSH);
			button.setToolTipText("Overlay Mode");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY, IApplicationImage.SIZE_16x16));
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					if(mode != Mode.OVERLAY) {
						mode = Mode.OVERLAY;
						refreshUpdateOverlayChart();
					}
				}
			});
		}
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartNMR.toggleSeriesLegendVisibility();
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

				applyOverlaySettings();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		if(preferenceStore == null) {
			return;
		}
		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageOverlay = new PreferencePageOverlay();
				preferencePageOverlay.setTitle("Overlay Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageOverlay));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.setPreferenceStore(preferenceStore);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applyOverlaySettings();
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void createOverlayChart(Composite parent) {

		chartNMR = new ChartNMR(parent, SWT.BORDER);
		chartNMR.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void applyOverlaySettings() {

	}

	private void refreshUpdateOverlayChart() {

		chartNMR.deleteSeries();
		if(dataNMRSelections.size() > 0) {
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			int i = 1;
			Color color = colorSchemeNormal.getColor();
			//
			double yOffset = 0;
			for(IDataNMRSelection dataNMRSelection : dataNMRSelections) {
				ILineSeriesData lineSeriesData = getLineSeriesData(dataNMRSelection, "NMR_" + i++, yOffset);
				if(lineSeriesData != null) {
					ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
					lineSeriesSettings.setLineColor(color);
					lineSeriesSettings.setEnableArea(false);
					//
					lineSeriesDataList.add(lineSeriesData);
					color = colorSchemeNormal.getNextColor();
					if(mode == Mode.STACKED) {
						yOffset = getMax(lineSeriesData.getSeriesData()) * 1.1d;
					}
				}
			}
			//
			chartNMR.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
		}
	}

	private double getMax(ISeriesData seriesData) {

		double max = 0;
		double[] ySeries = seriesData.getYSeries();
		for(double y : ySeries) {
			if(y > max) {
				max = y;
			}
		}
		return max;
	}

	private ILineSeriesData getLineSeriesData(IDataNMRSelection dataNMRSelection, String id, double yOffset) {

		ISeriesData seriesData = getSeriesDataProcessed(dataNMRSelection, id, yOffset);
		if(seriesData == null) {
			return null;
		}
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		return lineSeriesData;
	}

	private ISeriesData getSeriesDataProcessed(IDataNMRSelection dataNMRSelection, String id, double yOffset) {

		IComplexSignalMeasurement<?> measurement = dataNMRSelection.getMeasurement();
		if(measurement instanceof SpectrumMeasurement) {
			SpectrumMeasurement spectrum = (SpectrumMeasurement)measurement;
			Collection<? extends SpectrumSignal> signals = spectrum.getSignals();
			return ChartNMR.createSignalSeries(id, signals, true, yOffset);
		}
		return null;
	}
}
