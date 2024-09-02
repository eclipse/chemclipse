/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.swt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.model.core.IMassSpectrumPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorStandaloneMassSpectrum;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.UpdateMenuEntry;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.axisconverter.PercentageConverter;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ISeriesData;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.SeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.linecharts.LineSeriesData;
import org.eclipse.swtchart.extensions.marker.LabelMarker;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class MassSpectrumChartProfile extends LineChart implements IMassSpectrumChart {

	private static final int MAX_NUMBER_MZ = 25000;
	//
	private IScanMSD massSpectrum = null;

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

		deleteSeries();
		if(massSpectrum != null) {
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			ISeriesData seriesData = getMassSpectrum(massSpectrum);
			ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
			lineSeriesDataList.add(lineSeriesData);
			if(massSpectrum instanceof IVendorStandaloneMassSpectrum standaloneMassSpectrum) {
				LineSeriesData peakLineSeriesData = getPeaks(standaloneMassSpectrum);
				lineSeriesDataList.add(peakLineSeriesData);
				createAnnotations(standaloneMassSpectrum);
			}
			addSeriesData(lineSeriesDataList, MAX_NUMBER_MZ);
			UpdateNotifier.update(massSpectrum);
		}
	}

	private void initialize() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitle("");
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.setCreateMenu(true);
		//
		chartSettings.addMenuEntry(new UpdateMenuEntry());
		addMassSpectrumFilter(chartSettings);
		addMassSpectrumIdentifier(chartSettings);
		//
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setZeroX(false);
		rangeRestriction.setZeroY(false);
		rangeRestriction.setRestrictFrame(true);
		rangeRestriction.setExtendTypeX(RangeRestriction.ExtendType.ABSOLUTE);
		rangeRestriction.setExtendMinX(2.0d);
		rangeRestriction.setExtendMaxX(2.0d);
		rangeRestriction.setExtendTypeY(RangeRestriction.ExtendType.RELATIVE);
		rangeRestriction.setExtendMaxY(0.5d);
		//
		setPrimaryAxisSet(chartSettings);
		addSecondaryAxisSet(chartSettings);
		applySettings(chartSettings);
	}

	private void addMassSpectrumFilter(IChartSettings chartSettings) {

		IMassSpectrumFilterSupport massSpectrumFilterSupport = MassSpectrumFilter.getMassSpectrumFilterSupport();
		for(IMassSpectrumFilterSupplier supplier : massSpectrumFilterSupport.getSuppliers()) {
			chartSettings.addMenuEntry(new IChartMenuEntry() {

				@Override
				public String getName() {

					return supplier.getFilterName();
				}

				@Override
				public String getCategory() {

					return ICategories.FILTER;
				}

				@Override
				public void execute(Shell shell, ScrollableChart scrollableChart) {

					if(massSpectrum != null) {
						MassSpectrumFilter.applyFilter(massSpectrum, supplier.getId(), new NullProgressMonitor());
						massSpectrum.setDirty(true);
						update();
					}
				}
			});
		}
	}

	private void addMassSpectrumIdentifier(IChartSettings chartSettings) {

		IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
		for(IMassSpectrumIdentifierSupplier supplier : massSpectrumIdentifierSupport.getSuppliers()) {
			chartSettings.addMenuEntry(new IChartMenuEntry() {

				@Override
				public String getName() {

					return supplier.getIdentifierName();
				}

				@Override
				public String getCategory() {

					return ICategories.IDENTIFIER;
				}

				@Override
				public Image getIcon() {

					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM, IApplicationImageProvider.SIZE_16x16);
				}

				@Override
				public void execute(Shell shell, ScrollableChart scrollableChart) {

					if(massSpectrum != null) {
						MassSpectrumIdentifier.identify(massSpectrum, supplier.getId(), new NullProgressMonitor());
						update();
					}
				}
			});
		}
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("m/z");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0.0##"), new DecimalFormatSymbols(Locale.ENGLISH)));
		if(PreferencesSupport.isDarkTheme()) {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}
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

	private void addSecondaryAxisSet(IChartSettings chartSettings) {

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

	private ISeriesData getMassSpectrum(IScanMSD massSpectrum) {

		List<IIon> ions = massSpectrum.getIons();
		int size = ions.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		//
		for(int i = 0; i < size; i++) {
			IIon ion = ions.get(i);
			xSeries[i] = ion.getIon();
			ySeries[i] = ion.getAbundance();
		}
		//
		return new SeriesData(xSeries, ySeries, "Mass Spectrum");
	}

	private LineSeriesData getPeaks(IVendorStandaloneMassSpectrum massSpectrum) {

		ISeriesData peakSeriesData = createPeakSeries("Peaks", massSpectrum, 0, 0);
		LineSeriesData peakSeries = new LineSeriesData(peakSeriesData);
		ILineSeriesSettings lineSeriesSettings = peakSeries.getLineSeriesSettings();
		lineSeriesSettings.setEnableArea(false);
		lineSeriesSettings.setLineStyle(LineStyle.NONE);
		lineSeriesSettings.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
		lineSeriesSettings.setSymbolSize(5);
		if(Display.isSystemDarkTheme()) {
			lineSeriesSettings.setLineColor(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			lineSeriesSettings.setSymbolColor(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		} else {
			lineSeriesSettings.setLineColor(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			lineSeriesSettings.setSymbolColor(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		}
		return peakSeries;
	}

	public static ISeriesData createPeakSeries(String id, IVendorStandaloneMassSpectrum massSpectrum, double yOffset, double xOffset) {

		List<IMassSpectrumPeak> peaks = massSpectrum.getPeaks();
		int size = peaks.size();
		double[] xSeries = new double[size];
		double[] ySeries = new double[size];
		int index = 0;
		for(IMassSpectrumPeak peak : peaks) {
			Optional<IIon> nearestIon = massSpectrum.getIons().stream() //
					.min(Comparator.comparingDouble(i -> Math.abs(i.getIon() - peak.getIon())));
			if(nearestIon.isPresent()) {
				xSeries[index] = nearestIon.get().getIon() + xOffset;
				ySeries[index] = nearestIon.get().getAbundance() + yOffset;
			} else {
				xSeries[index] = Double.NaN;
				ySeries[index] = Double.NaN;
			}
			index++;
		}
		return new SeriesData(xSeries, ySeries, id);
	}

	private void createAnnotations(IVendorStandaloneMassSpectrum massSpectrum) {

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
}
