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
import java.util.EnumSet;
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
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChartConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlayChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TraceValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageNamedTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageOverlay;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.Derivative;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.DisplayModus;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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

public class ExtendedChromatogramOverlayUI implements ConfigurableUI<ChromatogramOverlayUIConfig> {

	private NamedTracesUI namedTracesUI;
	private DataShiftControllerUI dataShiftControllerUI;
	private ChromatogramChart chromatogramChart;
	//
	private Composite toolbarMain;
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
	private final int style;
	private boolean lockZoom = false;

	public ExtendedChromatogramOverlayUI(Composite parent) {
		this(parent, SWT.BORDER);
	}

	public ExtendedChromatogramOverlayUI(Composite parent, int style) {
		this.style = style;
		initialize(parent);
	}

	@SuppressWarnings("rawtypes")
	public void update(List<IChromatogramSelection> chromatogramSelections) {

		this.chromatogramSelections.clear();
		for(IChromatogramSelection selection : chromatogramSelections) {
			this.chromatogramSelections.put(selection, new ArrayList<>());
		}
		refreshUpdateOverlayChart();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		toolbarMain = createToolbarMain(parent);
		namedTracesUI = createNamedTraces(parent);
		dataShiftControllerUI = createDataShiftControllerUI(parent);
		chromatogramChart = createOverlayChart(parent);
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
		parent.layout(true);
	}

