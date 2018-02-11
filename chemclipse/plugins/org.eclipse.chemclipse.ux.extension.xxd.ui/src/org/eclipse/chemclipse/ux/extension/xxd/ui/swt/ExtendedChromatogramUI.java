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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.ChromatogramFilterCSD;
import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.IChromatogramFilterSupportCSD;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupport;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupportMSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.IdentificationLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.eavp.service.swtchart.axisconverter.MillisecondsToScanNumberConverter;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ICustomSelectionHandler;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.core.RangeRestriction;
import org.eclipse.eavp.service.swtchart.core.ScrollableChart;
import org.eclipse.eavp.service.swtchart.core.SecondaryAxisSettings;
import org.eclipse.eavp.service.swtchart.events.AbstractHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.events.IHandledEventProcessor;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.menu.AbstractChartMenuEntry;
import org.eclipse.eavp.service.swtchart.menu.IChartMenuEntry;
import org.eclipse.eavp.service.swtchart.menu.ResetChartHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Position;
import org.swtchart.IAxisSet;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.LineStyle;
import org.swtchart.Range;

public class ExtendedChromatogramUI {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	private static final String LABEL_SCAN_NUMBER = "Scan Number";
	//
	private static final String TYPE_GENERIC = "TYPE_GENERIC";
	private static final String TYPE_MSD = "TYPE_MSD";
	private static final String TYPE_CSD = "TYPE_CSD";
	private static final String TYPE_WSD = "TYPE_WSD";
	//
	private static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";
	private static final String SERIES_ID_BASELINE = "Baseline";
	private static final String SERIES_ID_PEAKS_NORMAL_ACTIVE = "Peak(s) [Active]";
	private static final String SERIES_ID_PEAKS_NORMAL_INACTIVE = "Peak(s) [Inactive]";
	private static final String SERIES_ID_PEAKS_ISTD_ACTIVE = "Peak(s) ISTD [Active]";
	private static final String SERIES_ID_PEAKS_ISTD_INACTIVE = "Peak(s) ISTD [Inactive]";
	private static final String SERIES_ID_SELECTED_PEAK_MARKER = "Selected Peak Marker";
	private static final String SERIES_ID_SELECTED_PEAK_SHAPE = "Selected Peak Shape";
	private static final String SERIES_ID_SELECTED_PEAK_BACKGROUND = "Selected Peak Background";
	private static final String SERIES_ID_SELECTED_SCAN = "Selected Scan";
	private static final String SERIES_ID_IDENTIFIED_SCANS = "Identified Scans";
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scan Selected";
	//
	private Composite toolbarInfo;
	private Label labelChromatogramInfo;
	private Composite toolbarChromatograms;
	private Combo comboChromatograms;
	private ChromatogramChart chromatogramChart;
	//
	private List<IChromatogramSelection> chromatogramSelections = null;
	private IChromatogramSelection chromatogramSelection = null;
	private List<IChartMenuEntry> chartMenuEntriesFilter;
	//
	private Map<String, IdentificationLabelMarker> peakLabelMarkerMap = new HashMap<String, IdentificationLabelMarker>();
	private Map<String, IdentificationLabelMarker> scanLabelMarkerMap = new HashMap<String, IdentificationLabelMarker>();
	//
	private PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private Display display = Display.getDefault();
	private Shell shell = display.getActiveShell();

