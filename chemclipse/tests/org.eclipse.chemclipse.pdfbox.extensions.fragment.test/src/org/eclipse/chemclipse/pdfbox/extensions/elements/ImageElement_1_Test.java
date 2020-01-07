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

import org.eclipse.chemclipse.pdfbox.extensions.settings.ReferenceX;
import org.eclipse.chemclipse.pdfbox.extensions.settings.ReferenceY;

import junit.framework.TestCase;

public class ImageElement_1_Test extends TestCase {

	private ImageElement element;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		element = new ImageElement(10.0f, 20.0f);
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

		assertEquals(0.0f, element.getHeight());
	}

	public void test3a() {

		element.setHeight(30.0f);
		assertEquals(30.0f, element.getHeight());
	}

	public void test4() {

		assertEquals(0.0f, element.getWidth());
	}

	public void test4a() {

		element.setWidth(40.0f);
		assertEquals(40.0f, element.getWidth());
	}

	public void test5() {

		assertNull(element.getImage());
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
