/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class SampleQuantExportPDF {

	private static final Logger logger = Logger.getLogger(SampleQuantExportPDF.class);
	/*
	 * A4 PORTRAIT = 595.0 x 842.0 pt
	 * A4 LANDSCAPE = 842.0 x 595.0 pt
	 * 0,0 is lower left!
	 */
	private static final float L_1_MM = 2.8346f;
	private static final float L_0_5_MM = 0.5f * L_1_MM;
	private static final float L_1_4_MM = 1.4f * L_1_MM;
	private static final float L_15_MM = 15 * L_1_MM;
	private static final float L_20_MM = 20 * L_1_MM;
	private static final float L_200_MM = 200 * L_1_MM;
	private static final float L_297_MM = 297 * L_1_MM;
	//
	private static final int RESULTS_PER_PAGE = 26;
	//
	private PDFont font = PDType1Font.HELVETICA;
	private PDFont fontBold = PDType1Font.HELVETICA_BOLD;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public void write(File file, ISampleQuantReport sampleQuantReport, IProgressMonitor monitor) throws Exception {

		PDDocument document = null;
		try {
			document = new PDDocument();
			/*
			 * Limit the amount of results per page.
			 * Otherwise, the table creates a page overflow.
			 */
			List<ISampleQuantSubstance> sampleQuantSubstances = getSampleQuantSubstancesToPrint(sampleQuantReport);
			int pages = (int)Math.ceil((double)sampleQuantSubstances.size() / RESULTS_PER_PAGE);
			int page = 1;
			//
			int start = 0;
			int stop = RESULTS_PER_PAGE;
			while(page <= pages) {
				/*
				 * Get a chunk of x results per page.
				 */
				List<ISampleQuantSubstance> sampleQuantSubstancesChunk = getSampleQuantSubstancesChunk(sampleQuantSubstances, start, stop);
				createPage(document, sampleQuantSubstancesChunk, page++, pages, monitor);
				start += RESULTS_PER_PAGE;
				stop += RESULTS_PER_PAGE;
			}
			document.save(file);
		} catch(IOException e) {
			logger.warn(e);
		} finally {
			/*
			 * Close the doc.
			 */
			if(document != null) {
				document.close();
			}
		}
	}

	private List<ISampleQuantSubstance> getSampleQuantSubstancesToPrint(ISampleQuantReport sampleQuantReport) {

		List<ISampleQuantSubstance> sampleQuantSubstances = new ArrayList<ISampleQuantSubstance>();
		for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantReport.getSampleQuantSubstances()) {
			if(sampleQuantSubstance.getType().equals(ISampleQuantSubstance.TYPE_SAMPLE)) {
				sampleQuantSubstances.add(sampleQuantSubstance);
			}
		}
		//
		return sampleQuantSubstances;
	}

	/**
	 * Returns the chunk from start to stop.
	 * start - inclusive
	 * stop - exclusive
	 * 
	 * @param sampleQuantSubstances
	 * @param start
	 * @param stop
	 * @return List<ISampleQuantSubstance>
	 */
	private List<ISampleQuantSubstance> getSampleQuantSubstancesChunk(List<ISampleQuantSubstance> sampleQuantSubstances, int start, int stop) {

		List<ISampleQuantSubstance> sampleQuantSubstancesChunk = new ArrayList<ISampleQuantSubstance>();
		/*
		 * Stop is used exclusively.
		 * => index < stop
		 */
		if(stop > sampleQuantSubstances.size()) {
			stop = sampleQuantSubstances.size();
		}
		//
		for(int index = start; index < stop; index++) {
			sampleQuantSubstancesChunk.add(sampleQuantSubstances.get(index));
		}
		//
		return sampleQuantSubstancesChunk;
	}

	private PDPage createPage(PDDocument document, List<ISampleQuantSubstance> sampleQuantSubstances, int page, int pages, IProgressMonitor monitor) throws IOException {

		PDPage pdPage = new PDPage(PDRectangle.A4);
		document.addPage(pdPage);
		//
		PDPageContentStream contentStream = new PDPageContentStream(document, pdPage);
		contentStream.setFont(font, 12);
		float xPosition = L_20_MM;
		float yPosition = 0;
		/*
		 * The method calls could be optimized
		 * Print 28 result per page only.
		 */
		yPosition = printTableResults(contentStream, sampleQuantSubstances, xPosition, yPosition);
		printPageFooter(document, contentStream, page, pages);
		//
		contentStream.close();
		return pdPage;
	}

	private void printPageFooter(PDDocument document, PDPageContentStream contentStream, int page, int pages) throws IOException {

		printText(contentStream, L_15_MM, getPositionFromTop(285 * L_1_MM), "Page " + page + "/" + pages); // $NON-NLS-1$
	}

	/**
	 * Prints the results table.
	 * Note, that 28 results shall be printed on each page only.
	 * 
	 * @param contentStream
	 * @param sampleQuantSubstances
	 * @param xPosition
	 * @param yPosition
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private float printTableResults(PDPageContentStream contentStream, List<ISampleQuantSubstance> sampleQuantSubstances, float xPosition, float yPosition) throws IOException {

		/*
		 * L_200_MM - L_20_MM
		 * ~520
		 */
		yPosition += L_15_MM;
		float yStartPosition = yPosition;
		/*
		 * Headline
		 */
		List<PdfTableCell> cellsMaster = new ArrayList<PdfTableCell>();
		cellsMaster.add(new PdfTableCell("Name", 190.0f));
		cellsMaster.add(new PdfTableCell("CAS#", 90.0f));
		cellsMaster.add(new PdfTableCell("Conc.", 55.0f));
		cellsMaster.add(new PdfTableCell("Unit", 55.0f));
		cellsMaster.add(new PdfTableCell("MQ", 55.0f));
		cellsMaster.add(new PdfTableCell("OK", 75.0f));
		yPosition = printTableLine(contentStream, xPosition, yPosition, cellsMaster, Color.GRAY, true, true);
		/*
		 * Data
		 */
		List<PdfTableCell> cells;
		int i = 1;
		for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantSubstances) {
			cells = new ArrayList<PdfTableCell>();
			cells.add(new PdfTableCell(sampleQuantSubstance.getName(), 190.0f));
			cells.add(new PdfTableCell(sampleQuantSubstance.getCasNumber(), 90.0f));
			cells.add(new PdfTableCell(decimalFormat.format(sampleQuantSubstance.getConcentration()), 55.0f));
			cells.add(new PdfTableCell(sampleQuantSubstance.getUnit().replace("µ", "u"), 55.0f));
			cells.add(new PdfTableCell(decimalFormat.format(sampleQuantSubstance.getMatchQuality()), 55.0f));
			cells.add(new PdfTableCell((sampleQuantSubstance.isValidated()) ? "+" : "-", 75.0f));
			if(i % 2 == 0) {
				yPosition = printTableLine(contentStream, xPosition, yPosition, cells, Color.LIGHT_GRAY, false, true);
			} else {
				yPosition = printTableLine(contentStream, xPosition, yPosition, cells, null, false, true);
			}
			i++;
		}
		/*
		 * Print last line.
		 */
		float top = getPositionFromTop(yPosition * L_1_MM);
		contentStream.drawLine(xPosition, top, L_200_MM, top);
		/*
		 * Print vertical lines.
		 */
		float yStart = getPositionFromTop(yStartPosition * L_1_MM);
		float yStartExtraSpace = getPositionFromTop((yStartPosition + L_1_4_MM + L_0_5_MM) * L_1_MM);
		float yStop = getPositionFromTop(yPosition * L_1_MM);
		float left = xPosition;
		for(PdfTableCell cell : cellsMaster) {
			if(cell.isPrintLeftLine()) {
				contentStream.drawLine(left, yStart, left, yStop);
			} else {
				contentStream.drawLine(left, yStartExtraSpace, left, yStop);
			}
			left += cell.getWidth();
		}
		contentStream.drawLine(L_200_MM, yStart, L_200_MM, yStop);
		return yPosition;
	}

	/**
	 * Print a table line.
	 * If color == null, no background will be printed.
	 * 
	 * @param contentStream
	 * @param xPosition
	 * @param yPosition
	 * @param cells
	 * @param color
	 * @param bold
	 * @return float
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private float printTableLine(PDPageContentStream contentStream, float xPosition, float yPosition, List<PdfTableCell> cells, Color color, boolean bold, boolean drawTopLine) throws IOException {

		float left = xPosition;
		float top = getPositionFromTop(yPosition * L_1_MM);
		/*
		 * Draw a colored background.
		 */
		if(color != null) {
			contentStream.setNonStrokingColor(color);
			float heightx = L_1_4_MM + L_0_5_MM;
			float topx = getPositionFromTop((yPosition + heightx) * L_1_MM);
			float widthx = L_200_MM - xPosition;
			contentStream.fillRect(xPosition, topx, widthx, heightx * L_1_MM);
		}
		contentStream.setNonStrokingColor(Color.BLACK);
		/*
		 * Draw line at bottom?
		 */
		if(drawTopLine) {
			contentStream.setStrokingColor(Color.BLACK);
		} else {
			contentStream.setStrokingColor(color);
		}
		contentStream.drawLine(xPosition, top, L_200_MM, top);
		yPosition += L_1_4_MM;
		/*
		 * Print the text
		 */
		top = getPositionFromTop(yPosition * L_1_MM);
		for(PdfTableCell cell : cells) {
			if(bold) {
				contentStream.setFont(fontBold, 12);
			}
			printText(contentStream, left, top, " " + cell.getText()); // $NON-NLS-1$
			left += cell.getWidth();
		}
		/*
		 * Add space if there is a top line.
		 */
		yPosition += L_0_5_MM;
		/*
		 * Set normal font
		 */
		contentStream.setFont(font, 12);
		return yPosition;
	}

	/**
	 * Print test.
	 * 
	 * @param contentStream
	 * @param xPosition
	 * @param yPosition
	 * @param text
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private void printText(PDPageContentStream contentStream, float xPosition, float yPosition, String text) throws IOException {

		contentStream.beginText();
		contentStream.moveTextPositionByAmount(xPosition, yPosition);
		contentStream.drawString(text);
		contentStream.endText();
	}

	private float getPositionFromTop(float x) {

		return L_297_MM - x;
	}
}
