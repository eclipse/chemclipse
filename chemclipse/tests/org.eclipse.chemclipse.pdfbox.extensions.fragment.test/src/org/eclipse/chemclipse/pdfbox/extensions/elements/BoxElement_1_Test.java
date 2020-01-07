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

import org.eclipse.chemclipse.pdfbox.extensions.settings.ReferenceX;
import org.eclipse.chemclipse.pdfbox.extensions.settings.ReferenceY;

import junit.framework.TestCase;

public class BoxElement_1_Test extends TestCase {

	private BoxElement element;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		element = new BoxElement(10.0f, 20.0f, 30.0f, 40.0f);
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

		assertEquals(40.0f, element.getHeight());
	}

	public void test3a() {

		element.setHeight(45.0f);
		assertEquals(45.0f, element.getHeight());
	}

	public void test4() {

		assertEquals(30.0f, element.getWidth());
	}

	public void test4a() {

		element.setWidth(35.0f);
		assertEquals(35.0f, element.getWidth());
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
}
