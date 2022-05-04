/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for MALDI
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorStandaloneMassSpectrum;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.IRulerUpdateNotifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.MassSpectrumRulerChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.RulerEvent;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.ISeriesModificationListener;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;

public class ExtendedMassSpectrumOverlayUI extends Composite implements IExtendedPartUI {

	private static final String IMAGE_SHIFT = IApplicationImage.IMAGE_SHIFT;
	private static final String TOOLTIP_SHIFT = "the shift toolbar.";
	private Button buttonToolbarDataShift;
	private AtomicReference<DataShiftControllerUI> toolbarDataShift = new AtomicReference<>();
	private Label labelStatus;
	//
	private static final String IMAGE_RULER = IApplicationImage.IMAGE_RULER;
	private static final String TOOLTIP_RULER = "the ruler toolbar.";
	private Button buttonToolbarRulerDetails;
	private AtomicReference<RulerDetailsUI> toolbarRulerDetails = new AtomicReference<>();
	//
	private AtomicReference<MassSpectrumRulerChart> chartControl = new AtomicReference<>();
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	//
	private List<IScanMSD> scanSelections = new ArrayList<>();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IColorScheme colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_OVERLAY));

	@Inject
	public ExtendedMassSpectrumOverlayUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update() {

		scanSelections = editorUpdateSupport.getMassSpectrumSelections();
		refreshUpdateOverlayChart();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createRulerDetailsUI(this);
		createDataShiftControllerUI(this);
		createOverlayChart(this);
		//
		enableToolbar(toolbarDataShift, buttonToolbarDataShift, IMAGE_SHIFT, TOOLTIP_SHIFT, false);
		enableToolbar(toolbarRulerDetails, buttonToolbarRulerDetails, IMAGE_RULER, TOOLTIP_RULER, false);
		//
		toolbarDataShift.get().setScrollableChart(chartControl.get());
		toolbarRulerDetails.get().setScrollableChart(chartControl.get());
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		labelStatus = createLabelStatus(composite);
		createButtonToggleChartLegend(composite, chartControl, IMAGE_LEGEND);
		createResetButton(composite);
		createSettingsButton(composite);
		createNewOverlayPartButton(composite);
		//
		buttonToolbarDataShift = createButtonToggleToolbar(composite, toolbarDataShift, IMAGE_SHIFT, TOOLTIP_SHIFT);
		buttonToolbarRulerDetails = createButtonToggleToolbar(composite, toolbarRulerDetails, IMAGE_RULER, TOOLTIP_RULER);
	}

	private void createDataShiftControllerUI(Composite parent) {

		DataShiftControllerUI dataShiftControllerUI = new DataShiftControllerUI(parent, SWT.NONE);
		dataShiftControllerUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarDataShift.set(dataShiftControllerUI);
	}

	private void createRulerDetailsUI(Composite parent) {

		RulerDetailsUI rulerDetailsUI = new RulerDetailsUI(parent, SWT.NONE);
		rulerDetailsUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarRulerDetails.set(rulerDetailsUI);
	}

	private void createNewOverlayPartButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open a new Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PLUS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String bundle = Activator.getDefault().getBundle().getSymbolicName();
				String classPath = PartSupport.PART_OVERLAY_MASS_SPECTRUM;
				String name = "Mass Spectrum Overlay";
				createNewPart(bundle, classPath, name);
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

				applySettings();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageOverlay.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createOverlayChart(Composite parent) {

		MassSpectrumRulerChart chart = new MassSpectrumRulerChart(parent, SWT.BORDER);
		chart.setLayoutData(new GridData(GridData.FILL_BOTH));
		chartControl.set(chart);
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		chartSettings.setSupportDataShift(true);
		chartSettings.getRangeRestriction().setZeroY(false);
		chart.applySettings(chartSettings);
		//
		BaseChart baseChart = chart.getBaseChart();
		baseChart.addSeriesModificationListener(new ISeriesModificationListener() {

			@Override
			public void handleSeriesModificationEvent() {

				modifyDataStatusLabel();
			}
		});
		//
		chart.setRulerUpdateNotifier(new IRulerUpdateNotifier() {

			@Override
			public void update(RulerEvent rulerEvent) {

				toolbarRulerDetails.get().setInput(rulerEvent);
			}
		});
		//
		chartControl.set(chart);
	}

	private void applySettings() {

		refreshUpdateOverlayChart();
		toolbarDataShift.get().update();
		modifyDataStatusLabel();
	}

	private void refreshUpdateOverlayChart() {

		MassSpectrumRulerChart chart = chartControl.get();
		chart.deleteSeries();
		if(scanSelections.size() > 0) {
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			Color color = colorSchemeNormal.getColor();
			for(IScanMSD scanMSD : scanSelections) {
				/*
				 * Get the data.
				 */
				ILineSeriesData lineSeriesData = getLineSeriesData(scanMSD);
				if(lineSeriesData != null) {
					ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
					lineSeriesSettings.setLineColor(color);
					lineSeriesSettings.setEnableArea(false);
					lineSeriesDataList.add(lineSeriesData);
					color = colorSchemeNormal.getNextColor();
				}
			}
			chart.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
		}
	}

	private ILineSeriesData getLineSeriesData(IScanMSD scanMSD) {

		if(scanMSD instanceof IVendorStandaloneMassSpectrum) {
			IVendorStandaloneMassSpectrum massSpectrum = (IVendorStandaloneMassSpectrum)scanMSD;
			ILineSeriesData lineSeriesData = new LineSeriesData(getSeriesDataProcessed(scanMSD, massSpectrum.getName()));
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineColor(Colors.RED);
			lineSeriesSettings.setEnableArea(true);
			return lineSeriesData;
		}
		return null;
	}

	private ISeriesData getSeriesDataProcessed(IScanMSD scanMSD, String id) {

		double[] xSeries;
		double[] ySeries;
		//
		if(scanMSD != null) {
			int size = scanMSD.getNumberOfIons();
			xSeries = new double[size];
			ySeries = new double[size];
			int index = 0;
			for(IIon ion : scanMSD.getIons()) {
				xSeries[index] = ion.getIon();
				ySeries[index] = ion.getAbundance();
				index++;
			}
		} else {
			xSeries = new double[0];
			ySeries = new double[0];
		}
		//
		return new SeriesData(xSeries, ySeries, id);
	}

	private void modifyDataStatusLabel() {

		if(chartControl.get().getBaseChart().isDataShifted()) {
			labelStatus.setText("The displayed data is shifted.");
			labelStatus.setBackground(Colors.getColor(Colors.LIGHT_YELLOW));
		} else {
			labelStatus.setText("");
			labelStatus.setBackground(null);
		}
	}

	private Label createLabelStatus(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setToolTipText("Indicates whether the data has been modified or not.");
		label.setText("");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return label;
	}

	private void createNewPart(String bundle, String classPath, String name) {

		/*
		 * Services
		 */
		EModelService modelService = Activator.getDefault().getModelService();
		MApplication application = Activator.getDefault().getApplication();
		EPartService partService = Activator.getDefault().getPartService();
		//
		if(modelService != null && application != null && partService != null) {
			MPart part = MBasicFactory.INSTANCE.createPart();
			part.setLabel(name);
			part.setCloseable(true);
			part.setContributionURI("bundleclass://" + bundle + "/" + classPath);
			//
			MPartStack partStack = PartSupport.getPartStack(PartSupport.PARTSTACK_LEFT_CENTER, modelService, application);
			partStack.getChildren().add(part);
			PartSupport.showPart(part, partService);
		}
	}

	public void dispose() {

		chartControl.get().dispose();
		super.dispose();
	}
}