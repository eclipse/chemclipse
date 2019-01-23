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

import org.eclipse.chemclipse.pdfbox.extensions.core.PDTable;

import junit.framework.TestCase;

public class TableElement_1_Test extends TestCase {

	private TableElement element;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		element = new TableElement(10.0f, 20.0f, 30.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(10.0f, element.getX());
	}

	public void test1a() {

		element.setX(15.0f);
		assertEquals(15.0f, element.getX());
	}

	public void test2() {

		assertEquals(20.0f, element.getY());
	}

	public void test2a() {

		element.setY(25.0f);
		assertEquals(25.0f, element.getY());
	}

	public void test3() {

		assertEquals(30.0f, element.getColumnHeight());
	}

	public void test3a() {

		element.setColumnHeight(35.0f);
		assertEquals(35.0f, element.getColumnHeight());
	}

	public void test4() {

		assertEquals(1.0f, element.getLineWidth());
	}

	public void test4a() {

		element.setLineWidth(0.2f);
		assertEquals(0.2f, element.getLineWidth());
	}

	public void test5() {

		assertEquals(Color.GRAY, element.getColorTitle());
	}

	public void test5a() {

		element.setColorTitle(Color.CYAN);
		assertEquals(Color.CYAN, element.getColorTitle());
	}

	public void test6() {

		assertEquals(0.0f, element.getTextOffsetX());
	}

	public void test6a() {

		element.setTextOffsetX(10.0f);
		assertEquals(10.0f, element.getTextOffsetX());
	}

	public void test7() {

		assertEquals(0.0f, element.getTextOffsetY());
	}

	public void test7a() {

		element.setTextOffsetY(10.0f);
		assertEquals(10.0f, element.getTextOffsetY());
	}

	public void test8() {

		assertEquals(Color.LIGHT_GRAY, element.getColorData());
	}

	public void test8a() {

		element.setColorData(Color.LIGHT_GRAY);
		assertEquals(Color.LIGHT_GRAY, element.getColorData());
	}

	public void test9() {

		assertNotNull(element.getPDTable());
	}

	public void test9a() {

		element.setPDTable(new PDTable());
		assertNotNull(element.getPDTable());
	}
}
