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
 * Alexander Kerner - Generics
 * Christoph Läubrich - propagate result of methods to the user
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.model.updates.IChromatogramSelectionUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.RetentionIndexUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.IdentificationLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramAxes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramPeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramActionUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramReferencesUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.HeatmapUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IChromatogramReferencesListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ToolbarConfig;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryNameComparator;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToScanNumberConverter;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.ResetChartHandler;

@SuppressWarnings("rawtypes")
public class ExtendedChromatogramUI implements ToolbarConfig {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	protected static final String TYPE_GENERIC = "TYPE_GENERIC";
	protected static final String TYPE_MSD = "TYPE_MSD";
	protected static final String TYPE_CSD = "TYPE_CSD";
	protected static final String TYPE_WSD = "TYPE_WSD";
	//
	private static final String TITLE_X_AXIS_SCANS = "Scans (approx.)";
	private static final String LABEL_SCAN_NUMBER = "Scan Number";
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
	private static final int THREE_MINUTES = (int)(AbstractChromatogram.MINUTE_CORRELATION_FACTOR * 3);
	private static final int FIVE_MINUTES = (int)(AbstractChromatogram.MINUTE_CORRELATION_FACTOR * 5);
	//
	private static final String TOOLBAR_INFO = "TOOLBAR_INFO";
	private static final String TOOLBAR_RETENTION_INDICES = "TOOLBAR_RETENTION_INDICES";
	private static final String TOOLBAR_METHOD = "TOOLBAR_METHOD";
	private static final String TOOLBAR_CHROMATOGRAM_ALIGNMENT = "TOOLBAR_CHROMATOGRAM_ALIGNMENT";
	private static final String TOOLBAR_EDIT = "TOOLBAR_EDIT";
	private Map<String, Composite> toolbars = new HashMap<>();
	//
	private Composite toolbarMain;
	private Label labelChromatogramInfo;
	private ChromatogramReferencesUI chromatogramReferencesUI;
	private RetentionIndexUI retentionIndexUI;
	private ChromatogramChart chromatogramChart;
	private ComboViewer comboViewerSeparationColumn;
	private ChromatogramActionUI chromatogramActionUI;
	private HeatmapUI heatmapUI;
	private Composite heatmapArea;
	//
	private IChromatogramSelection<?, ?> chromatogramSelection = null;
	private List<IChartMenuEntry> cachedMenuEntries = new ArrayList<>();
	//
	private Map<String, IdentificationLabelMarker> peakLabelMarkerMap = new HashMap<>();
	private Map<String, IdentificationLabelMarker> scanLabelMarkerMap = new HashMap<>();
	//
	private PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private ChartSupport chartSupport = new ChartSupport(Activator.getDefault().getPreferenceStore());
	//
	private DisplayType displayType = DisplayType.TIC;
	//
	private boolean suspendUpdate = false;
	private IPreferenceStore preferenceStore;
	private ProcessTypeSupport processTypeSupport;
	//
	private IEventBroker eventBroker;

	public ExtendedChromatogramUI(Composite parent, int style, IPreferenceStore preferenceStore, ProcessTypeSupport processTypeSupport, IEventBroker eventBroker) {
		this.preferenceStore = preferenceStore;
		this.processTypeSupport = processTypeSupport;
		this.eventBroker = eventBroker;
		initialize(parent, style);
	}

