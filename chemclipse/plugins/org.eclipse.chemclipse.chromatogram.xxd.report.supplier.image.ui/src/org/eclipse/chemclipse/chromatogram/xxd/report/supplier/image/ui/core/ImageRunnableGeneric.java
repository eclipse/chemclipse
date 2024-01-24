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
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings.IChromatogramImageReportSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.export.images.ImageFactory;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;

public class ImageRunnableGeneric implements Runnable {

	private static final Logger logger = Logger.getLogger(ImageRunnableGeneric.class);
	//
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	//
	private File file;
	private IChromatogram<? extends IPeak> chromatogram;
	//
	private IChromatogramImageReportSettings settings;

	public ImageRunnableGeneric(File file, IChromatogram<? extends IPeak> chromatogram, IChromatogramImageReportSettings settings) {

		this.file = file;
		this.chromatogram = chromatogram;
		this.settings = settings;
	}

	@Override
	public void run() {

		if(chromatogram == null) {
			return;
		}
		try {
			ImageFactory<ChromatogramChart> imageFactory = new ImageFactory<>(ChromatogramChart.class, settings.getWidth(), settings.getHeight());
			ChromatogramChart chromatogramChart = imageFactory.getChart();
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			chartSettings.setBackground(Colors.WHITE);
			chartSettings.setBackgroundChart(Colors.WHITE);
			chartSettings.setBackgroundPlotArea(Colors.WHITE);
			RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
			rangeRestriction.setExtendMaxY(0.1d);
			rangeRestriction.setForceZeroMinY(false);
			rangeRestriction.setZeroY(false);
			chromatogramChart.applySettings(chartSettings);
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<>();
			//
			lineSeriesDataList.add(chromatogramChartSupport.getLineSeriesDataChromatogram(chromatogram, chromatogram.getName(), Colors.RED));
			BaseChart baseChart = chromatogramChart.getBaseChart();
			if(settings.isPeaks()) {
				addPeaks(baseChart, lineSeriesDataList);
			}
			if(settings.isScans()) {
				addScans(baseChart, lineSeriesDataList);
			}
			chromatogramChart.addSeriesData(lineSeriesDataList);
			if(!settings.isAppend()) {
				imageFactory.saveImage(file.getAbsolutePath(), settings.getFormat().getConstant());
			} else {
				appendImage(chromatogramChart.getDisplay(), imageFactory, file);
			}
			imageFactory.closeShell();
		} catch(InstantiationException e) {
			logger.warn(e);
		} catch(IllegalAccessException e) {
			logger.warn(e);
		}
	}

	private void appendImage(Display display, ImageFactory<ChromatogramChart> imageFactory, File file) {

		GC gc = null;
		Image temp = null;
		Image target = null;
		Image combinedImage = null;
		try {
			File tempFile = File.createTempFile(file.getName(), settings.getFormat().getExtension());
			imageFactory.saveImage(tempFile.getAbsolutePath(), settings.getFormat().getConstant());
			temp = new Image(display, tempFile.getAbsolutePath());
			target = new Image(display, file.getAbsolutePath());
			int combinedWidth = temp.getBounds().width + target.getBounds().width;
			int combinedHeight = Math.max(temp.getBounds().height, target.getBounds().height);
			combinedImage = new Image(display, combinedWidth, combinedHeight);
			gc = new GC(combinedImage);
			gc.drawImage(temp, 0, 0);
			gc.drawImage(target, temp.getBounds().width, 0);
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[]{combinedImage.getImageData()};
			imageLoader.save(file.getAbsolutePath(), settings.getFormat().getConstant());
		} catch(SWTException e) {
			if(e.getMessage().equals("Unsupported or unrecognized format")) {
				imageFactory.saveImage(file.getAbsolutePath(), settings.getFormat().getConstant());
			}
		} catch(IOException e) {
			logger.warn(e);
		} finally {
			if(gc != null) {
				gc.dispose();
			}
			if(temp != null) {
				temp.dispose();
			}
			if(target != null) {
				target.dispose();
			}
			if(combinedImage != null) {
				combinedImage.dispose();
			}
		}
	}

	private void addPeaks(BaseChart baseChart, List<ILineSeriesData> lineSeriesDataList) {

		List<? extends IPeak> peaks = chromatogram.getPeaks();
		if(!peaks.isEmpty()) {
			Collections.sort(peaks, peakRetentionTimeComparator);
			ILineSeriesData lineSeriesData = peakChartSupport.getPeaks(peaks, true, false, Colors.GRAY, "Peaks");
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
			lineSeriesSettings.setSymbolSize(5);
			lineSeriesSettings.setSymbolColor(Colors.DARK_GRAY);
			lineSeriesDataList.add(lineSeriesData);
			//
			IPlotArea plotArea = baseChart.getPlotArea();
			int indexSeries = lineSeriesDataList.size() - 1;
			PeakLabelMarker peakLabelMarker = new PeakLabelMarker(baseChart, indexSeries, peaks);
			plotArea.addCustomPaintListener(peakLabelMarker);
		}
	}

	private void addScans(BaseChart baseChart, List<ILineSeriesData> lineSeriesDataList) {

		List<IScan> scans = ChromatogramDataSupport.getIdentifiedScans(chromatogram);
		if(!scans.isEmpty()) {
			ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scans, false, "Scans");
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
			lineSeriesSettings.setSymbolSize(5);
			lineSeriesSettings.setSymbolColor(Colors.DARK_GRAY);
			lineSeriesDataList.add(lineSeriesData);
			//
			IPlotArea plotArea = baseChart.getPlotArea();
			int indexSeries = lineSeriesDataList.size() - 1;
			ScanLabelMarker scanLabelMarker = new ScanLabelMarker(baseChart, indexSeries, scans);
			plotArea.addCustomPaintListener(scanLabelMarker);
		}
	}
}
