/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.swt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMassSpectrumPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.supplier.IScanProcessSupplier;
import org.eclipse.chemclipse.model.supplier.ScanProcessSupplier;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverterSupport;
import org.eclipse.chemclipse.msd.identifier.comparison.MassSpectrumComparatorContext;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumUpdateNotifierUI;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageProvider;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier.SupplierType;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.ProcessSettingsSupport;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.support.formats.MagnitudeScaledDecimalFormat;
import org.eclipse.chemclipse.ux.extension.msd.ui.handlers.DynamicHandler;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.UpdateMenuEntry;
import org.eclipse.chemclipse.ux.extension.ui.editors.ProcessorSupplierMenuEntry;
import org.eclipse.chemclipse.ux.extension.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryNameComparator;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.chemclipse.xxd.process.ui.menu.IMenuIcon;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;
import org.eclipse.swtchart.extensions.marker.LabelMarker;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

public class MassSpectrumChartProfile extends LineChart implements IMassSpectrumChart {

	private static final Logger logger = Logger.getLogger(MassSpectrumChartProfile.class);

	private static final DecimalFormatSymbols ENGLISH_SYMBOLS = new DecimalFormatSymbols(Locale.ENGLISH);

	private IScanMSD massSpectrum = null;

	private IScanMSD menuCache = null;
	private final List<IChartMenuEntry> cachedMenuEntries = new ArrayList<>();

	private IProcessSupplierContext processTypeSupport = new ProcessTypeSupport();

