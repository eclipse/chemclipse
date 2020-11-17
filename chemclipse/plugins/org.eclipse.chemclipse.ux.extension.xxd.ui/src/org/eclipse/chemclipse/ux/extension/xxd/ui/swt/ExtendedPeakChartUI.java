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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.listener.SplitSelectionPaintListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ManualPeakDetector;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;
import org.eclipse.swtchart.extensions.core.IMouseSupport;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

@SuppressWarnings("rawtypes")
public class ExtendedPeakChartUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedPeakChartUI.class);
	//
	private static final String DETECTION_TYPE_TANGENT = "DETECTION_TYPE_TANGENT";
	private static final String DETECTION_TYPE_PERPENDICULAR = "DETECTION_TYPE_TYPE_PERPENDICULAR";
	private static final String DETECTION_TYPE_NONE = "";
	//
	private Map<String, String> detectionTypeDescriptions;
	//
	private static final char KEY_TANGENT = IKeyboardSupport.KEY_CODE_LC_T;
	private static final char KEY_PERPENDICULAR = IKeyboardSupport.KEY_CODE_LC_P;
	//
	private Composite toolbarInfo;
	private Label labelPeak;
	private Label labelDetectionType;
	private Button buttonDetectionTypeTangent;
	private Button buttonDetectionTypePerpendicular;
	private Button buttonAddPeak;
	private PeakChartUI peakChart;
	//
	private IPeak peak = null; // Original Peak
	private IPeak peakSplitted1 = null; // #1 after split
	private IPeak peakSplitted2 = null; // #2 after split
	private SplitSelectionPaintListener splitSelectionPaintListener;
	//
	private int xStart;
	private int xStop;
	//
	private String detectionType = DETECTION_TYPE_NONE;
	//
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

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

	public ExtendedPeakChartUI(Composite parent, int style) {

		super(parent, style);
		detectionTypeDescriptions = new HashMap<String, String>();
		detectionTypeDescriptions.put(DETECTION_TYPE_TANGENT, "Modus (Tangent) [Key:" + KEY_TANGENT + "]");
		detectionTypeDescriptions.put(DETECTION_TYPE_PERPENDICULAR, "Modus (Perpendicular) [Key:" + KEY_PERPENDICULAR + "]");
		detectionTypeDescriptions.put(DETECTION_TYPE_NONE, "");
		createControl();
	}

	public boolean setFocus() {

		updatePeaks();
		return true;
	}

	public void update(IPeak peak) {

		this.peak = peak;
		resetSplittedPeaks();
		labelPeak.setText(peakDataSupport.getPeakLabel(peak));
		//
		if(peak instanceof IChromatogramPeakCSD || peak instanceof IChromatogramPeakMSD) {
			buttonDetectionTypeTangent.setEnabled(true);
			buttonDetectionTypePerpendicular.setEnabled(true);
		} else {
			buttonDetectionTypeTangent.setEnabled(false);
			buttonDetectionTypePerpendicular.setEnabled(false);
		}
		updatePeaks();
	}

	private void updatePeaks() {

		if(peakSplitted1 == null && peakSplitted2 == null) {
			peakChart.setInput(peak);
		} else {
			peakChart.setInput(peakSplitted1, peakSplitted2);
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		toolbarInfo = createToolbarInfo(this);
		createPeakChart(this);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(9, false));
		//
		labelDetectionType = createDetectionTypeLabel(composite);
		createButtonToggleToolbarInfo(composite);
		buttonDetectionTypeTangent = createDetectionTypeTangentButton(composite);
		buttonDetectionTypePerpendicular = createDetectionTypePerpendicularButton(composite);
		buttonAddPeak = createAddPeakButton(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createResetButton(composite);
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

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelPeak = new Label(composite, SWT.NONE);
		labelPeak.setText("");
		labelPeak.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createDetectionTypeTangentButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Use Tangent Skim.");
		button.setText("");
		button.setEnabled(false);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DETECTION_TYPE_TANGENT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setDetectionType(DETECTION_TYPE_TANGENT);
			}
		});
		//
		return button;
	}

	private Button createDetectionTypePerpendicularButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Use Perpendicular Drop.");
		button.setText("");
		button.setEnabled(false);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DETECTION_TYPE_PERPENDICULAR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setDetectionType(DETECTION_TYPE_PERPENDICULAR);
			}
		});
		//
		return button;
	}

	private Button createAddPeakButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the splitted peaks.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(peak instanceof IChromatogramPeakMSD) {
					IChromatogramPeakMSD peakMSD = (IChromatogramPeakMSD)peak;
					IChromatogramMSD chromatogram = peakMSD.getChromatogram();
					if(chromatogram != null) {
						addPeaks(chromatogram, peak, peakSplitted1, peakSplitted2);
						peak = peakSplitted1;
						resetSplittedPeaks();
						setDetectionType(DETECTION_TYPE_NONE);
						updatePeaks();
					}
				} else if(peak instanceof IChromatogramPeakCSD) {
					IChromatogramPeakCSD peakCSD = (IChromatogramPeakCSD)peak;
					IChromatogramCSD chromatogram = peakCSD.getChromatogram();
					if(chromatogram != null) {
						addPeaks(chromatogram, peak, peakSplitted1, peakSplitted2);
						peak = peakSplitted1;
						resetSplittedPeaks();
						setDetectionType(DETECTION_TYPE_NONE);
						updatePeaks();
					}
				}
			}
		});
		return button;
	}

	private void addPeaks(IChromatogram chromatogram, IPeak peakOriginal, IPeak peak1, IPeak peak2) {

		if(peak1 != null || peak2 != null) {
			removePeakFromChromatogram(chromatogram, peakOriginal);
			addPeakToChromatogram(chromatogram, peak1);
			addPeakToChromatogram(chromatogram, peak2);
		}
	}

	private void removePeakFromChromatogram(IChromatogram chromatogram, IPeak peak) {

		if(peak != null) {
			if(chromatogram instanceof IChromatogramMSD && peak instanceof IChromatogramPeakMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				chromatogramMSD.removePeak((IChromatogramPeakMSD)peak);
			} else if(chromatogram instanceof IChromatogramCSD && peak instanceof IChromatogramPeakCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				chromatogramCSD.removePeak((IChromatogramPeakCSD)peak);
			}
		}
	}

	private void addPeakToChromatogram(IChromatogram chromatogram, IPeak peak) {

		if(peak != null) {
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				chromatogramMSD.addPeak((IChromatogramPeakMSD)peak);
			} else if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				chromatogramCSD.addPeak((IChromatogramPeakCSD)peak);
			}
		}
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				peakChart.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createToggleLegendMarkerButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend marker.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_LEGEND_MARKER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IChartSettings chartSettings = peakChart.getChartSettings();
				boolean isShowLegendMarker = chartSettings.isShowLegendMarker();
				chartSettings.setShowLegendMarker(!isShowLegendMarker);
				peakChart.applySettings(chartSettings);
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

				setDetectionType(DETECTION_TYPE_NONE);
				reset();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePeaks.class, PreferencePageScans.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createPeakChart(Composite parent) {

		peakChart = new PeakChartUI(parent, SWT.BORDER);
		peakChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = peakChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setShowPositionMarker(true);
		chartSettings.setShowLegendMarker(false);
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(KEY_TANGENT));
		chartSettings.addHandledEventProcessor(new KeyPressedEventProcessor(KEY_PERPENDICULAR));
		chartSettings.addHandledEventProcessor(new MouseDoubleClickEventProcessor());
		peakChart.applySettings(chartSettings);
		//
		IPlotArea plotArea = getPlotArea();
		splitSelectionPaintListener = new SplitSelectionPaintListener();
		plotArea.addCustomPaintListener(splitSelectionPaintListener);
	}

	private void reset() {

		resetSplittedPeaks();
		updatePeaks();
	}

	private void applySettings() {

		updatePeaks();
	}

	private void setDetectionType(String detectionType) {

		/*
		 * Defaults
		 */
		this.detectionType = detectionType;
		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			resetSelectedRange();
		}
		/*
		 * Label / Buttons
		 */
		enableButtons(detectionType);
		labelDetectionType.setText(detectionTypeDescriptions.get(detectionType));
	}

	private void resetSelectedRange() {

		xStart = 0;
		xStop = 0;
	}

	private void enableButtons(String detectionType) {

		boolean enabled = detectionType.equals(DETECTION_TYPE_NONE);
		buttonAddPeak.setEnabled((peakSplitted1 != null || peakSplitted2 != null));
		buttonDetectionTypeTangent.setEnabled(enabled);
		buttonDetectionTypePerpendicular.setEnabled(enabled);
	}

	private void handleKeyPressedEvent(Event event) {

		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			if(event.keyCode == KEY_TANGENT) {
				setDetectionType(DETECTION_TYPE_TANGENT);
			} else if(event.keyCode == KEY_PERPENDICULAR) {
				setDetectionType(DETECTION_TYPE_PERPENDICULAR);
			}
		}
	}

	private void handleMouseDoubleClickEvent(Event event) {

		if(detectionType.equals(DETECTION_TYPE_TANGENT)) {
			if(xStart == 0) {
				xStart = event.x;
				splitSelectionPaintListener.setX1(event.x);
				splitSelectionPaintListener.setY1(event.y);
			} else {
				if(event.x > xStart) {
					xStop = event.x;
				} else {
					xStop = xStart;
					xStart = event.x;
				}
				splitSelectionPaintListener.setX2(event.x);
				splitSelectionPaintListener.setY2(event.y);
				splitPeak();
			}
		} else if(detectionType.equals(DETECTION_TYPE_PERPENDICULAR)) {
			xStart = event.x;
			xStop = xStart;
			splitSelectionPaintListener.setX1(event.x);
			splitSelectionPaintListener.setY1(event.y);
			splitSelectionPaintListener.setX2(event.x);
			splitSelectionPaintListener.setY2(event.y);
			splitPeak();
		}
	}

	private void splitPeak() {

		if(peak != null) {
			/*
			 * Calculate the position.
			 */
			Rectangle rectangle = getPlotArea().getBounds();
			int width = rectangle.width;
			double factorWidth = 100.0d / width;
			BaseChart baseChart = peakChart.getBaseChart();
			IAxis retentionTime = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			Range millisecondsRange = retentionTime.getRange();
			double millisecondsWidth = millisecondsRange.upper - millisecondsRange.lower;
			//
			if(xStart == xStop) {
				/*
				 * Perpendicular Drop
				 */
				int startRetentionTime;
				int stopRetentionTime;
				float startAbundance;
				float stopAbundance;
				//
				double percentageDrop = (factorWidth * xStart) / 100.0d;
				int dropRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageDrop);
				IPeakModel peakModel = peak.getPeakModel();
				/*
				 * Peak 1
				 */
				startRetentionTime = peakModel.getStartRetentionTime();
				stopRetentionTime = dropRetentionTime;
				startAbundance = peakModel.getBackgroundAbundance(startRetentionTime);
				stopAbundance = peakModel.getBackgroundAbundance(stopRetentionTime);
				peakSplitted1 = extractPeakByCoordinates(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				/*
				 * Peak 2
				 */
				startRetentionTime = dropRetentionTime;
				stopRetentionTime = peakModel.getStopRetentionTime();
				startAbundance = peakModel.getBackgroundAbundance(startRetentionTime);
				stopAbundance = peakModel.getBackgroundAbundance(stopRetentionTime);
				peakSplitted2 = extractPeakByCoordinates(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				//
				if(peakSplitted1 != null || peakSplitted2 != null) {
					buttonAddPeak.setEnabled(true);
				}
			} else {
				/*
				 * Tangent Skim
				 */
				float startAbundance;
				float stopAbundance;
				//
				double percentageStartWidth = (factorWidth * xStart) / 100.0d;
				double percentageStopWidth = (factorWidth * xStop) / 100.0d;
				int startRetentionTimeSkim = (int)(millisecondsRange.lower + millisecondsWidth * percentageStartWidth);
				int stopRetentionTimeSkim = (int)(millisecondsRange.lower + millisecondsWidth * percentageStopWidth);
				//
				IPeakModel peakModel = peak.getPeakModel();
				int retentionTimeAtMaximum = peakModel.getRetentionTimeAtPeakMaximum();
				//
				if(startRetentionTimeSkim < retentionTimeAtMaximum && stopRetentionTimeSkim < retentionTimeAtMaximum) {
					/*
					 * Left Skim Peak 1
					 */
					startAbundance = peakModel.getBackgroundAbundance(startRetentionTimeSkim);
					stopAbundance = peakModel.getBackgroundAbundance(stopRetentionTimeSkim);
					peakSplitted1 = extractPeakByCoordinates(startRetentionTimeSkim, stopRetentionTimeSkim, startAbundance, stopAbundance);
					/*
					 * Right Normal Peak 2
					 */
					int startRetentionTime = stopRetentionTimeSkim;
					int stopRetentionTime = peakModel.getStopRetentionTime();
					startAbundance = peakModel.getBackgroundAbundance(startRetentionTime);
					stopAbundance = peakModel.getBackgroundAbundance(stopRetentionTime);
					peakSplitted2 = extractPeakByCoordinates(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				} else if(startRetentionTimeSkim > retentionTimeAtMaximum && stopRetentionTimeSkim > retentionTimeAtMaximum) {
					/*
					 * Right Skim Peak 1
					 */
					startAbundance = peakModel.getBackgroundAbundance(startRetentionTimeSkim);
					stopAbundance = peakModel.getBackgroundAbundance(stopRetentionTimeSkim);
					peakSplitted1 = extractPeakByCoordinates(startRetentionTimeSkim, stopRetentionTimeSkim, startAbundance, stopAbundance);
					/*
					 * Left Normal Peak 2
					 */
					int startRetentionTime = peakModel.getStartRetentionTime();
					int stopRetentionTime = startRetentionTimeSkim;
					startAbundance = peakModel.getBackgroundAbundance(startRetentionTime);
					stopAbundance = peakModel.getBackgroundAbundance(stopRetentionTime);
					peakSplitted2 = extractPeakByCoordinates(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				} else {
					/*
					 * Middle Selection
					 */
					startAbundance = peakModel.getBackgroundAbundance(startRetentionTimeSkim);
					stopAbundance = peakModel.getBackgroundAbundance(stopRetentionTimeSkim);
					peakSplitted1 = extractPeakByCoordinates(startRetentionTimeSkim, stopRetentionTimeSkim, startAbundance, stopAbundance);
					peakSplitted2 = null;
				}
				//
				if(peakSplitted1 != null) {
					buttonAddPeak.setEnabled(true);
				}
			}
		}
		//
		setDetectionType(DETECTION_TYPE_NONE);
		updatePeaks();
	}

	private IPeak extractPeakByCoordinates(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		IPeak peakSplitted = null;
		//
		if(peak instanceof IChromatogramPeakMSD) {
			/*
			 * Peak Detection MSD
			 */
			try {
				IChromatogramPeakMSD chromatogramPeakMSD = (IChromatogramPeakMSD)peak;
				if(chromatogramPeakMSD.getChromatogram() != null) {
					ManualPeakDetector manualPeakDetector = new ManualPeakDetector();
					IChromatogramMSD chromatogram = chromatogramPeakMSD.getChromatogram();
					peakSplitted = manualPeakDetector.calculatePeak(chromatogram, startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				}
			} catch(PeakException e) {
				logger.warn(e);
			}
		} else if(peak instanceof IChromatogramPeakCSD) {
			/*
			 * Peak Detection FID
			 */
			try {
				IChromatogramPeakCSD chromatogramPeakCSD = (IChromatogramPeakCSD)peak;
				if(chromatogramPeakCSD.getChromatogram() != null) {
					ManualPeakDetector manualPeakDetector = new ManualPeakDetector();
					IChromatogramCSD chromatogram = chromatogramPeakCSD.getChromatogram();
					peakSplitted = manualPeakDetector.calculatePeak(chromatogram, startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
				}
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		//
		return peakSplitted;
	}

	private IPlotArea getPlotArea() {

		return peakChart.getBaseChart().getPlotArea();
	}

	private void resetSplittedPeaks() {

		this.peakSplitted1 = null;
		this.peakSplitted2 = null;
		buttonAddPeak.setEnabled(false);
		splitSelectionPaintListener.reset();
	}
}
