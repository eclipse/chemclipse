/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.pdf.io;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.pdf.settings.ReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.pdf.support.ValueScaling;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.targets.TargetSupport;
import org.eclipse.chemclipse.pdfbox.extensions.core.PageUtil;
import org.eclipse.chemclipse.pdfbox.extensions.settings.PageBase;
import org.eclipse.chemclipse.pdfbox.extensions.settings.PageSettings;
import org.eclipse.chemclipse.pdfbox.extensions.settings.Unit;
import org.eclipse.core.runtime.IProgressMonitor;

public class ReportWriter {

	private static final Logger logger = Logger.getLogger(ReportWriter.class);
	private static final DecimalFormat decimalFormat = new DecimalFormat("0.0E0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	//
	private static final Unit PAGE_UNIT = Unit.PT;
	private static final boolean LANDSCAPE = true;

	public void generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, ReportSettings reportSettings, IProgressMonitor monitor) throws IOException {

		if(file.exists() && append) {
			try (PDDocument document = PDDocument.load(file)) {
				addPages(document, file, chromatogram, reportSettings, monitor);
			}
		}
		try (PDDocument document = new PDDocument()) {
			addPages(document, file, chromatogram, reportSettings, monitor);
		}
	}

	private void addPages(PDDocument document, File file, IChromatogram<? extends IPeak> chromatogram, ReportSettings reportSettings, IProgressMonitor monitor) throws IOException {

		int pages = reportSettings.getPages();
		ITotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		int size = totalScanSignalExtractor.getTotalScanSignals().size();
		int chunkSize = (size + pages - 1) / pages;
		monitor.beginTask("Render Chromatogram", size);
		for(int i = 1; i < size; i += chunkSize) {
			ITotalScanSignals scans = totalScanSignalExtractor.getTotalScanSignals(i, Math.min(size, i + chunkSize), false);
			PageUtil pageUtil = new PageUtil(document, new PageSettings(PDRectangle.A4, PageBase.TOP_LEFT, PAGE_UNIT, LANDSCAPE));
			printPageHeader(chromatogram, pageUtil);
			ValueScaling valueScaling = new ValueScaling(chromatogram, pageUtil.getPage(), scans);
			drawChromatogram(pageUtil, chromatogram, scans, valueScaling, monitor);
			drawPeaks(chromatogram, valueScaling, pageUtil);
			pageUtil.close();
		}
		document.save(file);
	}

	private void printPageHeader(IChromatogram<? extends IPeak> chromatogram, PageUtil pageUtil) throws IOException {

		try (PDPageContentStream contentStream = new PDPageContentStream(pageUtil.getDocument(), pageUtil.getPage(), AppendMode.APPEND, false, false);) {
			String name = chromatogram.getDataName().isBlank() ? chromatogram.getFile().getName() : chromatogram.getDataName();
			float textWidth = pageUtil.calculateTextWidth(PDType1Font.HELVETICA, 12, name);
			float textHeight = pageUtil.calculateTextHeight(PDType1Font.HELVETICA, 12);
			float center = (350 + textWidth) / 2;
			contentStream.beginText();
			contentStream.setFont(PDType1Font.HELVETICA, 12);
			contentStream.newLineAtOffset(center, 575 - textHeight);
			contentStream.showText(name);
			contentStream.endText();
		}
	}

	private void drawChromatogram(PageUtil pageUtil, IChromatogram<? extends IPeak> chromatogram, ITotalScanSignals scans, ValueScaling valueScaling, IProgressMonitor monitor) {

		try (PDPageContentStream contentStream = new PDPageContentStream(pageUtil.getDocument(), pageUtil.getPage(), AppendMode.APPEND, false, false)) {
			contentStream.setStrokingColor(1f, 0f, 0f);
			boolean firstPoint = true;
			for(ITotalScanSignal scan : scans.getTotalScanSignals()) {
				int rt = scan.getRetentionTime();
				float abundance = scan.getTotalSignal();
				float x = valueScaling.getX(rt);
				float y = valueScaling.getY(abundance);
				if(firstPoint) {
					contentStream.moveTo(x, y);
					firstPoint = false;
				} else {
					contentStream.lineTo(x, y);
				}
				monitor.worked(1);
			}
			contentStream.stroke();
			drawAxis(contentStream, chromatogram, scans);
		} catch(IOException e) {
			logger.error(e);
		}
	}

