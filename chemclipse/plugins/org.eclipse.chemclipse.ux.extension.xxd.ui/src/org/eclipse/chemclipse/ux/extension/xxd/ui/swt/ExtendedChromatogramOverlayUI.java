/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support for configuration, zoom lock
 * Alexander Kerner - Generics
 * Matthias Mailänder - Add support for Max Plots
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.traces.NamedTrace;
import org.eclipse.chemclipse.model.traces.NamedTraces;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.validators.TraceValidator;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramRulerChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.IRulerUpdateNotifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.RulerEvent;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageNamedTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.Derivative;
import org.eclipse.chemclipse.ux.extension.xxd.ui.traces.NamedTracesUI;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.ISeriesModificationListener;
import org.eclipse.swtchart.extensions.core.ISeriesSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.RangeRestriction.ExtendType;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.core.SeriesMapper;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;
import org.eclipse.swtchart.extensions.preferences.PreferencePage;

public class ExtendedChromatogramOverlayUI extends Composite implements IExtendedPartUI {

	private static final String IMAGE_SHIFT = IApplicationImage.IMAGE_SHIFT;
	private static final String TOOLTIP_SHIFT = "the shift toolbar.";
	private static final String IMAGE_RULER = IApplicationImage.IMAGE_RULER;
	private static final String TOOLTIP_RULER = "the ruler toolbar.";
	//
	private static final String MPC_LABEL = "Max Plot";
	//
	// The traces toolbar is controlled by the combo overlay type.
	//
	private AtomicReference<NamedTracesUI> toolbarNamedTraces = new AtomicReference<>();
	private Button buttonToolbarDataShift;
	private AtomicReference<DataShiftControllerUI> toolbarDataShift = new AtomicReference<>();
	private Button buttonToolbarRulerDetails;
	private AtomicReference<RulerDetailsUI> toolbarRulerDetails = new AtomicReference<>();
	private Button buttonChartGrid;
	private AtomicReference<ChromatogramRulerChart> chartControl = new AtomicReference<>();
	private ChartGridSupport chartGridSupport = new ChartGridSupport();
	//
	private Label labelStatus;
	private Combo comboOverlayType;
	private int previousChromatograms;
	private ComboViewer comboViewerDerivative;
	//
	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private final OverlayChartSupport overlayChartSupport = new OverlayChartSupport();
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	@SuppressWarnings("rawtypes")
	private final Map<IChromatogramSelection, List<String>> chromatogramSelections = new LinkedHashMap<>();
	private boolean lockZoom = false;

	public ExtendedChromatogramOverlayUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@SuppressWarnings("rawtypes")
	public void update(List<IChromatogramSelection> chromatogramSelections) {

		this.chromatogramSelections.clear();
		for(IChromatogramSelection selection : chromatogramSelections) {
			this.chromatogramSelections.put(selection, new ArrayList<>());
		}
		refreshUpdateOverlayChart();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createNamedTraces(this);
		createDataShiftControllerUI(this);
		createRulerDetailsUI(this);
		createOverlayChart(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarNamedTraces, false);
		enableToolbar(toolbarDataShift, buttonToolbarDataShift, IMAGE_SHIFT, TOOLTIP_SHIFT, false);
		enableToolbar(toolbarRulerDetails, buttonToolbarRulerDetails, IMAGE_RULER, TOOLTIP_RULER, false);
		enableChartGrid(chartControl, buttonChartGrid, IMAGE_CHART_GRID, chartGridSupport);
		//
		toolbarDataShift.get().setScrollableChart(chartControl.get());
		toolbarRulerDetails.get().setScrollableChart(chartControl.get());
		modifyWidgetStatus();
		setDerivatives();
		/*
		 * This is needed to layout both combo boxes accordingly.
		 */
		this.layout(true);
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(10, false));
		//
		labelStatus = createLabelStatus(composite);
		comboOverlayType = createOverlayTypeCombo(composite);
		comboViewerDerivative = createDerivativeComboViewer(composite);
		buttonToolbarDataShift = createButtonToggleToolbar(composite, toolbarDataShift, IMAGE_SHIFT, TOOLTIP_SHIFT);
		buttonToolbarRulerDetails = createButtonToggleToolbar(composite, toolbarRulerDetails, IMAGE_RULER, TOOLTIP_RULER);
		createButtonToggleChartLegend(composite, chartControl, IMAGE_LEGEND);
		createResetButton(composite);
		createNewOverlayPartButton(composite);
		buttonChartGrid = createButtonToggleChartGrid(composite, chartControl, IMAGE_CHART_GRID, chartGridSupport);
		createSettingsButton(composite);
		//
		return composite;
	}

	private void createNamedTraces(Composite parent) {

		NamedTracesUI namedTracesUI = new NamedTracesUI(parent, SWT.NONE);
		namedTracesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		namedTracesUI.setInput(new NamedTraces(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES)));
		namedTracesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				NamedTraces namedTraces = namedTracesUI.getNamedTraces();
				if(namedTraces != null) {
					preferenceStore.putValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES, namedTraces.save());
					chartControl.get().deleteSeries();
					refreshUpdateOverlayChart();
				}
			}
		});
		//
		toolbarNamedTraces.set(namedTracesUI);
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

	private Label createLabelStatus(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setToolTipText("Indicates whether the data has been modified or not.");
		label.setText("");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return label;
	}

	private Combo createOverlayTypeCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select the overlay type");
		GridData gridData = new GridData();
		gridData.minimumWidth = 150;
		combo.setLayoutData(gridData);
		combo.setItems(overlayChartSupport.getOverlayTypes());
		combo.select(0);
		//
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = combo.getSelectionIndex();
				comboOverlayType.select(index);
				modifyWidgetStatus();
				refreshUpdateOverlayChart();
			}
		});
		//
		return combo;
	}

	private ComboViewer createDerivativeComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Derivative) {
					Derivative derivative = (Derivative)element;
					return derivative.label();
				}
				return null;
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText("Select the derivative type");
		GridData gridData = new GridData();
		gridData.minimumWidth = 150;
		combo.setLayoutData(gridData);
		//
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				refreshUpdateOverlayChart();
			}
		});
		//
		return comboViewer;
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

	private void reset() {

		applySettings();
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
				String classPath = PartSupport.PART_OVERLAY_CHROMATOGRAM;
				String name = "Chromatogram Overlay";
				createNewPart(bundle, classPath, name);
			}
		});
	}

	private void createNewPart(String bundle, String classPath, String name) {

		String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA);
		if(!partStackId.equals(PartSupport.PARTSTACK_NONE)) {
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
				MPartStack partStack = PartSupport.getPartStack(partStackId, modelService, application);
				partStack.getChildren().add(part);
				PartSupport.showPart(part, partService);
			}
		}
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList( //
				PreferencePageOverlay.class, //
				PreferencePageNamedTraces.class, //
				PreferencePageChromatogram.class, //
				PreferencePage.class //
		), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
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

	private void modifyWidgetStatus() {

		/*
		 * Overlay Type
		 */
		Set<DisplayType> types = getDisplayType();
		comboOverlayType.setToolTipText(DisplayType.toDescription(types));
		// comboOverlayType.setText(DisplayType.toShortcut(types));
		if(preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS)) {
			if(isExtractedIonsModusEnabled() || isExtractedWavelengthsModusEnabled()) {
				enableToolbar(toolbarNamedTraces, true);
			} else {
				enableToolbar(toolbarNamedTraces, false);
			}
		}
		//
		NamedTracesUI namedTracesUI = toolbarNamedTraces.get();
		if(isExtractedIonsModusEnabled()) {
			namedTracesUI.setEnabled(true);
		} else if(isExtractedWavelengthsModusEnabled()) {
			namedTracesUI.setEnabled(true);
		} else {
			namedTracesUI.setEnabled(false);
		}
		//
		toolbarDataShift.get().update();
	}

	private void setDerivatives() {

		Derivative[] derivatives = Derivative.values();
		comboViewerDerivative.setInput(Derivative.values());
		if(derivatives.length > 0) {
			comboViewerDerivative.getCombo().select(0);
		}
	}

	private void applySettings() {

		updateNamedTraces();
		chromatogramChartSupport.loadUserSettings();
		chartControl.get().deleteSeries();
		refreshUpdateOverlayChart();
		modifyWidgetStatus();
		modifyDataStatusLabel();
	}

	private void updateNamedTraces() {

		toolbarNamedTraces.get().setInput(new NamedTraces(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES)));
	}

	private void createOverlayChart(Composite parent) {

		ChromatogramRulerChart chromatogramRulerChart = new ChromatogramRulerChart(parent, SWT.BORDER);
		chromatogramRulerChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramRulerChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		chartSettings.setSupportDataShift(true);
		chartSettings.getRangeRestriction().setZeroY(false);
		chromatogramRulerChart.applySettings(chartSettings);
		//
		BaseChart baseChart = chromatogramRulerChart.getBaseChart();
		baseChart.addSeriesModificationListener(new ISeriesModificationListener() {

			@Override
			public void handleSeriesModificationEvent() {

				modifyDataStatusLabel();
			}
		});
		//
		chromatogramRulerChart.setRulerUpdateNotifier(new IRulerUpdateNotifier() {

			@Override
			public void update(RulerEvent rulerEvent) {

				toolbarRulerDetails.get().setInput(rulerEvent);
			}
		});
		//
		chartControl.set(chromatogramRulerChart);
	}

	@SuppressWarnings("rawtypes")
	private void refreshUpdateOverlayChart() {

		ChromatogramChart chromatogramChart = chartControl.get();
		if(chromatogramSelections.size() > 0) {
			/*
			 * Reset the range restriction
			 */
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
			rangeRestriction.setExtendTypeX(ExtendType.ABSOLUTE);
			rangeRestriction.setExtendMaxX(0.0d);
			rangeRestriction.setExtendTypeY(ExtendType.ABSOLUTE);
			rangeRestriction.setExtendMaxY(0.0d);
			chromatogramChart.applySettings(chartSettings);
			//
			IAxisSet axisSet = chromatogramChart.getBaseChart().getAxisSet();
			Range xrange = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
			Range yrange = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
			Set<String> availableSeriesIds = new HashSet<>();
			BaseChart baseChart = chromatogramChart.getBaseChart();
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			LinkedHashSet<String> usefulTypes = new LinkedHashSet<String>();
			int i = 0;
			for(Entry<IChromatogramSelection, List<String>> entry : chromatogramSelections.entrySet()) {
				IChromatogramSelection<?, ?> chromatogramSelection = entry.getKey();
				if(previousChromatograms != chromatogramSelections.size()) {
					if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC));
					} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.SWC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.XWC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.MPC));
					} else {
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.BPC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.XIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.SIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TSC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC, DisplayType.BPC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC, DisplayType.XIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC, DisplayType.SIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC, DisplayType.TSC));
					}
					comboOverlayType.setItems(usefulTypes.toArray(new String[usefulTypes.size()]));
					comboOverlayType.select(0);
				}
				//
				List<String> selectionSeries = entry.getValue();
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				String chromatogramName = chromatogram.getName() + ChromatogramChartSupport.EDITOR_TAB + (i + 1);
				/*
				 * refreshUpdateOverlayChart
				 * Select which series shall be displayed.
				 */
				Set<DisplayType> displayTypes = getDisplayType();
				for(DisplayType displayType : displayTypes) {
					if(displayType.equals(DisplayType.SIC)) {
						appendSIC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					} else if(displayType.equals(DisplayType.SWC)) {
						appendSWC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					} else if(displayType.equals(DisplayType.XWC)) {
						appendXWC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					} else if(displayType.equals(DisplayType.MPC)) {
						appendMPC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					} else {
						if(displayType.equals(DisplayType.BPC) || displayType.equals(DisplayType.XIC) || displayType.equals(DisplayType.TSC)) {
							appendXXC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
						} else {
							appendTIC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
						}
					}
				}
				i++;
			}
			if(previousChromatograms != chromatogramSelections.size()) {
				comboOverlayType.setItems(usefulTypes.toArray(new String[usefulTypes.size()]));
				comboOverlayType.select(0);
			}
			previousChromatograms = chromatogramSelections.size();
			/*
			 * Add the selected series
			 */
			String compressionType = preferenceStore.getString(PreferenceConstants.P_OVERLAY_CHART_COMPRESSION_TYPE);
			int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
			chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
			/*
			 * Delete non-available series.
			 */
			for(ISeries series : baseChart.getSeriesSet().getSeries()) {
				String seriesId = series.getId();
				if(!availableSeriesIds.contains(seriesId)) {
					baseChart.deleteSeries(seriesId);
				}
			}
			/*
			 * Reset the selected series selection.
			 */
			toolbarDataShift.get().reset();
			modifyDataStatusLabel();
			chromatogramChart.adjustRange(true);
			if(lockZoom) {
				chromatogramChart.setRange(IExtendedChart.X_AXIS, xrange);
				chromatogramChart.setRange(IExtendedChart.Y_AXIS, yrange);
			}
		}
	}

	private void appendSIC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		String seriesId;
		Color color;
		//
		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		/*
		 * SIC
		 */
		List<Number> ions = getSelectedTraces(true);
		if(chromatogram instanceof IChromatogramMSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, false);
			for(Number number : ions) {
				int ion = number.intValue();
				seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + ion + OverlayChartSupport.OVERLAY_STOP_MARKER;
				color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				if(!baseChart.isSeriesContained(seriesId)) {
					IMarkedIons markedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
					markedIons.add(ion);
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedIons, false);
					lineSeriesData.getSettings().setDescription("m/z " + Integer.toString(ion) + " (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramMSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
					for(Number number : ions) {
						int ion = number.intValue();
						String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
						seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + ion + OverlayChartSupport.OVERLAY_STOP_MARKER;
						color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
						availableSeriesIds.add(seriesId);
						selectionSeries.add(seriesId);
						if(!baseChart.isSeriesContained(seriesId)) {
							IMarkedIons markedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
							markedIons.add(ion);
							ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, seriesId, displayType, derivative, color, markedIons, false);
							lineSeriesData.getSettings().setDescription("m/z " + Integer.toString(ion) + " (" + description + ")");
							lineSeriesDataList.add(lineSeriesData);
						}
					}
				}
			}
		}
	}

	private void appendSWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		String seriesId;
		Color color;
		//
		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		/*
		 * SWC
		 */
		List<Number> wavelengths = getSelectedTraces(false);
		if(chromatogram instanceof IChromatogramWSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, false);
			for(Number number : wavelengths) {
				double wavelength = number.doubleValue();
				seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(wavelength);
				//
				if(!baseChart.isSeriesContained(seriesId)) {
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
					lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramWSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					for(Number number : wavelengths) {
						double wavelength = number.doubleValue();
						seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
						availableSeriesIds.add(seriesId);
						selectionSeries.add(seriesId);
						color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
						IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
						markedWavelengths.add(wavelength);
						//
						if(!baseChart.isSeriesContained(seriesId)) {
							ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
							lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + description + ")");
							lineSeriesDataList.add(lineSeriesData);
						}
					}
				}
			}
		}
	}

	private void appendXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		boolean showOptimizedXWC = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_OPTIMIZED_CHROMATOGRAM_XWC);
		/*
		 * Master
		 */
		if(chromatogram instanceof IChromatogramWSD) {
			if(showOptimizedXWC) {
				appendOptimizedXWC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName, 0);
			} else {
				appendMasterFullXWC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramWSD) {
					if(showOptimizedXWC) {
						String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
						appendOptimizedXWC(availableSeriesIds, selectionSeries, lineSeriesDataList, referencedChromatogram, displayType, referenceChromatogramName, j);
					} else {
						appendReferenceFullXWC(availableSeriesIds, selectionSeries, lineSeriesDataList, referencedChromatogram, displayType, chromatogramName, j);
					}
				}
			}
		}
	}

	private void appendMasterFullXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		//
		if(chromatogram instanceof IChromatogramWSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, false);
			for(double wavelength : ((IChromatogramWSD)chromatogram).getWavelengths()) {
				String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				Color color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(wavelength);
				//
				if(!baseChart.isSeriesContained(seriesId)) {
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
					lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
	}

	private void appendReferenceFullXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> referencedChromatogram, DisplayType displayType, String chromatogramName, int j) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		//
		if(referencedChromatogram instanceof IChromatogramWSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
			String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
			for(double wavelength : ((IChromatogramWSD)referencedChromatogram).getWavelengths()) {
				//
				String seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				Color color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(wavelength);
				//
				if(!baseChart.isSeriesContained(seriesId)) {
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
					lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
	}

	private void appendOptimizedXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName, int index) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = Derivative.NONE; // Always no derivative at the moment.
		LineStyle lineStyle = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_OVERLAY));
		boolean showArea = preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_SHOW_AREA);
		//
		if(chromatogram instanceof IChromatogramWSD) {
			/*
			 * WSD
			 */
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, index, false);
			IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
			/*
			 * Preselect certain wavelengths if too many are available.
			 */
			Set<Double> wavelengths = new HashSet<>();
			List<Double> wavelengthList = new ArrayList<>(chromatogramWSD.getWavelengths());
			if(wavelengthList.size() > 40) {
				Collections.sort(wavelengthList);
				int h = 0;
				for(Double wavelength : wavelengthList) {
					if(h % 4 == 0) {
						wavelengths.add(wavelength);
					}
					h++;
				}
			} else {
				wavelengths.addAll(wavelengthList);
			}
			/*
			 * Map the data.
			 */
			Map<Double, ILineSeriesData> lineSeriesMap = new HashMap<>();
			int numberOfScans = chromatogram.getNumberOfScans();
			double displayWidth = Display.getDefault().getClientArea().width;
			int modulo = (int)Math.round(numberOfScans / displayWidth);
			int length = chromatogram.getNumberOfScans() / modulo;
			int i = 0;
			int j = 0;
			//
			for(IScan scan : chromatogramWSD.getScans()) {
				if(j % modulo == 0) {
					if(scan instanceof IScanWSD) {
						IScanWSD scanWSD = (IScanWSD)scan;
						IExtractedWavelengthSignal extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
						int retentionTime = scanWSD.getRetentionTime();
						for(double wavelength : wavelengths) {
							/*
							 * Line Series Data
							 */
							ILineSeriesData lineSeriesData = lineSeriesMap.get(wavelength);
							if(lineSeriesData == null) {
								String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
								double[] xSeries = new double[length];
								double[] ySeries = new double[length];
								ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
								lineSeriesData = new LineSeriesData(seriesData);
								ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
								lineSeriesSettings.setLineColor(chromatogramChartSupport.getSeriesColor(seriesId, displayType));
								lineSeriesSettings.setLineStyle(lineStyle);
								lineSeriesSettings.setEnableArea(showArea);
								ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
								lineSeriesSettingsHighlight.setLineWidth(2);
								lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + description + ")");
								lineSeriesMap.put(wavelength, lineSeriesData);
							}
							/*
							 * Data
							 */
							if(i < length) {
								ISeriesData seriesData = lineSeriesData.getSeriesData();
								seriesData.getXSeries()[i] = retentionTime;
								seriesData.getYSeries()[i] = extractedWavelengthSignal.getAbundance((int)wavelength);
							}
						}
					}
					i++;
				}
				j++;
			}
			//
			for(ILineSeriesData lineSeriesData : lineSeriesMap.values()) {
				String seriesId = lineSeriesData.getSeriesData().getId();
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				if(!baseChart.isSeriesContained(seriesId)) {
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
	}

	private void appendXXC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
		Color color = chromatogramChartSupport.getSeriesColor(chromatogramName, displayType);
		String displayTypeInfo = " (" + displayType.getShortcut() + ")";
		/*
		 * BPC, XIC, TSC
		 */
		IMarkedIons markedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		List<Number> ions = getSelectedTraces(true);
		for(Number ion : ions) {
			markedIons.add(ion.intValue());
		}
		//
		if(chromatogram instanceof IChromatogramMSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, true);
			description += displayTypeInfo;
			availableSeriesIds.add(seriesId);
			selectionSeries.add(seriesId);
			if(!baseChart.isSeriesContained(seriesId)) {
				ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedIons, false);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setDescription(description);
				ISeriesSettings seriesSettingsDefault = SeriesMapper.getSeriesSettingsDefault(seriesId, chartControl.get());
				SeriesMapper.mapSetting(seriesId, lineSeriesSettings, seriesSettingsDefault);
				lineSeriesDataList.add(lineSeriesData);
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramMSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, true);
					description += displayTypeInfo;
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
					availableSeriesIds.add(referenceSeriesId);
					selectionSeries.add(referenceSeriesId);
					if(!baseChart.isSeriesContained(referenceSeriesId)) {
						color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, displayType);
						ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, displayType, derivative, color, markedIons, false);
						ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
						lineSeriesSettings.setDescription(description);
						ISeriesSettings seriesSettingsDefault = SeriesMapper.getSeriesSettingsDefault(referenceSeriesId, chartControl.get());
						SeriesMapper.mapSetting(referenceSeriesId, lineSeriesSettings, seriesSettingsDefault);
						lineSeriesDataList.add(lineSeriesData);
					}
				}
			}
		}
	}

	private void appendMPC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		String seriesId;
		Color color;
		//
		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		//
		IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
		/*
		 * Max Plot
		 */
		if(chromatogram instanceof IChromatogramWSD) {
			IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
			markedWavelengths.add(chromatogramWSD.getWavelengths());
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, false);
			seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + MPC_LABEL + OverlayChartSupport.OVERLAY_STOP_MARKER;
			availableSeriesIds.add(seriesId);
			selectionSeries.add(seriesId);
			color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
			//
			if(!baseChart.isSeriesContained(seriesId)) {
				ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setDescription(MPC_LABEL + " (" + description + ")");
				ISeriesSettings seriesSettingsDefault = SeriesMapper.getSeriesSettingsDefault(seriesId, chartControl.get());
				SeriesMapper.mapSetting(seriesId, lineSeriesSettings, seriesSettingsDefault);
				lineSeriesDataList.add(lineSeriesData);
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramWSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + MPC_LABEL + OverlayChartSupport.OVERLAY_STOP_MARKER;
					availableSeriesIds.add(seriesId);
					selectionSeries.add(seriesId);
					color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
					//
					if(!baseChart.isSeriesContained(seriesId)) {
						ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
						ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
						lineSeriesSettings.setDescription(MPC_LABEL + " (" + description + ")");
						ISeriesSettings seriesSettingsDefault = SeriesMapper.getSeriesSettingsDefault(seriesId, chartControl.get());
						SeriesMapper.mapSetting(seriesId, lineSeriesSettings, seriesSettingsDefault);
						lineSeriesDataList.add(lineSeriesData);
					}
				}
			}
		}
	}

	private void appendTIC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
		Color color = chromatogramChartSupport.getSeriesColor(chromatogramName, displayType);
		/*
		 * TIC
		 */
		availableSeriesIds.add(seriesId);
		selectionSeries.add(seriesId);
		if(!baseChart.isSeriesContained(seriesId)) {
			ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, null, false);
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, true);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setDescription(description);
			ISeriesSettings seriesSettingsDefault = SeriesMapper.getSeriesSettingsDefault(seriesId, chartControl.get());
			SeriesMapper.mapSetting(seriesId, lineSeriesSettings, seriesSettingsDefault);
			lineSeriesDataList.add(lineSeriesData);
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram != null) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, true);
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
					availableSeriesIds.add(referenceSeriesId);
					selectionSeries.add(referenceSeriesId);
					if(!baseChart.isSeriesContained(referenceSeriesId)) {
						color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, displayType);
						ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, displayType, derivative, color, null, false);
						ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
						lineSeriesSettings.setDescription(description);
						ISeriesSettings seriesSettingsDefault = SeriesMapper.getSeriesSettingsDefault(referenceSeriesId, chartControl.get());
						SeriesMapper.mapSetting(referenceSeriesId, lineSeriesSettings, seriesSettingsDefault);
						lineSeriesDataList.add(lineSeriesData);
					}
				}
			}
		}
	}

	/**
	 * Return the selected derivative or NONE as a default.
	 * 
	 * @return {@link Derivative}
	 */
	private Derivative getSelectedDerivative() {

		Object object = comboViewerDerivative.getStructuredSelection().getFirstElement();
		if(object instanceof Derivative) {
			return (Derivative)object;
		}
		//
		return Derivative.NONE;
	}

	private List<Number> getSelectedTraces(boolean isNominal) {

		List<Number> traceList = new ArrayList<>();
		NamedTrace namedTrace = toolbarNamedTraces.get().getNamedTrace();
		if(namedTrace != null) {
			TraceValidator traceValidator = new TraceValidator();
			IStatus status = traceValidator.validate(namedTrace.getTraces());
			if(status.isOK()) {
				if(isNominal) {
					traceList.addAll(traceValidator.getTracesAsInteger());
				} else {
					traceList.addAll(traceValidator.getTracesAsDouble());
				}
			}
		}
		//
		return traceList;
	}

	private Set<DisplayType> getDisplayType() {

		return DisplayType.toDisplayTypes(comboOverlayType.getText().trim());
	}

	private boolean isExtractedIonsModusEnabled() {

		Set<DisplayType> overlayType = getDisplayType();
		return overlayType.contains(DisplayType.XIC) || //
				overlayType.contains(DisplayType.SIC) || //
				overlayType.contains(DisplayType.TSC);
	}

	private boolean isExtractedWavelengthsModusEnabled() {

		return getDisplayType().contains(DisplayType.SWC);
	}

	public void setZoomLocked(boolean lockZoom) {

		this.lockZoom = lockZoom;
	}

	@Override
	public void dispose() {

		chartControl.get().dispose();
		super.dispose();
	}
}
