/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pdfbox.extensions.settings;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import junit.framework.TestCase;

public class PageBaseConverter_2_Test extends TestCase {

	private IPageBaseConverter converter;
	private PDRectangle pdRectangle;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converter = ConverterFactory.getInstance(PageBase.TOP_LEFT);
		pdRectangle = PDRectangle.A4;
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1a() {

		assertEquals(-1.0f, converter.getPositionX(pdRectangle.getWidth(), -1.0f));
	}

	public void test1b() {

		assertEquals(0.0f, converter.getPositionX(pdRectangle.getWidth(), 0.0f));
	}

	public void test1c() {

		assertEquals(1.0f, converter.getPositionX(pdRectangle.getWidth(), 1.0f));
	}

	public void test2a() {

		assertEquals(842.8898f, converter.getPositionY(pdRectangle.getHeight(), -1.0f));
	}

	public void test2b() {

		assertEquals(841.8898f, converter.getPositionY(pdRectangle.getHeight(), 0.0f));
	}

	public void test2c() {

		assertEquals(840.8898f, converter.getPositionY(pdRectangle.getHeight(), 1.0f));
	}
}
