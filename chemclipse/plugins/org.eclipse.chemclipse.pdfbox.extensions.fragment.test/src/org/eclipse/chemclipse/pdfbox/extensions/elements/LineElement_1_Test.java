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

import junit.framework.TestCase;

public class LineElement_1_Test extends TestCase {

	private LineElement element;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		element = new LineElement(10.0f, 20.0f, 30.0f, 40.0f);
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

		assertEquals(30.0f, element.getX1());
	}

	public void test3a() {

		element.setX1(35.0f);
		assertEquals(35.0f, element.getX1());
	}

	public void test4() {

		assertEquals(1.0f, element.getLineWidth());
	}

	public void test4a() {

		element.setLineWidth(0.2f);
		assertEquals(0.2f, element.getLineWidth());
	}

	public void test5() {

		assertEquals(Color.BLACK, element.getColor());
	}

	public void test5a() {

		element.setColor(Color.CYAN);
		assertEquals(Color.CYAN, element.getColor());
	}

	public void test6() {

		assertEquals(40.0f, element.getY1());
	}

	public void test6a() {

		element.setY1(45.0f);
		assertEquals(45.0f, element.getY1());
	}
}
