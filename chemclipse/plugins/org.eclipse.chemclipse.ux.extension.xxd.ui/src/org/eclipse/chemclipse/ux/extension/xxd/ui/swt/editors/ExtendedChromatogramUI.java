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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.updates.IChromatogramSelectionUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier.SupplierType;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EditorToolBar;
import org.eclipse.chemclipse.support.ui.swt.ProcessorToolbar;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.TargetLabelEditAction;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.TargetLabelEditAction.LabelChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.RetentionIndexUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorProcessTypeSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.TargetReferenceLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceInitializer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramAxes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramPeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.PreferenceStoreTargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.SignalTargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.WorkspaceTargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramReferencesUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.HeatmapUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ToolbarConfig;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryNameComparator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ICustomPaintListener;
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
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.ResetChartHandler;

@SuppressWarnings("rawtypes")
public class ExtendedChromatogramUI implements ToolbarConfig {

	// this is a private preference
	private static final String PREFERENCE_CHROMATOGRAM_UI_SHOW_TOOLBAR_TEXT = "ChromatogramUI.showToolbarText";
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
	public static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";
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
	private final Map<String, Composite> toolbars = new HashMap<>();
	//
	private EditorToolBar toolbarMain;
	private Label labelChromatogramInfo;
	private ChromatogramReferencesUI chromatogramReferencesUI;
	private RetentionIndexUI retentionIndexUI;
	private ChromatogramChart chromatogramChart;
	private ComboViewer comboViewerSeparationColumn;
	private HeatmapUI heatmapUI;
	private Composite heatmapArea;
	//
	private IChromatogramSelection<?, ?> chromatogramSelection = null;
	private final List<IChartMenuEntry> cachedMenuEntries = new ArrayList<>();
	//
	private final Map<String, TargetReferenceLabelMarker> peakLabelMarkerMap = new HashMap<>();
	private final Map<String, TargetReferenceLabelMarker> scanLabelMarkerMap = new HashMap<>();
	//
	private final PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private final PeakChartSupport peakChartSupport = new PeakChartSupport();
	private final ScanChartSupport scanChartSupport = new ScanChartSupport();
	private final ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private final ChartSupport chartSupport;
	//
	private DisplayType displayType = DisplayType.TIC;
	//
	private boolean suspendUpdate = false;
	private final IPreferenceStore preferenceStore;
	private final ProcessSupplierContext processTypeSupport;
	//
	private final IEventBroker eventBroker;
	private MethodSupportUI methodSupportUI;
	private WorkspaceTargetDisplaySettings targetDisplaySettings;
	private Predicate<IProcessSupplier<?>> dataCategoryPredicate;
	private ProcessorToolbar processorToolbar;

