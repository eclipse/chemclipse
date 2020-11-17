/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use {@link IPlotArea} instead of raw composite
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.listener.BoxSelectionPaintListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ManualPeakDetector;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.BaselineSelectionPaintListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

@SuppressWarnings("rawtypes")
public class ExtendedPeakDetectorUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedPeakDetectorUI.class);
	//
	private static final String ID_PEAK = "Peak";
	private static final String ID_BACKGROUND = "Background";
	/*
	 * Detection Types
	 */
	private static final String DETECTION_TYPE_BASELINE = "DETECTION_TYPE_BASELINE";
	private static final String DETECTION_TYPE_BOX = "DETECTION_TYPE_BOX";
	private static final String DETECTION_TYPE_BOX_BB = DETECTION_TYPE_BOX + "_BB";
	private static final String DETECTION_TYPE_BOX_VV = DETECTION_TYPE_BOX + "_VV";
	private static final String DETECTION_TYPE_BOX_BV = DETECTION_TYPE_BOX + "_BV";
	private static final String DETECTION_TYPE_BOX_VB = DETECTION_TYPE_BOX + "_VB";
	private static final String DETECTION_TYPE_NONE = "";
	//
	private Map<String, String> detectionTypeDescriptions;
	//
	private static final char KEY_BASELINE = IKeyboardSupport.KEY_CODE_LC_D;
	private static final char KEY_BB = IKeyboardSupport.KEY_CODE_LC_B;
	private static final char KEY_VV = IKeyboardSupport.KEY_CODE_LC_V;
	private static final char KEY_BV = IKeyboardSupport.KEY_CODE_LC_N;
	private static final char KEY_VB = IKeyboardSupport.KEY_CODE_LC_C;
	/*
	 * Detection Box
	 */
	private static final String DETECTION_BOX_LEFT = "DETECTION_BOX_LEFT";
	private static final String DETECTION_BOX_RIGHT = "DETECTION_BOX_RIGHT";
	private static final String DETECTION_BOX_NONE = "DETECTION_BOX_NONE";
	//
	private static final int BOX_SNAP_MARKER_WINDOW = 4;
	private static final int BOX_MAX_DELTA = 1;
	//
	private static final int STATUS_DETECTION_HINT_NONE = -1;
	private static final int STATUS_DETECTION_HINT_INACTIVE = 0;
	private static final int STATUS_DETECTION_HINT_ACTIVE = 1;
	private static final String MESSAGE_DETECTION_MODUS = "CTRL";
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Label labelDetectionType;
	private Label labelDetectionModus;
	private Button buttonDetectionTypeBaseline;
	private Button buttonDetectionTypeBoxBB;
	private Button buttonDetectionTypeBoxVV;
	private Button buttonDetectionTypeBoxBV;
	private Button buttonDetectionTypeBoxVB;
	private Button buttonAddPeak;
	private ChromatogramChart chromatogramChart;
	//
	private IChromatogramSelection chromatogramSelection;
	private IPeak peak;
	//
	private BaselineSelectionPaintListener baselineSelectionPaintListener;
	private BoxSelectionPaintListener boxSelectionPaintListener;
	//
	private Cursor defaultCursor;
	private String detectionType = DETECTION_TYPE_NONE;
	private String detectionBox = DETECTION_BOX_NONE;
	/*
	 * Baseline / Box Detection Method
	 */
	private int xStart;
	private int yStart;
	private int xStop;
	private int yStop;
	private int xBoxMoveStart;
	//
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private PeakChartSupport peakChartSupport = new PeakChartSupport();

	private class KeyPressedEventProcessor extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		private int keyCode;

		public KeyPressedEventProcessor(int keyCode) {

			this.keyCode = keyCode;
		}

		@Override
		public int getEvent() {

			return IKeyboardSupport.EVENT_KEY_DOWN;
		}

		@Override
		public int getButton() {

			return keyCode;
		}

		@Override
		public int getStateMask() {

			return SWT.NONE;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleKeyPressedEvent(event);
		}
	}

	private class MouseDownEventProcessor extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return IMouseSupport.EVENT_MOUSE_DOWN;
		}

		@Override
		public int getButton() {

			return IMouseSupport.MOUSE_BUTTON_LEFT;
		}

		@Override
		public int getStateMask() {

			return SWT.CTRL;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleMouseDownEvent(event);
		}
	}

	private class MouseMoveEventProcessor extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return IMouseSupport.EVENT_MOUSE_MOVE;
		}

		@Override
		public int getStateMask() {

			return SWT.CTRL;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleMouseMoveEvent(event);
		}
	}

	private class MouseUpEventProcessor extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return IMouseSupport.EVENT_MOUSE_UP;
		}

		@Override
		public int getButton() {

			return IMouseSupport.MOUSE_BUTTON_LEFT;
		}

		@Override
		public int getStateMask() {

			return SWT.BUTTON1;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleMouseUpEvent(event);
		}
	}

	private class MouseDoubleClickEventProcessor extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return IMouseSupport.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getButton() {

			return IMouseSupport.MOUSE_BUTTON_LEFT;
		}

		@Override
		public int getStateMask() {

			return SWT.NONE;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleMouseDoubleClickEvent(event);
		}
	}

	@Inject
	public ExtendedPeakDetectorUI(Composite parent, int style) {

		super(parent, SWT.NONE);
		detectionTypeDescriptions = new HashMap<String, String>();
		detectionTypeDescriptions.put(DETECTION_TYPE_BASELINE, "Modus (Baseline) [Key:" + KEY_BASELINE + "]");
		detectionTypeDescriptions.put(DETECTION_TYPE_BOX_BB, "Modus (BB) [Key:" + KEY_BB + "]");
		detectionTypeDescriptions.put(DETECTION_TYPE_BOX_VV, "Modus (VV) [Key:" + KEY_VV + "]");
		detectionTypeDescriptions.put(DETECTION_TYPE_BOX_BV, "Modus (BV) [Key:" + KEY_BV + "]");
		detectionTypeDescriptions.put(DETECTION_TYPE_BOX_VB, "Modus (VB) [Key:" + KEY_VB + "]");
		detectionTypeDescriptions.put(DETECTION_TYPE_NONE, "");
		//
		createControl();
	}

	public boolean setFocus() {

		updateChromatogramAndPeak();
		return true;
	}

	public void update(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		IChromatogram chromatogram = null;
		if(chromatogramSelection != null) {
			chromatogram = chromatogramSelection.getChromatogram();
		}
		//
		setDetectionType(DETECTION_TYPE_NONE);
		toolbarInfo.get().setText(ChromatogramDataSupport.getChromatogramLabel(chromatogram));
		this.peak = null;
		//
		updateChromatogramAndPeak();
	}

	private void updateChromatogramAndPeak() {

		chromatogramChart.deleteSeries();
		buttonAddPeak.setEnabled(false);
		enableButtons(DETECTION_TYPE_NONE);
		//
		if(chromatogramSelection != null) {
			//
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			/*
			 * Chromatogram
			 */
			Color colorChromatogram = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_DETECTOR_CHROMATOGRAM));
			boolean enableAreaChromatogram = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAK_DETECTOR_CHROMATOGRAM_AREA);
			int scanMarkerSize = preferenceStore.getInt(PreferenceConstants.P_PEAK_DETECTOR_SCAN_MARKER_SIZE);
			Color scanMarkerColor = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_PEAK_DETECTOR_SCAN_MARKER_COLOR));
			PlotSymbolType scanMarkerSymbol = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_PEAK_DETECTOR_SCAN_MARKER_TYPE));
			//
			ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesDataChromatogram(chromatogramSelection, "Chromatogram", colorChromatogram);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setEnableArea(enableAreaChromatogram);
			lineSeriesSettings.setSymbolSize(scanMarkerSize);
			lineSeriesSettings.setSymbolColor(scanMarkerColor);
			lineSeriesSettings.setSymbolType(scanMarkerSymbol);
			lineSeriesDataList.add(lineSeriesData);
			//
			if(peak != null) {
				/*
				 * Peak
				 */
				buttonAddPeak.setEnabled(true);
				boolean includeBackground = true;
				boolean mirrored = false;
				Color colorPeak = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_1));
				lineSeriesDataList.add(peakChartSupport.getPeak(peak, includeBackground, mirrored, colorPeak, ID_PEAK));
				if(includeBackground) {
					Color colorBackground = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BACKGROUND));
					lineSeriesDataList.add(peakChartSupport.getPeakBackground(peak, mirrored, colorBackground, ID_BACKGROUND));
				}
			}
			//
			chromatogramChart.addSeriesData(lineSeriesDataList, LineChart.LOW_COMPRESSION);
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createChromatogramChart(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(12, false));
		//
		labelDetectionType = createDetectionTypeLabel(composite);
		labelDetectionModus = createDetectionModusLabel(composite);
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonDetectionTypeBaseline = createDetectionTypeButton(composite, DETECTION_TYPE_BASELINE, IApplicationImage.IMAGE_DETECTION_TYPE_BASELINE);
		buttonDetectionTypeBoxBB = createDetectionTypeButton(composite, DETECTION_TYPE_BOX_BB, IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_BB);
		buttonDetectionTypeBoxVV = createDetectionTypeButton(composite, DETECTION_TYPE_BOX_VV, IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_VV);
		buttonDetectionTypeBoxBV = createDetectionTypeButton(composite, DETECTION_TYPE_BOX_BV, IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_BV);
		buttonDetectionTypeBoxVB = createDetectionTypeButton(composite, DETECTION_TYPE_BOX_VB, IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_VB);
		buttonAddPeak = createAddPeakButton(composite);
		createToggleChartLegendButton(composite);
		createDetectionTypeButton(composite, DETECTION_TYPE_NONE, IApplicationImage.IMAGE_RESET);
		createSettingsButton(composite);
	}

	private Label createDetectionTypeLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		return label;
	}

	private Label createDetectionModusLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(MESSAGE_DETECTION_MODUS);
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		label.setLayoutData(gridData);
		return label;
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private Button createDetectionTypeButton(Composite parent, String detectionType, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(detectionTypeDescriptions.get(detectionType));
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setDetectionType(detectionType);
				if(detectionType.equals(DETECTION_TYPE_NONE)) {
					reset();
				}
			}
		});
		return button;
	}

	private Button createAddPeakButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the manually detected peak.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(peak != null) {
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					if(chromatogram instanceof IChromatogramMSD) {
						if(peak instanceof IChromatogramPeakMSD) {
							IChromatogramPeakMSD peakMSD = (IChromatogramPeakMSD)peak;
							((IChromatogramMSD)chromatogram).addPeak(peakMSD);
							peak = null;
							setDetectionType(DETECTION_TYPE_NONE);
							updateChromatogramAndPeak();
							chromatogramSelection.update(true);
						}
					} else if(chromatogram instanceof IChromatogramCSD) {
						if(peak instanceof IChromatogramPeakCSD) {
							IChromatogramPeakCSD peakCSD = (IChromatogramPeakCSD)peak;
							((IChromatogramCSD)chromatogram).addPeak(peakCSD);
							peak = null;
							setDetectionType(DETECTION_TYPE_NONE);
							updateChromatogramAndPeak();
							chromatogramSelection.update(true);
						}
					}
				}
			}
		});
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

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePeaks.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Get the default cursor.
		 */
		defaultCursor = chromatogramChart.getBaseChart().getCursor();
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		chartSettings.setSupportDataShift(false);
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(KEY_BASELINE));
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(KEY_BB));
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(KEY_VV));
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(KEY_BV));
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(KEY_VB));
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new MouseDownEventProcessor());
		chartSettings.addHandledEventProcessor(new MouseMoveEventProcessor());
		chartSettings.addHandledEventProcessor(new MouseUpEventProcessor());
		chartSettings.addHandledEventProcessor(new MouseDoubleClickEventProcessor());
		chromatogramChart.applySettings(chartSettings);
		/*
		 * Add the paint listeners to draw the selected peak range.
		 */
		IPlotArea plotArea = getPlotArea();
		baselineSelectionPaintListener = new BaselineSelectionPaintListener();
		boxSelectionPaintListener = new BoxSelectionPaintListener();
		plotArea.addCustomPaintListener(baselineSelectionPaintListener);
		plotArea.addCustomPaintListener(boxSelectionPaintListener);
	}

	private void setDetectionType(String detectionType) {

		/*
		 * Defaults
		 */
		this.detectionType = detectionType;
		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			setDetectionModusLabel(STATUS_DETECTION_HINT_NONE);
			setCursorDefault();
			resetSelectedRange();
		} else {
			if(detectionType.startsWith(DETECTION_TYPE_BOX)) {
				setDetectionModusLabel(STATUS_DETECTION_HINT_NONE);
			} else {
				setDetectionModusLabel(STATUS_DETECTION_HINT_INACTIVE);
			}
		}
		/*
		 * Conditions
		 */
		if(detectionType.equals(DETECTION_TYPE_NONE) || detectionType.equals(DETECTION_TYPE_BASELINE)) {
			this.detectionBox = DETECTION_BOX_NONE;
		}
		/*
		 * Label / Buttons
		 */
		enableButtons(detectionType);
		if(!detectionType.equals(DETECTION_TYPE_BOX)) {
			labelDetectionType.setText(detectionTypeDescriptions.get(detectionType));
		}
	}

	private void enableButtons(String detectionType) {

		boolean enabled = detectionType.equals(DETECTION_TYPE_NONE);
		buttonDetectionTypeBaseline.setEnabled(enabled);
		buttonDetectionTypeBoxBB.setEnabled(enabled);
		buttonDetectionTypeBoxVV.setEnabled(enabled);
		buttonDetectionTypeBoxBV.setEnabled(enabled);
		buttonDetectionTypeBoxVB.setEnabled(enabled);
	}

	private void resetSelectedRange() {

		baselineSelectionPaintListener.reset();
		boxSelectionPaintListener.reset();
		//
		xStart = 0;
		yStart = 0;
		xStop = 0;
		yStop = 0;
		xBoxMoveStart = 0;
	}

	private void handleKeyPressedEvent(Event event) {

		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			if(event.keyCode == KEY_BASELINE) {
				setDetectionType(DETECTION_TYPE_BASELINE);
			} else if(event.keyCode == KEY_BB) {
				setDetectionType(DETECTION_TYPE_BOX_BB);
			} else if(event.keyCode == KEY_VV) {
				setDetectionType(DETECTION_TYPE_BOX_VV);
			} else if(event.keyCode == KEY_BV) {
				setDetectionType(DETECTION_TYPE_BOX_BV);
			} else if(event.keyCode == KEY_VB) {
				setDetectionType(DETECTION_TYPE_BOX_VB);
			}
		} else if(detectionType.startsWith(DETECTION_TYPE_BOX)) {
			if(event.keyCode == SWT.ARROW_LEFT) {
				if(detectionBox.equals(DETECTION_BOX_LEFT)) {
					xStart -= 1;
					redrawBoxPeakSelection(true);
				} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
					xStop -= 1;
					redrawBoxPeakSelection(true);
				}
			} else if(event.keyCode == SWT.ARROW_RIGHT) {
				if(detectionBox.equals(DETECTION_BOX_LEFT)) {
					xStart += 1;
					redrawBoxPeakSelection(true);
				} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
					xStop += 1;
					redrawBoxPeakSelection(true);
				}
			}
		}
	}

	private void handleMouseDownEvent(Event event) {

		if(detectionType.equals(DETECTION_TYPE_BASELINE)) {
			setDetectionModusLabel(STATUS_DETECTION_HINT_ACTIVE);
			startBaselineSelection(event.x, event.y);
			setCursor(SWT.CURSOR_CROSS);
		} else if(detectionType.startsWith(DETECTION_TYPE_BOX)) {
			setDetectionModusLabel(STATUS_DETECTION_HINT_ACTIVE);
			if(isLeftMoveSnapMarker(event.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
				xBoxMoveStart = event.x;
				detectionBox = DETECTION_BOX_LEFT;
			} else if(isRightMoveSnapMarker(event.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
				xBoxMoveStart = event.x;
				detectionBox = DETECTION_BOX_RIGHT;
			} else {
				setCursor(SWT.CURSOR_CROSS);
				detectionBox = DETECTION_BOX_NONE;
			}
		} else {
			setDetectionModusLabel(STATUS_DETECTION_HINT_NONE);
		}
	}

	private void handleMouseMoveEvent(Event event) {

		if(detectionType.equals(DETECTION_TYPE_BASELINE)) {
			if(isControlKeyPressed(event)) {
				if(xStart > 0 && yStart > 0) {
					trackBaselineSelection(event.x, event.y);
				}
			}
		} else if(detectionType.startsWith(DETECTION_TYPE_BOX)) {
			/*
			 * Cursor
			 */
			if(isLeftMoveSnapMarker(event.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
			} else if(isRightMoveSnapMarker(event.x)) {
				setCursor(SWT.CURSOR_SIZEWE);
			} else {
				setCursor(SWT.CURSOR_CROSS);
			}
			//
			if(isLeftMouseButtonPressed(event)) {
				if(!detectionBox.equals(DETECTION_BOX_NONE)) {
					int delta = getDeltaMove(event.x);
					if(detectionBox.equals(DETECTION_BOX_LEFT)) {
						xStart += delta;
						redrawBoxPeakSelection(false);
					} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
						xStop += delta;
						redrawBoxPeakSelection(false);
					}
				}
			}
		}
	}

	private void handleMouseUpEvent(Event event) {

		if(detectionType.equals(DETECTION_TYPE_BASELINE)) {
			if(isControlKeyPressed(event)) {
				stopBaselineSelection(event.x, event.y);
				setCursorDefault();
			}
		} else if(detectionType.startsWith(DETECTION_TYPE_BOX)) {
			if(event.button == IMouseSupport.MOUSE_BUTTON_LEFT) {
				setCursor(SWT.CURSOR_CROSS);
				if(!detectionBox.equals(DETECTION_BOX_NONE)) {
					int delta = getDeltaMove(event.x);
					if(detectionBox.equals(DETECTION_BOX_LEFT)) {
						xStart += delta;
						redrawBoxPeakSelection(true);
					} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
						xStop += delta;
						redrawBoxPeakSelection(true);
					}
				}
				if(event.count == 1) {
					detectionBox = getDetectionBox(event.x);
					redrawBoxPeakSelection(false);
				}
			}
		} else {
			setDetectionModusLabel(STATUS_DETECTION_HINT_NONE);
		}
	}

	private boolean isControlKeyPressed(Event event) {

		return (event.stateMask & SWT.CTRL) == SWT.CTRL;
	}

	private boolean isLeftMouseButtonPressed(Event event) {

		return (event.stateMask & SWT.BUTTON1) == SWT.BUTTON1;
	}

	private void handleMouseDoubleClickEvent(Event event) {

		if(detectionType.startsWith(DETECTION_TYPE_BOX)) {
			setDetectionModusLabel(STATUS_DETECTION_HINT_INACTIVE);
			setCursor(SWT.CURSOR_CROSS);
			if(xStart == 0) {
				int y;
				switch(detectionType) {
					case DETECTION_TYPE_BOX_BB:
						y = getPlotArea().getSize().y;
						break;
					case DETECTION_TYPE_BOX_VV:
						y = event.y;
						break;
					case DETECTION_TYPE_BOX_BV:
						y = getPlotArea().getSize().y;
						break;
					case DETECTION_TYPE_BOX_VB:
						y = event.y;
						break;
					default:
						y = getPlotArea().getSize().y;
				}
				startBoxPeakSelection(event.x, y);
			} else if(xStart > 0 && xStop == 0) {
				int y;
				switch(detectionType) {
					case DETECTION_TYPE_BOX_BB:
						y = getPlotArea().getSize().y;
						break;
					case DETECTION_TYPE_BOX_VV:
						y = event.y;
						break;
					case DETECTION_TYPE_BOX_BV:
						y = getPlotArea().getSize().y;
						break;
					case DETECTION_TYPE_BOX_VB:
						y = event.y;
						break;
					default:
						y = getPlotArea().getSize().y;
				}
				stopBoxPeakSelection(event.x, y);
			} else {
				setDetectionType(DETECTION_TYPE_NONE);
			}
		} else {
			setDetectionModusLabel(STATUS_DETECTION_HINT_NONE);
		}
	}

	private IPlotArea getPlotArea() {

		return chromatogramChart.getBaseChart().getPlotArea();
	}

	private void applySettings() {

		updateChromatogramAndPeak();
	}

	public void redraw() {

		chromatogramChart.getBaseChart().redraw();
	}

	private void setCursor(int cursorId) {

		chromatogramChart.getBaseChart().setCursor(DisplayUtils.getDisplay().getSystemCursor(cursorId));
	}

	private void setCursorDefault() {

		chromatogramChart.getBaseChart().setCursor(defaultCursor);
	}

	private String getDetectionBox(int x) {

		if(xStart > 0) {
			if(x <= xStart) {
				return DETECTION_BOX_LEFT;
			} else {
				if(xStop > 0) {
					if(x >= xStop) {
						return DETECTION_BOX_RIGHT;
					}
				}
			}
		}
		return DETECTION_BOX_NONE;
	}

	private boolean isLeftMoveSnapMarker(int x) {

		if(x > xStart - BOX_SNAP_MARKER_WINDOW && x < xStart + BOX_SNAP_MARKER_WINDOW) {
			return true;
		}
		return false;
	}

	private boolean isRightMoveSnapMarker(int x) {

		if(x > xStop - BOX_SNAP_MARKER_WINDOW && x < xStop + BOX_SNAP_MARKER_WINDOW) {
			return true;
		}
		return false;
	}

	private int getDeltaMove(int x) {

		int delta = x - xBoxMoveStart;
		if(Math.abs(delta) > BOX_MAX_DELTA) {
			if(delta < 0) {
				delta = -BOX_MAX_DELTA;
			} else {
				delta = BOX_MAX_DELTA;
			}
		}
		return delta;
	}

	private void startBaselineSelection(int x, int y) {

		xStart = x;
		yStart = y;
		/*
		 * Set the start point.
		 */
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
	}

	private void trackBaselineSelection(int x, int y) {

		xStop = x;
		yStop = y;
		//
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
		baselineSelectionPaintListener.setX2(xStop);
		baselineSelectionPaintListener.setY2(yStop);
		//
		redraw();
	}

	private void stopBaselineSelection(int x, int y) {

		xStop = x;
		yStop = y;
		//
		extractPeak(DETECTION_TYPE_NONE);
	}

	private void startBoxPeakSelection(int x, int y) {

		xStart = x;
		yStart = y;
		//
		setCursor(SWT.CURSOR_CROSS);
		boxSelectionPaintListener.setX1(xStart);
		boxSelectionPaintListener.setX2(xStop);
		//
		redraw();
	}

	private void stopBoxPeakSelection(int x, int y) {

		if(x > xStart) {
			xStop = x;
			yStop = y;
			boxSelectionPaintListener.setX1(xStart);
			boxSelectionPaintListener.setX2(xStop);
			//
			redraw();
			//
			extractPeak(DETECTION_TYPE_BOX);
			enableButtons(DETECTION_TYPE_BOX);
		}
	}

	private void redrawBoxPeakSelection(boolean extractPeak) {

		boxSelectionPaintListener.setX1(xStart);
		boxSelectionPaintListener.setX2(xStop);
		//
		if(detectionBox.equals(DETECTION_BOX_LEFT)) {
			boxSelectionPaintListener.setHighlightBox(BoxSelectionPaintListener.HIGHLIGHT_BOX_LEFT);
		} else if(detectionBox.equals(DETECTION_BOX_RIGHT)) {
			boxSelectionPaintListener.setHighlightBox(BoxSelectionPaintListener.HIGHLIGHT_BOX_RIGHT);
		} else {
			boxSelectionPaintListener.setHighlightBox(BoxSelectionPaintListener.HIGHLIGHT_BOX_NONE);
		}
		//
		redraw();
		/*
		 * Extract the peak.
		 */
		if(extractPeak) {
			extractPeak(DETECTION_TYPE_BOX);
			enableButtons(DETECTION_TYPE_BOX);
		}
	}

	private void extractPeak(String detectionType) {

		this.peak = extractPeakFromUserSelection(xStart, yStart, xStop, yStop);
		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
		}
		/*
		 * Get the selected range.
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		IAxisSet axisSet = baseChart.getAxisSet();
		IAxis xAxis = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
		Range xRange = xAxis.getRange();
		IAxis yAxis = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
		Range yRange = yAxis.getRange();
		/*
		 * Update the chromatogram and peak
		 */
		setDetectionType(detectionType);
		updateChromatogramAndPeak();
		/*
		 * Restore the selected range.
		 */
		xAxis.setRange(xRange);
		yAxis.setRange(yRange);
		redraw();
	}

	/**
	 * Extracts the selected peak.
	 * 
	 * @param xStop
	 * @param yStop
	 */
	private IPeak extractPeakFromUserSelection(int xStart, int yStart, int xStop, int yStop) {

		IPeak peak = null;
		/*
		 * Calculate the rectangle factors.
		 */
		Point size = getPlotArea().getSize();
		int height = size.y;
		double factorHeight = 100.0d / height;
		int width = size.x;
		double factorWidth = 100.0d / width;
		/*
		 * Calculate the percentage heights and widths.
		 */
		double percentageStartHeight = (100.0d - (factorHeight * yStart)) / 100.0d;
		double percentageStopHeight = (100.0d - (factorHeight * yStop)) / 100.0d;
		double percentageStartWidth = (factorWidth * xStart) / 100.0d;
		double percentageStopWidth = (factorWidth * xStop) / 100.0d;
		/*
		 * Calculate the start and end points.
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		IAxis retentionTime = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
		Range millisecondsRange = retentionTime.getRange();
		IAxis intensity = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
		Range abundanceRange = intensity.getRange();
		/*
		 * With abundance and retention time.
		 */
		double abundanceHeight = abundanceRange.upper - abundanceRange.lower;
		double millisecondsWidth = millisecondsRange.upper - millisecondsRange.lower;
		/*
		 * Peak start and stop abundances and retention times.
		 */
		int startRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStartWidth);
		int stopRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStopWidth);
		float startAbundance = (float)(abundanceRange.lower + abundanceHeight * percentageStartHeight);
		float stopAbundance = (float)(abundanceRange.lower + abundanceHeight * percentageStopHeight);
		/*
		 * Try to detect the peak.
		 */
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			/*
			 * Peak Detection MSD
			 */
			try {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				ManualPeakDetector manualPeakDetector = new ManualPeakDetector();
				IChromatogramMSD chromatogram = chromatogramSelectionMSD.getChromatogram();
				IChromatogramPeakMSD chromatogramPeak = manualPeakDetector.calculatePeak(chromatogram, startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				peak = chromatogramPeak;
			} catch(PeakException e) {
				logger.warn(e);
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			/*
			 * Peak Detection CSD
			 */
			try {
				IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
				ManualPeakDetector manualPeakDetector = new ManualPeakDetector();
				IChromatogramCSD chromatogram = chromatogramSelectionCSD.getChromatogram();
				IChromatogramPeakCSD chromatogramPeak = manualPeakDetector.calculatePeak(chromatogram, startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				peak = chromatogramPeak;
			} catch(PeakException e) {
				logger.warn(e);
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			/*
			 * Peak Detection WSD
			 */
			try {
				IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
				ManualPeakDetector manualPeakDetector = new ManualPeakDetector();
				IChromatogramWSD chromatogram = chromatogramSelectionWSD.getChromatogram();
				IChromatogramPeakWSD chromatogramPeak = manualPeakDetector.calculatePeak(chromatogram, startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				peak = chromatogramPeak;
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		//
		return peak;
	}

	private void reset() {

		this.peak = null;
		setDetectionType(DETECTION_TYPE_NONE);
		updateChromatogramAndPeak();
	}

	private void setDetectionModusLabel(int status) {

		switch(status) {
			case 0:
				labelDetectionModus.setText(MESSAGE_DETECTION_MODUS);
				labelDetectionModus.setBackground(Colors.YELLOW);
				break;
			case 1:
				labelDetectionModus.setText(MESSAGE_DETECTION_MODUS);
				labelDetectionModus.setBackground(Colors.GREEN);
				break;
			default:
				labelDetectionModus.setText("");
				labelDetectionModus.setBackground(null);
		}
	}
}
