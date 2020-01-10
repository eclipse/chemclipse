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
package org.eclipse.chemclipse.pdfbox.extensions.elements;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.eclipse.chemclipse.pdfbox.extensions.settings.ReferenceX;
import org.eclipse.chemclipse.pdfbox.extensions.settings.ReferenceY;
import org.eclipse.chemclipse.pdfbox.extensions.settings.TextOption;

import junit.framework.TestCase;

public class CellElement_1_Test extends TestCase {

	private CellElement element;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		element = new CellElement("Hello", 10.0f, CellElement.BORDER_ALL);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(-1.0f, element.getX());
	}

	public void test1a() {

		element.setX(10.0f);
		assertEquals(10.0f, element.getX());
	}

	public void test2() {

		assertEquals(-1.0f, element.getY());
	}

	public void test2a() {

		element.setY(10.0f);
		assertEquals(10.0f, element.getY());
	}

	public void test3() {

		assertEquals(12.0f, element.getFontSize());
	}

	public void test3a() {

		element.setFontSize(14.0f);
		assertEquals(14.0f, element.getFontSize());
	}

	public void test4() {

		assertEquals(PDType1Font.HELVETICA, element.getFont());
	}

	public void test4a() {

		element.setFont(PDType1Font.COURIER);
		assertEquals(PDType1Font.COURIER, element.getFont());
	}

	public void test5() {

		assertEquals(Color.BLACK, element.getColor());
	}

	public void test5a() {

		element.setColor(Color.CYAN);
		assertEquals(Color.CYAN, element.getColor());
	}

	public void test6() {

		assertEquals(ReferenceX.LEFT, element.getReferenceX());
	}

	public void test6a() {

		element.setReferenceX(ReferenceX.RIGHT);
		assertEquals(ReferenceX.RIGHT, element.getReferenceX());
	}

	public void test7() {

		assertEquals(ReferenceY.TOP, element.getReferenceY());
	}

	public void test7a() {

		element.setReferenceY(ReferenceY.BOTTOM);
		assertEquals(ReferenceY.BOTTOM, element.getReferenceY());
	}

	public void test7b() {

		element.setReferenceY(ReferenceY.CENTER);
		assertEquals(ReferenceY.CENTER, element.getReferenceY());
	}

	public void test8() {

		assertEquals(-1.0f, element.getMinHeight());
	}

	public void test8a() {

		element.setMinHeight(10.0f);
		assertEquals(10.0f, element.getMinHeight());
	}

	public void test9() {

		assertEquals("Hello", element.getText());
	}

	public void test9a() {

		element.setText("Hello World");
		assertEquals("Hello World", element.getText());
	}

	public void test10() {

		assertEquals(TextOption.NONE, element.getTextOption());
	}

	public void test10a() {

		element.setTextOption(TextOption.SHORTEN);
		assertEquals(TextOption.SHORTEN, element.getTextOption());
	}

	public void test10b() {

		element.setTextOption(TextOption.MULTI_LINE);
		assertEquals(TextOption.MULTI_LINE, element.getTextOption());
	}

	public void test11() {

		assertTrue(element.isBorderSet());
	}

	public void test11a() {

		element.setBorder(CellElement.BORDER_NONE);
		assertFalse(element.isBorderSet());
	}

	public void test12() {

		assertTrue(element.isBorderSet(CellElement.BORDER_LEFT));
	}

	public void test12a() {

		element.setBorder(CellElement.BORDER_RIGHT | CellElement.BORDER_BOTTOM | CellElement.BORDER_TOP);
		assertFalse(element.isBorderSet(CellElement.BORDER_LEFT));
	}

	public void test13() {

		assertTrue(element.isBorderSet(CellElement.BORDER_RIGHT));
	}

	public void test13a() {

		element.setBorder(CellElement.BORDER_LEFT | CellElement.BORDER_BOTTOM | CellElement.BORDER_TOP);
		assertFalse(element.isBorderSet(CellElement.BORDER_RIGHT));
	}

	public void test14() {

		assertTrue(element.isBorderSet(CellElement.BORDER_TOP));
	}

	public void test14a() {

		element.setBorder(CellElement.BORDER_LEFT | CellElement.BORDER_RIGHT | CellElement.BORDER_BOTTOM);
		assertFalse(element.isBorderSet(CellElement.BORDER_TOP));
	}

	public void test15() {

		assertTrue(element.isBorderSet(CellElement.BORDER_BOTTOM));
	}

	public void test15a() {

		element.setBorder(CellElement.BORDER_LEFT | CellElement.BORDER_RIGHT | CellElement.BORDER_TOP);
		assertFalse(element.isBorderSet(CellElement.BORDER_BOTTOM));
	}

	public void test16() {

		assertEquals(10.0f, element.getMaxWidth());
	}

	public void test16a() {

		element.setMaxWidth(20.0f);
		assertEquals(20.0f, element.getMaxWidth());
	}
}