	private void drawAxis(PDPageContentStream contentStream, IChromatogram<? extends IPeak> chromatogram, ITotalScanSignals scans) throws IOException {

		contentStream.setStrokingColor(0f, 0f, 0f);
		contentStream.setLineWidth(1f);
		drawXAxis(contentStream);
		drawYAxis(contentStream);
		contentStream.stroke();
		drawAxisLabels(contentStream);
		drawXAxisMarkers(contentStream, scans);
		drawYAxisMarkers(contentStream, chromatogram);
	}

	private void drawXAxis(PDPageContentStream contentStream) throws IOException {

		contentStream.moveTo(50, 50);
		contentStream.lineTo(800, 50);
	}

	private void drawYAxis(PDPageContentStream contentStream) throws IOException {

		contentStream.moveTo(50, 50);
		contentStream.lineTo(50, 550);
	}

	private void drawAxisLabels(PDPageContentStream contentStream) throws IOException {

		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
		contentStream.newLineAtOffset(400, 15);
		contentStream.showText("Time [min]");
		contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, 20, 300));
		contentStream.showText("Intensity");
		contentStream.endText();
	}

	private void drawXAxisMarkers(PDPageContentStream contentStream, ITotalScanSignals scans) throws IOException {

		contentStream.setFont(PDType1Font.HELVETICA, 8);
		double startTime = scans.getFirstTotalScanSignal().getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		double stopTime = scans.getLastTotalScanSignal().getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		float ticks = Math.round(stopTime - startTime);
		for(int i = 0; i <= ticks; i++) {
			float xPosition = 50 + (i * (750 / ticks));
			contentStream.moveTo(xPosition, 50);
			contentStream.lineTo(xPosition, 45);
			contentStream.stroke();
			long rt = Math.round(startTime + i);
			contentStream.beginText();
			float offset = rt < 10 ? 2.5f : 5f;
			contentStream.newLineAtOffset(xPosition - offset, 35);
			contentStream.showText(Long.toString(rt));
			contentStream.endText();
		}
	}

	private void drawYAxisMarkers(PDPageContentStream contentStream, IChromatogram<? extends IPeak> chromatogram) throws IOException {

		contentStream.setFont(PDType1Font.HELVETICA, 8);
		float minY = 50;
		float maxY = 500;
		float logMin = (float)Math.log10(chromatogram.getMinSignal());
		float logMax = (float)Math.log10(chromatogram.getMaxSignal());
		float scaleHeight = maxY - minY;
		float logRange = logMax - logMin;
		int divisions = 4;
		for(int i = 0; i <= divisions; i++) {
			float logValue = logMin + (logRange / divisions) * i;
			float value = (float)Math.pow(10, logValue);
			float y = minY + scaleHeight * (logValue - logMin) / logRange;
			contentStream.moveTo(50, y);
			contentStream.lineTo(45, y);
			contentStream.stroke();
			contentStream.beginText();
			contentStream.newLineAtOffset(20, y - 3);
			contentStream.showText(decimalFormat.format(value));
			contentStream.endText();
		}
	}

	private void drawPeaks(IChromatogram<? extends IPeak> chromatogram, ValueScaling valueScaling, PageUtil pageUtil) throws IOException {

		float textHeight = pageUtil.calculateTextHeight(PDType1Font.HELVETICA, 8);
		try (PDPageContentStream contentStream = new PDPageContentStream(pageUtil.getDocument(), pageUtil.getPage(), AppendMode.APPEND, false, false)) {
			contentStream.beginText();
			contentStream.setFont(PDType1Font.HELVETICA, 8);
			for(IPeak peak : chromatogram.getPeaks()) {
				IPeakModel peakModel = peak.getPeakModel();
				float x = valueScaling.getX(peakModel.getRetentionTimeAtPeakMaximum()) + (textHeight / 2);
				if(x < 50 || x > 800) {
					continue;
				}
				float y = valueScaling.getY(peakModel.getBackgroundAbundance() + peakModel.getPeakAbundance());
				String target = TargetSupport.getBestTargetLibraryField(peak);
				float textWidth = pageUtil.calculateTextWidth(PDType1Font.HELVETICA, 8, target);
				if(y + textWidth > 500) {
					y -= textWidth;
					if(y < 0) {
						y = 50;
					}
				}
				contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, x, y));
				contentStream.showText(target);
			}
			contentStream.endText();
		}
	}
}