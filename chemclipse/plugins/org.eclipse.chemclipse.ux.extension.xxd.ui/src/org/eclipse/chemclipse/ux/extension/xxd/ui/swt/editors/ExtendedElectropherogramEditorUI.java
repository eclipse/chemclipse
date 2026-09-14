/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.chemclipse.converter.methods.MetaProcessorProcessSupplier;
import org.eclipse.chemclipse.converter.methods.UserMethodProcessSupplier;
import org.eclipse.chemclipse.dsd.model.core.IChromatogramDSD;
import org.eclipse.chemclipse.dsd.model.core.Nucleobase;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.core.MarkedTraces;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageProvider;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.AbstractProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier.SupplierType;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.ProcessSettingsSupport;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.formats.MagnitudeScaledDecimalFormat;
import org.eclipse.chemclipse.support.traces.ITrace;
import org.eclipse.chemclipse.support.traces.TraceRasteredWSD;
import org.eclipse.chemclipse.swt.ui.marker.TargetMarker;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.swt.ui.support.ColorScheme;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.ui.editors.ProcessorSupplierMenuEntry;
import org.eclipse.chemclipse.ux.extension.ui.methods.MethodCancelException;
import org.eclipse.chemclipse.ux.extension.ui.methods.MethodParameters;
import org.eclipse.chemclipse.ux.extension.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.methods.ResumeMethodSupport;
import org.eclipse.chemclipse.ux.extension.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.ux.extension.ui.support.AuditTrailSupport;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.ChartGridSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorProcessTypeSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.help.HelpContext;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.NucleotideLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.TargetReferenceSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers.DynamicHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessorToolbarWSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.NoiseFactorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.NucleotideSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IToolbarConfig;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ProcessorToolbarUI;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryNameComparator;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageChromatogramExport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageReportExport;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;
import org.eclipse.swtchart.extensions.model.ICustomSeries;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.internal.keys.model.KeyController;
import org.eclipse.ui.keys.IBindingService;