	private ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);

	public MassSpectrumChartProfile() {

		super();
		initialize();
	}

	public MassSpectrumChartProfile(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	@Override
	public void update(IScanMSD massSpectrum) {

		if(this.massSpectrum != massSpectrum) {
			this.massSpectrum = massSpectrum;
			update();
		}
	}

	@Override
	public void update() {

		if(isDisposed()) {
			return;
		}
		deleteSeries();
		if(massSpectrum != null) {
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			ISeriesData seriesData = getMassSpectrum(massSpectrum);
			ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);

			ILineSeriesSettings settings = lineSeriesData.getSettings();
			settings.setEnableArea(false);
			settings.setAreaStrict(false);

			lineSeriesDataList.add(lineSeriesData);

			if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
				LineSeriesData peakLineSeriesData = getPeaks(standaloneMassSpectrum);
				lineSeriesDataList.add(peakLineSeriesData);
				LineSeriesData noiseLineSeriesData = getNoise(standaloneMassSpectrum);
				lineSeriesDataList.add(noiseLineSeriesData);
				LineSeriesData baselineLineSeriesData = getBaseline(standaloneMassSpectrum);
				lineSeriesDataList.add(baselineLineSeriesData);
				createAnnotations(standaloneMassSpectrum);
			}

			addSeriesData(lineSeriesDataList);
			updateAxis();
			updateMenu();
		}
	}

	private void updateAxis() {

		IChartSettings chartSettings = getChartSettings();
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		int exponent = MagnitudeScaledDecimalFormat.orderOfMagnitude(massSpectrum.getHighestAbundance().getAbundance());
		primaryAxisSettingsY.setDecimalFormat(new MagnitudeScaledDecimalFormat("0.#", ENGLISH_SYMBOLS, exponent));
		primaryAxisSettingsY.setHorizontalLabel("×10" + MagnitudeScaledDecimalFormat.toSuperscript(String.valueOf(exponent)));
		applySettings(chartSettings);
	}

	private void initialize() {

		setLayoutData(new GridData(GridData.FILL_BOTH));

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitle("");
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.setCreateMenu(true);

		chartSettings.addMenuEntry(new UpdateMenuEntry());
		addMassSpectrumExport(chartSettings);

		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.ABSOLUTE);
		rangeRestriction.setExtendMinX(2.0d);
		rangeRestriction.setExtendMaxX(2.0d);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMaxY(0.5d);

		setPrimaryAxisSet(chartSettings);
		applySettings(chartSettings);
	}

	private void updateMenu() {

		IChartSettings chartSettings = getChartSettings();
		if(processTypeSupport != null && menuCache != massSpectrum) {
			/*
			 * Clean the Menu
			 */
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
			applySettings(chartSettings);
			menuCache = massSpectrum;
		}
	}

	private boolean isValidSupplier(IProcessSupplier<?> supplier) {

		if(supplier.getType() == SupplierType.STRUCTURAL) {
			return false;
		}

		String category = supplier.getCategory();
		if(!category.equals(ICategories.MASS_SPECTRUM_FILTER) //
				&& !category.equals(ICategories.BASELINE_DETECTOR) //
				&& !category.equals(ICategories.PEAK_PICKER) //
				&& !category.equals(ICategories.NOISE_ESTIMATOR) //
				&& !category.equals(ICategories.MASS_SPECTRUM_IDENTIFIER) //
				&& !category.equals(ICategories.MASS_SPECTRUM_REPORTS)) {
			return false;
		}

		if(supplier instanceof ScanProcessSupplier scanProcessSupplier) {
			return scanProcessSupplier.isValidFor(massSpectrum);
		}

		return false;
	}

	private void addCommand(IProcessSupplier<?> supplier, IChartMenuEntry cachedEntry) {

		Command command = commandService.getCommand(supplier.getId());
		Category category = commandService.getCategory(supplier.getCategory());
		if(!category.isDefined()) {
			category.define(supplier.getCategory(), "");
		}
		command.define(supplier.getName(), supplier.getDescription(), category);
		command.setHandler(new DynamicHandler(cachedEntry, this));
	}

	private <C> void executeSupplier(IProcessSupplier<C> processSupplier, IProcessSupplierContext processSupplierContext) {

		try {
			Shell shell = getShell();
			IProcessorPreferences<C> settings;
			MassSpectrumComparatorContext.setMassSpectrum(massSpectrum);
			try {
				settings = SettingsWizard.getSettings(shell, ProcessSettingsSupport.getWorkspacePreferences(processSupplier), true);
			} finally {
				MassSpectrumComparatorContext.setMassSpectrum(null);
			}
			if(settings == null) {
				return;
			}
			/*
			 * Apply
			 */
			processMassSpectrum(monitor -> executeMethod(massSpectrum, scanMSD -> {

				DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
				AbstractProcessSupplier.applyProcessor(settings, IScanProcessSupplier.createConsumer(scanMSD), new ProcessExecutionContext(monitor, processingInfo, processSupplierContext));
				updateResult(processingInfo);
			}), shell);
		} catch(IOException e) {
			DefaultProcessingResult<Object> processingInfo = new DefaultProcessingResult<>();
			processingInfo.addErrorMessage(processSupplier.getName(), "The process method can't be applied.");
			logger.error(e);
			updateResult(processingInfo);
		}
	}

	private void processMassSpectrum(IRunnableWithProgress runnable, Shell shell) {

		ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
		try {
			monitor.run(true, true, runnable);
			massSpectrum.setDirty(true);
			update();
			if(!isDisposed()) {
				MassSpectrumUpdateNotifierUI.update(getDisplay(), massSpectrum);
			}
		} catch(InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		}
	}

	public void updateResult(IMessageProvider processingInfo) {

		if(!isDisposed()) {
			getDisplay().asyncExec(() -> ProcessingInfoPartSupport.getInstance().update(processingInfo, true));
		}
	}

	private void executeMethod(IScanMSD scanMSD, Consumer<IScanMSD> consumer) {

		if(scanMSD != null) {
			consumer.accept(scanMSD);
		}
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("m/z");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0"), ENGLISH_SYMBOLS));

		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
	}

	private ISeriesData getMassSpectrum(IScanMSD massSpectrum) {

		List<IIon> ions = massSpectrum.getIons();
		int size = ions.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];

		for(int i = 0; i < size; i++) {
			IIon ion = ions.get(i);
			xSeries[i] = ion.getIon();
			ySeries[i] = ion.getAbundance();
		}

		return new SeriesData(xSeries, ySeries, "Mass Spectrum");
	}

	private LineSeriesData getPeaks(IStandaloneMassSpectrum massSpectrum) {

		ISeriesData peakSeriesData = createPeakSeries("Peaks", massSpectrum);
		LineSeriesData peakSeries = new LineSeriesData(peakSeriesData);
		ILineSeriesSettings lineSeriesSettings = peakSeries.getLineSeriesSettings();
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setLineStyle(LineStyle.NONE);
		lineSeriesSettings.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
		lineSeriesSettings.setSymbolSize(5);
		lineSeriesSettings.setLineColor(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		lineSeriesSettings.setSymbolColor(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		return peakSeries;
	}

	public static ISeriesData createPeakSeries(String id, IStandaloneMassSpectrum massSpectrum) {

		List<IMassSpectrumPeak> peaks = massSpectrum.getPeaks();
		int size = peaks.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(IMassSpectrumPeak peak : peaks) {
			xSeries[index] = peak.getIon();
			ySeries[index] = peak.getAbundance();
			index++;
		}
		return new SeriesData(xSeries, ySeries, id);
	}

	private LineSeriesData getNoise(IStandaloneMassSpectrum massSpectrum) {

		ISeriesData noiseSeriesData = createNoiseSeries("Noise", massSpectrum);
		LineSeriesData noiseSeries = new LineSeriesData(noiseSeriesData);
		ILineSeriesSettings lineSeriesSettings = noiseSeries.getLineSeriesSettings();
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setLineWidth(2);
		lineSeriesSettings.setLineStyle(LineStyle.DOT);
		lineSeriesSettings.setSymbolType(PlotSymbolType.NONE);
		lineSeriesSettings.setLineColor(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		lineSeriesSettings.setSymbolColor(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		return noiseSeries;
	}

	private static ISeriesData createNoiseSeries(String id, IStandaloneMassSpectrum massSpectrum) {

		List<Double> noiseList = massSpectrum.getNoise();
		int size = Math.min(noiseList.size(), massSpectrum.getNumberOfIons());
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(int i = 0; i < size; i++) {
			xSeries[index] = massSpectrum.getIons().get(index).getIon();
			ySeries[index] = noiseList.get(index);
			index++;
		}
		return new SeriesData(xSeries, ySeries, id);
	}

	private LineSeriesData getBaseline(IStandaloneMassSpectrum massSpectrum) {

		ISeriesData noiseSeriesData = createBaselineSeries("Baseline", massSpectrum);
		LineSeriesData noiseSeries = new LineSeriesData(noiseSeriesData);
		ILineSeriesSettings lineSeriesSettings = noiseSeries.getLineSeriesSettings();
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setLineWidth(2);
		lineSeriesSettings.setLineStyle(LineStyle.SOLID);
		lineSeriesSettings.setSymbolType(PlotSymbolType.NONE);
		lineSeriesSettings.setLineColor(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		lineSeriesSettings.setSymbolColor(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		return noiseSeries;
	}

	private static ISeriesData createBaselineSeries(String id, IStandaloneMassSpectrum massSpectrum) {

		List<Double> baselineList = massSpectrum.getBaseline();
		int size = Math.min(baselineList.size(), massSpectrum.getNumberOfIons());
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(int i = 0; i < size; i++) {
			xSeries[index] = massSpectrum.getIons().get(index).getIon();
			ySeries[index] = baselineList.get(index);
			index++;
		}
		return new SeriesData(xSeries, ySeries, id);
	}

	private void createAnnotations(IStandaloneMassSpectrum massSpectrum) {

		IPlotArea plotarea = getBaseChart().getPlotArea();
		LabelMarker labelMarker = new LabelMarker(getBaseChart());
		List<IMassSpectrumPeak> peaks = massSpectrum.getPeaks();
		List<String> labels = new ArrayList<>();
		for(IMassSpectrumPeak peak : peaks) {
			if(peak.getTargets().isEmpty()) {
				labels.add("");
			} else if(peak.getTargets().iterator().hasNext()) {
				IIdentificationTarget identificationTarget = peak.getTargets().iterator().next();
				ILibraryInformation info = identificationTarget.getLibraryInformation();
				if(info != null) {
					labels.add(info.getName());
				}
			}
		}
		labelMarker.setLabels(labels, 1, SWT.VERTICAL);
		plotarea.addCustomPaintListener(labelMarker);
	}

	private void addMassSpectrumExport(IChartSettings chartSettings) {

		MassSpectrumConverterSupport converterSupport = MassSpectrumConverter.getMassSpectrumConverterSupport();
		List<ISupplier> exportSupplier = converterSupport.getExportSupplier();
		for(ISupplier supplier : exportSupplier) {
			chartSettings.addMenuEntry(new IChartMenuEntry() {

				@Override
				public String getName() {

					return supplier.getFilterName();
				}

				@Override
				public String getToolTipText() {

					return supplier.getDescription();
				}

				@Override
				public String getCategory() {

					return "Export";
				}

				@Override
				public Image getIcon() {

					IExtensionRegistry registry = Platform.getExtensionRegistry();
					IConfigurationElement[] config = registry.getConfigurationElementsFor(IMenuIcon.EXTENSION_POINT_ID);
					try {
						for(IConfigurationElement element : config) {
							final String id = element.getAttribute("id");
							if(!supplier.getId().equals(id)) {
								continue;
							}
							final Object object = element.createExecutableExtension("class");
							if(object instanceof IMenuIcon menuIcon) {
								return menuIcon.getImage();
							}
						}
					} catch(CoreException e) {
						logger.warn(e);
					}
					return null;
				}

				@Override
				public void execute(Shell shell, ScrollableChart scrollableChart) {

					if(massSpectrum == null) {
						return;
					}
					FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
					fileDialog.setText("Mass Spectrum Export");
					String fileName = "Mass Spectrum";
					if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
						fileName = standaloneMassSpectrum.getName();
					}
					fileDialog.setFileName(fileName + "." + supplier.getFileExtension());
					fileDialog.setFilterExtensions("*" + supplier.getFileExtension());
					fileDialog.setFilterNames(supplier.getFilterName());
					String pathname = fileDialog.open();
					if(pathname != null) {
						File file = new File(pathname);
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
						try {
							dialog.run(true, true, monitor -> {

								IProcessingInfo<File> convert = MassSpectrumConverter.convert(file, massSpectrum, false, supplier.getId(), monitor);
								ProcessingInfoPartSupport.getInstance().update(convert);
							});
						} catch(InvocationTargetException e) {
							IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
							processingInfo.addErrorMessage("MS Export", "Export failed");
							logger.error(e.getCause());
							ProcessingInfoPartSupport.getInstance().update(processingInfo);
						} catch(InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
			});
		}
	}
}