	@Override
	public void setToolbarVisible(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarMain, visible);
	}

	@Override
	public boolean isToolbarVisible() {

		return toolbarMain.isVisible();
	}

	public void fireUpdate(Display display) {

		fireUpdateChromatogram(display);
		if(!fireUpdatePeak(display)) {
			fireUpdateScan(display);
		}
	}

	public boolean fireUpdateChromatogram(Display display) {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null && display != null) {
			/*
			 * Will be removed as soon as the topics
			 * IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION
			 * ...
			 * are removed.
			 */
			if(preferenceStore.getBoolean(PreferenceConstants.P_LEGACY_UPDATE_CHROMATOGRAM_MODUS)) {
				fireUpdateChromatogramLegacy(display);
			}
			//
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, chromatogramSelection);
				}
			});
		}
		return chromatogramSelection != null ? true : false;
	}

	private void fireUpdateChromatogramLegacy(Display display) {

		Map<String, Object> map = new HashMap<>();
		map.put(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, chromatogramSelection);
		map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, true);
		String topic;
		//
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION;
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION;
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION;
		} else {
			topic = null;
		}
		//
		if(topic != null && eventBroker != null && display != null) {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					eventBroker.post(topic, map);
				}
			});
		}
	}

	public boolean fireUpdatePeak(Display display) {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null && display != null) {
			final IPeak peak = chromatogramSelection.getSelectedPeak();
			if(peak != null) {
				/*
				 * Will be removed as soon as the topics
				 * IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK
				 * ...
				 * are removed.
				 */
				if(preferenceStore.getBoolean(PreferenceConstants.P_LEGACY_UPDATE_PEAK_MODUS)) {
					fireUpdatePeakLegacy(peak, display);
				}
				//
				update = true;
				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
					}
				});
			}
		}
		return update;
	}

	private void fireUpdatePeakLegacy(IPeak peak, Display display) {

		if(peak != null && eventBroker != null && display != null) {
			Map<String, Object> map = new HashMap<>();
			map.put(IChemClipseEvents.PROPERTY_PEAK_MSD, peak);
			map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, true);
			String topic;
			//
			if(peak instanceof IPeakMSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK;
			} else if(peak instanceof IPeakCSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAK;
			} else if(peak instanceof IPeakWSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_PEAK;
			} else {
				topic = null;
			}
			//
			if(topic != null) {
				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						eventBroker.post(topic, map);
					}
				});
			}
		}
	}

	public boolean fireUpdateScan(Display display) {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null && display != null) {
			final IScan scan = chromatogramSelection.getSelectedScan();
			if(scan != null) {
				update = true;
				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						eventBroker.post(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
					}
				});
			}
		}
		return update;
	}

	public ChromatogramChart getChromatogramChart() {

		return chromatogramChart;
	}

	public synchronized void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(this.chromatogramSelection != chromatogramSelection) {
			this.chromatogramSelection = chromatogramSelection;
			chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
			updateToolbar(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), chromatogramSelection);
			//
			if(chromatogramSelection != null) {
				/*
				 * Adjust
				 */
				adjustAxisSettings();
				addChartMenuEntries();
				updateChromatogram();
				setSeparationColumnSelection();
				chromatogramReferencesUI.updateChromatogramSelection(chromatogramSelection);
				retentionIndexUI.setInput(chromatogramSelection.getChromatogram().getSeparationColumnIndices());
			} else {
				chromatogramReferencesUI.clear();
				retentionIndexUI.setInput(null);
				updateChromatogram();
			}
		}
	}

	public void update() {

		if(!suspendUpdate) {
			updateChromatogram();
			adjustChromatogramSelectionRange();
			setSeparationColumnSelection();
		}
	}

	public void updateSelectedScan() {

		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_SCAN);
		chromatogramChart.deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
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
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
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

	protected void setChromatogramSelectionRange(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, false);
		suspendUpdate = true;
		chromatogramSelection.update(true);
		suspendUpdate = false;
		adjustChromatogramSelectionRange();
	}

	protected void updateSelection() {

		if(chromatogramSelection != null) {
			chromatogramSelection.update(true);
			adjustChromatogramSelectionRange();
		}
	}

	/**
	 * @deprecated use {@link #processChromatogram(IRunnableWithProgress, Shell)}
	 * 
	 * @param runnable
	 */
	@Deprecated
	protected void processChromatogram(IRunnableWithProgress runnable) {

		processChromatogram(runnable, DisplayUtils.getShell());
	}

	protected void processChromatogram(IRunnableWithProgress runnable, Shell shell) {

		ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
		try {
			monitor.run(true, true, runnable);
			updateChromatogram();
			updateSelection();
			fireUpdate(shell.getDisplay());
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void adjustMinuteScale() {

		if(chromatogramSelection != null) {
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int deltaRetentionTime = stopRetentionTime - startRetentionTime + 1;
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			List<ISecondaryAxisSettings> axisSettings = chartSettings.getSecondaryAxisSettingsListX();
			for(ISecondaryAxisSettings axisSetting : axisSettings) {
				if(axisSetting.getTitle().equals("Minutes")) {
					if(deltaRetentionTime >= FIVE_MINUTES) {
						axisSetting.setDecimalFormat(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH)));
					} else if(deltaRetentionTime >= THREE_MINUTES) {
						axisSetting.setDecimalFormat(new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.ENGLISH)));
					} else {
						axisSetting.setDecimalFormat(new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.ENGLISH)));
					}
				}
			}
			chromatogramChart.applySettings(chartSettings);
		}
	}

	private void addChartMenuEntries() {

		if(processTypeSupport != null) {
			/*
			 * Type
			 */
			DataType datatype = DataType.AUTO_DETECT;
			if(chromatogramSelection != null) {
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				if(chromatogram instanceof IChromatogramMSD) {
					datatype = DataType.MSD;
				} else if(chromatogram instanceof IChromatogramWSD) {
					datatype = DataType.WSD;
				} else if(chromatogram instanceof IChromatogramCSD) {
					datatype = DataType.CSD;
				}
			}
			/*
			 * Clean the Menu
			 */
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			for(IChartMenuEntry cachedEntry : cachedMenuEntries) {
				chartSettings.removeMenuEntry(cachedEntry);
			}
			cachedMenuEntries.clear();
			/*
			 * Dynamic Menu Items
			 */
			List<IProcessSupplier<?>> suplierList = new ArrayList<>(processTypeSupport.getSupplier(EnumSet.of(datatype.toDataCategory())));
			Collections.sort(suplierList, new CategoryNameComparator());
			for(IProcessSupplier<?> supplier : suplierList) {
				IChartMenuEntry cachedEntry = new ProcessorSupplierMenuEntry<>(() -> getChromatogramSelection(), this::processChromatogram, supplier);
				cachedMenuEntries.add(cachedEntry);
				chartSettings.addMenuEntry(cachedEntry);
			}
			/*
			 * Manage Item
			 */
			IChartMenuEntry cachedEntry = createProcessSettingsMenuEntry();
			cachedMenuEntries.add(cachedEntry);
			chartSettings.addMenuEntry(cachedEntry);
			/*
			 * Apply the menu items.
			 */
			chromatogramChart.applySettings(chartSettings);
		}
	}

	private IChartMenuEntry createProcessSettingsMenuEntry() {

		return new IChartMenuEntry() {

			@Override
			public String getName() {

				return "Manage Process Settings";
			}

			@Override
			public String getCategory() {

				return "Settings";
			}

			@Override
			public void execute(Shell shell, ScrollableChart scrollableChart) {

				SettingsWizard.openManagePreferencesWizard(shell, () -> SettingsWizard.getAllPreferences(processTypeSupport));
			}

			@Override
			public boolean isEnabled(ScrollableChart scrollableChart) {

				return !SettingsWizard.getAllPreferences(processTypeSupport).isEmpty();
			}
		};
	}

	private void updateChromatogram() {

		updateLabel();
		clearPeakAndScanLabels();
		adjustMinuteScale();
		deleteScanNumberSecondaryAxisX();
		chromatogramChart.deleteSeries();
		//
		if(chromatogramSelection != null) {
			addjustChromatogramChart();
			addChromatogramSeriesData();
			adjustChromatogramSelectionRange();
		}
	}

	private void clearPeakAndScanLabels() {

		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_NORMAL_ACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_NORMAL_INACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_ISTD_ACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_ISTD_INACTIVE);
		removeIdentificationLabelMarker(scanLabelMarkerMap, SERIES_ID_IDENTIFIED_SCANS);
		//
		clearLabelMarker(peakLabelMarkerMap);
		clearLabelMarker(scanLabelMarkerMap);
	}

	private void clearLabelMarker(Map<String, IdentificationLabelMarker> markerMap) {

		for(IdentificationLabelMarker identificationLabelMarker : markerMap.values()) {
			identificationLabelMarker.clear();
		}
		markerMap.clear();
	}

	private void removeIdentificationLabelMarker(Map<String, IdentificationLabelMarker> markerMap, String seriesId) {

		IPlotArea plotArea = chromatogramChart.getBaseChart().getPlotArea();
		IdentificationLabelMarker labelMarker = markerMap.get(seriesId);
		/*
		 * Remove the label marker.
		 */
		if(labelMarker != null) {
			plotArea.removeCustomPaintListener(labelMarker);
		}
	}

	private void addjustChromatogramChart() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setForceZeroMinY(false);
		/*
		 * Add space on top to show labels correctly.
		 */
		double extendX = preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_EXTEND_X);
		rangeRestriction.setExtendMaxY(extendX);
		/*
		 * MSD has no negative intensity values, so setZeroY(true)
		 */
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			rangeRestriction.setZeroY(true);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			rangeRestriction.setZeroY(false);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			rangeRestriction.setZeroY(false);
		}
		/*
		 * Zooming
		 */
		rangeRestriction.setXZoomOnly(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_X_ZOOM_ONLY));
		rangeRestriction.setYZoomOnly(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_Y_ZOOM_ONLY));
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChromatogramSeriesData() {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		//
		addChromatogramData(lineSeriesDataList);
		addPeakData(lineSeriesDataList);
		addIdentifiedScansData(lineSeriesDataList);
		addSelectedPeakData(lineSeriesDataList);
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addBaselineData(lineSeriesDataList);
		//
		addLineSeriesData(lineSeriesDataList);
	}

	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList) {

		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM));
		boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA);
		//
		ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogramSelection, SERIES_ID_CHROMATOGRAM, displayType, color, false);
		lineSeriesData.getSettings().setEnableArea(enableChromatogramArea);
		lineSeriesDataList.add(lineSeriesData);
	}

	private void addPeakData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			PlotSymbolType symbolTypeActiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_MARKER_TYPE));
			PlotSymbolType symbolTypeInactiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL_MARKER_TYPE));
			PlotSymbolType symbolTypeActiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_MARKER_TYPE));
			PlotSymbolType symbolTypeInactiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_ISTD_MARKER_TYPE));
			//
			List<? extends IPeak> peaks = ChromatogramDataSupport.getPeaks(chromatogram);
			List<IPeak> peaksActiveNormal = new ArrayList<>();
			List<IPeak> peaksInactiveNormal = new ArrayList<>();
			List<IPeak> peaksActiveISTD = new ArrayList<>();
			List<IPeak> peaksInactiveISTD = new ArrayList<>();
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
			addPeaks(lineSeriesDataList, peaksActiveNormal, symbolTypeActiveNormal, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveNormal, symbolTypeInactiveNormal, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_NORMAL_INACTIVE);
			addPeaks(lineSeriesDataList, peaksActiveISTD, symbolTypeActiveIstd, symbolSize, Colors.RED, SERIES_ID_PEAKS_ISTD_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveISTD, symbolTypeInactiveIstd, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_ISTD_INACTIVE);
		}
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(peaks.size() > 0) {
			//
			Collections.sort(peaks, peakRetentionTimeComparator);
			ILineSeriesData lineSeriesData = peakChartSupport.getPeaks(peaks, true, false, symbolColor, seriesId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
			boolean showChromatogramPeakLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS);
			if(showChromatogramPeakLabels) {
				IPlotArea plotArea = chromatogramChart.getBaseChart().getPlotArea();
				int indexSeries = lineSeriesDataList.size() - 1;
				IdentificationLabelMarker peakLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, peaks, null);
				plotArea.addCustomPaintListener(peakLabelMarker);
				peakLabelMarkerMap.put(seriesId, peakLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			String seriesId = SERIES_ID_IDENTIFIED_SCANS;
			List<IScan> scans = ChromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_MARKER_TYPE));
			addIdentifiedScansData(lineSeriesDataList, scans, symbolType, symbolSize, Colors.DARK_GRAY, seriesId);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(scanLabelMarkerMap, seriesId);
			boolean showChromatogramScanLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS);
			if(showChromatogramScanLabels) {
				IPlotArea plotArea = chromatogramChart.getBaseChart().getPlotArea();
				int indexSeries = lineSeriesDataList.size() - 1;
				IdentificationLabelMarker scanLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, null, scans);
				plotArea.addCustomPaintListener(scanLabelMarker);
				scanLabelMarkerMap.put(seriesId, scanLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, List<IScan> scans, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(scans.size() > 0) {
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scans, false, seriesId, displayType, chromatogramSelection);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
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
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_IDENTIFIED_SCAN));
				PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_IDENTIFIED_SCAN_MARKER_TYPE));
				int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
				List<IScan> scans = new ArrayList<>();
				scans.add(scan);
				addIdentifiedScansData(lineSeriesDataList, scans, symbolType, symbolSize, color, seriesId);
			}
		}
	}

	private void addSelectedPeakData(List<ILineSeriesData> lineSeriesDataList) {

		IPeak peak = chromatogramSelection.getSelectedPeak();
		if(peak != null) {
			/*
			 * Settings
			 */
			boolean mirrored = false;
			ILineSeriesData lineSeriesData;
			Color colorPeak = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK));
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			PlotSymbolType symbolTypePeakMarker = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE));
			int scanMarkerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_SIZE);
			PlotSymbolType symbolTypeScanMarker = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_TYPE));
			/*
			 * Peak Marker
			 */
			List<IPeak> peaks = new ArrayList<>();
			peaks.add(peak);
			addPeaks(lineSeriesDataList, peaks, symbolTypePeakMarker, symbolSize, colorPeak, SERIES_ID_SELECTED_PEAK_MARKER);
			/*
			 * Peak
			 */
			lineSeriesData = peakChartSupport.getPeak(peak, true, mirrored, colorPeak, SERIES_ID_SELECTED_PEAK_SHAPE);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setSymbolType(symbolTypeScanMarker);
			lineSeriesSettings.setSymbolColor(colorPeak);
			lineSeriesSettings.setSymbolSize(scanMarkerSize);
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
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN));
			int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE));
			ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scan, false, SERIES_ID_SELECTED_SCAN, displayType, chromatogramSelection);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesSettings.setSymbolColor(color);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addBaselineData(List<ILineSeriesData> lineSeriesDataList) {

		boolean showChromatogramBaseline = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE);
		//
		if(chromatogramSelection != null && showChromatogramBaseline) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE));
			boolean enableBaselineArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_BASELINE_AREA);
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = chromatogramChartSupport.getLineSeriesDataBaseline(chromatogramSelection, SERIES_ID_BASELINE, displayType, color, false);
			lineSeriesData.getSettings().setEnableArea(enableBaselineArea);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Define the compression level.
		 */
		String compressionType = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
		chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
	}

	private void initialize(Composite parent, int style) {

		parent.setLayout(new GridLayout(1, true));
		//
		toolbarMain = createToolbarMain(parent);
		toolbars.put(TOOLBAR_INFO, createToolbarInfo(parent));
		toolbars.put(TOOLBAR_EDIT, createToolbarEdit(parent));
		toolbars.put(TOOLBAR_CHROMATOGRAM_ALIGNMENT, createChromatogramAlignmentUI(parent));
		toolbars.put(TOOLBAR_METHOD, createToolbarMethod(parent));
		toolbars.put(TOOLBAR_RETENTION_INDICES, retentionIndexUI = createToolbarRetentionIndexUI(parent));
		//
		SashForm chartsArea = new SashForm(parent, SWT.HORIZONTAL);
		chartsArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		createChromatogramChart(chartsArea, style);
		createHeatmap(chartsArea);
		//
		comboViewerSeparationColumn.setInput(SeparationColumnFactory.getSeparationColumns());
		//
		PartSupport.setCompositeVisibility(toolbarMain, true);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_INFO), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_EDIT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_METHOD), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_RETENTION_INDICES), false);
		PartSupport.setCompositeVisibility(heatmapArea, false);
		//
		chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(10, false));
		//
		createToggleToolbarButton(composite, "Toggle the info toolbar.", IApplicationImage.IMAGE_INFO, TOOLBAR_INFO);
		comboViewerSeparationColumn = createComboViewerSeparationColumn(composite);
		createChromatogramReferencesUI(composite);
		createToggleToolbarButton(composite, "Toggle the edit toolbar.", IApplicationImage.IMAGE_EDIT, TOOLBAR_EDIT);
		createToggleToolbarButton(composite, "Toggle the chromatogram alignment toolbar.", IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS, TOOLBAR_CHROMATOGRAM_ALIGNMENT);
		createToggleToolbarButton(composite, "Toggle the method toolbar.", IApplicationImage.IMAGE_METHOD, TOOLBAR_METHOD);
		chromatogramActionUI = createChromatogramActionUI(composite);
		createResetButton(composite);
		createSettingsButton(composite);
		//
		return composite;
	}

	private void createChromatogramReferencesUI(Composite parent) {

		chromatogramReferencesUI = new ChromatogramReferencesUI(parent, SWT.NONE);
		chromatogramReferencesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		chromatogramReferencesUI.setChromatogramReferencesListener(new IChromatogramReferencesListener() {

			@Override
			public void update(IChromatogramSelection chromatogramSelection) {

				updateChromatogramSelection(chromatogramSelection);
				fireUpdate(chromatogramReferencesUI.getDisplay());
			}
		});
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

	private RetentionIndexUI createToolbarRetentionIndexUI(Composite parent) {

		RetentionIndexUI retentionIndexUI = new RetentionIndexUI(parent, SWT.NONE);
		retentionIndexUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		retentionIndexUI.setSearchVisibility(false);
		//
		return retentionIndexUI;
	}

	private Composite createToolbarMethod(Composite parent) {

		MethodSupportUI methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@Override
			public void execute(IProcessMethod processMethod, IProgressMonitor monitor) {

				ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
				IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
				IChromatogramSelectionProcessSupplier.applyProcessMethod(chromatogramSelection, processMethod, processTypeSupport, processingInfo, monitor);
				chromatogramSelection.update(false);
				ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, processingInfo.hasErrorMessages());
			}
		});
		//
		return methodSupportUI;
	}

	private ChromatogramAlignmentUI createChromatogramAlignmentUI(Composite parent) {

		ChromatogramAlignmentUI composite = new ChromatogramAlignmentUI(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	private ComboViewer createComboViewerSeparationColumn(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISeparationColumn) {
					ISeparationColumn separationColumn = (ISeparationColumn)element;
					return separationColumn.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a chromatogram column.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn && chromatogramSelection != null) {
					ISeparationColumn separationColumn = (ISeparationColumn)object;
					chromatogramSelection.getChromatogram().getSeparationColumnIndices().setSeparationColumn(separationColumn);
					updateLabel();
				}
			}
		});
		//
		return comboViewer;
	}

	private Composite createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createToggleToolbarButton(composite, "Toggle the retention index toolbar.", IApplicationImage.IMAGE_RETENION_INDEX, TOOLBAR_RETENTION_INDICES);
		createVerticalSeparator(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		createVerticalSeparator(composite);
		createButtonSignalSelection(composite);
		//
		return composite;
	}

	private void createButtonSignalSelection(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select signal");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEATMAP_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				signalSelectionHeatMap();
			}
		});
	}

	private void signalSelectionHeatMap() {

		PartSupport.setCompositeVisibility(heatmapArea, !heatmapArea.getVisible());
		heatmapUI.setChromatogramSelection(chromatogramSelection);
		if(chromatogramSelection instanceof ChromatogramSelectionWSD) {
			if(displayType.equals(DisplayType.SWC)) {
				displayType = DisplayType.TIC;
				update();
			} else if(displayType.equals(DisplayType.TIC)) {
				displayType = DisplayType.SWC;
				update();
			}
		}
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private void createChromatogramChart(Composite parent, int style) {

		chromatogramChart = new ChromatogramChart(parent, style);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Custom Selection Handler
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addCustomRangeSelectionHandler(new ChromatogramSelectionHandler(this));
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
		chartSettings.addMenuEntry(new ChromatogramResetHandler(this));
		chartSettings.addHandledEventProcessor(new ScanSelectionHandler(this));
		chartSettings.addHandledEventProcessor(new PeakSelectionHandler(this));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(this, SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(this, SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_UP));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_DOWN));
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void createHeatmap(Composite composite) {

		heatmapArea = new Composite(composite, SWT.None);
		heatmapArea.setLayout(new FillLayout());
		heatmapUI = new HeatmapUI(heatmapArea);
	}

	private Button createToggleToolbarButton(Composite parent, String tooltip, String image, String toolbar) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(tooltip);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(toolbars.containsKey(toolbar)) {
					boolean visible = PartSupport.toggleCompositeVisibility(toolbars.get(toolbar));
					if(visible) {
						button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
					} else {
						button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
					}
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

				chromatogramChart.togglePositionLegendVisibility();
				chromatogramChart.redraw();
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

	private ChromatogramActionUI createChromatogramActionUI(Composite parent) {

		return new ChromatogramActionUI(parent, SWT.NONE);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chromatogram");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset(true);
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
				preferencePageChromatogram.setTitle("Chromatogram Settings");
				IPreferencePage preferencePageChromatogramAxes = new PreferencePageChromatogramAxes();
				preferencePageChromatogramAxes.setTitle("Chromatogram Axes");
				IPreferencePage preferencePageChromatogramPeaks = new PreferencePageChromatogramPeaks();
				preferencePageChromatogramPeaks.setTitle("Chromatogram Peaks");
				IPreferencePage preferencePageChromatogramScans = new PreferencePageChromatogramScans();
				preferencePageChromatogramScans.setTitle("Chromatogram Scans");
				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageChromatogramAxes));
				preferenceManager.addToRoot(new PreferenceNode("3", preferencePageChromatogramPeaks));
				preferenceManager.addToRoot(new PreferenceNode("4", preferencePageChromatogramScans));
				preferenceManager.addToRoot(new PreferenceNode("5", preferencePageSWT));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		adjustAxisSettings();
		updateChromatogram();
	}

	private void reset(boolean resetRange) {

		updateChromatogram();
		if(resetRange && chromatogramSelection != null) {
			chromatogramSelection.reset(true);
		}
	}

	private void updateLabel() {

		if(chromatogramSelection != null) {
			labelChromatogramInfo.setText(ChromatogramDataSupport.getChromatogramLabelExtended(chromatogramSelection.getChromatogram()));
		} else {
			labelChromatogramInfo.setText("");
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

	private void adjustChromatogramSelectionRange() {

		if(chromatogramSelection != null) {
			chromatogramChart.setRange(IExtendedChart.X_AXIS, chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
			chromatogramChart.setRange(IExtendedChart.Y_AXIS, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
		}
	}

	private void setSeparationColumnSelection() {

		if(chromatogramSelection != null) {
			ISeparationColumn separationColumn = chromatogramSelection.getChromatogram().getSeparationColumnIndices().getSeparationColumn();
			if(separationColumn != null) {
				String name = separationColumn.getName();
				int index = -1;
				exitloop:
				for(String item : comboViewerSeparationColumn.getCombo().getItems()) {
					index++;
					if(item.equals(name)) {
						break exitloop;
					}
				}
				//
				if(index >= 0) {
					comboViewerSeparationColumn.getCombo().select(index);
				}
			}
		}
	}

	private void adjustAxisSettings() {

		chromatogramChart.modifyAxes(true);
		/*
		 * Scan Axis
		 */
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsX(TITLE_X_AXIS_SCANS, chartSettings);
				//
				if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SCANS)) {
					if(axisSettings == null) {
						try {
							int scanDelay = chromatogram.getScanDelay();
							int scanInterval = chromatogram.getScanInterval();
							ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_SCANS, new MillisecondsToScanNumberConverter(scanDelay, scanInterval));
							secondaryAxisSettingsX.setTitleVisible(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SCANS));
							setScanAxisSettings(secondaryAxisSettingsX);
							chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
						} catch(Exception e) {
							logger.warn(e);
						}
					} else {
						setScanAxisSettings(axisSettings);
					}
				} else {
					/*
					 * Remove
					 */
					if(axisSettings != null) {
						chartSettings.getSecondaryAxisSettingsListX().remove(axisSettings);
					}
				}
				chromatogramChart.applySettings(chartSettings);
			}
		}
	}

	private void setScanAxisSettings(IAxisSettings axisSettings) {

		ChartSupport chartSupport = new ChartSupport(Activator.getDefault().getPreferenceStore());
		Position position = Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_SCANS));
		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_X_AXIS_SCANS));
		LineStyle gridLineStyle = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS));
		Color gridColor = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS));
		chartSupport.setAxisSettings(axisSettings, position, "0", color, gridLineStyle, gridColor);
		//
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_X_AXIS_SCANS);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_X_AXIS_SCANS);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_X_AXIS_SCANS);
		Font titleFont = Fonts.getCachedFont(chromatogramChart.getDisplay(), name, height, style);
		axisSettings.setTitleFont(titleFont);
	}

	private void updateToolbar(Composite composite, IChromatogramSelection chromatogramSelection) {

		if(composite instanceof IChromatogramSelectionUpdateListener) {
			IChromatogramSelectionUpdateListener listener = (IChromatogramSelectionUpdateListener)composite;
			listener.update(chromatogramSelection);
		}
	}
}
