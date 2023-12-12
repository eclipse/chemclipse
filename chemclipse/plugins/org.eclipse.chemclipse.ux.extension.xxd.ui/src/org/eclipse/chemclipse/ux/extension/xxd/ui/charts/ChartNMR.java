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
 * Christoph Läubrich - add static helper method
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.eclipse.chemclipse.converter.scan.IScanConverterSupport;
import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.PeakPosition;
import org.eclipse.chemclipse.nmr.converter.core.ScanConverterNMR;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.files.ExtendedFileDialog;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.axisconverter.PassThroughConverter;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.IAxisScaleConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class ChartNMR extends LineChart {

	private static final boolean PROCESSED_AXIS_REVERSED = true;
	private IAxisScaleConverter ppmconverter;

	public ChartNMR(Composite parent, int style) {

		this(parent, style, null);
	}

	@SuppressWarnings("deprecation")
	public ChartNMR(Composite parent, int style, Supplier<IComplexSignalMeasurement<?>> measurementSupplier) {

		super(parent, style);
		initialize();
		if(measurementSupplier != null) {
			IChartSettings settings = getChartSettings();
			IScanConverterSupport converterSupport = ScanConverterNMR.getScanConverterSupport();
			List<ISupplier> exportSupplier = converterSupport.getExportSupplier();
			for(ISupplier supplier : exportSupplier) {
				settings.addMenuEntry(new IChartMenuEntry() {

					@Override
					public String getName() {

						return supplier.getFilterName();
					}

					@Override
					public String getCategory() {

						return ICategories.EXPORT;
					}

					@Override
					public Image getIcon() {

						return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImageProvider.SIZE_16x16);
					}

					@Override
					public void execute(Shell shell, ScrollableChart scrollableChart) {

						FileDialog fileDialog = ExtendedFileDialog.create(shell, SWT.SAVE);
						fileDialog.setText("NMR Export");
						fileDialog.setFilterExtensions(new String[]{"*" + supplier.getFileExtension()});
						fileDialog.setFilterNames(new String[]{supplier.getFilterName()});
						String pathname = fileDialog.open();
						if(pathname != null) {
							File file = new File(pathname);
							ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
							try {
								dialog.run(true, true, new IRunnableWithProgress() {

									@Override
									public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

										IProcessingInfo<Void> export = ScanConverterNMR.export(file, measurementSupplier.get(), supplier.getId(), monitor);
										ProcessingInfoPartSupport.getInstance().update(export);
									}
								});
							} catch(InvocationTargetException e) {
								IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
								processingInfo.addErrorMessage("NMR Export", "Export failed", e.getCause());
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

	public void modifyChart(boolean rawData) {

		if(rawData) {
			modifyRaw();
		} else {
			modifyProcessed();
		}
	}

	public void setPPMconverter(IAxisScaleConverter ppmconverter) {

		this.ppmconverter = ppmconverter;
	}

	private void initialize() {

		modifyProcessed();
	}

	private void modifyRaw() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(false);
		chartSettings.getRangeRestriction().setZeroY(false);
		//
		setPrimaryAxisSetRaw(chartSettings);
		addSecondaryAxisSetRaw(chartSettings);
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSetRaw(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("scan");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		primaryAxisSettingsX.setPosition(Position.Primary);
		primaryAxisSettingsX.setVisible(false);
		primaryAxisSettingsX.setReversed(false);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	private void addSecondaryAxisSetRaw(IChartSettings chartSettings) {

		deleteSecondaryAxes(chartSettings);
		/*
		 * X
		 */
		ISecondaryAxisSettings secondaryAxisSettingsX1 = new SecondaryAxisSettings("t1 (sec)", new PassThroughConverter());
		secondaryAxisSettingsX1.setPosition(Position.Primary);
		secondaryAxisSettingsX1.setDecimalFormat(new DecimalFormat(("0.000"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			secondaryAxisSettingsX1.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			secondaryAxisSettingsX1.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX1);
		/*
		 * Y
		 */
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("Relative Intensity [%]", new PercentageConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	private void modifyProcessed() {

		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(false);
		chartSettings.getRangeRestriction().setZeroY(false);
		//
		setPrimaryAxisSetProcessed(chartSettings);
		addSecondaryAxisSetProcessed(chartSettings);
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSetProcessed(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Frequency [Hz]");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		primaryAxisSettingsX.setVisible(true);
		primaryAxisSettingsX.setReversed(PROCESSED_AXIS_REVERSED);
		if(ppmconverter == null) {
			primaryAxisSettingsX.setPosition(Position.Primary);
			primaryAxisSettingsX.setGridLineStyle(LineStyle.DASH);
		} else {
			primaryAxisSettingsX.setPosition(Position.Secondary);
			primaryAxisSettingsX.setGridLineStyle(LineStyle.NONE);
		}
		primaryAxisSettingsX.setExtraSpaceTitle(10);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		primaryAxisSettingsY.setGridLineStyle(LineStyle.NONE);
	}

	private void addSecondaryAxisSetProcessed(IChartSettings chartSettings) {

		deleteSecondaryAxes(chartSettings);
		if(ppmconverter != null) {
			ISecondaryAxisSettings secondaryAxisSettingsX1 = new SecondaryAxisSettings("ppm", ppmconverter);
			secondaryAxisSettingsX1.setPosition(Position.Primary);
			secondaryAxisSettingsX1.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
			if(PreferencesSupport.isDarkTheme()) {
				secondaryAxisSettingsX1.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			} else {
				secondaryAxisSettingsX1.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			}
			secondaryAxisSettingsX1.setVisible(true);
			secondaryAxisSettingsX1.setReversed(PROCESSED_AXIS_REVERSED);
			secondaryAxisSettingsX1.setExtraSpaceTitle(10);
			chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX1);
		}
		/*
		 * Y
		 */
		ISecondaryAxisSettings secondaryAxisSettingsY = new SecondaryAxisSettings("Relative Intensity [%]", new PercentageConverter(SWT.VERTICAL, true));
		secondaryAxisSettingsY.setPosition(Position.Secondary);
		secondaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			secondaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY);
	}

	private void deleteSecondaryAxes(IChartSettings chartSettings) {

		chartSettings.getSecondaryAxisSettingsListX().clear();
		chartSettings.getSecondaryAxisSettingsListY().clear();
	}

	public static ISeriesData createSignalSeries(String id, Collection<? extends ISignal> signals) {

		return createSignalSeries(id, signals, 0.0d, 0.0d);
	}

	public static ISeriesData createPeakSeries(String id, List<? extends ISignal> signals, Iterable<PeakPosition> iterable, double yOffset, double xOffset) {

		List<PeakPosition> list = StreamSupport.stream(iterable.spliterator(), false).toList();
		int size = list.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(PeakPosition position : list) {
			int maximum = position.getPeakMaximum();
			if(maximum > -1) {
				ISignal signal = signals.get(maximum);
				xSeries[index] = signal.getX() + xOffset;
				ySeries[index] = signal.getY() + yOffset;
			} else {
				xSeries[index] = Double.NaN;
				ySeries[index] = Double.NaN;
			}
			index++;
		}
		return new SeriesData(xSeries, ySeries, id);
	}

	public static ISeriesData createSignalSeries(String id, Collection<? extends ISignal> signals, double yOffset, double xOffset) {

		int size = signals.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(ISignal signal : signals) {
			xSeries[index] = signal.getX() + xOffset;
			ySeries[index] = signal.getY() + yOffset;
			index++;
		}
		return new SeriesData(xSeries, ySeries, id);
	}
}