	private class ChromatogramResetHandler extends ResetChartHandler {

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			super.execute(shell, scrollableChart);
			if(chromatogramSelection != null) {
				chromatogramSelection.reset(true);
			}
		}
	}

	private class ChromatogramSelectionHandler implements ICustomSelectionHandler {

		private BaseChart baseChart;

		public ChromatogramSelectionHandler(BaseChart baseChart) {
			this.baseChart = baseChart;
		}

		@Override
		public void handleUserSelection(Event event) {

			if(chromatogramSelection != null) {
				Range rangeX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS).getRange();
				Range rangeY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS).getRange();
				//
				int startRetentionTime = (int)rangeX.lower;
				int stopRetentionTime = (int)rangeX.upper;
				float startAbundance = (float)rangeY.lower;
				float stopAbundance = (float)rangeY.upper;
				setChromatogramSelectionRange(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance);
			}
		}
	}

	private class ScanSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getButton() {

			return BaseChart.BUTTON_LEFT;
		}

		@Override
		public int getStateMask() {

			return SWT.NONE;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				int retentionTime = (int)baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
				int scanNumber = chromatogram.getScanNumber(retentionTime);
				IScan scan = chromatogram.getScan(scanNumber);
				if(scan != null) {
					chromatogramSelection.setSelectedScan(scan);
					display.asyncExec(new Runnable() {

						@Override
						public void run() {

							IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
							eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
						}
					});
				}
			}
		}
	}

	private class PeakSelectionHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		@Override
		public int getEvent() {

			return BaseChart.EVENT_MOUSE_DOUBLE_CLICK;
		}

		@Override
		public int getButton() {

			return BaseChart.BUTTON_LEFT;
		}

		@Override
		public int getStateMask() {

			return SWT.ALT;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				int retentionTime = (int)baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
				IPeak peak = null;
				if(chromatogram instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
					peak = chromatogramMSD.getPeak(retentionTime);
				} else if(chromatogram instanceof IChromatogramCSD) {
					IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
					peak = chromatogramCSD.getPeak(retentionTime);
				} else if(chromatogram instanceof IChromatogramWSD) {
					// IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
					// peak = chromatogramWSD.getPeak(retentionTime);
				}
				if(peak != null) {
					/*
					 * Fire an update.
					 */
					chromatogramSelection.setSelectedPeak(peak);
					IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
					boolean moveRetentionTimeOnPeakSelection = preferenceStore.getBoolean(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
					if(moveRetentionTimeOnPeakSelection) {
						adjustChromatogramSelection(peak, chromatogramSelection);
					}
					//
					updateSelection();
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
				}
			}
		}

		private void adjustChromatogramSelection(IPeak peak, IChromatogramSelection chromatogramSelection) {

			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				List<? extends IPeak> peaks = chromatogramDataSupport.getPeaks(chromatogram);
				List<? extends IPeak> peaksSelection = new ArrayList<>(chromatogramDataSupport.getPeaks(chromatogram, chromatogramSelection));
				Collections.sort(peaks, peakRetentionTimeComparator);
				Collections.sort(peaksSelection, peakRetentionTimeComparator);
				//
				if(peaks.get(0).equals(peak) || peaks.get(peaks.size() - 1).equals(peak)) {
					/*
					 * Don't move if it is the first or last peak of the chromatogram.
					 */
				} else {
					/*
					 * First peak of the selection: move left
					 * Last peak of the selection: move right
					 */
					if(peaksSelection.get(0).equals(peak)) {
						ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
					} else if(peaksSelection.get(peaksSelection.size() - 1).equals(peak)) {
						ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
					}
				}
			}
		}
	}

	private class ScanSelectionArrowKeyHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		private int keyCode;

		public ScanSelectionArrowKeyHandler(int keyCode) {
			this.keyCode = keyCode;
		}

		@Override
		public int getEvent() {

			return BaseChart.EVENT_KEY_UP;
		}

		@Override
		public int getButton() {

			return keyCode;
		}

		@Override
		public int getStateMask() {

			return SWT.CTRL;
		}

		@Override
		public void handleEvent(BaseChart baseChart, Event event) {

			handleControlScanSelection(keyCode);
		}
	}

	private class ChromatogramMoveArrowKeyHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

		private int keyCode;

		public ChromatogramMoveArrowKeyHandler(int keyCode) {
			this.keyCode = keyCode;
		}

		@Override
		public int getEvent() {

			return BaseChart.EVENT_KEY_UP;
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

			handleArrowMoveWindowSelection(keyCode);
		}
	}

	private class FilterMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

		private String name;
		private String filterId;
		private String type;
		private IChromatogramSelection chromatogramSelection;

		public FilterMenuEntry(String name, String filterId, String type, IChromatogramSelection chromatogramSelection) {
			this.name = name;
			this.filterId = filterId;
			this.type = type;
			this.chromatogramSelection = chromatogramSelection;
		}

		@Override
		public String getCategory() {

			return "Filter";
		}

		@Override
		public String getName() {

			return name;
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			if(chromatogramSelection != null) {
				/*
				 * Create the runnable.
				 */
				IRunnableWithProgress runnable = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						switch(type) {
							case TYPE_GENERIC:
								ChromatogramFilter.applyFilter(chromatogramSelection, filterId, monitor);
								break;
							case TYPE_MSD:
								if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
									IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
									ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, filterId, monitor);
								}
								break;
							case TYPE_CSD:
								if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
									IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
									ChromatogramFilterCSD.applyFilter(chromatogramSelectionCSD, filterId, monitor);
								}
								break;
							case TYPE_WSD:
								//
								break;
						}
					}
				};
				/*
				 * Excecute
				 */
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
				try {
					monitor.run(true, true, runnable);
					updateChromatogram();
					updateSelection();
				} catch(InvocationTargetException e) {
					logger.warn(e);
				} catch(InterruptedException e) {
					logger.warn(e);
				}
			}
		}
	}

	@Inject
	public ExtendedChromatogramUI(Composite parent) {
		chartMenuEntriesFilter = new ArrayList<IChartMenuEntry>();
		initialize(parent);
	}

	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		addChartMenuEntriesFilter();
		updateChromatogram();
		//
		if(chromatogramSelections == null) {
			updateChromatogramCombo();
		}
	}

	public void update() {

		updateChromatogram();
		adjustChromatogramSelectionRange();
	}

	public void updateSelectedScan() {

		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_SCAN);
		chromatogramChart.deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChromatogramSelectionRange();
	}

	public void updateSelectedPeak() {

		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_MARKER);
		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_SHAPE);
		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_BACKGROUND);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		addSelectedPeakData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChromatogramSelectionRange();
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	public boolean isActiveChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(this.chromatogramSelection == chromatogramSelection) {
			return true;
		}
		return false;
	}

	private void addChartMenuEntriesFilter() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntriesFilter(chartSettings);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesFilter(chartSettings, ChromatogramFilter.getChromatogramFilterSupport(), TYPE_GENERIC);
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesFilterMSD(chartSettings, ChromatogramFilterMSD.getChromatogramFilterSupport(), TYPE_MSD);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesFilterCSD(chartSettings, ChromatogramFilterCSD.getChromatogramFilterSupport(), TYPE_CSD);
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
				//
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesFilter(IChartSettings chartSettings, IChromatogramFilterSupport chromatogramFilterSupport, String type) {

		try {
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, type, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	// TODO Refactor
	private void addChartMenuEntriesFilterMSD(IChartSettings chartSettings, IChromatogramFilterSupportMSD chromatogramFilterSupport, String type) {

		try {
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, type, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	// TODO Refactor
	private void addChartMenuEntriesFilterCSD(IChartSettings chartSettings, IChromatogramFilterSupportCSD chromatogramFilterSupport, String type) {

		try {
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(name, filterId, type, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void cleanChartMenuEntriesFilter(IChartSettings chartSettings) {

		for(IChartMenuEntry chartMenuEntry : chartMenuEntriesFilter) {
			chartSettings.removeMenuEntry(chartMenuEntry);
		}
		chartMenuEntriesFilter.clear();
	}

	private void updateChromatogram() {

		updateLabel();
		deleteScanNumberSecondaryAxisX();
		chromatogramChart.deleteSeries();
		//
		if(chromatogramSelection != null) {
			addjustChromatogramChart();
			addChromatogramSeriesData();
			addScanNumberSecondaryAxisX();
		}
	}

	private void addjustChromatogramChart() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setForceZeroMinY(false);
		//
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			rangeRestriction.setZeroY(true);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			rangeRestriction.setZeroY(false);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			rangeRestriction.setZeroY(false);
		}
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChromatogramSeriesData() {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		//
		addChromatogramData(lineSeriesDataList);
		addPeakData(lineSeriesDataList);
		addIdentifiedScansData(lineSeriesDataList);
		addSelectedPeakData(lineSeriesDataList);
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addBaselineData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
	}

	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM));
		boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA);
		//
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesDataChromatogram(chromatogram, SERIES_ID_CHROMATOGRAM, color);
		lineSeriesData.getLineSeriesSettings().setEnableArea(enableChromatogramArea);
		lineSeriesDataList.add(lineSeriesData);
	}

	private void addPeakData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			//
			List<? extends IPeak> peaks = chromatogramDataSupport.getPeaks(chromatogram);
			List<IPeak> peaksActiveNormal = new ArrayList<IPeak>();
			List<IPeak> peaksInactiveNormal = new ArrayList<IPeak>();
			List<IPeak> peaksActiveISTD = new ArrayList<IPeak>();
			List<IPeak> peaksInactiveISTD = new ArrayList<IPeak>();
			//
			for(IPeak peak : peaks) {
				if(peak.getInternalStandards().size() > 0) {
					if(peak.isActiveForAnalysis()) {
						peaksActiveISTD.add(peak);
					} else {
						peaksInactiveISTD.add(peak);
					}
				} else {
					if(peak.isActiveForAnalysis()) {
						peaksActiveNormal.add(peak);
					} else {
						peaksInactiveNormal.add(peak);
					}
				}
			}
			//
			addPeaks(lineSeriesDataList, peaksActiveNormal, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveNormal, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_NORMAL_INACTIVE);
			addPeaks(lineSeriesDataList, peaksActiveISTD, PlotSymbolType.DIAMOND, symbolSize, Colors.RED, SERIES_ID_PEAKS_ISTD_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveISTD, PlotSymbolType.DIAMOND, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_ISTD_INACTIVE);
		}
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(peaks.size() > 0) {
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			boolean showChromatogramPeakLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS);
			//
			Collections.sort(peaks, peakRetentionTimeComparator);
			ILineSeriesData lineSeriesData = peakChartSupport.getPeaks(peaks, true, false, symbolColor, seriesId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
			//
			IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
			IdentificationLabelMarker peakLabelMarker = peakLabelMarkerMap.get(seriesId);
			if(peakLabelMarker != null) {
				plotArea.removeCustomPaintListener(peakLabelMarker);
			}
			//
			if(showChromatogramPeakLabels) {
				int indexSeries = lineSeriesDataList.size() - 1;
				peakLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, peaks, null);
				plotArea.addCustomPaintListener(peakLabelMarker);
				peakLabelMarkerMap.put(seriesId, peakLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			String seriesId = SERIES_ID_IDENTIFIED_SCANS;
			List<IScan> scans = chromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
			addIdentifiedScansData(lineSeriesDataList, scans, PlotSymbolType.CIRCLE, symbolSize, Colors.DARK_GRAY, seriesId);
			//
			IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
			IdentificationLabelMarker scanLabelMarker = scanLabelMarkerMap.get(seriesId);
			if(scanLabelMarker != null) {
				plotArea.removeCustomPaintListener(scanLabelMarker);
			}
			//
			boolean showChromatogramScanLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS);
			if(showChromatogramScanLabels) {
				int indexSeries = lineSeriesDataList.size() - 1;
				scanLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, null, scans);
				plotArea.addCustomPaintListener(scanLabelMarker);
				scanLabelMarkerMap.put(seriesId, scanLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, List<IScan> scans, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(scans.size() > 0) {
			ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scans, false, seriesId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addSelectedIdentifiedScanData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IScan scan = chromatogramSelection.getSelectedIdentifiedScan();
			if(scan != null) {
				String seriesId = SERIES_ID_IDENTIFIED_SCAN_SELECTED;
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED));
				int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
				List<IScan> scans = new ArrayList<>();
				scans.add(scan);
				addIdentifiedScansData(lineSeriesDataList, scans, PlotSymbolType.CIRCLE, symbolSize, color, seriesId);
			}
		}
	}

	private void addSelectedPeakData(List<ILineSeriesData> lineSeriesDataList) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		IPeak peak = chromatogramSelection.getSelectedPeak();
		if(peak != null) {
			/*
			 * Settings
			 */
			boolean mirrored = false;
			ILineSeriesData lineSeriesData;
			Color colorPeak = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK));
			/*
			 * Peak Marker
			 */
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			List<IPeak> peaks = new ArrayList<>();
			peaks.add(peak);
			addPeaks(lineSeriesDataList, peaks, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, colorPeak, SERIES_ID_SELECTED_PEAK_MARKER);
			/*
			 * Peak
			 */
			int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE));
			lineSeriesData = peakChartSupport.getPeak(peak, true, mirrored, colorPeak, SERIES_ID_SELECTED_PEAK_SHAPE);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolColor(colorPeak);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Background
			 */
			Color colorBackground = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BACKGROUND));
			lineSeriesData = peakChartSupport.getPeakBackground(peak, mirrored, colorBackground, SERIES_ID_SELECTED_PEAK_BACKGROUND);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addSelectedScanData(List<ILineSeriesData> lineSeriesDataList) {

		IScan scan = chromatogramSelection.getSelectedScan();
		if(scan != null) {
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN));
			//
			int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE));
			ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scan, false, SERIES_ID_SELECTED_SCAN);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesSettings.setSymbolColor(color);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addBaselineData(List<ILineSeriesData> lineSeriesDataList) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean showChromatogramBaseline = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE);
		//
		if(chromatogramSelection != null && showChromatogramBaseline) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE));
			boolean enableBaselineArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_BASELINE_AREA);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesDataBaseline(chromatogram, SERIES_ID_BASELINE, color);
			lineSeriesData.getLineSeriesSettings().setEnableArea(enableBaselineArea);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		/*
		 * Define the compression level.
		 */
		int compressionToLength;
		String compressionType = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		switch(compressionType) {
			case LineChart.COMPRESSION_EXTREME:
				compressionToLength = LineChart.EXTREME_COMPRESSION;
				break;
			case LineChart.COMPRESSION_HIGH:
				compressionToLength = LineChart.HIGH_COMPRESSION;
				break;
			case LineChart.COMPRESSION_MEDIUM:
				compressionToLength = LineChart.MEDIUM_COMPRESSION;
				break;
			case LineChart.COMPRESSION_LOW:
				compressionToLength = LineChart.LOW_COMPRESSION;
				break;
			default:
				compressionToLength = LineChart.NO_COMPRESSION;
				break;
		}
		chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarChromatograms = createToolbarChromatograms(parent);
		createChromatogramChart(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, false);
		PartSupport.setCompositeVisibility(toolbarChromatograms, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(7, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarChromatograms(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarChromatograms(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonSelectPreviousChromatogram(composite);
		createComboChromatograms(composite);
		createButtonSelectNextChromatogram(composite);
		//
		return composite;
	}

	private void createButtonSelectPreviousChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select previous chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index--;
				index = (index < 0) ? 0 : index;
				selectChromatogram(index);
			}
		});
	}

	private void createComboChromatograms(Composite parent) {

		comboChromatograms = new Combo(parent, SWT.READ_ONLY);
		comboChromatograms.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboChromatograms.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramSelections != null) {
					int index = comboChromatograms.getSelectionIndex();
					selectChromatogram(index);
				}
			}
		});
	}

	private void createButtonSelectNextChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select next chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index++;
				index = (index >= comboChromatograms.getItemCount()) ? comboChromatograms.getItemCount() - 1 : index;
				selectChromatogram(index);
			}
		});
	}

	private void selectChromatogram(int index) {

		comboChromatograms.select(index);
		IChromatogramSelection chromatogramSelection = chromatogramSelections.get(index);
		if(chromatogramSelection != null) {
			updateChromatogramSelection(chromatogramSelection);
		}
	}

	private void updateChromatogramCombo() {

		List<String> references = new ArrayList<String>();
		/*
		 * Get the original and the referenced data.
		 */
		if(chromatogramSelection != null) {
			/*
			 * Initialize
			 */
			chromatogramSelections = new ArrayList<IChromatogramSelection>();
			/*
			 * Original Data
			 */
			chromatogramSelections.add(chromatogramSelection);
			references.add("Original Data");
			/*
			 * References
			 */
			List<IChromatogram> referencedChromatograms = chromatogramSelection.getChromatogram().getReferencedChromatograms();
			int i = 1;
			for(IChromatogram referencedChromatogram : referencedChromatograms) {
				IChromatogramSelection referencedChromatogramSelection = null;
				try {
					if(referencedChromatogram instanceof IChromatogramMSD) {
						referencedChromatogramSelection = new ChromatogramSelectionMSD(referencedChromatogram);
					} else if(referencedChromatogram instanceof IChromatogramCSD) {
						referencedChromatogramSelection = new ChromatogramSelectionCSD(referencedChromatogram);
					} else if(referencedChromatogram instanceof IChromatogramWSD) {
						referencedChromatogramSelection = new ChromatogramSelectionWSD(referencedChromatogram);
					}
				} catch(ChromatogramIsNullException e) {
					logger.warn(e);
				}
				//
				chromatogramSelections.add(referencedChromatogramSelection);
				references.add("Chromatogram Reference #" + i++);
			}
		}
		/*
		 * Set the items.
		 */
		comboChromatograms.setItems(references.toArray(new String[references.size()]));
		if(references.size() > 0) {
			comboChromatograms.select(0);
		}
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		BaseChart baseChart = chromatogramChart.getBaseChart();
		/*
		 * Custom Selection Handler
		 */
		baseChart.addCustomRangeSelectionHandler(new ChromatogramSelectionHandler(baseChart));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setRangeSelectorDefaultAxisX(1); // Minutes
		chartSettings.setRangeSelectorDefaultAxisY(1); // Relative Abundance
		chartSettings.setShowRangeSelectorInitially(false);
		IChartMenuEntry chartMenuEntry = chartSettings.getChartMenuEntry(ResetChartHandler.NAME);
		chartSettings.removeMenuEntry(chartMenuEntry);
		chartSettings.addMenuEntry(new ChromatogramResetHandler());
		chartSettings.addHandledEventProcessor(new ScanSelectionHandler());
		chartSettings.addHandledEventProcessor(new PeakSelectionHandler());
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_UP));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(SWT.ARROW_DOWN));
		//
		chromatogramChart.applySettings(chartSettings);
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

	private Button createButtonToggleToolbarChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle chromatograms toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarChromatograms);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleSeriesLegendVisibility();
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

				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				boolean isShowLegendMarker = chartSettings.isShowLegendMarker();
				chartSettings.setShowLegendMarker(!isShowLegendMarker);
				chromatogramChart.applySettings(chartSettings);
			}
		});
	}

	private void createToggleRangeSelectorButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart range selector.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_RANGE_SELECTOR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleRangeSelectorVisibility();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chromatogram");
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

				IPreferencePage preferencePageChromatogram = new PreferencePageChromatogram();
				preferencePageChromatogram.setTitle("Chromatogram Settings ");
				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSWT));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		updateChromatogram();
	}

	private void reset() {

		updateChromatogram();
	}

	private void updateLabel() {

		if(chromatogramSelection != null) {
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabel(chromatogramSelection.getChromatogram()));
		} else {
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabel(null));
		}
	}

	private void deleteScanNumberSecondaryAxisX() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		List<ISecondaryAxisSettings> secondaryAxisSettings = chartSettings.getSecondaryAxisSettingsListX();
		//
		ISecondaryAxisSettings secondaryAxisScanNumber = null;
		exitloop:
		for(ISecondaryAxisSettings secondaryAxis : secondaryAxisSettings) {
			if(secondaryAxis.getLabel().equals(LABEL_SCAN_NUMBER)) {
				secondaryAxisScanNumber = secondaryAxis;
				break exitloop;
			}
		}
		//
		if(secondaryAxisScanNumber != null) {
			secondaryAxisSettings.remove(secondaryAxisScanNumber);
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addScanNumberSecondaryAxisX() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean showChromatogramScanAxis = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_AXIS);
		//
		try {
			deleteScanNumberSecondaryAxisX();
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			if(chromatogramSelection != null && showChromatogramScanAxis) {
				//
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				int scanDelay = chromatogram.getScanDelay();
				int scanInterval = chromatogram.getScanInterval();
				//
				ISecondaryAxisSettings secondaryAxisSettings = new SecondaryAxisSettings(LABEL_SCAN_NUMBER, new MillisecondsToScanNumberConverter(scanDelay, scanInterval));
				secondaryAxisSettings.setPosition(Position.Secondary);
				secondaryAxisSettings.setDecimalFormat(ValueFormat.getDecimalFormatEnglish("0"));
				secondaryAxisSettings.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				secondaryAxisSettings.setExtraSpaceTitle(0);
				chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettings);
			}
			//
			chromatogramChart.applySettings(chartSettings);
			chromatogramChart.adjustRange(true);
			chromatogramChart.redraw();
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	private void setChromatogramSelectionRange(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, false);
		chromatogramSelection.update(true);
		adjustChromatogramSelectionRange();
	}

	private void adjustChromatogramSelectionRange() {

		if(chromatogramSelection != null) {
			BaseChart baseChart = chromatogramChart.getBaseChart();
			IAxisSet axisSet = baseChart.getAxisSet();
			IAxis xAxis = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			IAxis yAxis = axisSet.getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
			//
			Range xRange = new Range(chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
			Range yRange = new Range(chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
			//
			xAxis.setRange(xRange);
			yAxis.setRange(yRange);
			baseChart.adjustSecondaryAxes();
		}
	}

	private void handleControlScanSelection(int keyCode) {

		if(chromatogramSelection != null) {
			/*
			 * Select the next or previous scan.
			 */
			int scanNumber = chromatogramSelection.getSelectedScan().getScanNumber();
			if(keyCode == SWT.ARROW_RIGHT) {
				scanNumber++;
			} else {
				scanNumber--;
			}
			/*
			 * Set and fire an update.
			 */
			IScan selectedScan = chromatogramSelection.getChromatogram().getScan(scanNumber);
			//
			IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
			eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, selectedScan);
			//
			if(selectedScan != null) {
				/*
				 * The selection should slide with the selected scans.
				 */
				int scanRetentionTime = selectedScan.getRetentionTime();
				int startRetentionTime = chromatogramSelection.getStartRetentionTime();
				int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
				/*
				 * Left or right slide on demand.
				 */
				if(scanRetentionTime <= startRetentionTime) {
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
				} else if(scanRetentionTime >= stopRetentionTime) {
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
				}
				//
				chromatogramSelection.setSelectedScan(selectedScan, false);
				updateSelection();
			}
		}
	}

	private void handleArrowMoveWindowSelection(int keyCode) {

		if(chromatogramSelection != null) {
			if(keyCode == SWT.ARROW_RIGHT || keyCode == SWT.ARROW_LEFT) {
				/*
				 * Left, Right
				 * (Retention Time)
				 */
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				boolean useAlternateWindowMoveDirection = preferenceStore.getBoolean(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION);
				//
				if(keyCode == SWT.ARROW_RIGHT) {
					MoveDirection moveDirection = (useAlternateWindowMoveDirection) ? MoveDirection.LEFT : MoveDirection.RIGHT;
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
				} else {
					MoveDirection moveDirection = (useAlternateWindowMoveDirection) ? MoveDirection.RIGHT : MoveDirection.LEFT;
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
				}
				updateSelection();
				//
			} else if(keyCode == SWT.ARROW_UP || keyCode == SWT.ARROW_DOWN) {
				/*
				 * Up, Down
				 * (Abundance)
				 * Doesn't work if auto adjust signals is enabled.
				 */
				float stopAbundance = chromatogramSelection.getStopAbundance();
				float newStopAbundance;
				if(PreferenceSupplier.useAlternateWindowMoveDirection()) {
					newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance - stopAbundance / 20.0f : stopAbundance + stopAbundance / 20.0f;
				} else {
					newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance + stopAbundance / 20.0f : stopAbundance - stopAbundance / 20.0f;
				}
				//
				int startRetentionTime = chromatogramSelection.getStartRetentionTime();
				int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
				float startAbundance = chromatogramSelection.getStartAbundance();
				setChromatogramSelectionRange(startRetentionTime, stopRetentionTime, startAbundance, newStopAbundance);
				updateSelection();
			}
		}
	}

	private void updateSelection() {

		if(chromatogramSelection != null) {
			chromatogramSelection.update(true);
			adjustChromatogramSelectionRange();
		}
	}
}