public class ExtendedElectropherogramEditorUI extends Composite implements IToolbarConfig, IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedElectropherogramEditorUI.class);

	private static final String IMAGE_METHOD = IApplicationImage.IMAGE_METHOD;
	private static final String TOOLTIP_METHOD = "the method toolbar.";

	private static final String LABEL_SCAN_TARGETS = "Scan Targets";

	public static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";

	private static final String SERIES_ID_SELECTED_SCAN = "Selected Scan";
	private static final String SERIES_ID_IDENTIFIED_SCANS = "Identified Scans";
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scans Selected";
	private static final String MAIN_MENU_CHROMATOGRAM = "org.eclipse.chemclipse.ux.extension.ui.menu.chromatogram";
	private static final String MENU_CONTRIBUTOR_URI = "org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI";

	private static final String KEY_CLASS_PREFIX = "org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI";

	private static final DecimalFormatSymbols ENGLISH_SYMBOLS = new DecimalFormatSymbols(Locale.ENGLISH);

	private static final Set<String> DISABLED_CATEGORIES = Set.of( //
			ICategories.CHROMATOGRAM_CALCULATOR, //
			ICategories.CHROMATOGRAM_CLASSIFIER, //
			ICategories.CHROMATOGRAM_FILTER, //
			ICategories.CHROMATOGRAM_INTEGRATOR, //
			ICategories.COMBINED_CHROMATOGRAM_PEAK_INTEGRATOR, //
			ICategories.PEAK_IDENTIFIER, //
			ICategories.SCAN_IDENTIFIER, //
			ICategories.MASS_SPECTRUM_IDENTIFIER, //
			ICategories.IDENTIFIER, //
			ICategories.PEAK_DETECTOR, //
			ICategories.PEAK_INTEGRATOR, //
			ICategories.PEAK_QUANTIFIER, //
			ICategories.PEAK_FILTER, //
			ICategories.SYSTEM);

	private ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
	private IContextService contextService = (IContextService)PlatformUI.getWorkbench().getService(IContextService.class);
	private IBindingService bindingService = PlatformUI.getWorkbench().getService(IBindingService.class);

	private AtomicReference<ProcessorToolbarUI> processorToolbarControl = new AtomicReference<>();
	private AtomicReference<Composite> toolbarMainControl = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarMethod = new AtomicReference<>();
	private AtomicReference<MethodSupportUI> toolbarMethodControl = new AtomicReference<>();
	private AtomicReference<Button> buttonChartGridControl = new AtomicReference<>();
	private AtomicReference<LineChart> chartControl = new AtomicReference<>();

	private IChromatogramSelection chromatogramSelection = null;
	private final List<IChartMenuEntry> cachedMenuEntries = new ArrayList<>();

	private final Map<String, NucleotideLabelMarker> scanLabelMarkerMap = new HashMap<>();

	private DisplayType displayType;

	private boolean suspendUpdate = false;
	private IProcessSupplierContext processTypeSupport;
	private Predicate<IProcessSupplier<?>> dataCategoryPredicate = null;

	private Object menuCache = null;
	private boolean menuActive = false;

	private TargetMarker targetMarker;

	private MApplication application = Activator.getDefault().getApplication();
	private IEventBroker eventBroker = Activator.getDefault().getEventBroker();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ChartGridSupport chartGridSupport = new ChartGridSupport();

	public ExtendedElectropherogramEditorUI(Composite parent, int style, IProcessSupplierContext processTypeSupport) {

		super(parent, style);
		this.processTypeSupport = processTypeSupport != null ? processTypeSupport : new ProcessTypeSupport();
		contextService.activateContext(KEY_CLASS_PREFIX);
		createControl();
	}

	@Override
	public void setToolbarVisible(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarMainControl.get(), visible);
		if(!visible) {
			enableToolbar(toolbarMethodControl, buttonToolbarMethod.get(), IMAGE_METHOD, TOOLTIP_METHOD, false);
		}
	}

	@Override
	public boolean isToolbarVisible() {

		return toolbarMainControl.get().isVisible();
	}

	public void updateToolbar() {

		if(!isDisposed()) {
			ProcessorToolbarUI processorToolbarUI = processorToolbarControl.get();
			if(processorToolbarUI.isVisible()) {
				processorToolbarUI.updateToolbar(getDataCategory());
			}
		}
	}

	/**
	 * Resets the chart ranges 1:1.
	 */
	public void adjustChromatogramChart() {

		if(!menuActive) {
			chartControl.get().adjustRange(true);
		}
	}

	public void fireUpdate(Display display) {

		fireUpdateChromatogram(display);
		fireUpdateScan(display);
	}

	public boolean fireUpdateChromatogram(Display display) {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null && eventBroker != null) {
			UpdateNotifierUI.update(display, chromatogramSelection);
		}
		return chromatogramSelection != null;
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

	public LineChart getChromatogramChart() {

		return chartControl.get();
	}

	public synchronized void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection == null) {
			return;
		}
		setChromatogramSelectionInternal(chromatogramSelection);
	}

	private void setChromatogramSelectionInternal(IChromatogramSelection chromatogramSelection) {

		if(this.chromatogramSelection != chromatogramSelection) {
			/*
			 * Adjust Toolbar / Menu
			 */
			this.chromatogramSelection = chromatogramSelection;
			dataCategoryPredicate = IProcessSupplierContext.createDataCategoryPredicate(getDataCategory());
			updateToolbar();

			if(chromatogramSelection != null) {
				adjustAxisSettings();
				updateMenu();
				updateChromatogram();
			} else {
				adjustAxisSettings();
				updateChromatogram();
			}
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

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramWSD) {
				dataCategory = DataCategory.WSD;
			}
		}

		return dataCategory;
	}

	@Override
	public void update() {

		if(!suspendUpdate) {
			if(!menuActive) {
				updateChromatogram();
				adjustChromatogramSelectionRange();
			}
		}
	}

	public void updateSelectedScan() {

		if(!menuActive) {
			chartControl.get().deleteSeries(SERIES_ID_SELECTED_SCAN);
			chartControl.get().deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);

			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			addSelectedScanData(lineSeriesDataList);
			addSelectedIdentifiedScanData(lineSeriesDataList);
			chartControl.get().addSeriesData(lineSeriesDataList);
			adjustChromatogramSelectionRange();
		}
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	public boolean isActiveChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		return (this.chromatogramSelection == chromatogramSelection);
	}

	protected void updateSelection() {

		if(chromatogramSelection != null) {
			chromatogramSelection.update(true);
			adjustChromatogramSelectionRange();
		}
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
			IChartSettings chartSettings = chartControl.get().getChartSettings();
			for(IChartMenuEntry cachedEntry : cachedMenuEntries) {
				chartSettings.removeMenuEntry(cachedEntry);
			}
			cachedMenuEntries.clear();
			/*
			 * Dynamic Menu Items
			 */
			List<IProcessSupplier<?>> processSupplierList = new ArrayList<>(processTypeSupport.getSupplier(this::isValidSupplier));
			Collections.sort(processSupplierList, new CategoryNameComparator());
			for(IProcessSupplier<?> processSupplier : processSupplierList) {
				IChartMenuEntry chartMenuEntry = new ProcessorSupplierMenuEntry<>(processSupplier, processTypeSupport, this::executeSupplier);
				cachedMenuEntries.add(chartMenuEntry);
				chartSettings.addMenuEntry(chartMenuEntry);
				addCommand(processSupplier, chartMenuEntry);
			}
			/*
			 * Apply the menu items.
			 */
			chartControl.get().applySettings(chartSettings);
			menuCache = chromatogramSelection;
		}
	}

	public void updateMethods() {

		toolbarMethodControl.get().updateInput();
	}

	public void updateCommands() {

		List<IProcessSupplier<?>> suplierList = new ArrayList<>(processTypeSupport.getSupplier(this::isValidSupplier));
		Collections.sort(suplierList, new CategoryNameComparator());
		for(IProcessSupplier<?> supplier : suplierList) {
			IChartMenuEntry cachedEntry = new ProcessorSupplierMenuEntry<>(supplier, processTypeSupport, this::executeSupplier);
			addCommand(supplier, cachedEntry);
		}
		restoreKeyBindingsFromSettings();
	}

	private void addCommand(IProcessSupplier<?> supplier, IChartMenuEntry cachedEntry) {

		Command command = commandService.getCommand(supplier.getId());
		Category category = commandService.getCategory(supplier.getCategory());
		if(!category.isDefined()) {
			category.define(supplier.getCategory(), "");
		}
		command.define(supplier.getName(), supplier.getDescription(), category);
		command.setHandler(new DynamicHandler(cachedEntry, chartControl.get()));
		addMainMenu(supplier, command);
	}

	@SuppressWarnings("restriction")
	private void restoreKeyBindingsFromSettings() {

		KeyController keyController = new KeyController();
		keyController.init(PlatformUI.getWorkbench());
		keyController.saveBindings(bindingService);
	}

	private void addMainMenu(IProcessSupplier<?> supplier, Command command) {

		try {
			if(supplier.getCategory().equals(ICategories.CHROMATOGRAM_EXPORT)) {
				createMenuEntry(MAIN_MENU_CHROMATOGRAM, MAIN_MENU_CHROMATOGRAM + ".export", command);
			} else if(supplier.getCategory().equals(ICategories.CHROMATOGRAM_IDENTIFIER)) {
				createMenuEntry(MAIN_MENU_CHROMATOGRAM, MAIN_MENU_CHROMATOGRAM + ".identifier", command);
			} else if(supplier.getCategory().equals(ICategories.CHROMATOGRAM_REPORTS)) {
				createMenuEntry(MAIN_MENU_CHROMATOGRAM, MAIN_MENU_CHROMATOGRAM + ".reports", command);
			}
		} catch(NotDefinedException e) {
			logger.warn(e);
		}
	}

	private MMenu getSubMenu(String parent) throws NotDefinedException {

		MWindow window = application.getChildren().get(0);
		MMenu mainMenu = window.getMainMenu();
		Optional<MMenuElement> element = mainMenu.getChildren().stream().filter(c -> c.getElementId().equals(parent)).findFirst();
		if(element.isPresent() && element.get() instanceof MMenu mMenu) {
			return mMenu;
		}
		throw new NotDefinedException(parent);
	}

	private void createMenuEntry(String parent, String id, Command command) throws NotDefinedException {

		MMenu mMenu = getSubMenu(parent);
		createSubMenuEntry(mMenu, id, command);
	}

	private void createSubMenuEntry(MMenu subMenu, String id, Command command) throws NotDefinedException {

		Optional<MMenuElement> element = subMenu.getChildren().stream().filter(c -> c.getElementId().equals(id)).findFirst();
		if(element.isPresent() && element.get() instanceof MMenu mMenu) {
			Optional<MMenuElement> previous = mMenu.getChildren().stream().filter(c -> c.getElementId().equals(command.getId())).findAny();
			if(previous.isPresent() && previous.get() instanceof MHandledMenuItem handledMenuItem) {
				handledMenuItem.setCommand(createModelCommand(command)); // reroute to this widget
			} else {
				MHandledMenuItem menuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
				menuItem.setElementId(command.getId());
				menuItem.setLabel(command.getName());
				menuItem.setCommand(createModelCommand(command));
				menuItem.setContributorURI(MENU_CONTRIBUTOR_URI);
				mMenu.getChildren().add(menuItem);
			}
		} else {
			throw new NotDefinedException(id);
		}
	}

	private MCommand createModelCommand(Command command) {

		MCommand mCommand = MCommandsFactory.INSTANCE.createCommand();
		mCommand.setElementId(command.getId());
		try {
			mCommand.setCommandName(command.getName());
		} catch(NotDefinedException e) {
			logger.warn(e);
		}
		return mCommand;
	}

	private <C> void executeSupplier(IProcessSupplier<C> processSupplier, IProcessSupplierContext processSupplierContext) {

		try {
			Shell shell = getChromatogramChart().getShell();
			IProcessorPreferences<C> settings = SettingsWizard.getSettings(shell, ProcessSettingsSupport.getWorkspacePreferences(processSupplier), true);
			if(settings == null) {
				return;
			}
			/*
			 * Meta Processor
			 */
			if(processSupplier instanceof MetaProcessorProcessSupplier metaProcessorProcessSupplier) {
				IProcessMethod processMethod = metaProcessorProcessSupplier.getProcessMethod();
				MethodParameters methodParameters = ResumeMethodSupport.selectMethodParameters(shell, processMethod);
				processMethod.setActiveProfile(methodParameters.getProfile());
				processMethod.setResumeIndex(methodParameters.getResumeIndex());
			} else if(processSupplier instanceof UserMethodProcessSupplier userProcessorProcessSupplier) {
				IProcessMethod processMethod = userProcessorProcessSupplier.getProcessMethod();
				MethodParameters methodParameters = ResumeMethodSupport.selectMethodParameters(shell, processMethod);
				processMethod.setActiveProfile(methodParameters.getProfile());
				processMethod.setResumeIndex(methodParameters.getResumeIndex());
			}
			/*
			 * Process Method Macro Recorder
			 * ProcessSupplier, MetaProcessorProcessSupplier
			 * NodeProcessorPreferences
			 */
			ProcessMethod processMethodMacroRecorder = toolbarMethodControl.get().getProcessMethodMacroRecorder();
			if(processMethodMacroRecorder != null) {
				ProcessEntry processEntry = new ProcessEntry(processMethodMacroRecorder);
				processEntry.setProcessorId(processSupplier.getId());
				processEntry.setName(processSupplier.getName());
				processEntry.setDescription(processSupplier.getDescription());
				processEntry.setSettings(settings.getUserSettingsAsString());
				processMethodMacroRecorder.addProcessEntry(processEntry);
			}
			/*
			 * Apply
			 */
			processChromatogram(monitor -> executeMethod(getChromatogramSelection(), chromatogramSelection -> {

				DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
				AbstractProcessSupplier.applyProcessor(settings, IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection), new ProcessExecutionContext(monitor, processingInfo, processSupplierContext));
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				updateResult(processingInfo);
				AuditTrailSupport.updateAuditTrail(chromatogram, processingInfo, processSupplier);
				NoiseFactorSupport.updateNoiseFactor(chromatogram, processSupplier);
			}), shell);
		} catch(IOException e) {
			DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
			processingInfo.addErrorMessage(processSupplier.getName(), "The process method can't be applied.");
			logger.error(e);
			updateResult(processingInfo);
		} catch(MethodCancelException e) {
			DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
			processingInfo.addWarnMessage(processSupplier.getName(), "The process method execution has been cancelled.");
			updateResult(processingInfo);
		}
	}

	public void updateResult(IMessageProvider processingInfo) {

		getDisplay().asyncExec(() -> ProcessingInfoPartSupport.getInstance().update(processingInfo, true));
	}

	private boolean isValidSupplier(IProcessSupplier<?> supplier) {

		if(supplier.getType() == SupplierType.STRUCTURAL) {
			return false;
		}

		if(supplier.getTypeSupplier() instanceof EditorProcessTypeSupplier) {
			return false;
		}

		if(DISABLED_CATEGORIES.contains(supplier.getCategory())) {
			return false;
		}

		return dataCategoryPredicate != null && dataCategoryPredicate.test(supplier);
	}

	private void updateChromatogram() {

		clearPeakAndScanLabels();
		chartControl.get().deleteSeries();

		if(chromatogramSelection != null) {
			setRangeRestrictions();
			displayType = DisplayType.XWC;
			addChromatogramSeriesData();
			adjustChromatogramSelectionRange();
		}
	}

	private void clearPeakAndScanLabels() {

		for(String key : scanLabelMarkerMap.keySet()) {
			removeIdentificationLabelMarker(scanLabelMarkerMap, key);
		}

		scanLabelMarkerMap.clear();
	}

	private void removeIdentificationLabelMarker(Map<String, NucleotideLabelMarker> markerMap, String seriesId) {

		BaseChart baseChart = chartControl.get().getBaseChart();
		IPlotArea plotArea = baseChart.getPlotArea();
		NucleotideLabelMarker labelMarker = markerMap.get(seriesId);
		if(labelMarker != null) {
			ICustomSeries customSeries = labelMarker.getCustomSeries();
			if(customSeries != null) {
				baseChart.deleteCustomSeries(customSeries.getId());
			}
			plotArea.removeCustomPaintListener(labelMarker);
		}
	}

	private void setRangeRestrictions() {

		IChartSettings chartSettings = chartControl.get().getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		/*
		 * Add space on top to show labels correctly.
		 */
		double extendY = preferenceStore.getDouble(PreferenceSupplier.P_CHROMATOGRAM_EXTEND_Y);
		rangeRestriction.setExtendMaxY(extendY);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setForceZeroMinY(false);
		/*
		 * Zooming
		 */
		rangeRestriction.setRestrictSelectX(preferenceStore.getBoolean(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_SELECT_X));
		rangeRestriction.setRestrictSelectY(preferenceStore.getBoolean(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_SELECT_Y));
		rangeRestriction.setReferenceZoomZeroX(preferenceStore.getBoolean(PreferenceSupplier.P_CHROMATOGRAM_REFERENCE_ZOOM_ZERO_X));
		rangeRestriction.setReferenceZoomZeroY(preferenceStore.getBoolean(PreferenceSupplier.P_CHROMATOGRAM_REFERENCE_ZOOM_ZERO_Y));
		rangeRestriction.setRestrictZoomX(preferenceStore.getBoolean(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_ZOOM_X));
		rangeRestriction.setRestrictZoomY(preferenceStore.getBoolean(PreferenceSupplier.P_CHROMATOGRAM_RESTRICT_ZOOM_Y));

		chartControl.get().applySettings(chartSettings);
	}

	private void addChromatogramSeriesData() {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();

		addChromatogramData(lineSeriesDataList);
		addSelectedScanData(lineSeriesDataList);
		addIdentifiedScansData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);

		chartControl.get().addSeriesData(lineSeriesDataList);
	}

	/**
	 * A sequencing trace is shown as its four dye traces instead of a summed signal.
	 * Multiple wavelengths stacked. Otherwise the bases can't be told apart.
	 */
	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection.getChromatogram() instanceof IChromatogramDSD chromatogramDSD) {
			IColorScheme colorScheme = generateColorScheme(chromatogramDSD);
			for(Float wavelength : chromatogramDSD.getWavelengthMapping().keySet()) {
				IMarkedTraces<ITrace> markedTraces = new MarkedTraces(MarkedTraceModus.INCLUDE);
				markedTraces.add(new TraceRasteredWSD(wavelength));
				Nucleobase nucleobase = chromatogramDSD.getWavelengthMapping().get(wavelength);
				String seriesId = SERIES_ID_CHROMATOGRAM + " " + nucleobase.letter();
				ILineSeriesData lineSeriesData = getLineSeriesData(chromatogramDSD, seriesId, colorScheme.getColor(), markedTraces);
				lineSeriesData.getSettings().setEnableArea(preferenceStore.getBoolean(PreferenceSupplier.P_ENABLE_CHROMATOGRAM_AREA));
				lineSeriesData.getSettings().setDescription(String.valueOf(nucleobase.label()));
				lineSeriesDataList.add(lineSeriesData);
				colorScheme.incrementColor();
			}
		}
	}

	private ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, Color color, IMarkedTraces<ITrace> signals) {

		int startScan = 1;
		int stopScan = chromatogram.getNumberOfScans();
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, color, signals);
	}

	private ILineSeriesData getLineSeriesData(IChromatogram chromatogram, int startScan, int stopScan, String seriesId, Color color, IMarkedTraces<ITrace> signals) {

		int length = stopScan - startScan + 1;
		double[] xSeries = new double[length];
		double[] ySeries = new double[length];

		/*
		 * A missing scan must not leave a 0/0 entry behind, that would draw a point at the origin, outside of the scan range.
		 */
		int index = 0;
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			if(scan != null) {
				xSeries[index] = scan.getScanNumber();
				ySeries[index] = getIntensity(scan, signals);
				index++;
			}
		}

		if(index < length) {
			xSeries = Arrays.copyOf(xSeries, index);
			ySeries = Arrays.copyOf(ySeries, index);
		}

		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
		lineSeriesSettings.setLineColor(color);

		lineSeriesSettings.setLineStyle(LineStyle.valueOf(preferenceStore.getString(PreferenceSupplier.P_LINE_STYLE_DISPLAY_OVERLAY)));
		lineSeriesSettings.setEnableArea(preferenceStore.getBoolean(PreferenceSupplier.P_OVERLAY_SHOW_AREA));
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);

		return lineSeriesData;
	}

	private double getIntensity(IScan scan, IMarkedTraces<ITrace> signals) {

		double intensity = Double.NaN;

		if(scan instanceof IScanWSD scanWSD && signals != null) {
			IExtractedWavelengthSignal extractedWavelengthSignal = scanWSD.getExtractedWavelengthSignal();
			Set<Integer> wavelengths = new HashSet<>();
			for(ITrace signal : signals) {
				wavelengths.add((int)Math.round(signal.getValue()));
			}
			for(int wavelength : wavelengths) {
				intensity = extractedWavelengthSignal.getAbundance(wavelength);
			}
		}

		return intensity;
	}

	private IColorScheme generateColorScheme(IChromatogramDSD chromatogramDSD) {

		List<Color> colorsNucleobases = new ArrayList<>();
		for(Nucleobase nucleobase : chromatogramDSD.getWavelengthMapping().values()) {
			colorsNucleobases.add(NucleotideSupport.getColor(nucleobase));
		}
		return new ColorScheme(colorsNucleobases);
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			String seriesId = SERIES_ID_IDENTIFIED_SCANS;
			List<IScan> scans = ChromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
			if(!scans.isEmpty()) {
				addIdentifiedScansData(lineSeriesDataList, scans, seriesId);
				/*
				 * Add the labels.
				 */
				removeIdentificationLabelMarker(scanLabelMarkerMap, seriesId);
				ITargetDisplaySettings targetDisplaySettings = chromatogramSelection.getChromatogram();
				BaseChart baseChart = chartControl.get().getBaseChart();
				IPlotArea plotArea = baseChart.getPlotArea();
				Collection<? extends TargetReference> scanReferences = TargetReference.getScanReferences(scans, targetDisplaySettings);
				TargetReferenceSettings targetReferenceSettings = new TargetReferenceSettings(scanReferences, targetDisplaySettings, PreferenceSupplier.DEF_SYMBOL_SIZE * 2);
				targetReferenceSettings.setBaseChart(baseChart);
				targetReferenceSettings.setLabel(LABEL_SCAN_TARGETS);
				targetReferenceSettings.setDescription("Nucleotides");
				Optional<String> chromatogramSeriesId = lineSeriesDataList.stream().map(l -> l.getSeriesData().getId()).filter(s -> s.startsWith(SERIES_ID_CHROMATOGRAM)).findFirst();
				if(chromatogramSeriesId.isPresent()) {
					targetReferenceSettings.setReferenceSeriesId(chromatogramSeriesId.get());
				}
				NucleotideLabelMarker scanLabelMarker = new NucleotideLabelMarker(targetReferenceSettings);
				plotArea.addCustomPaintListener(scanLabelMarker);
				scanLabelMarkerMap.put(seriesId, scanLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, List<IScan> scans, String seriesId) {

		if(!scans.isEmpty()) {
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = getLineSeriesDataPoint(scans, seriesId, displayType, chromatogramSelection);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setLineColor(Colors.GRAY);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private ILineSeriesData getLineSeriesDataPoint(List<IScan> scans, String seriesId, DisplayType displayType, IChromatogramSelection chromatogramSelection) {

		List<ITrace> traces = new ArrayList<>();
		if(displayType.equals(DisplayType.SWC)) {
			traces = ((IChromatogramSelectionWSD)chromatogramSelection).getSelectedWavelengths();
		}

		IMarkedTraces<ITrace> markedTraces = new MarkedTraces(MarkedTraceModus.INCLUDE);
		markedTraces.addAll(traces);

		return getLineSeriesDataPoint(scans, seriesId, displayType, markedTraces);
	}

	private ILineSeriesData getLineSeriesDataPoint(List<IScan> scans, String seriesId, DisplayType displayType, IMarkedTraces<ITrace> markedSignals) {

		List<Double> xSeries = new ArrayList<>(scans.size());
		List<Double> ySeries = new ArrayList<>(scans.size());

		for(IScan scan : scans) {
			if(scan != null) {
				xSeries.add((double)scan.getScanNumber());
				ySeries.add((double)scan.getTotalSignal());
			}
		}

		ISeriesData seriesData = new SeriesData(xSeries.stream().mapToDouble(Double::doubleValue).toArray(), ySeries.stream().mapToDouble(Double::doubleValue).toArray(), seriesId);
		return new LineSeriesData(seriesData);
	}

	private void addSelectedIdentifiedScanData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			List<IScan> selectedIdentifiedScans = chromatogramSelection.getSelectedIdentifiedScans();
			if(!selectedIdentifiedScans.isEmpty()) {
				String seriesId = SERIES_ID_IDENTIFIED_SCAN_SELECTED;
				addIdentifiedScansData(lineSeriesDataList, selectedIdentifiedScans, seriesId);
			}
		}
	}

	private void addSelectedScanData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection == null) {
			return;
		}

		IScan scan = chromatogramSelection.getSelectedScan();
		if(scan != null) {
			Color color = Colors.GRAY;
			int markerSize = preferenceStore.getInt(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE));
			/*
			 * ScanChartSupport is retention time based and knows no XWC, hence the scan number based variant is used here.
			 */
			ILineSeriesData lineSeriesData = getLineSeriesDataPoint(Collections.singletonList(scan), SERIES_ID_SELECTED_SCAN, displayType, chromatogramSelection);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineColor(Colors.GRAY);
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesSettings.setSymbolColor(color);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		createChromatogramSection(this);
		initialize();
	}

	private void createChromatogramSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));

		createToolbarMain(composite);
		createToolbarMethod(composite);
		createChromatogramChart(composite);
	}

	private void initialize() {

		setHelp(this, HelpContext.CHROMATOGRAM_EDITOR);

		enableToolbar(toolbarMethodControl, buttonToolbarMethod.get(), IMAGE_METHOD, TOOLTIP_METHOD, preferenceStore.getBoolean(PreferenceSupplier.P_CHROMATOGRAM_SHOW_METHODS_TOOLBAR));
		enableChartGrid(chartControl, buttonChartGridControl.get(), IMAGE_CHART_GRID, chartGridSupport);
	}

	private void createToolbarMethod(Composite parent) {

		MethodSupportUI methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener((processMethod, monitor) -> executeMethod(chromatogramSelection, chromatogramSelection -> {

			IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
			AbstractProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processingInfo, processTypeSupport), IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection));
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			updateResult(processingInfo);
			AuditTrailSupport.updateAuditTrail(chromatogram, processingInfo, processMethod, processTypeSupport);
			NoiseFactorSupport.updateNoiseFactor(chromatogram, processMethod, processTypeSupport);
			UpdateNotifierUI.update(getDisplay(), chromatogramSelection.getSelectedScan());
		}));

		toolbarMethodControl.set(methodSupportUI);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(7, false));

		createProcessorToolbarUI(composite);
		createButtonToggleMethod(composite);
		createButtonToggleChartGrid(composite);
		createToggleChartSeriesLegendButton(composite);
		createButtonReset(composite);
		createButtonHelp(composite, HelpContext.CHROMATOGRAM_EDITOR);
		createButtonSettings(composite);

		toolbarMainControl.set(composite);
	}

	private void createButtonToggleChartGrid(Composite parent) {

		Button button = createButtonToggleChartGrid(parent, chartControl, IMAGE_CHART_GRID, chartGridSupport);
		buttonChartGridControl.set(button);
	}

	private void createProcessorToolbarUI(Composite parent) {

		ProcessorToolbarUI processorToolbarUI = new ProcessorToolbarUI(parent, SWT.NONE);
		processorToolbarUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		processorToolbarUI.setInput(this::executeSupplier);

		processorToolbarControl.set(processorToolbarUI);
	}

	private void createButtonToggleMethod(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarMethodControl, IMAGE_METHOD, TOOLTIP_METHOD);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceSupplier.P_CHROMATOGRAM_SHOW_METHODS_TOOLBAR, toolbarMethodControl.get().isVisible());
			}
		});

		buttonToolbarMethod.set(button);
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, getPreferencePagesSupplier(), this::applySettings, false);
	}

	private Supplier<List<Class<? extends IPreferencePage>>> getPreferencePagesSupplier() {

		return () -> {

			List<Class<? extends IPreferencePage>> preferencePages = new ArrayList<>();
			/*
			 * Specific Pages
			 */
			preferencePages.add(PreferencePageProcessorToolbarWSD.class);
			/*
			 * Standard Pages
			 */
			preferencePages.add(PreferencePageProcessors.class);
			preferencePages.add(PreferencePageChromatogram.class);
			preferencePages.add(PreferencePageChromatogramChart.class);
			preferencePages.add(PreferencePageChromatogramScans.class);
			preferencePages.add(PreferencePageSystem.class);
			preferencePages.add(PreferencePage.class);
			preferencePages.add(PreferencePageReportExport.class);
			preferencePages.add(PreferencePageChromatogramExport.class);

			return preferencePages;
		};
	}

	private void createChromatogramChart(Composite parent) {

		LineChart chart = new LineChart(parent, SWT.BORDER);
		chart.setLayoutData(new GridData(GridData.FILL_BOTH));
		chart.setFileName("Chromatogram");
		BaseChart baseChart = chart.getBaseChart();
		IPlotArea plotArea = baseChart.getPlotArea();
		/*
		 * Target Marker
		 */
		targetMarker = new TargetMarker(chart.getBaseChart());
		plotArea.addCustomPaintListener(targetMarker);
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setRangeSelectorDefaultAxisX(0);
		chartSettings.getPrimaryAxisSettingsX().setTitleVisible(false);
		chartSettings.setRangeSelectorDefaultAxisY(0);
		chartSettings.getPrimaryAxisSettingsY().setTitleVisible(false);

		chart.applySettings(chartSettings);
		/*
		 * Add listener to check if the menu has been opened/closed.
		 */
		chart.setMenuListener(new MenuListener() {

			@Override
			public void menuShown(MenuEvent menuEvent) {

				menuActive = true;
			}

			@Override
			public void menuHidden(MenuEvent menuEvent) {

				menuActive = false;
			}
		});

		chartControl.set(chart);
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chartControl.get().toggleSeriesLegendVisibility();
			}
		});
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chromatogram");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset(true);
			}
		});

		return button;
	}

	private void applySettings(Display display) {

		adjustAxisSettings();
		updateChromatogram();
		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_TOOLBAR_UPDATE, true);
	}

	private void reset(boolean resetRange) {

		updateChromatogram();
		if(resetRange && chromatogramSelection != null) {
			chromatogramSelection.reset(true);
		}
	}

	private void adjustChromatogramSelectionRange() {

		if(chromatogramSelection != null) {
			BaseChart baseChart = chartControl.get().getBaseChart();
			IAxis x = baseChart.getAxisSet().getXAxes()[0];
			IAxis y = baseChart.getAxisSet().getYAxes()[0];
			baseChart.setRange(x, chromatogramSelection.getStartScan(), PreferenceSupplier.getZoomedInNucleobasesStart(), false);
			baseChart.setRange(y, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance(), false);
		}
	}

	private void adjustAxisSettings() {

		IChartSettings chartSettings = chartControl.get().getChartSettings();
		adjustAxisSettingsX(chartSettings);
		adjustAxisSettingsY(chartSettings);
		chartControl.get().applySettings(chartSettings);
	}

	private void adjustAxisSettingsX(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat("0", ENGLISH_SYMBOLS));
	}

	private void adjustAxisSettingsY(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		int exponent = MagnitudeScaledDecimalFormat.orderOfMagnitude(chromatogramSelection.getChromatogram().getMaxSignal());
		primaryAxisSettingsY.setDecimalFormat(new MagnitudeScaledDecimalFormat("0.#", ENGLISH_SYMBOLS, exponent));
		primaryAxisSettingsY.setHorizontalLabel("×10" + MagnitudeScaledDecimalFormat.toSuperscript(String.valueOf(exponent)));
	}

	@Override
	public void dispose() {

		chartControl.get().dispose();
		super.dispose();
	}

	private void executeMethod(IChromatogramSelection chromatogramSelection, Consumer<IChromatogramSelection> consumer) {

		if(chromatogramSelection != null) {
			consumer.accept(chromatogramSelection);
		}
	}
}
