/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IChromatogram;
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageNamedTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.Derivative;
import org.eclipse.chemclipse.ux.extension.xxd.ui.traces.NamedTracesUI;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
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
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ISeriesModificationListener;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.RangeRestriction.ExtendType;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;

public class ExtendedChromatogramOverlayUI extends Composite implements IExtendedPartUI {

	private NamedTracesUI namedTracesUI;
	private DataShiftControllerUI dataShiftControllerUI;
	private ChromatogramChart chromatogramChart;
	//
	private Label labelStatus;
	private Combo comboOverlayType;
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
		namedTracesUI = createNamedTraces(this);
		dataShiftControllerUI = createDataShiftControllerUI(this);
		chromatogramChart = createOverlayChart(this);
		//
		PartSupport.setCompositeVisibility(namedTracesUI, false);
		PartSupport.setCompositeVisibility(dataShiftControllerUI, false);
		//
		dataShiftControllerUI.setScrollableChart(chromatogramChart);
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
		composite.setLayout(new GridLayout(8, false));
		//
		labelStatus = createLabelStatus(composite);
		comboOverlayType = createOverlayTypeCombo(composite);
		comboViewerDerivative = createDerivativeComboViewer(composite);
		createToggleToolbarShiftButton(composite);
		createToggleChartLegendButton(composite);
		createResetButton(composite);
		createNewOverlayPartButton(composite);
		createSettingsButton(composite);
		//
		return composite;
	}

	private NamedTracesUI createNamedTraces(Composite parent) {

		NamedTracesUI namedTracesUI = new NamedTracesUI(parent, SWT.NONE);
		namedTracesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		namedTracesUI.setInput(new NamedTraces(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES)));
		namedTracesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				NamedTraces namedTraces = namedTracesUI.getNamedTraces();
				if(namedTraces != null) {
					preferenceStore.putValue(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES, namedTraces.save());
					chromatogramChart.deleteSeries();
					refreshUpdateOverlayChart();
				}
			}
		});
		return namedTracesUI;
	}

	private DataShiftControllerUI createDataShiftControllerUI(Composite parent) {

		DataShiftControllerUI dataShiftControllerUI = new DataShiftControllerUI(parent, SWT.NONE);
		dataShiftControllerUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return dataShiftControllerUI;
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
					return derivative.getLabel();
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

	private Button createToggleToolbarShiftButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the data shift toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(dataShiftControllerUI);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_DEFAULT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleSeriesLegendVisibility();
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

		createSettingsButton(parent, Arrays.asList(PreferencePageOverlay.class, PreferencePageNamedTraces.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void modifyDataStatusLabel() {

		if(chromatogramChart.getBaseChart().isDataShifted()) {
			labelStatus.setText("The displayed data is shifted.");
			labelStatus.setBackground(Colors.YELLOW);
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
		if(preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS)) {
			if(isExtractedIonsModusEnabled() || isExtractedWavelengthsModusEnabled()) {
				PartSupport.setCompositeVisibility(namedTracesUI, true);
			} else {
				PartSupport.setCompositeVisibility(namedTracesUI, false);
			}
		}
		//
		if(isExtractedIonsModusEnabled()) {
			namedTracesUI.setEnabled(true);
		} else if(isExtractedWavelengthsModusEnabled()) {
			namedTracesUI.setEnabled(true);
		} else {
			namedTracesUI.setEnabled(false);
		}
		//
		dataShiftControllerUI.update();
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
		chromatogramChart.deleteSeries();
		refreshUpdateOverlayChart();
		modifyWidgetStatus();
		modifyDataStatusLabel();
	}

	private void updateNamedTraces() {

		namedTracesUI.setInput(new NamedTraces(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES)));
	}

	private ChromatogramChart createOverlayChart(Composite parent) {

		ChromatogramChart chromatogramChart = new ChromatogramChart(parent, SWT.NONE);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		chartSettings.setSupportDataShift(true);
		chartSettings.getRangeRestriction().setZeroY(false);
		chartSettings.setBufferSelection(preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_BUFFERED_SELECTION));
		chromatogramChart.applySettings(chartSettings);
		//
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addSeriesModificationListener(new ISeriesModificationListener() {

			@Override
			public void handleSeriesModificationEvent() {

				modifyDataStatusLabel();
			}
		});
		//
		return chromatogramChart;
	}

	@SuppressWarnings("rawtypes")
	private void refreshUpdateOverlayChart() {

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
			chartSettings.setBufferSelection(preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_BUFFERED_SELECTION));
			chromatogramChart.applySettings(chartSettings);
			//
			IAxisSet axisSet = chromatogramChart.getBaseChart().getAxisSet();
			Range xrange = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
			Range yrange = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
			Set<String> availableSeriesIds = new HashSet<>();
			BaseChart baseChart = chromatogramChart.getBaseChart();
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			int i = 0;
			for(Entry<IChromatogramSelection, List<String>> entry : chromatogramSelections.entrySet()) {
				IChromatogramSelection<?, ?> chromatogramSelection = entry.getKey();
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
			dataShiftControllerUI.reset();
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
		BaseChart baseChart = chromatogramChart.getBaseChart();
		Derivative derivative = getSelectedDerivative();
		/*
		 * SIC
		 */
		List<Number> ions = getSelectedTraces(true);
		if(chromatogram instanceof IChromatogramMSD) {
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
					lineSeriesData.getSettings().setDescription("m/z " + Integer.toString(ion) + " (" + chromatogram.getName() + ")");
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
							ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedIons, false);
							lineSeriesData.getSettings().setDescription("m/z " + Integer.toString(ion) + " (" + chromatogram.getName() + ")");
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
		BaseChart baseChart = chromatogramChart.getBaseChart();
		Derivative derivative = getSelectedDerivative();
		/*
		 * SWC
		 */
		List<Number> wavelengths = getSelectedTraces(false);
		if(chromatogram instanceof IChromatogramWSD) {
			//
			for(Number number : wavelengths) {
				//
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
					lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + chromatogram.getName() + ")");
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
						String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
						for(Number number : wavelengths) {
							//
							double wavelength = number.doubleValue();
							seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
							availableSeriesIds.add(seriesId);
							selectionSeries.add(seriesId);
							color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
							IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
							markedWavelengths.add(wavelength);
							//
							if(!baseChart.isSeriesContained(seriesId)) {
								ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
								lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + chromatogram.getName() + ")");
								lineSeriesDataList.add(lineSeriesData);
							}
						}
					}
				}
			}
		}
	}

	private void appendXWC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		String seriesId;
		Color color;
		//
		BaseChart baseChart = chromatogramChart.getBaseChart();
		Derivative derivative = getSelectedDerivative();
		/*
		 * XWC
		 */
		if(chromatogram instanceof IChromatogramWSD) {
			//
			for(double wavelength : ((IChromatogramWSD)chromatogram).getWavelengths()) {
				//
				seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
				availableSeriesIds.add(seriesId);
				selectionSeries.add(seriesId);
				color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(wavelength);
				//
				if(!baseChart.isSeriesContained(seriesId)) {
					ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
					lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + chromatogram.getName() + ")");
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
						String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
						for(double wavelength : ((IChromatogramWSD)referencedChromatogram).getWavelengths()) {
							//
							seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
							availableSeriesIds.add(seriesId);
							selectionSeries.add(seriesId);
							color = chromatogramChartSupport.getSeriesColor(seriesId, displayType);
							IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
							markedWavelengths.add(wavelength);
							//
							if(!baseChart.isSeriesContained(seriesId)) {
								ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedWavelengths, false);
								lineSeriesData.getSettings().setDescription(Double.toString(wavelength) + " nm (" + chromatogram.getName() + ")");
								lineSeriesDataList.add(lineSeriesData);
							}
						}
					}
				}
			}
		}
	}

	private void appendXXC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		BaseChart baseChart = chromatogramChart.getBaseChart();
		Derivative derivative = getSelectedDerivative();
		String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
		Color color = chromatogramChartSupport.getSeriesColor(chromatogramName, displayType);
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
			availableSeriesIds.add(seriesId);
			selectionSeries.add(seriesId);
			if(!baseChart.isSeriesContained(seriesId)) {
				ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, displayType, derivative, color, markedIons, false);
				lineSeriesData.getSettings().setDescription(chromatogram.getName() + " (" + displayType.getShortcut() + ")");
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
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
					availableSeriesIds.add(referenceSeriesId);
					selectionSeries.add(seriesId);
					if(!baseChart.isSeriesContained(referenceSeriesId)) {
						color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, displayType);
						ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, displayType, derivative, color, markedIons, false);
						lineSeriesData.getSettings().setDescription(chromatogram.getName() + " (" + displayType.getShortcut() + ")");
						lineSeriesDataList.add(lineSeriesData);
					}
				}
			}
		}
	}

	private void appendTIC(Set<String> availableSeriesIds, List<String> selectionSeries, List<ILineSeriesData> lineSeriesDataList, IChromatogram<?> chromatogram, DisplayType displayType, String chromatogramName) {

		BaseChart baseChart = chromatogramChart.getBaseChart();
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
			lineSeriesData.getSettings().setDescription(chromatogram.getName());
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
					String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
					String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + displayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
					availableSeriesIds.add(referenceSeriesId);
					selectionSeries.add(referenceSeriesId);
					if(!baseChart.isSeriesContained(referenceSeriesId)) {
						color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, displayType);
						ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, displayType, derivative, color, null, false);
						lineSeriesData.getSettings().setDescription(chromatogram.getName());
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
		NamedTrace namedTrace = namedTracesUI.getNamedTrace();
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
}
