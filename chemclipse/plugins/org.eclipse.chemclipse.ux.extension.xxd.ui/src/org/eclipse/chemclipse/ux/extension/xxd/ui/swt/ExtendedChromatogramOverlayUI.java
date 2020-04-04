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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.AxisConfig.ChartAxis;
import org.eclipse.chemclipse.ux.extension.xxd.ui.traces.NamedTrace;
import org.eclipse.chemclipse.ux.extension.xxd.ui.traces.NamedTraces;
import org.eclipse.chemclipse.ux.extension.xxd.ui.traces.NamedTracesUI;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisScaleConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ISeriesModificationListener;
import org.eclipse.swtchart.extensions.core.SeriesStatusAdapter;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;

public class ExtendedChromatogramOverlayUI implements ConfigurableUI<ChromatogramOverlayUIConfig> {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramOverlayUI.class);
	/*
	 * Mirror Button
	 */
	private static final String BUTTON_MIRROR_KEY = "DisplayModus";
	private static final String normalTooltip = "Set the selected series to normal modus.";
	private static final String mirrorTooltip = "Set the selected series to mirrored modus.";
	//
	private NamedTracesUI namedTracesUI;
	private Composite toolbarShift;
	private ChromatogramChart chromatogramChart;
	//
	private Combo comboOverlayType;
	private ComboViewer comboViewerDerivative;
	private Combo comboSelectedSeries;
	//
	private Text textShiftX;
	private Combo comboScaleX;
	private Button buttonShiftLeft;
	private Button buttonShiftRight;
	private Text textShiftY;
	private Combo comboScaleY;
	private Button buttonShiftUp;
	private Button buttonShiftDown;
	private Button buttonMirrorSeries;
	private Label labelDataStatus;
	//
	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private final OverlayChartSupport overlayChartSupport = new OverlayChartSupport();
	private final Set<String> mirroredSeries = new HashSet<>();
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	@SuppressWarnings("rawtypes")
	private final Map<IChromatogramSelection, List<String>> chromatogramSelections = new LinkedHashMap<>();
	private Composite toolbarMain;
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
		createToolbarMain(parent);
		namedTracesUI = createNamedTraces(parent);
		toolbarShift = createToolbarShift(parent);
		createOverlayChart(parent);
		//
		PartSupport.setCompositeVisibility(namedTracesUI, false);
		PartSupport.setCompositeVisibility(toolbarShift, false);
		//
		modifyWidgetStatus();
		setDerivatives();
	}

	private void createToolbarMain(Composite parent) {

		toolbarMain = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		toolbarMain.setLayoutData(gridData);
		toolbarMain.setLayout(new GridLayout(11, false));
		//
		createDataStatusLabel(toolbarMain);
		comboOverlayType = createOverlayTypeCombo(toolbarMain);
		comboViewerDerivative = createDerivativeComboViewer(toolbarMain);
		createButtonToggleToolbarShift(toolbarMain);
		createToggleChartLegendButton(toolbarMain);
		createButtonAutoMirror(toolbarMain);
		createButtonShiftY(toolbarMain);
		createButtonShiftXY(toolbarMain);
		createResetButton(toolbarMain);
		createNewOverlayPartButton(toolbarMain);
		createSettingsButton(toolbarMain);
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

	private Composite createToolbarShift(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(12, false));
		//
		createTextShiftX(composite);
		createComboScaleX(composite);
		createTextShiftY(composite);
		createComboScaleY(composite);
		createVerticalSeparator(composite);
		comboSelectedSeries = createSelectedSeriesCombo(composite);
		buttonMirrorSeries = createButtonMirror(composite);
		createButtonLeft(composite);
		createButtonRight(composite);
		createButtonUp(composite);
		createButtonDown(composite);
		createVerticalSeparator(composite);
		//
		return composite;
	}

	private Combo createOverlayTypeCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select the overlay type");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
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
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 60;
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				refreshUpdateOverlayChart();
			}
		});
		//
		return comboViewer;
	}

	private Combo createSelectedSeriesCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Highlight the selected series");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		combo.setItems(new String[]{BaseChart.SELECTED_SERIES_NONE});
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = combo.getSelectionIndex();
				comboSelectedSeries.select(index);
				String selectedSeriesId = combo.getText().trim();
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.resetSeriesSettings();
				baseChart.selectSeries(selectedSeriesId);
				baseChart.redraw();
				modifyWidgetStatus();
			}
		});
		//
		return combo;
	}

	private void createButtonAutoMirror(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Auto Mirror Chromatograms");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_AUTO_MIRROR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				//
				int i = 0;
				for(ISeries series : baseChart.getSeriesSet().getSeries()) {
					if(i % 2 == 1) {
						String seriesId = series.getId();
						if(!mirroredSeries.contains(seriesId)) {
							baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
							mirroredSeries.add(seriesId);
						}
					}
					i++;
				}
				//
				chromatogramChart.applySettings(chartSettings);
				chromatogramChart.adjustRange(true);
				chromatogramChart.redraw();
			}
		});
	}

	private Button createButtonMirror(Composite parent) {

		//
		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(""); // Will be set dynamically
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_MIRROR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(button.getData(BUTTON_MIRROR_KEY) != null) {
					/*
					 * Get the display modus.
					 */
					String displayModus = button.getData(BUTTON_MIRROR_KEY).toString();
					setDisplayModus(displayModus, getSelectedSeriesId());
				}
			}
		});
		//
		return button;
	}

	protected void setDisplayModus(String displayModus, String seriesId) {

		BaseChart baseChart = chromatogramChart.getBaseChart();
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		if(displayModus.equals(OverlayChartSupport.DISPLAY_MODUS_MIRRORED)) {
			if(!mirroredSeries.contains(seriesId)) {
				baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
				mirroredSeries.add(seriesId);
			}
		} else {
			if(mirroredSeries.contains(seriesId)) {
				baseChart.multiplySeries(seriesId, IExtendedChart.Y_AXIS, -1.0d);
				mirroredSeries.remove(seriesId);
			}
		}
		//
		modifyWidgetStatus();
		chromatogramChart.applySettings(chartSettings);
		chromatogramChart.adjustRange(true);
		chromatogramChart.redraw();
	}

	private void createTextShiftX(Composite parent) {

		textShiftX = new Text(parent, SWT.BORDER);
		textShiftX.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		textShiftX.setLayoutData(gridData);
	}

	private void createComboScaleX(Composite parent) {

		comboScaleX = new Combo(parent, SWT.READ_ONLY);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		comboScaleX.setLayoutData(gridData);
	}

	private void createButtonLeft(Composite parent) {

		buttonShiftLeft = new Button(parent, SWT.PUSH);
		buttonShiftLeft.setToolTipText("Move Left");
		buttonShiftLeft.setText("");
		buttonShiftLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		buttonShiftLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftX = getShiftValuePrimary(IExtendedChart.X_AXIS) * -1.0d;
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
				persistOverlayShiftX();
			}
		});
	}

	private void createButtonRight(Composite parent) {

		buttonShiftRight = new Button(parent, SWT.PUSH);
		buttonShiftRight.setToolTipText("Move Right");
		buttonShiftRight.setText("");
		buttonShiftRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		buttonShiftRight.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftX = getShiftValuePrimary(IExtendedChart.X_AXIS);
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
				persistOverlayShiftX();
			}
		});
	}

	private void createTextShiftY(Composite parent) {

		textShiftY = new Text(parent, SWT.BORDER);
		textShiftY.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		textShiftY.setLayoutData(gridData);
	}

	private void createComboScaleY(Composite parent) {

		comboScaleY = new Combo(parent, SWT.READ_ONLY);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		comboScaleY.setLayoutData(gridData);
	}

	private void createButtonUp(Composite parent) {

		buttonShiftUp = new Button(parent, SWT.PUSH);
		buttonShiftUp.setToolTipText("Move Up");
		buttonShiftUp.setText("");
		buttonShiftUp.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		buttonShiftUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
				baseChart.redraw();
				persistOverlayShiftY();
			}
		});
	}

	private void createButtonDown(Composite parent) {

		buttonShiftDown = new Button(parent, SWT.PUSH);
		buttonShiftDown.setToolTipText("Move Down");
		buttonShiftDown.setText("");
		buttonShiftDown.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		buttonShiftDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftY = getShiftValuePrimary(IExtendedChart.Y_AXIS) * -1.0d;
				String selectedSeriesId = getSelectedSeriesId();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
				baseChart.redraw();
				persistOverlayShiftY();
			}
		});
	}

	private void createButtonShiftY(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Shift Y");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_Y, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS)) {
					PartSupport.setCompositeVisibility(toolbarShift, true);
				}
				//
				applyOverlaySettings();
				//
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.suspendUpdate(true);
				double shiftY = 0.0d;
				double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
				for(ISeries series : baseChart.getSeriesSet().getSeries()) {
					shiftY += deltaY;
					String seriesId = series.getId();
					baseChart.shiftSeries(seriesId, 0.0d, shiftY);
				}
				baseChart.suspendUpdate(false);
				baseChart.redraw();
				persistOverlayShiftY();
			}
		});
	}

	private void createButtonShiftXY(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Shift XY");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHIFT_XY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(preferenceStore.getBoolean(PreferenceConstants.P_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS)) {
					PartSupport.setCompositeVisibility(toolbarShift, true);
				}
				//
				applyOverlaySettings();
				//
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.suspendUpdate(true);
				double shiftX = 0.0d;
				double shiftY = 0.0d;
				double deltaX = getShiftValuePrimary(IExtendedChart.X_AXIS);
				double deltaY = getShiftValuePrimary(IExtendedChart.Y_AXIS);
				for(ISeries series : baseChart.getSeriesSet().getSeries()) {
					shiftX += deltaX;
					shiftY += deltaY;
					String seriesId = series.getId();
					baseChart.shiftSeries(seriesId, shiftX, shiftY);
				}
				baseChart.suspendUpdate(false);
				baseChart.redraw();
				persistOverlayShiftX();
				persistOverlayShiftY();
			}
		});
	}

	private void createDataStatusLabel(Composite parent) {

		labelDataStatus = new Label(parent, SWT.NONE);
		labelDataStatus.setToolTipText("Indicates whether the data has been modified or not.");
		labelDataStatus.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 120;
		labelDataStatus.setLayoutData(gridData);
	}

	private void createButtonToggleToolbarShift(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle shift toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_SHIFT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarShift);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_SHIFT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_SHIFT, IApplicationImage.SIZE_16x16));
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

				PartSupport.setCompositeVisibility(toolbarShift, false);
				applyOverlaySettings();
			}
		});
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
			MPart part = MBasicFactory.INSTANCE.createPart();
			part.setLabel(name);
			part.setCloseable(true);
			part.setContributionURI("bundleclass://" + bundle + "/" + classPath);
			//
			MPartStack partStack = PartSupport.getPartStack(partStackId, ModelSupportAddon.getModelService(), ModelSupportAddon.getApplication());
			partStack.getChildren().add(part);
			PartSupport.showPart(part, ModelSupportAddon.getPartService());
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
			labelDataStatus.setText("Shifted Data");
			labelDataStatus.setBackground(Colors.YELLOW);
		} else {
			labelDataStatus.setText("");
			labelDataStatus.setBackground(null);
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
			namedTracesUI.setEnabled(false);
		} else {
			namedTracesUI.setEnabled(false);
		}
		/*
		 * Selected Series
		 */
		String selectedSeries = getSelectedSeriesId();
		boolean isSeriesSelected = !selectedSeries.equals(BaseChart.SELECTED_SERIES_NONE);
		buttonShiftLeft.setEnabled(isSeriesSelected);
		buttonShiftRight.setEnabled(isSeriesSelected);
		buttonShiftUp.setEnabled(isSeriesSelected);
		buttonShiftDown.setEnabled(isSeriesSelected);
		//
		buttonMirrorSeries.setEnabled(isSeriesSelected);
		if(mirroredSeries.contains(selectedSeries)) {
			buttonMirrorSeries.setData(BUTTON_MIRROR_KEY, OverlayChartSupport.DISPLAY_MODUS_NORMAL);
			buttonMirrorSeries.setToolTipText(normalTooltip);
		} else {
			buttonMirrorSeries.setData(BUTTON_MIRROR_KEY, OverlayChartSupport.DISPLAY_MODUS_MIRRORED);
			buttonMirrorSeries.setToolTipText(mirrorTooltip);
		}
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
		mirroredSeries.clear();
		refreshUpdateOverlayChart();
		modifyWidgetStatus();
		modifyDataStatusLabel();
	}

	private void updateNamedTraces() {

		namedTracesUI.setInput(new NamedTraces(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_NAMED_TRACES)));
	}

	private void createOverlayChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, style);
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
		baseChart.addSeriesStatusListener(new SeriesStatusAdapter() {

			@Override
			public void handleSeriesSelectionEvent(String seriesId) {

				comboSelectedSeries.setText(seriesId);
				modifyWidgetStatus();
			}
		});
		//
		setComboAxisItems();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void refreshUpdateOverlayChart() {

		if(chromatogramSelections.size() > 0) {
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
			baseChart.resetSeriesSettings();
			String[] items = new String[availableSeriesIds.size() + 1];
			items[0] = BaseChart.SELECTED_SERIES_NONE;
			int index = 1;
			for(String seriesId : availableSeriesIds) {
				items[index++] = seriesId;
			}
			setSelectedSeries(items, BaseChart.SELECTED_SERIES_NONE);
			//
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

	public void setComboAxisItems() {

		/*
		 * X Axes
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		String[] axisLabelsX = baseChart.getAxisLabels(IExtendedChart.X_AXIS);
		comboScaleX.setItems(axisLabelsX);
		if(axisLabelsX.length > 0) {
			/*
			 * Get the shift value from the settings.
			 */
			double overlayShiftX = overlayChartSupport.getOverlayShiftX();
			int indexShiftX = overlayChartSupport.getIndexShiftX();
			//
			if(indexShiftX >= 0 && indexShiftX < axisLabelsX.length) {
				DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, indexShiftX);
				comboScaleX.select(indexShiftX);
				textShiftX.setText(decimalFormat.format(overlayShiftX));
			} else {
				/*
				 * Default: Milliseconds
				 */
				comboScaleX.select(0);
				textShiftX.setText(Integer.toString(0));
			}
		}
		/*
		 * Y Axes
		 */
		String[] axisLabelsY = baseChart.getAxisLabels(IExtendedChart.Y_AXIS);
		comboScaleY.setItems(axisLabelsY);
		if(axisLabelsY.length > 0) {
			/*
			 * Get the shift value from the settings.
			 */
			double absoluteShiftY = overlayChartSupport.getOverlayShiftY();
			int indexShiftY = overlayChartSupport.getIndexShiftY();
			//
			if(indexShiftY >= 0 && indexShiftY < axisLabelsY.length) {
				DecimalFormat decimalFormat = baseChart.getDecimalFormat(IExtendedChart.Y_AXIS, indexShiftY);
				comboScaleY.select(indexShiftY);
				textShiftY.setText(decimalFormat.format(absoluteShiftY));
			} else {
				/*
				 * Default: Absolute Intensity
				 */
				comboScaleY.select(0);
				textShiftY.setText(Double.toString(0.0d));
			}
		}
	}

	private double getShiftValuePrimary(String axis) {

		double shiftValue = 0.0d;
		BaseChart baseChart = chromatogramChart.getBaseChart();
		/*
		 * Get the selected axis.
		 */
		int selectedAxis;
		if(axis.equals(IExtendedChart.X_AXIS)) {
			selectedAxis = comboScaleX.getSelectionIndex();
		} else {
			selectedAxis = comboScaleY.getSelectionIndex();
		}
		/*
		 * Get the value.
		 */
		double value;
		if(axis.equals(IExtendedChart.X_AXIS)) {
			value = getTextValue(textShiftX);
		} else {
			value = getTextValue(textShiftY);
		}
		/*
		 * Convert the range on demand.
		 */
		if(selectedAxis == 0) {
			shiftValue = value;
		} else {
			IAxisScaleConverter axisScaleConverter = baseChart.getAxisScaleConverter(axis, selectedAxis);
			shiftValue = axisScaleConverter.convertToPrimaryUnit(value);
		}
		//
		return shiftValue;
	}

	private String getSelectedSeriesId() {

		return comboSelectedSeries.getText().trim();
	}

	private void setSelectedSeries(String[] items, String text) {

		comboSelectedSeries.setItems(items);
		comboSelectedSeries.setText(text);
		modifyWidgetStatus();
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

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private double getTextValue(Text text) {

		double value = 0.0d;
		try {
			value = Double.parseDouble(text.getText().trim());
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return value;
	}

	private void persistOverlayShiftX() {

		overlayChartSupport.setOverlayShiftX(getTextValue(textShiftX));
		overlayChartSupport.setIndexShiftX(comboScaleX.getSelectionIndex());
	}

	private void persistOverlayShiftY() {

		overlayChartSupport.setOverlayShiftY(getTextValue(textShiftY));
		overlayChartSupport.setIndexShiftY(comboScaleY.getSelectionIndex());
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
							ExtendedChromatogramOverlayUI.this.setDisplayModus(OverlayChartSupport.DISPLAY_MODUS_MIRRORED, id);
						} else {
							ExtendedChromatogramOverlayUI.this.setDisplayModus(OverlayChartSupport.DISPLAY_MODUS_NORMAL, id);
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
