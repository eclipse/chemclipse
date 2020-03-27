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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.eclipse.chemclipse.pdfbox.extensions.settings.PageBase;
import org.eclipse.chemclipse.pdfbox.extensions.settings.PageSettings;
import org.eclipse.chemclipse.pdfbox.extensions.settings.Unit;

import junit.framework.TestCase;

public class PageUtil_1_Test extends TestCase {

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

	public void test1() {

		assertEquals(0.0f, pageUtilPT.getPositionBaseX(0.0f));
		assertEquals(0.0f, pageUtilMM.getPositionBaseX(0.0f));
	}

	public void test2() {

		assertEquals(0.0f, pageUtilPT.getPositionBaseY(0.0f));
		assertEquals(841.8898f, pageUtilMM.getPositionBaseY(0.0f));
	}

	public void test3() {

		assertEquals(595.2765f, pageUtilPT.getPositionBaseX(595.2765f));
		assertEquals(595.2765f, pageUtilMM.getPositionBaseX(210.0f));
	}

	public void test4() {

		assertEquals(841.89105f, pageUtilPT.getPositionBaseY(841.89105f));
		assertEquals(-0.0012817383f, pageUtilMM.getPositionBaseY(297.0f)); // rounding uncertainty
	}
}
