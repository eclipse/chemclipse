/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - propagate result of methods to the user, add label selection support
 * Matthias Mailänder - display selected wavelengths
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.chemclipse.converter.methods.MetaProcessorProcessSupplier;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.model.updates.IChromatogramSelectionUpdateListener;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
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
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.processors.ProcessorToolbar;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EditorToolBar;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.GridLineEditAction;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.ILabelEditSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.TargetLabelEditAction;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.RetentionIndexUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorProcessTypeSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.TargetReferenceLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodCancelException;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.ResumeMethodSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisIntensity;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisMilliseconds;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisMinutes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisRelativeIntensity;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisSeconds;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceInitializer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramPeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentPaintListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramBaselinesUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramReferencesUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ToolbarConfig;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
import org.eclipse.swtchart.extensions.preferences.PreferencePage;

@SuppressWarnings("rawtypes")
public class ExtendedChromatogramUI extends Composite implements ToolbarConfig {

	public static final String PREFERENCE_SHOW_TOOLBAR_TEXT = "ChromatogramUI.showToolbarText";
	//
	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	protected static final String TYPE_GENERIC = "TYPE_GENERIC";
	protected static final String TYPE_MSD = "TYPE_MSD";
	protected static final String TYPE_CSD = "TYPE_CSD";
	protected static final String TYPE_WSD = "TYPE_WSD";
	//
	private String titleScans = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_SCANS);
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
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scans Selected";
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
	private ChromatogramBaselinesUI chromatogramBaselinesUI;
	private ChromatogramReferencesUI chromatogramReferencesUI;
	private ChromatogramAlignmentUI chromatogramAlignmentUI;
	private RetentionIndexUI retentionIndexUI;
	private ChromatogramChart chromatogramChart;
	private ComboViewer comboViewerSeparationColumn;
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
	//
	private DisplayType displayType = DisplayType.TIC;
	//
	private boolean suspendUpdate = false;
	private final IPreferenceStore preferenceStore;
	private final ProcessSupplierContext processTypeSupport;
	//
	private final IEventBroker eventBroker;
	private MethodSupportUI methodSupportUI;
	private ITargetDisplaySettings targetDisplaySettings;
	private Predicate<IProcessSupplier<?>> dataCategoryPredicate;
	private ProcessorToolbar processorToolbar;
	//
	private Object menuCache = null;
	private boolean menuActive = false;
	//
	private final List<ISeparationColumn> separationColumns = SeparationColumnFactory.getSeparationColumns();

	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker) {

		this(parent, style, eventBroker, Activator.getDefault().getPreferenceStore());
	}

	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker, ProcessSupplierContext supplierContext) {

		this(parent, style, eventBroker, supplierContext, Activator.getDefault().getPreferenceStore());
	}

	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker, IPreferenceStore store) {

		this(parent, style, eventBroker, new org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport(), store);
	}

	public ExtendedChromatogramUI(Composite parent, int style, IEventBroker eventBroker, ProcessSupplierContext supplierContext, IPreferenceStore store) {

		super(parent, style);
		this.eventBroker = eventBroker;
		processTypeSupport = supplierContext;
		preferenceStore = store;
		createControl();
	}

	@Override
	public void setToolbarVisible(boolean visible) {

		toolbarMain.setVisible(visible);
	}

	@Override
	public boolean isToolbarVisible() {

		return toolbarMain.isVisible();
	}

	/**
	 * Resets the chart ranges 1:1.
	 */
	public void adjustChromatogramChart() {

		if(!menuActive) {
			chromatogramChart.adjustRange(true);
		}
	}

	public void fireUpdate(Display display) {

		fireUpdateChromatogram(display);
		if(!fireUpdatePeak(display)) {
			fireUpdateScan(display);
		}
	}

	public boolean fireUpdateChromatogram(Display display) {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null) {
			UpdateNotifierUI.update(display, chromatogramSelection);
		}
		return chromatogramSelection != null ? true : false;
	}

	public boolean fireUpdatePeak(Display display) {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null) {
			final IPeak peak = chromatogramSelection.getSelectedPeak();
			if(peak != null) {
				update = true;
				UpdateNotifierUI.update(display, peak);
			}
		}
		return update;
	}

	public boolean fireUpdateScan(Display display) {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null) {
			final IScan scan = chromatogramSelection.getSelectedScan();
			if(scan != null) {
				update = true;
				UpdateNotifierUI.update(display, scan);
			}
		}
		return update;
	}

	public ChromatogramChart getChromatogramChart() {

		return chromatogramChart;
	}

	public synchronized void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		setChromatogramSelectionInternal(chromatogramSelection);
		chromatogramBaselinesUI.update(chromatogramSelection.getChromatogram());
		chromatogramReferencesUI.setMasterChromatogram(chromatogramSelection);
		chromatogramAlignmentUI.update(chromatogramReferencesUI.getChromatogramSelections());
	}

	private void updateWavelengths() {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		if(!(chromatogram instanceof IChromatogramWSD)) {
			return;
		}
		IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
		if(chromatogramSelectionWSD.getSelectedWavelengths().getWavelengths().size() == 1) {
			displayType = DisplayType.SWC;
		} else {
			displayType = DisplayType.TIC;
		}
	}

	private void setChromatogramSelectionInternal(IChromatogramSelection chromatogramSelection) {

		if(this.chromatogramSelection != chromatogramSelection) {
			DataCategory dataCategory = DataCategory.AUTO_DETECT;
			if(chromatogramSelection != null) {
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				if(chromatogram instanceof IChromatogramMSD) {
					dataCategory = DataCategory.MSD;
				} else if(chromatogram instanceof IChromatogramWSD) {
					dataCategory = DataCategory.WSD;
				} else if(chromatogram instanceof IChromatogramCSD) {
					dataCategory = DataCategory.CSD;
				}
			}
			/*
			 * Adjust
			 */
			dataCategoryPredicate = ProcessSupplierContext.createDataCategoryPredicate(dataCategory);
			targetDisplaySettings = null;
			this.chromatogramSelection = chromatogramSelection;
			updateToolbar(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), chromatogramSelection);
			//
			if(chromatogramSelection != null) {
				adjustAxisSettings();
				updateMenu();
				updateChromatogram();
				setSeparationColumnSelection();
				retentionIndexUI.setInput(chromatogramSelection.getChromatogram().getSeparationColumnIndices());
			} else {
				adjustAxisSettings();
				updateChromatogram();
				retentionIndexUI.setInput(null);
			}
			/*
			 * Update the chart.
			 * fireUpdate(getChromatogramChart().getDisplay()); makes problems here.
			 * The update process needs to be addressed generally.
			 */
			processorToolbar.update();
			if(chromatogramSelection != null) {
				UpdateNotifierUI.update(getDisplay(), chromatogramSelection);
			}
		}
	}

	@Override
	public void update() {

		if(!suspendUpdate) {
			if(!menuActive) {
				updateChromatogram();
				adjustChromatogramSelectionRange();
				setSeparationColumnSelection();
				updateWavelengths();
			}
		}
	}

	public void updateSelectedScan() {

		if(!menuActive) {
			chromatogramChart.deleteSeries(SERIES_ID_SELECTED_SCAN);
			chromatogramChart.deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			addSelectedScanData(lineSeriesDataList);
			addSelectedIdentifiedScanData(lineSeriesDataList);
			addLineSeriesData(lineSeriesDataList);
			adjustChromatogramSelectionRange();
		}
	}

	public void updateSelectedPeak() {

		if(!menuActive) {
			Set<String> seriesIds = chromatogramChart.getBaseChart().getSeriesIds();
			//
			chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_MARKER);
			for(String seriesId : seriesIds) {
				if(seriesId.startsWith(SERIES_ID_SELECTED_PEAK_SHAPE)) {
					chromatogramChart.deleteSeries(seriesId);
				} else if(seriesId.startsWith(SERIES_ID_SELECTED_PEAK_BACKGROUND)) {
					chromatogramChart.deleteSeries(seriesId);
				}
			}
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			addSelectedPeakData(lineSeriesDataList, getTargetSettings());
			addLineSeriesData(lineSeriesDataList);
			adjustChromatogramSelectionRange();
		}
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
			/*
			 * Select the reference chromatogram.
			 */
			updateChromatogram();
			chromatogramReferencesUI.update();
			updateSelection();
			fireUpdate(shell.getDisplay());
		} catch(InterruptedException e) {
			logger.error(e.getLocalizedMessage(), e);
			Thread.currentThread().interrupt();
		} catch(InvocationTargetException e) {
			logger.warn(e);
		}
	}

	public void updateMenu() {

		if(processTypeSupport != null) {
			if(menuCache != chromatogramSelection) {
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
				menuCache = chromatogramSelection;
			}
		}
	}

	private <C> void executeSupplier(IProcessSupplier<C> processSupplier, ProcessSupplierContext processSupplierContext) {

		try {
			Shell shell = getChromatogramChart().getShell();
			ProcessorPreferences<C> settings = SettingsWizard.getSettings(shell, SettingsWizard.getWorkspacePreferences(processSupplier), true);
			if(settings == null) {
				return;
			}
			//
			if(processSupplier instanceof MetaProcessorProcessSupplier) {
				MetaProcessorProcessSupplier metaProcessorProcessSupplier = (MetaProcessorProcessSupplier)processSupplier;
				IProcessMethod processMethod = metaProcessorProcessSupplier.getProcessMethod();
				int resumeIndex = ResumeMethodSupport.selectResumeIndex(shell, processMethod);
				processMethod.setResumeIndex(resumeIndex);
			}
			//
			processChromatogram(new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
					IProcessSupplier.applyProcessor(settings, IChromatogramSelectionProcessSupplier.createConsumer(getChromatogramSelection()), new ProcessExecutionContext(monitor, processingInfo, processSupplierContext));
					updateResult(processingInfo);
				}
			}, shell);
		} catch(IOException e) {
			DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
			processingInfo.addErrorMessage(processSupplier.getName(), "The process method can't be applied.", e);
			updateResult(processingInfo);
		} catch(MethodCancelException e) {
			DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
			processingInfo.addWarnMessage(processSupplier.getName(), "The process method execution has been cancelled.");
			updateResult(processingInfo);
		}
	}

	private void forceReset(boolean resetRange) {

		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				reset(resetRange);
			}
		});
	}

	public void updateResult(MessageProvider processingInfo) {

		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				ProcessingInfoPartSupport.getInstance().update(processingInfo, true);
			}
		});
	}

	private boolean isValidSupplier(IProcessSupplier<?> supplier) {

		if(supplier.getType() == SupplierType.STRUCTURAL) {
			return false;
		}
		//
		if(supplier.getTypeSupplier() instanceof EditorProcessTypeSupplier) {
			return false;
		}
		//
		return dataCategoryPredicate != null && dataCategoryPredicate.test(supplier);
	}

	private void updateChromatogram() {

		updateLabel();
		clearPeakAndScanLabels();
		deleteScanNumberSecondaryAxisX();
		chromatogramChart.deleteSeries();
		//
		if(chromatogramSelection != null) {
			setRangeRestrictions();
			addChromatogramSeriesData();
			adjustChromatogramSelectionRange();
			chromatogramAlignmentUI.update(chromatogramReferencesUI.getChromatogramSelections());
		}
	}

	private void clearPeakAndScanLabels() {

		for(String key : peakLabelMarkerMap.keySet()) {
			removeIdentificationLabelMarker(peakLabelMarkerMap, key);
		}
		//
		for(String key : scanLabelMarkerMap.keySet()) {
			removeIdentificationLabelMarker(scanLabelMarkerMap, key);
		}
		//
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

	private void setRangeRestrictions() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		/*
		 * Add space on top to show labels correctly.
		 */
		double extendY = preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_EXTEND_Y);
		rangeRestriction.setExtendMaxY(extendY);
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			/*
			 * MSD has no negative intensity values, so setZeroY(true)
			 * Normally, the background is not displayed (cut-off).
			 * setForceZeroMinY(true) shows all data from the 0 baseline.
			 */
			rangeRestriction.setZeroY(true);
			rangeRestriction.setForceZeroMinY(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_FORCE_ZERO_MIN_Y_MSD));
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD || chromatogramSelection instanceof IChromatogramSelectionWSD) {
			/*
			 * FID and DAD could contain negative scan intensities.
			 * setForceZeroMinY(true) would display only 0 or positive scan intensities.
			 */
			rangeRestriction.setZeroY(false);
			rangeRestriction.setForceZeroMinY(false);
		}
		/*
		 * Zooming
		 */
		rangeRestriction.setRestrictSelectX(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_RESTRICT_SELECT_X));
		rangeRestriction.setRestrictSelectY(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_RESTRICT_SELECT_Y));
		rangeRestriction.setReferenceZoomZeroX(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_REFERENCE_ZOOM_ZERO_X));
		rangeRestriction.setReferenceZoomZeroY(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_REFERENCE_ZOOM_ZERO_Y));
		rangeRestriction.setRestrictZoomX(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_RESTRICT_ZOOM_X));
		rangeRestriction.setRestrictZoomY(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_RESTRICT_ZOOM_Y));
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChromatogramSeriesData() {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
		ITargetDisplaySettings targetSettings = getTargetSettings();
		//
		addChromatogramData(lineSeriesDataList);
		addPeakData(lineSeriesDataList, targetSettings);
		addIdentifiedScansData(lineSeriesDataList, targetSettings);
		addSelectedPeakData(lineSeriesDataList, targetSettings);
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addBaselineData(lineSeriesDataList);
		//
		addLineSeriesData(lineSeriesDataList);
	}

	private ITargetDisplaySettings getTargetSettings() {

		if(targetDisplaySettings == null) {
			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				targetDisplaySettings = chromatogram;
			}
		}
		return targetDisplaySettings;
	}

	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList) {

		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM));
		boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA);
		//
		ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogramSelection, SERIES_ID_CHROMATOGRAM, displayType, color, false);
		lineSeriesData.getSettings().setEnableArea(enableChromatogramArea);
		lineSeriesDataList.add(lineSeriesData);
	}

	private void addPeakData(List<ILineSeriesData> lineSeriesDataList, ITargetDisplaySettings settings) {

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
				if(!peak.getInternalStandards().isEmpty()) {
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

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<? extends IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId, ITargetDisplaySettings displaySettings) {

		if(!peaks.isEmpty()) {
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
				ITargetDisplaySettings targetDisplaySettings = chromatogramSelection.getChromatogram();
				BaseChart baseChart = chromatogramChart.getBaseChart();
				IPlotArea plotArea = baseChart.getPlotArea();
				List<TargetReference> peakReferences = TargetReference.getPeakReferences(peaks, targetDisplaySettings);
				//
				TargetReferenceLabelMarker peakLabelMarker = new TargetReferenceLabelMarker(peakReferences, displaySettings, symbolSize * 2);
				plotArea.addCustomPaintListener(peakLabelMarker);
				peakLabelMarkerMap.put(seriesId, peakLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, ITargetDisplaySettings displaySettings) {

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
			if(displaySettings.isShowScanLabels()) {
				ITargetDisplaySettings targetDisplaySettings = chromatogramSelection.getChromatogram();
				BaseChart baseChart = chromatogramChart.getBaseChart();
				IPlotArea plotArea = baseChart.getPlotArea();
				TargetReferenceLabelMarker scanLabelMarker = new TargetReferenceLabelMarker(TargetReference.getScanReferences(scans, targetDisplaySettings), displaySettings, symbolSize * 2);
				plotArea.addCustomPaintListener(scanLabelMarker);
				scanLabelMarkerMap.put(seriesId, scanLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, List<IScan> scans, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(!scans.isEmpty()) {
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
			List<IScan> selectedIdentifiedScans = chromatogramSelection.getSelectedIdentifiedScans();
			if(!selectedIdentifiedScans.isEmpty()) {
				String seriesId = SERIES_ID_IDENTIFIED_SCAN_SELECTED;
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_IDENTIFIED_SCAN));
				PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_IDENTIFIED_SCAN_MARKER_TYPE));
				int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
				addIdentifiedScansData(lineSeriesDataList, selectedIdentifiedScans, symbolType, symbolSize, color, seriesId);
			}
		}
	}

	private void addSelectedPeakData(List<ILineSeriesData> lineSeriesDataList, ITargetDisplaySettings settings) {

		List<? extends IPeak> peaks = new ArrayList<>(chromatogramSelection.getSelectedPeaks());
		if(!peaks.isEmpty()) {
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
			addPeaks(lineSeriesDataList, peaks, symbolTypePeakMarker, symbolSize, colorPeak, SERIES_ID_SELECTED_PEAK_MARKER, settings);
			//
			int i = 0;
			for(IPeak peak : peaks) {
				/*
				 * Peak
				 */
				lineSeriesData = peakChartSupport.getPeak(peak, true, mirrored, colorPeak, SERIES_ID_SELECTED_PEAK_SHAPE + i);
				ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
				lineSeriesSettings.setSymbolType(symbolTypeScanMarker);
				lineSeriesSettings.setSymbolColor(colorPeak);
				lineSeriesSettings.setSymbolSize(scanMarkerSize);
				lineSeriesDataList.add(lineSeriesData);
				/*
				 * Background
				 */
				Color colorBackground = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BACKGROUND));
				lineSeriesData = peakChartSupport.getPeakBackground(peak, mirrored, colorBackground, SERIES_ID_SELECTED_PEAK_BACKGROUND + i);
				lineSeriesDataList.add(lineSeriesData);
				//
				i++;
			}
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

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		toolbarMain = createToolbarMain(this);
		toolbars.put(TOOLBAR_INFO, createToolbarInfo(this));
		toolbars.put(TOOLBAR_EDIT, createToolbarEdit(this));
		toolbars.put(TOOLBAR_CHROMATOGRAM_ALIGNMENT, createChromatogramAlignmentUI(this));
		toolbars.put(TOOLBAR_METHOD, createToolbarMethod(this));
		toolbars.put(TOOLBAR_RETENTION_INDICES, retentionIndexUI = createToolbarRetentionIndexUI(this));
		//
		createChromatogramChart(this);
		//
		initialize();
	}

	private void initialize() {

		comboViewerSeparationColumn.setInput(separationColumns);
		//
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_INFO), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_EDIT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_METHOD), preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_SHOW_METHODS_TOOLBAR));
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_RETENTION_INDICES), false);
		//
		chromatogramReferencesUI.setComboVisible(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_SHOW_REFERENCES_COMBO));
		chromatogramReferencesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_SHOW_REFERENCES_COMBO, chromatogramReferencesUI.isComboVisible());
			}
		});
	}

	private EditorToolBar createToolbarMain(Composite parent) {

		EditorToolBar editorToolBar = new EditorToolBar(parent);
		processorToolbar = new ProcessorToolbar(editorToolBar, processTypeSupport, this::isValidSupplier, this::executeSupplier);
		//
		editorToolBar.addAction(createLabelsAction());
		editorToolBar.addAction(createToggleToolbarAction("Info", "the info toolbar.", IApplicationImage.IMAGE_INFO, TOOLBAR_INFO));
		editorToolBar.createCombo(this::initComboViewerSeparationColumn, true, 250);
		chromatogramReferencesUI = new ChromatogramReferencesUI(editorToolBar, this::setChromatogramSelectionInternal);
		editorToolBar.addAction(createToggleToolbarAction("Edit", "the edit toolbar.", IApplicationImage.IMAGE_EDIT, TOOLBAR_EDIT));
		editorToolBar.addAction(createToggleToolbarAction("Alignment", "the chromatogram alignment toolbar.", IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS, TOOLBAR_CHROMATOGRAM_ALIGNMENT));
		editorToolBar.addAction(createToggleToolbarAction("Methods", "the method toolbar.", IApplicationImage.IMAGE_METHOD, TOOLBAR_METHOD, PreferenceConstants.P_CHROMATOGRAM_SHOW_METHODS_TOOLBAR));
		createResetButton(editorToolBar);
		editorToolBar.enableToolbarTextPage(preferenceStore, PREFERENCE_SHOW_TOOLBAR_TEXT);
		processorToolbar.enablePreferencePage(preferenceStore, PreferenceConstants.P_CHROMATOGRAM_PROCESSOR_TOOLBAR);
		editorToolBar.addAction(createGridLineAction());
		editorToolBar.addPreferencePages(new Supplier<Collection<? extends IPreferencePage>>() {

			@Override
			public Collection<? extends IPreferencePage> get() {

				List<IPreferencePage> preferencePages = new ArrayList<>();
				preferencePages.add(new PreferencePageProcessors(processTypeSupport));
				preferencePages.add(new PreferencePageChromatogram());
				preferencePages.add(new ChromatogramAxisMilliseconds());
				preferencePages.add(new ChromatogramAxisIntensity());
				preferencePages.add(new ChromatogramAxisSeconds());
				preferencePages.add(new ChromatogramAxisScans());
				preferencePages.add(new ChromatogramAxisMinutes());
				preferencePages.add(new ChromatogramAxisRelativeIntensity());
				preferencePages.add(new PreferencePageChromatogramPeaks());
				preferencePages.add(new PreferencePageChromatogramScans());
				preferencePages.add(new PreferencePageSystem());
				preferencePages.add(new PreferencePage());
				return preferencePages;
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

		methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@Override
			public void execute(IProcessMethod processMethod, IProgressMonitor monitor) {

				IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
				IChromatogramSelection chromatogramSelection = getChromatogramSelection();
				ProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processingInfo, processTypeSupport), IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection));
				chromatogramSelection.getChromatogram().setDirty(true); // TODO: check each entry
				updateResult(processingInfo);
				forceReset(true);
			}
		});
		toolbarMain.addPreferencePages(() -> Arrays.asList(methodSupportUI.getPreferencePages()), methodSupportUI::applySettings);
		//
		return methodSupportUI;
	}

	private ChromatogramAlignmentUI createChromatogramAlignmentUI(Composite parent) {

		chromatogramAlignmentUI = new ChromatogramAlignmentUI(parent, SWT.NONE);
		chromatogramAlignmentUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return chromatogramAlignmentUI;
	}

	private void initComboViewerSeparationColumn(ComboViewer comboViewer) {

		comboViewerSeparationColumn = comboViewer;
		//
		Control combo = comboViewer.getControl();
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISeparationColumn) {
					ISeparationColumn separationColumn = (ISeparationColumn)element;
					return SeparationColumnFactory.getColumnLabel(separationColumn, 25);
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a separation column.");
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn && chromatogramSelection != null) {
					/*
					 * Set the column
					 */
					ISeparationColumn separationColumn = (ISeparationColumn)object;
					IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
					chromatogram.getSeparationColumnIndices().setSeparationColumn(separationColumn);
					/*
					 * Transfer to references?
					 */
					if(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_COLUMN_TYPE_TO_REFERENCES)) {
						for(IChromatogram chromatogramReference : chromatogram.getReferencedChromatograms()) {
							chromatogramReference.getSeparationColumnIndices().setSeparationColumn(separationColumn);
						}
					}
					//
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
		composite.setLayout(new GridLayout(6, false));
		//
		createToggleToolbarButton(composite, "Toggle the retention index toolbar.", IApplicationImage.IMAGE_RETENION_INDEX, TOOLBAR_RETENTION_INDICES);
		chromatogramBaselinesUI = createChromatogramBaselinesUI(composite);
		createVerticalSeparator(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		//
		return composite;
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Custom Selection Handler
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addCustomRangeSelectionHandler(new ChromatogramSelectionHandler(this));
		//
		boolean markAnalysisSegments = preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_MARK_ANALYSIS_SEGMENTS);
		if(markAnalysisSegments) {
			AnalysisSegmentPaintListener<IAnalysisSegment> listener = new AnalysisSegmentPaintListener<>(AnalysisSegmentColorScheme.CHROMATOGRAM, new Supplier<Collection<IAnalysisSegment>>() {

				@Override
				public Collection<IAnalysisSegment> get() {

					if(chromatogramSelection != null) {
						return chromatogramSelection.getChromatogram().getAnalysisSegments();
					}
					return Collections.emptyList();
				}
			}, always -> false);
			listener.setPaintArea(true);
			listener.setPaintLines(true);
			listener.setAlpha(50);
			baseChart.getPlotArea().addCustomPaintListener(listener);
		}
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setRangeSelectorDefaultAxisX(1); // Minutes
		chartSettings.setRangeSelectorDefaultAxisY(1); // Relative Abundance
		chartSettings.setShowRangeSelectorInitially(false);
		/*
		 * Replace the existing reset handler with a specific
		 * chromatogram reset handler.
		 */
		IChartMenuEntry chartMenuEntry = chartSettings.getChartMenuEntry(new ResetChartHandler().getName());
		chartSettings.removeMenuEntry(chartMenuEntry);
		chartSettings.addMenuEntry(new ChromatogramResetHandler(this));
		//
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
		/*
		 * Add listener to check if the menu has been opened/closed.
		 */
		chromatogramChart.setMenuListener(new MenuListener() {

			@Override
			public void menuShown(MenuEvent menuEvent) {

				menuActive = true;
			}

			@Override
			public void menuHidden(MenuEvent menuEvent) {

				menuActive = false;
			}
		});
	}

	private Button createToggleToolbarButton(Composite parent, String tooltip, String image, String toolbar) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(tooltip);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(toolbars.containsKey(toolbar)) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImageProvider.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private TargetLabelEditAction createLabelsAction() {

		TargetLabelEditAction targetLabelEditAction = new TargetLabelEditAction(new ILabelEditSettings() {

			@Override
			public IChromatogramSelection<?, ?> getChromatogramSelection() {

				return chromatogramSelection;
			}

			@Override
			public ExtendedChromatogramUI getChromatogramUI() {

				return getExtendedChromatogramUI();
			}
		});
		return targetLabelEditAction;
	}

	private GridLineEditAction createGridLineAction() {

		return new GridLineEditAction(this);
	}

	private ExtendedChromatogramUI getExtendedChromatogramUI() {

		return this;
	}

	private IAction createToggleToolbarAction(String name, String tooltip, String image, String toolbar) {

		return createToggleToolbarAction(name, tooltip, image, toolbar, null);
	}

	private IAction createToggleToolbarAction(String name, String tooltip, String image, String toolbar, String preferenceKey) {

		return new Action(name, IAction.AS_CHECK_BOX) {

			{
				setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(image, IApplicationImage.SIZE_16x16));
				setToolTipText(tooltip);
				updateText();
			}

			@Override
			public void run() {

				if(toolbars.containsKey(toolbar)) {
					boolean isVisible = PartSupport.toggleCompositeVisibility(toolbars.get(toolbar));
					if(preferenceKey != null) {
						preferenceStore.setValue(preferenceKey, isVisible);
					}
					setChecked(isVisible);
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

	private ChromatogramBaselinesUI createChromatogramBaselinesUI(Composite parent) {

		ChromatogramBaselinesUI chromatogramBaselinesUI = new ChromatogramBaselinesUI(parent, SWT.NONE);
		chromatogramBaselinesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		chromatogramBaselinesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateChromatogram();
			}
		});
		//
		return chromatogramBaselinesUI;
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
		chromatogramReferencesUI.update();
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
				if(!separationColumns.contains(separationColumn)) {
					separationColumns.add(0, separationColumn);
					comboViewerSeparationColumn.setInput(separationColumns);
				}
				comboViewerSeparationColumn.setSelection(new StructuredSelection(separationColumn));
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
				ISecondaryAxisSettings axisSettings = ChartSupport.getSecondaryAxisSettingsX(titleScans, chartSettings);
				String title = preferenceStore.getString(PreferenceConstants.P_TITLE_X_AXIS_SCANS);
				//
				if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SCANS)) {
					if(axisSettings == null) {
						try {
							int scanDelay = chromatogram.getScanDelay();
							int scanInterval = chromatogram.getScanInterval();
							ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(title, new MillisecondsToScanNumberConverter(scanDelay, scanInterval));
							secondaryAxisSettingsX.setTitleVisible(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SCANS));
							setScanAxisSettings(secondaryAxisSettingsX);
							chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
						} catch(Exception e) {
							logger.warn(e);
						}
					} else {
						axisSettings.setTitle(title);
						setScanAxisSettings(axisSettings);
					}
				} else {
					/*
					 * Remove
					 */
					if(axisSettings != null) {
						axisSettings.setTitle(title);
						chartSettings.getSecondaryAxisSettingsListX().remove(axisSettings);
					}
				}
				/*
				 * Update the title to retrieve the correct axis.
				 */
				chromatogramChart.applySettings(chartSettings);
				titleScans = title;
			}
		}
	}

	private void setScanAxisSettings(IAxisSettings axisSettings) {

		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_SCANS;
		String patternNode = PreferenceConstants.P_FORMAT_X_AXIS_SCANS;
		String colorNode = PreferenceConstants.P_COLOR_X_AXIS_SCANS;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS;
		ChartSupport.setAxisSettingsExtended(axisSettings, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
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

	@Override
	public void dispose() {

		chromatogramChart.dispose();
		super.dispose();
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
		preferenceStore.setDefault(PREFERENCE_SHOW_TOOLBAR_TEXT, true);
	}
}