	private void setDisplayModus(DisplayModus displayModus, String seriesId) {

		dataShiftControllerUI.setDisplayModus(displayModus, seriesId);
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

		// PartSupport.setCompositeVisibility(dataShiftControllerUI, false);
		// dataShiftControllerUI.reset();
		// buttonToolbarShift.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_DEFAULT, IApplicationImage.SIZE_16x16));
		// PartSupport.setCompositeVisibility(namedTracesUI, false);
		// comboOverlayType.select(0);
		// comboViewerDerivative.getCombo().select(0);
		applyOverlaySettings();
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
			 * Getting the items via the EclipseContext failed here.
			 */
			EModelService modelService = ModelSupportAddon.getModelService();
			MApplication application = ModelSupportAddon.getApplication();
			EPartService partService = ModelSupportAddon.getPartService();
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

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageOverlay()));
				preferenceManager.addToRoot(new PreferenceNode("2", new PreferencePageNamedTraces()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				//
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
		Set<DisplayType> types = getOverlayType();
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

	private void applyOverlaySettings() {

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

		ChromatogramChart chromatogramChart = new ChromatogramChart(parent, style);
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

	@SuppressWarnings({"rawtypes", "unchecked"})
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
				IChromatogramSelection chromatogramSelection = entry.getKey();
				List<String> selectionSeries = entry.getValue();
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				List<IChromatogram> referencedChromatograms = chromatogram.getReferencedChromatograms();
				String chromatogramName = chromatogram.getName() + ChromatogramChartSupport.EDITOR_TAB + (i + 1);
				/*
				 * refreshUpdateOverlayChart
				 * Select which series shall be displayed.
				 */
				Set<DisplayType> displayTypes = getOverlayType();
				Derivative derivative = getSelectedDerivative();
				if(derivative != null) {
					//
					for(DisplayType overlayType : displayTypes) {
						if(overlayType.equals(DisplayType.SIC)) {
							/*
							 * SIC
							 */
							List<Number> ions = getSelectedTraces(true);
							if(chromatogram instanceof IChromatogramMSD) {
								for(Number number : ions) {
									int ion = number.intValue();
									String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + ion + OverlayChartSupport.OVERLAY_STOP_MARKER;
									Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
									availableSeriesIds.add(seriesId);
									selectionSeries.add(seriesId);
									if(!baseChart.isSeriesContained(seriesId)) {
										IMarkedIons markedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
										markedIons.add(ion);
										lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, markedIons, false));
									}
								}
							}
							/*
							 * References
							 */
							if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
								int j = 1;
								for(IChromatogram referencedChromatogram : referencedChromatograms) {
									if(referencedChromatogram instanceof IChromatogramMSD) {
										for(Number number : ions) {
											int ion = number.intValue();
											String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
											String seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + ion + OverlayChartSupport.OVERLAY_STOP_MARKER;
											Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
											availableSeriesIds.add(seriesId);
											selectionSeries.add(seriesId);
											if(!baseChart.isSeriesContained(seriesId)) {
												IMarkedIons markedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
												markedIons.add(ion);
												lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, markedIons, false));
											}
										}
									}
								}
							}
						} else if(overlayType.equals(DisplayType.SWC)) {
							/*
							 * SWC
							 */
							List<Number> wavelengths = getSelectedTraces(false);
							if(chromatogram instanceof IChromatogramWSD) {
								//
								for(Number number : wavelengths) {
									//
									double wavelength = number.doubleValue();
									String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
									availableSeriesIds.add(seriesId);
									selectionSeries.add(seriesId);
									Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
									IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
									markedWavelengths.add(wavelength);
									//
									if(!baseChart.isSeriesContained(seriesId)) {
										lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, markedWavelengths, false));
									}
								}
								/*
								 * References
								 */
								if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
									int j = 1;
									for(IChromatogram referencedChromatogram : referencedChromatograms) {
										if(referencedChromatogram instanceof IChromatogramWSD) {
											String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
											for(Number number : wavelengths) {
												//
												double wavelength = number.doubleValue();
												String seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
												availableSeriesIds.add(seriesId);
												selectionSeries.add(seriesId);
												Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
												IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
												markedWavelengths.add(wavelength);
												//
												if(!baseChart.isSeriesContained(seriesId)) {
													lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, markedWavelengths, false));
												}
											}
										}
									}
								}
							}
						} else if(overlayType.equals(DisplayType.XWC)) {
							/*
							 * AWC
							 */
							if(chromatogram instanceof IChromatogramWSD) {
								//
								for(double wavelength : ((IChromatogramWSD)chromatogram).getWavelengths()) {
									//
									String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
									availableSeriesIds.add(seriesId);
									selectionSeries.add(seriesId);
									Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
									IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
									markedWavelengths.add(wavelength);
									//
									if(!baseChart.isSeriesContained(seriesId)) {
										lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, markedWavelengths, false));
									}
								}
								/*
								 * References
								 */
								if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
									int j = 1;
									for(IChromatogram referencedChromatogram : referencedChromatograms) {
										if(referencedChromatogram instanceof IChromatogramWSD) {
											String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
											for(double wavelength : ((IChromatogramWSD)referencedChromatogram).getWavelengths()) {
												//
												String seriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + wavelength + OverlayChartSupport.OVERLAY_STOP_MARKER;
												availableSeriesIds.add(seriesId);
												selectionSeries.add(seriesId);
												Color color = chromatogramChartSupport.getSeriesColor(seriesId, overlayType);
												IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
												markedWavelengths.add(wavelength);
												//
												if(!baseChart.isSeriesContained(seriesId)) {
													lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, markedWavelengths, false));
												}
											}
										}
									}
								}
							}
						} else {
							//
							String seriesId = chromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
							Color color = chromatogramChartSupport.getSeriesColor(chromatogramName, overlayType);
							//
							if(overlayType.equals(DisplayType.BPC) || overlayType.equals(DisplayType.XIC) || overlayType.equals(DisplayType.TSC)) {
								/*
								 * BPC, XIC, TSC
								 */
								IMarkedIons markedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
								List ions = getSelectedTraces(true);
								markedIons.add(ions);
								//
								if(chromatogram instanceof IChromatogramMSD) {
									availableSeriesIds.add(seriesId);
									selectionSeries.add(seriesId);
									if(!baseChart.isSeriesContained(seriesId)) {
										lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, markedIons, false));
									}
								}
								/*
								 * References
								 */
								if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
									int j = 1;
									for(IChromatogram referencedChromatogram : referencedChromatograms) {
										if(referencedChromatogram instanceof IChromatogramMSD) {
											String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
											String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
											availableSeriesIds.add(referenceSeriesId);
											selectionSeries.add(seriesId);
											if(!baseChart.isSeriesContained(referenceSeriesId)) {
												color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, overlayType);
												lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, overlayType, derivative, color, markedIons, false));
											}
										}
									}
								}
							} else {
								/*
								 * TIC
								 */
								availableSeriesIds.add(seriesId);
								selectionSeries.add(seriesId);
								if(!baseChart.isSeriesContained(seriesId)) {
									lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(chromatogram, seriesId, overlayType, derivative, color, null, false));
								}
								/*
								 * References
								 */
								if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS)) {
									int j = 1;
									for(IChromatogram referencedChromatogram : referencedChromatograms) {
										if(referencedChromatogram != null) {
											String referenceChromatogramName = chromatogramName + ChromatogramChartSupport.REFERENCE_MARKER + j++;
											String referenceSeriesId = referenceChromatogramName + OverlayChartSupport.OVERLAY_START_MARKER + overlayType + OverlayChartSupport.DELIMITER_SIGNAL_DERIVATIVE + derivative + OverlayChartSupport.OVERLAY_STOP_MARKER;
											availableSeriesIds.add(referenceSeriesId);
											selectionSeries.add(referenceSeriesId);
											if(!baseChart.isSeriesContained(referenceSeriesId)) {
												color = chromatogramChartSupport.getSeriesColor(referenceChromatogramName, overlayType);
												lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesData(referencedChromatogram, referenceSeriesId, overlayType, derivative, color, null, false));
											}
										}
									}
								}
							}
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

	private Derivative getSelectedDerivative() {

		Object object = comboViewerDerivative.getStructuredSelection().getFirstElement();
		if(object instanceof Derivative) {
			return (Derivative)object;
		}
		return null;
	}

	private List<Number> getSelectedTraces(boolean isNominal) {

		List<Number> traceList = new ArrayList<>();
		NamedTrace namedTrace = namedTracesUI.getNamedTrace();
		if(namedTrace != null) {
			TraceValidator traceValidator = new TraceValidator();
			IStatus status = traceValidator.validate(namedTrace.getTraces());
			if(status.isOK()) {
				List<Double> traces = traceValidator.getTraces();
				if(isNominal) {
					Set<Integer> tracesInt = new HashSet<>();
					for(double trace : traces) {
						tracesInt.add(Math.round((float)trace));
					}
					traceList.addAll(tracesInt);
				} else {
					traceList.addAll(traces);
				}
			}
		}
		//
		return traceList;
	}

	private Set<DisplayType> getOverlayType() {

		return DisplayType.toDisplayTypes(comboOverlayType.getText().trim());
	}

	private boolean isExtractedIonsModusEnabled() {

		Set<DisplayType> overlayType = getOverlayType();
		return overlayType.contains(DisplayType.XIC) || //
				overlayType.contains(DisplayType.SIC) || //
				overlayType.contains(DisplayType.TSC);
	}

	private boolean isExtractedWavelengthsModusEnabled() {

		return getOverlayType().contains(DisplayType.SWC);
	}

	@Override
	public ChromatogramOverlayUIConfig getConfig() {

		return new ChromatogramOverlayUIConfig() {

			ChartConfigSupport chartConfigSupport = new ChartConfigSupport(chromatogramChart, EnumSet.of(ChartAxis.PRIMARY_X, ChartAxis.PRIMARY_Y, ChartAxis.SECONDARY_Y));

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain, visible);
			}

			@Override
			public boolean isToolbarVisible() {

				return toolbarMain.isVisible();
			}

			@Override
			public void setAxisLabelVisible(ChartAxis axis, boolean visible) {

				chartConfigSupport.setAxisLabelVisible(axis, visible);
			}

			@Override
			public void setAxisVisible(ChartAxis axis, boolean visible) {

				chartConfigSupport.setAxisVisible(axis, visible);
			}

			@Override
			public boolean hasAxis(ChartAxis axis) {

				return chartConfigSupport.hasAxis(axis);
			}

			@Override
			public void setDisplayModus(DisplayModus modus, IChromatogramSelection<?, ?> selection) {

				List<String> list = chromatogramSelections.get(selection);
				if(list != null) {
					for(String id : list) {
						if(modus == DisplayModus.MIRRORED) {
							ExtendedChromatogramOverlayUI.this.setDisplayModus(DisplayModus.MIRRORED, id);
						} else {
							ExtendedChromatogramOverlayUI.this.setDisplayModus(DisplayModus.NORMAL, id);
						}
					}
				}
			}
		};
	}

	public void setZoomLocked(boolean lockZoom) {

		this.lockZoom = lockZoom;
	}
}
