/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
 * Matthias Mailänder - display selected wavelengths, audit trail
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.chemclipse.converter.methods.MetaProcessorProcessSupplier;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.model.targets.TargetReferenceType;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IMessageProvider;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier.SupplierType;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.ILabelEditSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.TargetLabelEditAction;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorProcessTypeSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.help.HelpContext;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.TargetReferenceLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers.DynamicHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.marker.PositionMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.marker.RetentionIndexMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodCancelException;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.ResumeMethodSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisIntensity;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisMilliseconds;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisMinutes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisRelativeIntensity;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisRetentionIndex;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.ChromatogramAxisSeconds;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceInitializer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramPeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessorToolbarCSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessorToolbarISD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessorToolbarMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessorToolbarWSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentPaintListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChartGridSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramBaselinesUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramReferencesUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ProcessorToolbarUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ToolbarConfig;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryNameComparator;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageChromatogramExport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageReportExport;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

public class ExtendedChromatogramUI extends Composite implements ToolbarConfig, IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	private static final DecimalFormat FORMAT = ValueFormat.getDecimalFormatEnglish("0.000");
	private static final String LABEL_SCAN_NUMBER = "Scan Number";
	//
	private static final String IMAGE_ALIGNMENT = IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS;
	private static final String TOOLTIP_ALIGNMENT = "the alignment toolbar.";
	private static final String IMAGE_METHOD = IApplicationImage.IMAGE_METHOD;
	private static final String TOOLTIP_METHOD = "the method toolbar.";
	private static final String IMAGE_REFERENCES = IApplicationImage.IMAGE_EXPAND_ALL;
	private static final String TOOLTIP_REFERENCES = "the references toolbar.";
	//
	public static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";
	//
	private static final String SERIES_ID_BASELINE = "Baseline";
	private static final String SERIES_ID_PEAKS_NORMAL_ACTIVE = "Peaks [Active]";
	private static final String SERIES_ID_PEAKS_NORMAL_TARGETS_HIDDEN = "Peaks [Targets Hidden]";
	private static final String SERIES_ID_PEAKS_NORMAL_INACTIVE = "Peaks [Inactive]";
	private static final String SERIES_ID_PEAKS_ISTD_ACTIVE = "Peaks ISTD [Active]";
	private static final String SERIES_ID_PEAKS_ISTD_TARGETS_HIDDEN = "Peaks ISTD [Targets Hidden]";
	private static final String SERIES_ID_PEAKS_ISTD_INACTIVE = "Peaks ISTD [Inactive]";
	private static final String SERIES_ID_SELECTED_PEAK_MARKER = "Selected Peak Marker";
	private static final String SERIES_ID_SELECTED_PEAK_SHAPE = "Selected Peak Shape";
	private static final String SERIES_ID_SELECTED_PEAK_BACKGROUND = "Selected Peak Background";
	private static final String SERIES_ID_SELECTED_SCAN = "Selected Scan";
	private static final String SERIES_ID_IDENTIFIED_SCANS = "Identified Scans";
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scans Selected";
	//
	private String titleScans = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_SCANS);
	private ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
	//
	private AtomicReference<ProcessorToolbarUI> processorToolbarControl = new AtomicReference<>();
	private AtomicReference<Composite> toolbarMainControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoControl = new AtomicReference<>();
	private AtomicReference<ComboViewer> comboViewerColumnsControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarEdit = new AtomicReference<>();
	private AtomicReference<Composite> toolbarEditControl = new AtomicReference<>();
	private AtomicReference<ChromatogramBaselinesUI> chromatogramBaselinesControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarReferences = new AtomicReference<>();
	private AtomicReference<ChromatogramReferencesUI> toolbarReferencesControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarAlignment = new AtomicReference<>();
	private AtomicReference<ChromatogramAlignmentUI> toolbarAlignmentControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarMethod = new AtomicReference<>();
	private AtomicReference<MethodSupportUI> toolbarMethodControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToggleRetentionIndexControl = new AtomicReference<>();
	private AtomicReference<Button> buttonChartGridControl = new AtomicReference<>();
	private AtomicReference<ChromatogramChart> chromatogramChartControl = new AtomicReference<>();
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
	private DisplayType displayType;
	//
	private boolean suspendUpdate = false;
	private IProcessSupplierContext processTypeSupport;
	private MethodSupportUI methodSupportUI;
	private ITargetDisplaySettings targetDisplaySettings;
	private Predicate<IProcessSupplier<?>> dataCategoryPredicate = null;
	//
	private Object menuCache = null;
	private boolean menuActive = false;
	//
	private List<ISeparationColumn> separationColumns = SeparationColumnFactory.getSeparationColumns();
	private RetentionIndexMarker retentionIndexMarker;
	//
	private IEventBroker eventBroker = Activator.getDefault().getEventBroker();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ChartGridSupport chartGridSupport = new ChartGridSupport();

	public ExtendedChromatogramUI(Composite parent, int style, IProcessSupplierContext processTypeSupport) {

		super(parent, style);
		this.processTypeSupport = processTypeSupport != null ? processTypeSupport : new ProcessTypeSupport();
		createControl();
	}

	@Override
	public void setToolbarVisible(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarMainControl.get(), visible);
		if(!visible) {
			enableToolbar(toolbarReferencesControl, buttonToolbarReferences.get(), IMAGE_REFERENCES, TOOLTIP_REFERENCES, false);
			enableToolbar(toolbarMethodControl, buttonToolbarMethod.get(), IMAGE_METHOD, TOOLTIP_METHOD, false);
		}
	}

	@Override
	public boolean isToolbarVisible() {

		return toolbarMainControl.get().isVisible();
	}

	public void updateToolbar() {

		ProcessorToolbarUI processorToolbarUI = processorToolbarControl.get();
		if(processorToolbarUI.isVisible()) {
			processorToolbarUI.updateToolbar(getDataCategory());
		}
	}

	/**
	 * Resets the chart ranges 1:1.
	 */
	public void adjustChromatogramChart() {

		if(!menuActive) {
			chromatogramChartControl.get().adjustRange(true);
		}
	}

	public void fireUpdate(Display display) {

		fireUpdateChromatogram(display);
		if(!fireUpdatePeak(display)) {
			fireUpdateScan(display);
		}
	}

	public boolean fireUpdateChromatogram(Display display) {

		IChromatogramSelection<?, ?> chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null) {
			UpdateNotifierUI.update(display, chromatogramSelection);
		}
		return chromatogramSelection != null;
	}

	public boolean fireUpdatePeak(Display display) {

		boolean update = false;
		IChromatogramSelection<?, ?> chromatogramSelection = getChromatogramSelection();
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
		IChromatogramSelection<?, ?> chromatogramSelection = getChromatogramSelection();
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

		return chromatogramChartControl.get();
	}

	public synchronized void updateChromatogramSelection(IChromatogramSelection<?, ?> chromatogramSelection) {

		toolbarReferencesControl.get().update(chromatogramSelection, this::setChromatogramSelectionInternal);
		setChromatogramSelectionInternal(chromatogramSelection);
		chromatogramBaselinesControl.get().update(chromatogramSelection.getChromatogram());
		toolbarReferencesControl.get().setMasterChromatogram(chromatogramSelection);
		toolbarAlignmentControl.get().update(toolbarReferencesControl.get().getChromatogramSelections());
	}

	private void updateWavelengths() {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		if(!(chromatogram instanceof IChromatogramWSD)) {
			return;
		}
		//
		IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
		if(chromatogramSelectionWSD.getSelectedWavelengths().getWavelengths().size() == 1) {
			displayType = DisplayType.SWC;
		} else {
			displayType = DisplayType.TIC;
		}
	}

	private void setChromatogramSelectionInternal(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(this.chromatogramSelection != chromatogramSelection) {
			/*
			 * Adjust Toolbar / Menu
			 */
			this.chromatogramSelection = chromatogramSelection;
			dataCategoryPredicate = IProcessSupplierContext.createDataCategoryPredicate(getDataCategory());
			targetDisplaySettings = null;
			toolbarAlignmentControl.get().update(chromatogramSelection);
			updateToolbar();
			//
			if(chromatogramSelection != null) {
				adjustAxisSettings();
				updateMenu();
				updateChromatogram();
				setSeparationColumnSelection();
			} else {
				adjustAxisSettings();
				updateChromatogram();
			}
			updateMappedRetentionIndices();
			/*
			 * Update the chart.
			 * fireUpdate(getChromatogramChart().getDisplay()); makes problems here.
			 * The update process needs to be addressed generally.
			 */
			processorToolbarControl.get().update();
			if(chromatogramSelection != null) {
				UpdateNotifierUI.update(getDisplay(), chromatogramSelection);
			}
		}
	}

	private DataCategory getDataCategory() {

		DataCategory dataCategory = DataCategory.AUTO_DETECT;
		//
		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				dataCategory = DataCategory.MSD;
			} else if(chromatogram instanceof IChromatogramWSD) {
				dataCategory = DataCategory.WSD;
			} else if(chromatogram instanceof IChromatogramCSD) {
				dataCategory = DataCategory.CSD;
			} else if(chromatogram instanceof IChromatogramISD) {
				dataCategory = DataCategory.ISD;
			}
		}
		//
		return dataCategory;
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
			chromatogramChartControl.get().deleteSeries(SERIES_ID_SELECTED_SCAN);
			chromatogramChartControl.get().deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
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
			Set<String> seriesIds = chromatogramChartControl.get().getBaseChart().getSeriesIds();
			//
			chromatogramChartControl.get().deleteSeries(SERIES_ID_SELECTED_PEAK_MARKER);
			for(String seriesId : seriesIds) {
				if(seriesId.startsWith(SERIES_ID_SELECTED_PEAK_SHAPE) || seriesId.startsWith(SERIES_ID_SELECTED_PEAK_BACKGROUND)) {
					chromatogramChartControl.get().deleteSeries(seriesId);
				}
			}
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			addSelectedPeakData(lineSeriesDataList, getTargetSettings());
			addLineSeriesData(lineSeriesDataList);
			adjustChromatogramSelectionRange();
		}
	}

	public IChromatogramSelection<?, ?> getChromatogramSelection() {

		return chromatogramSelection;
	}

	public boolean isActiveChromatogramSelection(IChromatogramSelection<?, ?> chromatogramSelection) {

		return (this.chromatogramSelection == chromatogramSelection);
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
			updateSelection();
			fireUpdate(shell.getDisplay());
		} catch(InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		}
	}

	public void updateMenu() {

		if(processTypeSupport != null && menuCache != chromatogramSelection) {
			/*
			 * Clean the Menu
			 */
			IChartSettings chartSettings = chromatogramChartControl.get().getChartSettings();
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
				addCommand(supplier, cachedEntry);
			}
			/*
			 * Apply the menu items.
			 */
			chromatogramChartControl.get().applySettings(chartSettings);
			menuCache = chromatogramSelection;
		}
	}

	private void addCommand(IProcessSupplier<?> supplier, IChartMenuEntry cachedEntry) {

		Command command = commandService.getCommand(supplier.getId());
		Category category = commandService.getCategory(supplier.getCategory());
		command.define(supplier.getName(), supplier.getDescription(), category);
		command.setHandler(new DynamicHandler(cachedEntry, chromatogramChartControl.get()));
	}

	private <C> void executeSupplier(IProcessSupplier<C> processSupplier, IProcessSupplierContext processSupplierContext) {

		try {
			Shell shell = getChromatogramChart().getShell();
			IProcessorPreferences<C> settings = SettingsWizard.getSettings(shell, SettingsWizard.getWorkspacePreferences(processSupplier), true);
			if(settings == null) {
				return;
			}
			//
			if(processSupplier instanceof MetaProcessorProcessSupplier metaProcessorProcessSupplier) {
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
					updateAuditTrail(processingInfo, processSupplier);
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

	public void updateResult(IMessageProvider processingInfo) {

		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				ProcessingInfoPartSupport.getInstance().update(processingInfo, true);
			}
		});
	}

	private void updateAuditTrail(IMessageProvider processingInfo, IProcessMethod processMethod) {

		if(processingInfo.hasErrorMessages()) {
			return;
		}
		processMethod.forEach(p -> updateAuditTrail(p.getName()));
	}

	private void updateAuditTrail(IMessageProvider processingInfo, IProcessSupplier<?> processSupplier) {

		if(processingInfo.hasErrorMessages()) {
			return;
		}
		updateAuditTrail(processSupplier.getCategory() + ": " + processSupplier.getName());
	}

	private void updateAuditTrail(String description) {

		IChromatogram<?> chromatogram = getChromatogramSelection().getChromatogram();
		EditInformation editInformation = new EditInformation(description, UserManagement.getCurrentUser());
		chromatogram.getEditHistory().add(editInformation);
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
		chromatogramChartControl.get().deleteSeries();
		//
		if(chromatogramSelection != null) {
			setRangeRestrictions();
			displayType = DisplayType.TIC;
			addChromatogramSeriesData();
			adjustChromatogramSelectionRange();
			toolbarAlignmentControl.get().update(toolbarReferencesControl.get().getChromatogramSelections());
		}
		//
		updateMappedRetentionIndices();
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

		IPlotArea plotArea = chromatogramChartControl.get().getBaseChart().getPlotArea();
		ICustomPaintListener labelMarker = markerMap.get(seriesId);
		/*
		 * Remove the label marker.
		 */
		if(labelMarker != null) {
			plotArea.removeCustomPaintListener(labelMarker);
		}
	}

	private void setRangeRestrictions() {

		IChartSettings chartSettings = chromatogramChartControl.get().getChartSettings();
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
		chromatogramChartControl.get().applySettings(chartSettings);
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
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
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
			/*
			 * Settings
			 */
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			ITargetDisplaySettings targetDisplaySettings = chromatogram;
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			//
			PlotSymbolType symbolTypeActiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_MARKER_TYPE));
			PlotSymbolType symbolTypeInactiveNormal = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL_MARKER_TYPE));
			PlotSymbolType symbolTypeActiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_MARKER_TYPE));
			PlotSymbolType symbolTypeInactiveIstd = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_ISTD_MARKER_TYPE));
			//
			Color colorTypeActiveNormal = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL));
			Color colorTypeActiveTargetsHidden = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_TARGETS_HIDDEN));
			Color colorTypeInactiveNormal = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL));
			Color colorTypeActiveIstd = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_PEAKS_ACTIVE_ISTD));
			Color colorTypeActiveIstdTargetsHidden = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_TARGETS_HIDDEN));
			Color colorTypeInactiveIstd = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_PEAKS_INACTIVE_ISTD));
			//
			List<? extends IPeak> peaks = ChromatogramDataSupport.getPeaks(chromatogram);
			List<IPeak> peaksActiveNormal = new ArrayList<>();
			List<IPeak> peaksActiveTargetsHidden = new ArrayList<>();
			List<IPeak> peaksInactiveNormal = new ArrayList<>();
			List<IPeak> peaksActiveIstd = new ArrayList<>();
			List<IPeak> peaksActiveIstdTargetsHidden = new ArrayList<>();
			List<IPeak> peaksInactiveIstd = new ArrayList<>();
			//
			for(IPeak peak : peaks) {
				if(!peak.getInternalStandards().isEmpty()) {
					/*
					 * ISTD
					 */
					if(peak.isActiveForAnalysis()) {
						if(peak.getTargets().isEmpty()) {
							peaksActiveIstd.add(peak);
						} else {
							TargetReference targetReference = createTargetReference(peak);
							if(!targetDisplaySettings.isVisible(targetReference)) {
								peaksActiveIstdTargetsHidden.add(peak);
							} else {
								peaksActiveIstd.add(peak);
							}
						}
					} else {
						peaksInactiveIstd.add(peak);
					}
				} else /*
						 * Normal
						 */
				if(peak.isActiveForAnalysis()) {
					if(peak.getTargets().isEmpty()) {
						peaksActiveNormal.add(peak);
					} else {
						TargetReference targetReference = createTargetReference(peak);
						if(!targetDisplaySettings.isVisible(targetReference)) {
							peaksActiveTargetsHidden.add(peak);
						} else {
							peaksActiveNormal.add(peak);
						}
					}
				} else {
					peaksInactiveNormal.add(peak);
				}
			}
			//
			addPeaks(lineSeriesDataList, peaksActiveNormal, symbolTypeActiveNormal, symbolSize, colorTypeActiveNormal, SERIES_ID_PEAKS_NORMAL_ACTIVE, settings);
			addPeaks(lineSeriesDataList, peaksActiveTargetsHidden, symbolTypeActiveNormal, symbolSize, colorTypeActiveTargetsHidden, SERIES_ID_PEAKS_NORMAL_TARGETS_HIDDEN, settings);
			addPeaks(lineSeriesDataList, peaksInactiveNormal, symbolTypeInactiveNormal, symbolSize, colorTypeInactiveNormal, SERIES_ID_PEAKS_NORMAL_INACTIVE, settings);
			addPeaks(lineSeriesDataList, peaksActiveIstd, symbolTypeActiveIstd, symbolSize, colorTypeActiveIstd, SERIES_ID_PEAKS_ISTD_ACTIVE, settings);
			addPeaks(lineSeriesDataList, peaksActiveIstdTargetsHidden, symbolTypeActiveIstd, symbolSize, colorTypeActiveIstdTargetsHidden, SERIES_ID_PEAKS_ISTD_TARGETS_HIDDEN, settings);
			addPeaks(lineSeriesDataList, peaksInactiveIstd, symbolTypeInactiveIstd, symbolSize, colorTypeInactiveIstd, SERIES_ID_PEAKS_ISTD_INACTIVE, settings);
		}
	}

	private TargetReference createTargetReference(IPeak peak) {

		IPeakModel peakModel = peak.getPeakModel();
		String retentionTimeMinutes = FORMAT.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
		float retentionIndex = peakModel.getPeakMaximum().getRetentionIndex();
		return new TargetReference(peak, TargetReferenceType.PEAK, retentionTimeMinutes, retentionIndex);
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
				BaseChart baseChart = chromatogramChartControl.get().getBaseChart();
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
				BaseChart baseChart = chromatogramChartControl.get().getBaseChart();
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
		chromatogramChartControl.get().addSeriesData(lineSeriesDataList, compressionToLength);
	}

	private void createControl() {

		setLayout(new FillLayout());
		createChromatogramSection(this);
		initialize();
	}

	private void createChromatogramSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createToolbarInfo(composite);
		createToolbarReferences(composite);
		createToolbarEdit(composite);
		createToolbarAlignment(composite);
		createToolbarMethod(composite);
		createChromatogramChart(composite);
	}

	private void initialize() {

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, HelpContext.CHROMATOGRAM_EDITOR);
		//
		enableToolbar(toolbarInfoControl, buttonToolbarInfo.get(), IMAGE_INFO, TOOLTIP_INFO, false);
		enableToolbar(toolbarReferencesControl, buttonToolbarReferences.get(), IMAGE_REFERENCES, TOOLTIP_REFERENCES, preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_SHOW_REFERENCES_COMBO));
		enableToolbar(toolbarEditControl, buttonToolbarEdit.get(), IMAGE_EDIT, TOOLTIP_EDIT, false);
		enableToolbar(toolbarAlignmentControl, buttonToolbarAlignment.get(), IMAGE_ALIGNMENT, TOOLTIP_ALIGNMENT, false);
		enableToolbar(toolbarMethodControl, buttonToolbarMethod.get(), IMAGE_METHOD, TOOLTIP_METHOD, preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_SHOW_METHODS_TOOLBAR));
		enableChartGrid(chromatogramChartControl, buttonChartGridControl.get(), IMAGE_CHART_GRID, chartGridSupport);
		//
		comboViewerColumnsControl.get().setInput(separationColumns);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfoControl.set(informationUI);
	}

	private void createToolbarMethod(Composite parent) {

		MethodSupportUI methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@Override
			public void execute(IProcessMethod processMethod, IProgressMonitor monitor) {

				IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
				IChromatogramSelection<?, ?> chromatogramSelection = getChromatogramSelection();
				ProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processingInfo, processTypeSupport), IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection));
				chromatogramSelection.getChromatogram().setDirty(true); // TODO: check each entry
				updateResult(processingInfo);
				updateAuditTrail(processingInfo, processMethod);
				forceReset(true);
				UpdateNotifierUI.update(getDisplay(), chromatogramSelection.getSelectedScan());
			}
		});
		//
		toolbarMethodControl.set(methodSupportUI);
	}

	private void createToolbarAlignment(Composite parent) {

		ChromatogramAlignmentUI chromatogramAlignmentUI = new ChromatogramAlignmentUI(parent, SWT.NONE);
		chromatogramAlignmentUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarAlignmentControl.set(chromatogramAlignmentUI);
	}

	private void createToolbarReferences(Composite parent) {

		ChromatogramReferencesUI chromatogramReferencesUI = new ChromatogramReferencesUI(parent, SWT.NONE);
		chromatogramReferencesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarReferencesControl.set(chromatogramReferencesUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createChromatogramBaselinesUI(composite);
		createVerticalSeparator(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		//
		toolbarEditControl.set(composite);
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(13, false));
		//
		createProcessorToolbarUI(composite);
		createButtonToggleInfo(composite);
		createButtonTargetLabels(composite);
		createComboViewerColumns(composite);
		createButtonToggleReferences(composite);
		createButtonToggleEdit(composite);
		createButtonToggleAlignment(composite);
		createButtonToggleMethod(composite);
		createButtonToggleRetentionIndex(composite);
		createButtonToggleChartGrid(composite);
		createButtonReset(composite);
		createButtonHelp(composite);
		createButtonSettings(composite);
		//
		toolbarMainControl.set(composite);
	}

	private void createButtonToggleChartGrid(Composite parent) {

		Button button = createButtonToggleChartGrid(parent, chromatogramChartControl, IMAGE_CHART_GRID, chartGridSupport);
		buttonChartGridControl.set(button);
	}

	private void createProcessorToolbarUI(Composite parent) {

		ProcessorToolbarUI processorToolbarUI = new ProcessorToolbarUI(parent, SWT.NONE);
		processorToolbarUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		processorToolbarUI.setInput(this::executeSupplier);
		//
		processorToolbarControl.set(processorToolbarUI);
	}

	private void createButtonToggleInfo(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarInfoControl, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarInfo.set(button);
	}

	private void createButtonTargetLabels(Composite parent) {

		Button button = new Button(parent, SWT.NONE);
		button.setText("");
		button.setToolTipText(ExtensionMessages.manageLabelsChromatogram);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_LABELS, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TargetLabelEditAction targetLabelEditAction = getTargetLabelEditAction();
				targetLabelEditAction.openWizard(e.display.getActiveShell());
			}
		});
	}

	private TargetLabelEditAction getTargetLabelEditAction() {

		return new TargetLabelEditAction(new ILabelEditSettings() {

			@Override
			public IChromatogramSelection<?, ?> getChromatogramSelection() {

				return chromatogramSelection;
			}

			@Override
			public ExtendedChromatogramUI getChromatogramUI() {

				return getExtendedChromatogramUI();
			}
		});
	}

	private ExtendedChromatogramUI getExtendedChromatogramUI() {

		return this;
	}

	private void createComboViewerColumns(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISeparationColumn separationColumn) {
					return SeparationColumnFactory.getColumnLabel(separationColumn, 10);
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a separation column.");
		GridData gridData = new GridData();
		gridData.widthHint = 100;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn separationColumn && chromatogramSelection != null) {
					/*
					 * Set the column
					 */
					IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
					chromatogram.getSeparationColumnIndices().setSeparationColumn(separationColumn);
					/*
					 * Transfer to references?
					 */
					if(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_COLUMN_TYPE_TO_REFERENCES)) {
						for(IChromatogram<?> chromatogramReference : chromatogram.getReferencedChromatograms()) {
							chromatogramReference.getSeparationColumnIndices().setSeparationColumn(separationColumn);
						}
					}
					//
					updateLabel();
				}
			}
		});
		//
		comboViewerColumnsControl.set(comboViewer);
	}

	private void createButtonToggleReferences(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarReferencesControl, IMAGE_REFERENCES, TOOLTIP_REFERENCES);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_SHOW_REFERENCES_COMBO, toolbarReferencesControl.get().isVisible());
			}
		});
		//
		buttonToolbarReferences.set(button);
	}

	private void createButtonToggleEdit(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarEditControl, IMAGE_EDIT, TOOLTIP_EDIT);
		buttonToolbarEdit.set(button);
	}

	private void createButtonToggleAlignment(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarAlignmentControl, IMAGE_ALIGNMENT, TOOLTIP_ALIGNMENT);
		buttonToolbarAlignment.set(button);
	}

	private void createButtonToggleMethod(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarMethodControl, IMAGE_METHOD, TOOLTIP_METHOD);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_SHOW_METHODS_TOOLBAR, toolbarMethodControl.get().isVisible());
			}
		});
		//
		buttonToolbarMethod.set(button);
	}

	private void createButtonToggleRetentionIndex(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		setButtonImage(button, IMAGE_RETENTION_INDICES, PREFIX_SHOW, PREFIX_HIDE, TOOLTIP_RETENTION_INDICES, preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_MARKER));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean showRetentionIndexMarker = !preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_MARKER);
				preferenceStore.setValue(PreferenceConstants.P_SHOW_RETENTION_INDEX_MARKER, showRetentionIndexMarker);
				updateRetentionIndexDisplayStatus();
			}
		});
		//
		buttonToggleRetentionIndexControl.set(button);
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, getPreferencePagesSupplier(), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings(display);
			}
		}, false);
	}

	private Supplier<List<Class<? extends IPreferencePage>>> getPreferencePagesSupplier() {

		return new Supplier<List<Class<? extends IPreferencePage>>>() {

			@Override
			public List<Class<? extends IPreferencePage>> get() {

				List<Class<? extends IPreferencePage>> preferencePages = new ArrayList<>();
				/*
				 * Specific Pages
				 */
				DataCategory dataCategory = getDataCategory();
				switch(dataCategory) {
					case CSD:
						preferencePages.add(PreferencePageProcessorToolbarCSD.class);
						break;
					case MSD:
						preferencePages.add(PreferencePageProcessorToolbarMSD.class);
						break;
					case WSD:
						preferencePages.add(PreferencePageProcessorToolbarWSD.class);
						break;
					case ISD:
						preferencePages.add(PreferencePageProcessorToolbarISD.class);
						break;
					default:
						break;
				}
				/*
				 * Standard Pages
				 */
				preferencePages.add(PreferencePageProcessors.class);
				preferencePages.add(PreferencePageChromatogram.class);
				preferencePages.add(ChromatogramAxisMilliseconds.class);
				preferencePages.add(ChromatogramAxisRetentionIndex.class);
				preferencePages.add(ChromatogramAxisIntensity.class);
				preferencePages.add(ChromatogramAxisSeconds.class);
				preferencePages.add(ChromatogramAxisScans.class);
				preferencePages.add(ChromatogramAxisMinutes.class);
				preferencePages.add(ChromatogramAxisRelativeIntensity.class);
				preferencePages.add(PreferencePageChromatogramPeaks.class);
				preferencePages.add(PreferencePageChromatogramScans.class);
				preferencePages.add(PreferencePageSystem.class);
				preferencePages.add(PreferencePage.class);
				preferencePages.add(PreferencePageReportExport.class);
				preferencePages.add(PreferencePageChromatogramExport.class);
				//
				return preferencePages;
			}
		};
	}

	private void createChromatogramChart(Composite parent) {

		ChromatogramChart chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Custom Selection Handler
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addCustomRangeSelectionHandler(new ChromatogramSelectionHandler(this));
		/*
		 * Retention Index Marker
		 */
		IPlotArea plotArea = baseChart.getPlotArea();
		retentionIndexMarker = new RetentionIndexMarker(chromatogramChart.getBaseChart());
		plotArea.addCustomPaintListener(retentionIndexMarker);
		/*
		 * Some converter have an option to set analysis segments while parsing the chromatogram data.
		 * Via the preferences it's defined, whether these segements shall be displayed be default.
		 */
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
		 * Replace the existing reset handler with a specific chromatogram reset handler.
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
		//
		chromatogramChartControl.set(chromatogramChart);
	}

	private void createChromatogramBaselinesUI(Composite parent) {

		ChromatogramBaselinesUI chromatogramBaselinesUI = new ChromatogramBaselinesUI(parent, SWT.NONE);
		chromatogramBaselinesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		chromatogramBaselinesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateChromatogram();
			}
		});
		//
		chromatogramBaselinesControl.set(chromatogramBaselinesUI);
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChartControl.get().toggleSeriesLegendVisibility();
			}
		});
	}

	private void createToggleLegendMarkerButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend marker.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_LEGEND_MARKER, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChartControl.get().togglePositionLegendVisibility();
				chromatogramChartControl.get().redraw();
			}
		});
	}

	private void createToggleRangeSelectorButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart range selector.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_RANGE_SELECTOR, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChartControl.get().toggleRangeSelectorVisibility();
			}
		});
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset the chromatogram");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset(true);
			}
		});
		//
		return button;
	}

	private void applySettings(Display display) {

		adjustAxisSettings();
		updateChromatogram();
		toolbarReferencesControl.get().update();
		updateRetentionIndexDisplayStatus();
		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_TOOLBAR_UPDATE, true);
	}

	private void reset(boolean resetRange) {

		updateChromatogram();
		if(resetRange && chromatogramSelection != null) {
			chromatogramSelection.reset(true);
		}
	}

	private void updateLabel() {

		if(chromatogramSelection != null) {
			toolbarInfoControl.get().setText(ChromatogramDataSupport.getChromatogramLabelExtended(chromatogramSelection.getChromatogram()));
		} else {
			toolbarInfoControl.get().setText("");
		}
	}

	private void deleteScanNumberSecondaryAxisX() {

		IChartSettings chartSettings = chromatogramChartControl.get().getChartSettings();
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
		chromatogramChartControl.get().applySettings(chartSettings);
	}

	private void adjustChromatogramSelectionRange() {

		if(chromatogramSelection != null) {
			chromatogramChartControl.get().setRange(IExtendedChart.X_AXIS, chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
			chromatogramChartControl.get().setRange(IExtendedChart.Y_AXIS, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
		}
	}

	private void setSeparationColumnSelection() {

		if(chromatogramSelection != null) {
			ISeparationColumn separationColumn = chromatogramSelection.getChromatogram().getSeparationColumnIndices().getSeparationColumn();
			if(separationColumn != null) {
				if(!separationColumns.contains(separationColumn)) {
					separationColumns.add(0, separationColumn);
					comboViewerColumnsControl.get().setInput(separationColumns);
				}
				comboViewerColumnsControl.get().setSelection(new StructuredSelection(separationColumn));
			}
		}
	}

	private void adjustAxisSettings() {

		chromatogramChartControl.get().modifyAxes(true);
		/*
		 * Scan Axis
		 */
		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				IChartSettings chartSettings = chromatogramChartControl.get().getChartSettings();
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
				} else /*
						 * Remove
						 */
				if(axisSettings != null) {
					axisSettings.setTitle(title);
					chartSettings.getSecondaryAxisSettingsListX().remove(axisSettings);
				}
				chromatogramChartControl.get().applySettings(chartSettings);
				titleScans = title;
			}
		}
	}

	private void setScanAxisSettings(IAxisSettings axisSettings) {

		String positionNode = PreferenceConstants.P_POSITION_X_AXIS_SCANS;
		String patternNode = PreferenceConstants.P_FORMAT_X_AXIS_SCANS;
		String colorNode = PreferencesSupport.isDarkTheme() ? PreferenceConstants.P_COLOR_X_AXIS_SCANS_DARKTHEME : PreferenceConstants.P_COLOR_X_AXIS_SCANS;
		String gridLineStyleNode = PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS;
		String gridColorNode = PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS;
		ChartSupport.setAxisSettingsExtended(axisSettings, positionNode, patternNode, colorNode, gridLineStyleNode, gridColorNode);
		//
		String name = preferenceStore.getString(PreferenceConstants.P_FONT_NAME_X_AXIS_SCANS);
		int height = preferenceStore.getInt(PreferenceConstants.P_FONT_SIZE_X_AXIS_SCANS);
		int style = preferenceStore.getInt(PreferenceConstants.P_FONT_STYLE_X_AXIS_SCANS);
		Font titleFont = Fonts.getCachedFont(chromatogramChartControl.get().getDisplay(), name, height, style);
		axisSettings.setTitleFont(titleFont);
	}

	public void updateMethods() {

		methodSupportUI.applySettings();
	}

	@Override
	public void dispose() {

		chromatogramChartControl.get().dispose();
		super.dispose();
	}

	/**
	 * Initializes a store with required defaults so it can be used with the {@link ExtendedChromatogramUI}
	 * 
	 * @param preferenceStore
	 */
	public static void initializeChartDefaults(IPreferenceStore preferenceStore) {

		PreferenceInitializer.initializeChromatogramDefaults(preferenceStore);
	}

	private void updateMappedRetentionIndices() {

		if(chromatogramSelection != null) {
			/*
			 * Update the retention index marker
			 */
			ISeparationColumnIndices separationColumnIndices = chromatogramSelection.getChromatogram().getSeparationColumnIndices();
			List<PositionMarker> positionMarkers = new ArrayList<>();
			for(IRetentionIndexEntry retentionIndexEntry : separationColumnIndices.values()) {
				positionMarkers.add(new PositionMarker(retentionIndexEntry.getName(), retentionIndexEntry.getRetentionTime(), Math.round(retentionIndexEntry.getRetentionIndex())));
			}
			retentionIndexMarker.clear();
			retentionIndexMarker.getPositionMarkers().addAll(positionMarkers);
			retentionIndexMarker.setDraw(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_MARKER));
		} else {
			/*
			 * Clear
			 */
			retentionIndexMarker.clear();
			retentionIndexMarker.setDraw(false);
		}
	}

	private void updateRetentionIndexDisplayStatus() {

		boolean showRetentionIndexMarker = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_MARKER);
		setButtonImage(buttonToggleRetentionIndexControl.get(), IMAGE_RETENTION_INDICES, PREFIX_SHOW, PREFIX_HIDE, TOOLTIP_RETENTION_INDICES, showRetentionIndexMarker);
		retentionIndexMarker.setDraw(showRetentionIndexMarker);
		chromatogramChartControl.get().redraw();
	}
}