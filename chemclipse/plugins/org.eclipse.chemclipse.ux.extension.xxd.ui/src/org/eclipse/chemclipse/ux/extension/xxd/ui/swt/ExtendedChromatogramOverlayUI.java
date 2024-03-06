/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
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
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.MarkedTrace;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.core.MarkedTraces;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.traces.NamedTrace;
import org.eclipse.chemclipse.model.traces.NamedTraces;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.support.validators.TraceValidator;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramRulerChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.IRulerUpdateNotifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.RulerEvent;
import org.eclipse.chemclipse.ux.extension.xxd.ui.help.HelpContext;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageNamedTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.Derivative;
import org.eclipse.chemclipse.ux.extension.xxd.ui.traces.NamedTracesUI;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.selection.IChromatogramSelectionVSD;
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
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.RangeRestriction.ExtendType;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;
import org.eclipse.swtchart.extensions.preferences.PreferencePage;
import org.eclipse.ui.PlatformUI;

public class ExtendedChromatogramOverlayUI extends Composite implements IExtendedPartUI {

	private static final String IMAGE_SHIFT = IApplicationImage.IMAGE_SHIFT;
	private static final String TOOLTIP_SHIFT = "the shift toolbar.";
	private static final String IMAGE_RULER = IApplicationImage.IMAGE_RULER;
	private static final String TOOLTIP_RULER = "the ruler toolbar.";
	private static final String IMAGE_ZOOM_LOCKED = IApplicationImage.IMAGE_ZOOM_LOCKED;
	private static final String TOOLTIP_ZOOM_LOCKED = "the zoom lock functionality.";
	private static final String IMAGE_FOCUS_SELECTION = IApplicationImage.IMAGE_CHROMATOGRAM_SELECTION;
	private static final String TOOLTIP_FOCUS_SELECTION = "the focus chromatogram selection functionality.";
	//
	private static final String MPC_LABEL = "Max Plot";
	//
	// The traces toolbar is controlled by the combo overlay type.
	//
	private AtomicReference<Button> buttonLockControl = new AtomicReference<>();
	private AtomicReference<Button> buttonFocusControl = new AtomicReference<>();
	private AtomicReference<NamedTracesUI> toolbarNamedTraces = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarDataShift = new AtomicReference<>();
	private AtomicReference<DataShiftControllerUI> toolbarDataShift = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarRulerDetails = new AtomicReference<>();
	private AtomicReference<RulerDetailsUI> toolbarRulerDetails = new AtomicReference<>();
	private AtomicReference<Button> buttonChartGrid = new AtomicReference<>();
	private AtomicReference<ChromatogramRulerChart> chartControl = new AtomicReference<>();
	private AtomicReference<Label> labelStatus = new AtomicReference<>();
	private AtomicReference<Combo> comboOverlayType = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerDerivative = new AtomicReference<>();
	//
	private ChartGridSupport chartGridSupport = new ChartGridSupport();
	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private final OverlayChartSupport overlayChartSupport = new OverlayChartSupport();
	//
	private int previousChromatograms;
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private final Map<IChromatogramSelection<?, ?>, List<String>> chromatogramSelections = new LinkedHashMap<>();

	public ExtendedChromatogramOverlayUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection != null) {
			if(preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_FOCUS_SELECTION)) {
				ChromatogramChart chromatogramChart = chartControl.get();
				IAxisSet axisSet = chromatogramChart.getBaseChart().getAxisSet();
				Range xrange = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
				Range yrange = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
				xrange.lower = chromatogramSelection.getStartRetentionTime();
				xrange.upper = chromatogramSelection.getStopRetentionTime();
				yrange.lower = chromatogramSelection.getStartAbundance();
				yrange.upper = chromatogramSelection.getStopAbundance();
				chromatogramChart.setRange(IExtendedChart.X_AXIS, xrange);
				chromatogramChart.setRange(IExtendedChart.Y_AXIS, yrange);
			}
		}
	}

	public void update(List<IChromatogramSelection<?, ?>> chromatogramSelections) {

		this.chromatogramSelections.clear();
		for(IChromatogramSelection<?, ?> selection : chromatogramSelections) {
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
		enableToolbar(toolbarDataShift, buttonToolbarDataShift.get(), IMAGE_SHIFT, TOOLTIP_SHIFT, false);
		enableToolbar(toolbarRulerDetails, buttonToolbarRulerDetails.get(), IMAGE_RULER, TOOLTIP_RULER, false);
		enableChartGrid(chartControl, buttonChartGrid.get(), IMAGE_CHART_GRID, chartGridSupport);
		//
		toolbarDataShift.get().setScrollableChart(chartControl.get());
		toolbarRulerDetails.get().setScrollableChart(chartControl.get());
		modifyWidgetStatus();
		setDerivatives();
		/*
		 * This is needed to layout both combo boxes accordingly.
		 */
		this.layout(true);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, HelpContext.CHROMATOGRAM_OVERLAY);
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(13, false));
		//
		createLabelStatus(composite);
		createButtonZoomLock(composite);
		createButtonFocusSelection(composite);
		createOverlayTypeCombo(composite);
		createDerivativeComboViewer(composite);
		createToggleToolbarButton(composite);
		createToggleRulerButton(composite);
		createToggleLegendButton(composite);
		createResetButton(composite);
		createNewOverlayPartButton(composite);
		createToggleGridButton(composite);
		createButtonHelp(composite);
		createSettingsButton(composite);
		//
		return composite;
	}

	private void createToggleToolbarButton(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarDataShift, IMAGE_SHIFT, TOOLTIP_SHIFT);
		buttonToolbarDataShift.set(button);
	}

	private void createToggleLegendButton(Composite parent) {

		createButtonToggleChartLegend(parent, chartControl, IMAGE_LEGEND);
	}

	private void createToggleRulerButton(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarRulerDetails, IMAGE_RULER, TOOLTIP_RULER);
		buttonToolbarRulerDetails.set(button);
	}

	private void createToggleGridButton(Composite parent) {

		Button button = createButtonToggleChartGrid(parent, chartControl, IMAGE_CHART_GRID, chartGridSupport);
		buttonChartGrid.set(button);
	}

	private void createNamedTraces(Composite parent) {

		NamedTracesUI namedTracesUI = new NamedTracesUI(parent, SWT.NONE);
		namedTracesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		namedTracesUI.setInput(new NamedTraces(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES)));
		namedTracesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				NamedTraces namedTraces = namedTracesUI.getNamedTraces();
				if(namedTraces != null) {
					preferenceStore.setValue(PreferenceSupplier.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES, namedTraces.save());
					refreshUpdateOverlayChart(true);
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

	private void createLabelStatus(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setToolTipText("Indicates whether the data has been modified or not.");
		label.setText("");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		labelStatus.set(label);
	}

	private void createButtonZoomLock(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setText("");
		setButtonImage(button, IMAGE_ZOOM_LOCKED, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_ZOOM_LOCKED, preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_LOCK_ZOOM));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean active = !preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_LOCK_ZOOM);
				preferenceStore.setValue(PreferenceSupplier.P_OVERLAY_LOCK_ZOOM, active);
				setButtonImage(button, IMAGE_ZOOM_LOCKED, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_ZOOM_LOCKED, active);
			}
		});
		//
		buttonLockControl.set(button);
	}

	private void createButtonFocusSelection(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setText("");
		setButtonImage(button, IMAGE_FOCUS_SELECTION, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_FOCUS_SELECTION, preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_FOCUS_SELECTION));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean active = !preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_FOCUS_SELECTION);
				preferenceStore.setValue(PreferenceSupplier.P_OVERLAY_FOCUS_SELECTION, active);
				setButtonImage(button, IMAGE_FOCUS_SELECTION, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_FOCUS_SELECTION, active);
			}
		});
		//
		buttonFocusControl.set(button);
	}

	private void createOverlayTypeCombo(Composite parent) {

		Combo combo = EnhancedCombo.create(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select the overlay display type");
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
				combo.select(index);
				modifyWidgetStatus();
				refreshUpdateOverlayChart();
			}
		});
		//
		comboOverlayType.set(combo);
	}

	private void createDerivativeComboViewer(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Derivative derivative) {
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
		comboViewerDerivative.set(comboViewer);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PLUS, IApplicationImageProvider.SIZE_16x16));
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

		String partStackId = preferenceStore.getString(PreferenceSupplier.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA);
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

		Label label = labelStatus.get();
		if(chartControl.get().getBaseChart().isDataShifted()) {
			label.setText("The displayed data is shifted.");
			label.setBackground(Colors.getColor(Colors.LIGHT_YELLOW));
		} else {
			label.setText("");
			label.setBackground(null);
		}
	}

	private void modifyButtons() {

		setButtonImage(buttonLockControl.get(), IMAGE_ZOOM_LOCKED, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_ZOOM_LOCKED, preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_LOCK_ZOOM));
		setButtonImage(buttonFocusControl.get(), IMAGE_FOCUS_SELECTION, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_FOCUS_SELECTION, preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_FOCUS_SELECTION));
	}

	private void modifyWidgetStatus() {

		/*
		 * Overlay Type
		 */
		Set<DisplayType> types = getDisplayType();
		comboOverlayType.get().setToolTipText(DisplayType.toDescription(types));
		// comboOverlayType.setText(DisplayType.toShortcut(types));
		if(preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS)) {
			if(isExtractedIonsModusEnabled() || isExtractedWavelengthsModusEnabled() || isExtractedWavenumbersModusEnabled()) {
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
		} else if(isExtractedWavenumbersModusEnabled()) {
			namedTracesUI.setEnabled(true);
		} else {
			namedTracesUI.setEnabled(false);
		}
		//
		toolbarDataShift.get().update();
	}

	private void setDerivatives() {

		Derivative[] derivatives = Derivative.values();
		comboViewerDerivative.get().setInput(Derivative.values());
		if(derivatives.length > 0) {
			comboViewerDerivative.get().getCombo().select(0);
		}
	}

	private void applySettings() {

		updateNamedTraces();
		chromatogramChartSupport.loadUserSettings();
		refreshUpdateOverlayChart(true);
		modifyWidgetStatus();
		modifyDataStatusLabel();
		modifyButtons();
	}

	private void updateNamedTraces() {

		toolbarNamedTraces.get().setInput(new NamedTraces(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES)));
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

	private void refreshUpdateOverlayChart() {

		refreshUpdateOverlayChart(false);
	}

	private void refreshUpdateOverlayChart(boolean forceRedraw) {

		ChromatogramChart chromatogramChart = chartControl.get();
		if(chromatogramSelections.size() > 0) {
			/*
			 * X|Y range
			 */
			boolean overlayLockZoom = preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_LOCK_ZOOM);
			BaseChart baseChart = chromatogramChart.getBaseChart();
			boolean isEmpty = baseChart.getSeriesIds().isEmpty();
			IAxisSet axisSet = chromatogramChart.getBaseChart().getAxisSet();
			Range rangeX = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
			Range rangeY = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
			/*
			 * Delete Series?
			 */
			if(forceRedraw) {
				chartControl.get().deleteSeries();
			}
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
			Set<String> availableSeriesIds = new HashSet<>();
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			LinkedHashSet<String> usefulTypes = new LinkedHashSet<>();
			//
			int i = 0;
			for(Entry<IChromatogramSelection<?, ?>, List<String>> entry : chromatogramSelections.entrySet()) {
				IChromatogramSelection<?, ?> chromatogramSelection = entry.getKey();
				if(previousChromatograms != chromatogramSelections.size()) {
					if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC));
					} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.SWC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.XWC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.MPC));
					} else if(chromatogramSelection instanceof IChromatogramSelectionVSD) {
						usefulTypes.add(DisplayType.toShortcut(DisplayType.TIC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.XVC));
						usefulTypes.add(DisplayType.toShortcut(DisplayType.SVC));
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
					comboOverlayType.get().setItems(usefulTypes.toArray(new String[usefulTypes.size()]));
					comboOverlayType.get().select(0);
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
					} else if(displayType.equals(DisplayType.BPC) || displayType.equals(DisplayType.XIC) || displayType.equals(DisplayType.TSC)) {
						appendXXC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					} else if(displayType.equals(DisplayType.XVC)) {
						appendXVC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					} else if(displayType.equals(DisplayType.SVC)) {
						appendSVC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					} else {
						appendTIC(availableSeriesIds, selectionSeries, lineSeriesDataList, chromatogram, displayType, chromatogramName);
					}
				}
				i++;
			}
			//
			if(previousChromatograms != chromatogramSelections.size()) {
				comboOverlayType.get().setItems(usefulTypes.toArray(new String[usefulTypes.size()]));
				comboOverlayType.get().select(0);
			}
			previousChromatograms = chromatogramSelections.size();
			/*
			 * Add the selected series
			 */
			String compressionType = preferenceStore.getString(PreferenceSupplier.P_OVERLAY_CHART_COMPRESSION_TYPE);
			int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
			chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
			/*
			 * Delete non-available series.
			 */
			for(ISeries<?> series : baseChart.getSeriesSet().getSeries()) {
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
			//
			if(!isEmpty) {
				if(overlayLockZoom) {
					chromatogramChart.setRange(IExtendedChart.X_AXIS, rangeX);
					chromatogramChart.setRange(IExtendedChart.Y_AXIS, rangeY);
				}
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
					IMarkedIons markedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
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
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
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
							IMarkedIons markedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
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

	private void appendXVC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		String seriesId;
		Color color;
		//
		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		/*
		 * XSC
		 */
		IMarkedTraces<IMarkedTrace> markedTraces = new MarkedTraces(MarkedTraceModus.INCLUDE);
		List<Number> wavenumbers = getSelectedTraces(true);
		for(Number wavenumber : wavenumbers) {
			markedTraces.add(new MarkedTrace(wavenumber.intValue()));
		}
		//
		if(chromatogram instanceof IChromatogramVSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, false);
			seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + displayType.name() + OverlayChartSupport.OVERLAY_STOP_MARKER;
			color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
			availableSeriesIds.add(seriesId);
			selectionSeries.add(seriesId);
			if(!baseChart.isSeriesContained(seriesId)) {
				ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedTraces, false);
				lineSeriesData.getSettings().setDescription(displayType.name() + " (" + description + ")");
				lineSeriesDataList.add(lineSeriesData);
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramMSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + displayType.name() + OverlayChartSupport.OVERLAY_STOP_MARKER;
					color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
					availableSeriesIds.add(seriesId);
					selectionSeries.add(seriesId);
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, seriesId, displayType, derivative, color, markedTraces, false);
					lineSeriesData.getSettings().setDescription(displayType.name() + " (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
	}

	private void appendSVC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		String seriesId;
		Color color;
		//
		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		/*
		 * SSC
		 */
		List<Number> wavenumbers = getSelectedTraces(true);
		if(chromatogram instanceof IChromatogramVSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, false);
			for(Number number : wavenumbers) {
				int wavenumber = number.intValue();
				seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavenumber + OverlayChartSupport.OVERLAY_STOP_MARKER;
				color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				if(!baseChart.isSeriesContained(seriesId)) {
					IMarkedTraces<IMarkedTrace> markedTraces = new MarkedTraces(MarkedTraceModus.INCLUDE);
					markedTraces.add(new MarkedTrace(wavenumber));
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedTraces, false);
					lineSeriesData.getSettings().setDescription("Wavenumber " + Integer.toString(wavenumber) + " (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramMSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
					for(Number number : wavenumbers) {
						int wavenumber = number.intValue();
						String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
						seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavenumber + OverlayChartSupport.OVERLAY_STOP_MARKER;
						color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
						availableSeriesIds.add(seriesId);
						selectionSeries.add(seriesId);
						if(!baseChart.isSeriesContained(seriesId)) {
							IMarkedTraces<IMarkedTrace> markedTraces = new MarkedTraces(MarkedTraceModus.INCLUDE);
							markedTraces.add(new MarkedTrace(wavenumber));
							ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, seriesId, displayType, derivative, color, markedTraces, false);
							lineSeriesData.getSettings().setDescription("Wavenumber " + Integer.toString(wavenumber) + " (" + description + ")");
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
				float wavelength = number.floatValue();
				seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(wavelength);
				//
				if(!baseChart.isSeriesContained(seriesId)) {
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
					lineSeriesData.getSettings().setDescription(Float.toString(wavelength) + " nm (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramWSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					for(Number number : wavelengths) {
						float wavelength = number.floatValue();
						seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
						availableSeriesIds.add(seriesId);
						selectionSeries.add(seriesId);
						color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
						IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
						markedWavelengths.add(wavelength);
						//
						if(!baseChart.isSeriesContained(seriesId)) {
							ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
							lineSeriesData.getSettings().setDescription(Float.toString(wavelength) + " nm (" + description + ")");
							lineSeriesDataList.add(lineSeriesData);
						}
					}
				}
			}
		}
	}

	private void appendXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		boolean showOptimizedXWC = preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_OPTIMIZED_CHROMATOGRAM_XWC);
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
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
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
		if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, false);
			for(float wavelength : chromatogramWSD.getWavelengths()) {
				String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				Color color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(wavelength);
				//
				if(!baseChart.isSeriesContained(seriesId)) {
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
					lineSeriesData.getSettings().setDescription(Float.toString(wavelength) + " nm (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
	}

	private void appendReferenceFullXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> referencedChromatogram, DisplayType displayType, String chromatogramName, int j) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = getSelectedDerivative();
		//
		if(referencedChromatogram instanceof IChromatogramWSD referencedChromatogramWSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, false);
			String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j + 1;
			for(float wavelength : referencedChromatogramWSD.getWavelengths()) {
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
					lineSeriesData.getSettings().setDescription(Float.toString(wavelength) + " nm (" + description + ")");
					lineSeriesDataList.add(lineSeriesData);
				}
			}
		}
	}

	private void appendOptimizedXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName, int index) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		Derivative derivative = Derivative.NONE; // Always no derivative at the moment.
		LineStyle lineStyle = LineStyle.valueOf(preferenceStore.getString(PreferenceSupplier.P_LINE_STYLE_DISPLAY_OVERLAY));
		boolean showArea = preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_SHOW_AREA);
		//
		if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
			/*
			 * WSD
			 */
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, index, false);
			/*
			 * Preselect certain wavelengths if too many are available.
			 */
			Set<Float> wavelengths = new HashSet<>();
			List<Float> wavelengthList = new ArrayList<>(chromatogramWSD.getWavelengths());
			if(wavelengthList.size() > 40) {
				Collections.sort(wavelengthList);
				int h = 0;
				for(Float wavelength : wavelengthList) {
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
					if(scan instanceof IScanWSD scanWSD) {
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
		boolean addTypeInfo = preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_ADD_TYPE_INFO);
		/*
		 * BPC, XIC, TSC
		 */
		IMarkedIons markedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
		List<Number> ions = getSelectedTraces(true);
		for(Number ion : ions) {
			markedIons.add(ion.intValue());
		}
		//
		if(chromatogram instanceof IChromatogramMSD) {
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, addTypeInfo);
			description += displayTypeInfo;
			availableSeriesIds.add(seriesId);
			selectionSeries.add(seriesId);
			if(!baseChart.isSeriesContained(seriesId)) {
				ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedIons, false);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setDescription(description);
				lineSeriesDataList.add(lineSeriesData);
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramMSD) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, addTypeInfo);
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
		IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
		/*
		 * Max Plot
		 */
		if(chromatogram instanceof IChromatogramWSD || chromatogram instanceof IChromatogramVSD) {
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
				lineSeriesDataList.add(lineSeriesData);
			}
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramWSD || referencedChromatogram instanceof IChromatogramVSD) {
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
		boolean addTypeInfo = preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_ADD_TYPE_INFO);
		/*
		 * TIC
		 */
		availableSeriesIds.add(seriesId);
		selectionSeries.add(seriesId);
		if(!baseChart.isSeriesContained(seriesId)) {
			ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, null, false);
			String description = ChromatogramDataSupport.getReferenceLabel(chromatogram, 0, addTypeInfo);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setDescription(description);
			lineSeriesDataList.add(lineSeriesData);
		}
		/*
		 * References
		 */
		if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
			List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
			int j = 1;
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram != null) {
					String description = ChromatogramDataSupport.getReferenceLabel(referencedChromatogram, j, addTypeInfo);
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
					availableSeriesIds.add(referenceSeriesId);
					selectionSeries.add(referenceSeriesId);
					if(!baseChart.isSeriesContained(referenceSeriesId)) {
						color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, displayType);
						ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, displayType, derivative, color, null, false);
						ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
						lineSeriesSettings.setDescription(description);
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

		Object object = comboViewerDerivative.get().getStructuredSelection().getFirstElement();
		if(object instanceof Derivative derivative) {
			return derivative;
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

		return DisplayType.toDisplayTypes(comboOverlayType.get().getText().trim());
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

	private boolean isExtractedWavenumbersModusEnabled() {

		Set<DisplayType> overlayType = getDisplayType();
		return overlayType.contains(DisplayType.SVC) || //
				overlayType.contains(DisplayType.XVC);
	}

	@Override
	public void dispose() {

		chartControl.get().dispose();
		super.dispose();
	}
}