	@Deprecated
	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker) {
		this(parent, style, eventBroker, Activator.getDefault().getPreferenceStore());
	}

	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker, ProcessSupplierContext supplierContext) {
		this(parent, style, eventBroker, supplierContext, Activator.getDefault().getPreferenceStore());
	}

	@Deprecated
	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker, IPreferenceStore store) {
		this(parent, style, eventBroker, new org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport(), store);
	}

	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker, ProcessSupplierContext supplierContext, IPreferenceStore store) {
		this.eventBroker = eventBroker;
		processTypeSupport = supplierContext;
		preferenceStore = store;
		chartSupport = new ChartSupport(store);
		initialize(parent, style);
	}

	@Override
	public void setToolbarVisible(boolean visible) {

		toolbarMain.setVisible(visible);
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

		setChromatogramSelectionInternal(chromatogramSelection);
		chromatogramReferencesUI.setMasterChromatogram(chromatogramSelection);
	}

	private void setChromatogramSelectionInternal(IChromatogramSelection chromatogramSelection) {

		if(this.chromatogramSelection != chromatogramSelection) {
			DataCategory datatype = DataCategory.AUTO_DETECT;
			if(chromatogramSelection != null) {
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				if(chromatogram instanceof IChromatogramMSD) {
					datatype = DataCategory.MSD;
				} else if(chromatogram instanceof IChromatogramWSD) {
					datatype = DataCategory.WSD;
				} else if(chromatogram instanceof IChromatogramCSD) {
					datatype = DataCategory.CSD;
				}
			}
			dataCategoryPredicate = ProcessSupplierContext.createDataCategoryPredicate(datatype);
			targetDisplaySettings = null;
			this.chromatogramSelection = chromatogramSelection;
			updateToolbar(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), chromatogramSelection);
			//
			if(chromatogramSelection != null) {
				/*
				 * Adjust
				 */
				adjustAxisSettings();
				updateMenu(true);
				updateChromatogram();
				setSeparationColumnSelection();
				retentionIndexUI.setInput(chromatogramSelection.getChromatogram().getSeparationColumnIndices());
			} else {
				retentionIndexUI.setInput(null);
				updateChromatogram();
			}
			processorToolbar.update();
			fireUpdate(getChromatogramChart().getDisplay());
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
		addSelectedPeakData(lineSeriesDataList, getTargetSettings());
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
			chromatogramReferencesUI.update();
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

	public void updateMenu() {

		updateMenu(false);
	}

	public void updateMenu(boolean force) {

		if(processTypeSupport != null) {
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
			List<IProcessSupplier<?>> suplierList = new ArrayList<>(processTypeSupport.getSupplier(this::isValidSupplier));
			Collections.sort(suplierList, new CategoryNameComparator());
			for(IProcessSupplier<?> supplier : suplierList) {
				IChartMenuEntry cachedEntry = new ProcessorSupplierMenuEntry<>(supplier, processTypeSupport, this::executeSupplier);
				cachedMenuEntries.add(cachedEntry);
				chartSettings.addMenuEntry(cachedEntry);
			}
			/*
			 * Apply the menu items.
			 */
			chromatogramChart.applySettings(chartSettings);
		}
	}

	private <C> void executeSupplier(IProcessSupplier<C> processSupplier, ProcessSupplierContext processSupplierContext) {

		Shell shell = getChromatogramChart().getShell();
		try {
			ProcessorPreferences<C> settings = SettingsWizard.getSettings(shell, SettingsWizard.getWorkspacePreferences(processSupplier));
			if(settings == null) {
				return;
			}
			processChromatogram(new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					DefaultProcessingResult<Object> msgs = new DefaultProcessingResult<>();
					IProcessSupplier.applyProcessor(settings, IChromatogramSelectionProcessSupplier.createConsumer(getChromatogramSelection()), new ProcessExecutionContext(monitor, msgs, processSupplierContext));
					updateResult(shell, msgs);
				}
			}, shell);
		} catch(IOException e) {
			DefaultProcessingResult<Object> result = new DefaultProcessingResult<>();
			result.addErrorMessage(processSupplier.getName(), "can't process settings", e);
			updateResult(shell, result);
		}
	}

	@SuppressWarnings("deprecation")
	public void updateResult(Shell shell, MessageProvider result) {

		if(result != null) {
			shell.getDisplay().asyncExec(() -> ProcessingInfoViewSupport.updateProcessingInfo(result, result.hasErrorMessages()));
		}
	}

	private boolean isValidSupplier(IProcessSupplier<?> supplier) {

		if(supplier.getType() == SupplierType.STRUCTURAL) {
			return false;
		}
		if(supplier.getTypeSupplier() instanceof EditorProcessTypeSupplier) {
			return false;
		}
		return dataCategoryPredicate != null && dataCategoryPredicate.test(supplier);
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
			addChromatogramSeriesData(getTargetSettings());
			adjustChromatogramSelectionRange();
		}
	}

	private WorkspaceTargetDisplaySettings getTargetSettings() {

		if(targetDisplaySettings == null) {
			File chromatogramFile;
			if(chromatogramSelection != null) {
				chromatogramFile = chromatogramSelection.getChromatogram().getFile();
			} else {
				chromatogramFile = null;
			}
			targetDisplaySettings = WorkspaceTargetDisplaySettings.getWorkspaceSettings(chromatogramFile, PreferenceStoreTargetDisplaySettings.getSettings(preferenceStore));
		}
		return targetDisplaySettings;
	}

	private void clearPeakAndScanLabels() {

		for(String key : peakLabelMarkerMap.keySet()) {
			removeIdentificationLabelMarker(peakLabelMarkerMap, key);
		}
		for(String key : scanLabelMarkerMap.keySet()) {
			removeIdentificationLabelMarker(scanLabelMarkerMap, key);
		}
		peakLabelMarkerMap.clear();
		scanLabelMarkerMap.clear();
	}

	private void removeIdentificationLabelMarker(Map<String, ? extends ICustomPaintListener> markerMap, String seriesId) {

		IPlotArea plotArea = chromatogramChart.getBaseChart().getPlotArea();
		ICustomPaintListener labelMarker = markerMap.get(seriesId);
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

	private void addChromatogramSeriesData(TargetDisplaySettings settings) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		//
		addChromatogramData(lineSeriesDataList);
		addPeakData(lineSeriesDataList, settings);
		addIdentifiedScansData(lineSeriesDataList, settings);
		addSelectedPeakData(lineSeriesDataList, settings);
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

	private void addPeakData(List<ILineSeriesData> lineSeriesDataList, TargetDisplaySettings settings) {

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
			addPeaks(lineSeriesDataList, peaksActiveNormal, symbolTypeActiveNormal, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL_ACTIVE, settings);
			addPeaks(lineSeriesDataList, peaksInactiveNormal, symbolTypeInactiveNormal, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_NORMAL_INACTIVE, settings);
			addPeaks(lineSeriesDataList, peaksActiveISTD, symbolTypeActiveIstd, symbolSize, Colors.RED, SERIES_ID_PEAKS_ISTD_ACTIVE, settings);
			addPeaks(lineSeriesDataList, peaksInactiveISTD, symbolTypeInactiveIstd, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_ISTD_INACTIVE, settings);
		}
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId, TargetDisplaySettings displaySettings) {

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
			if(displaySettings.isShowPeakLabels()) {
				IPlotArea plotArea = chromatogramChart.getBaseChart().getPlotArea();
				TargetReferenceLabelMarker peakLabelMarker = new TargetReferenceLabelMarker(SignalTargetReference.getPeakReferences(peaks), displaySettings, symbolSize * 2);
				plotArea.addCustomPaintListener(peakLabelMarker);
				peakLabelMarkerMap.put(seriesId, peakLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, TargetDisplaySettings displaySettings) {

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
			if(displaySettings.isShowScanLables()) {
				IPlotArea plotArea = chromatogramChart.getBaseChart().getPlotArea();
				TargetReferenceLabelMarker scanLabelMarker = new TargetReferenceLabelMarker(SignalTargetReference.getScanReferences(scans), displaySettings, symbolSize * 2);
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

	private void addSelectedPeakData(List<ILineSeriesData> lineSeriesDataList, TargetDisplaySettings settings) {

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
			addPeaks(lineSeriesDataList, peaks, symbolTypePeakMarker, symbolSize, colorPeak, SERIES_ID_SELECTED_PEAK_MARKER, settings);
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
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_INFO), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_EDIT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_METHOD), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_RETENTION_INDICES), false);
		PartSupport.setCompositeVisibility(heatmapArea, false);
	}

	private EditorToolBar createToolbarMain(Composite parent) {

		EditorToolBar editorToolBar = new EditorToolBar(parent);
		processorToolbar = new ProcessorToolbar(editorToolBar, processTypeSupport, this::isValidSupplier, this::executeSupplier);
		editorToolBar.addAction(createLabelsAction());
		editorToolBar.addAction(createToggleToolbarAction("Info", "the info toolbar.", IApplicationImage.IMAGE_INFO, TOOLBAR_INFO));
		editorToolBar.createCombo(this::initComboViewerSeparationColumn, true, 150);
		chromatogramReferencesUI = new ChromatogramReferencesUI(editorToolBar, this::setChromatogramSelectionInternal);
		editorToolBar.addAction(createToggleToolbarAction("Edit", "the edit toolbar.", IApplicationImage.IMAGE_EDIT, TOOLBAR_EDIT));
		editorToolBar.addAction(createToggleToolbarAction("Alignment", "the chromatogram alignment toolbar.", IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS, TOOLBAR_CHROMATOGRAM_ALIGNMENT));
		editorToolBar.addAction(createToggleToolbarAction("Methods", "the method toolbar.", IApplicationImage.IMAGE_METHOD, TOOLBAR_METHOD));
		//
		createResetButton(editorToolBar);
		editorToolBar.enableToolbarTextPage(preferenceStore, PREFERENCE_CHROMATOGRAM_UI_SHOW_TOOLBAR_TEXT);
		processorToolbar.enablePreferencePage(preferenceStore, "ProcessorToolbar.Processors");
		editorToolBar.addPreferencePages(new Supplier<Collection<? extends IPreferencePage>>() {

			@Override
			public Collection<? extends IPreferencePage> get() {

				IPreferencePage processorsPage = new PreferencePageProcessors(processTypeSupport);
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
				return Arrays.asList(processorsPage, preferencePageChromatogram, preferencePageChromatogramAxes, preferencePageChromatogramPeaks, preferencePageChromatogramScans, preferencePageSWT);
			}
		}, this::applySettings);
		return editorToolBar;
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

		methodSupportUI = new MethodSupportUI(parent, SWT.NONE, false);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@Override
			public void execute(IProcessMethod processMethod, IProgressMonitor monitor) {

				IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
				IChromatogramSelection selection = getChromatogramSelection();
				ProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processingInfo, processTypeSupport), IChromatogramSelectionProcessSupplier.createConsumer(selection));
				selection.update(false);
				updateResult(parent.getShell(), processingInfo);
			}
		});
		toolbarMain.addPreferencePages(() -> Arrays.asList(methodSupportUI.getPreferencePages()), methodSupportUI::applySettings);
		//
		return methodSupportUI;
	}

	private ChromatogramAlignmentUI createChromatogramAlignmentUI(Composite parent) {

		ChromatogramAlignmentUI composite = new ChromatogramAlignmentUI(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	private void initComboViewerSeparationColumn(ComboViewer comboViewer) {

		comboViewerSeparationColumn = comboViewer;
		Control combo = comboViewer.getControl();
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
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn && chromatogramSelection != null) {
					ISeparationColumn separationColumn = (ISeparationColumn)object;
					chromatogramSelection.getChromatogram().getSeparationColumnIndices().setSeparationColumn(separationColumn);
					updateLabel();
				}
			}
		});
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

	private IAction createLabelsAction() {

		return new TargetLabelEditAction(new LabelChart() {

			@Override
			public void setChartMarkerVisible(boolean visible) {

				for(TargetReferenceLabelMarker marker : scanLabelMarkerMap.values()) {
					marker.setVisible(visible);
				}
				for(TargetReferenceLabelMarker marker : peakLabelMarkerMap.values()) {
					marker.setVisible(visible);
				}
			}

			@Override
			public void refresh() {

				getTargetSettings().flush();
				ExtendedChromatogramUI.this.updateChromatogram();
			}

			@Override
			public void redraw() {

				getChart().redraw();
			}

			@Override
			public WorkspaceTargetDisplaySettings getTargetSettings() {

				return ExtendedChromatogramUI.this.getTargetSettings();
			}

			@Override
			public IChromatogramSelection<?, ?> getChromatogramSelection() {

				return ExtendedChromatogramUI.this.getChromatogramSelection();
			}

			@Override
			public ChromatogramChart getChart() {

				return ExtendedChromatogramUI.this.getChromatogramChart();
			}
		});
	}

	private IAction createToggleToolbarAction(String name, String tooltip, String image, String toolbar) {

		return new Action(name, Action.AS_CHECK_BOX) {

			{
				setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(image, IApplicationImage.SIZE_16x16));
				setToolTipText(tooltip);
				updateText();
			}

			@Override
			public void run() {

				if(toolbars.containsKey(toolbar)) {
					setChecked(PartSupport.toggleCompositeVisibility(toolbars.get(toolbar)));
					updateText();
				}
			}

			private void updateText() {

				if(isChecked()) {
					setToolTipText("Hide " + tooltip);
				} else {
					setToolTipText("Show " + tooltip);
				}
			}
		};
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

	private void createResetButton(EditorToolBar editorToolBar) {

		editorToolBar.addAction(new Action("Reset", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Reset the chromatogram");
			}

			@Override
			public void run() {

				reset(true);
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

		ChartSupport chartSupport = new ChartSupport(preferenceStore);
		Position position = Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_SCANS));
		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_X_AXIS_SCANS));
		LineStyle gridLineStyle = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS));
		Color gridColor = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS));
		chartSupport.setAxisSettings(axisSettings, position, "0", color, gridLineStyle, gridColor);
		//
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

	public void updateMethods() {

		methodSupportUI.applySettings();
	}

	/**
	 * Initializes a store with required defaults so it can be used with the {@link ExtendedChromatogramUI}
	 * 
	 * @param preferenceStore
	 */
	public static void initializeChartDefaults(IPreferenceStore preferenceStore) {

		// we delegate here to PreferenceInitializer
		PreferenceInitializer.initializeChromatogramDefaults(preferenceStore);
		// and set our private preference also
		preferenceStore.setDefault(PREFERENCE_CHROMATOGRAM_UI_SHOW_TOOLBAR_TEXT, true);
	}
}
