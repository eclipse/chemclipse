/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pdfbox.extensions.core;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.eclipse.chemclipse.pdfbox.extensions.settings.PageBase;
import org.eclipse.chemclipse.pdfbox.extensions.settings.PageSettings;
import org.eclipse.chemclipse.pdfbox.extensions.settings.Unit;

import junit.framework.TestCase;

public class PageUtil_3_Test extends TestCase {

	private static final String TEXT = "Hallo Welt!";
	private static final PDFont FONT_REGULAR = PDType1Font.HELVETICA;
	private static final float FONT_SIZE_12 = 12.0f;
	//
	private PDDocument document;
	private PageUtil pageUtilPT;
	private PageUtil pageUtilMM;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		document = new PDDocument();
		pageUtilPT = new PageUtil(document, new PageSettings(PDRectangle.A4, PageBase.BOTTOM_LEFT, Unit.PT, false));
		pageUtilMM = new PageUtil(document, new PageSettings(PDRectangle.A4, PageBase.TOP_LEFT, Unit.MM, false));
	}

	@Override
	protected void tearDown() throws Exception {

		document.close();
		super.tearDown();
	}

	public void test1() throws IOException {

		assertEquals(58.008003f, pageUtilPT.calculateTextWidth(FONT_REGULAR, FONT_SIZE_12, TEXT));
		assertEquals(20.463903f, pageUtilMM.calculateTextWidth(FONT_REGULAR, FONT_SIZE_12, TEXT));
	}

	public void test2() throws IOException {

		assertEquals(9.43296f, pageUtilPT.calculateTextHeight(FONT_REGULAR, FONT_SIZE_12));
		assertEquals(3.3277333f, pageUtilMM.calculateTextHeight(FONT_REGULAR, FONT_SIZE_12));
	}
}
