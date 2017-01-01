/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.support;

import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;

import junit.framework.TestCase;

public class Offset_1_Test extends TestCase {

	private IOffset offset;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testOffset_1() {

		offset = new Offset(4.5, 86.6);
		assertEquals("x offset", 4.5, offset.getXOffset());
		assertEquals("y offset", 86.6, offset.getYOffset());
	}

	public void testOffset_2() {

		offset = new Offset(0, 86.6);
		assertEquals("x offset", 0.0, offset.getXOffset());
		assertEquals("y offset", 86.6, offset.getYOffset());
	}

	public void testOffset_3() {

		offset = new Offset(4.5, 0);
		assertEquals("x offset", 4.5, offset.getXOffset());
		assertEquals("y offset", 0.0, offset.getYOffset());
	}

	public void testOffset_4() {

		offset = new Offset(0, 0);
		assertEquals("x offset", 0.0, offset.getXOffset());
		assertEquals("y offset", 0.0, offset.getYOffset());
	}

	public void testOffset_5() {

		offset = new Offset(-1.4, 0);
		assertEquals("x offset", -1.4, offset.getXOffset());
		assertEquals("y offset", 0.0, offset.getYOffset());
	}

	public void testOffset_6() {

		offset = new Offset(0, -58.3);
		assertEquals("x offset", 0.0, offset.getXOffset());
		assertEquals("y offset", -58.3, offset.getYOffset());
	}

	public void testOffset_7() {

		offset = new Offset(-1.4, -58.3);
		assertEquals("x offset", -1.4, offset.getXOffset());
		assertEquals("y offset", -58.3, offset.getYOffset());
	}

	// ---------------------------
	public void testOffset_8() {

		offset = new Offset(0, 0);
		assertEquals("x offset", 0.0, offset.getXOffset());
		assertEquals("y offset", 0.0, offset.getYOffset());
		offset.incrementCurrentXOffset();
		offset.incrementCurrentYOffset();
		assertEquals("x offset", 0.0, offset.getCurrentXOffset());
		assertEquals("y offset", 0.0, offset.getCurrentYOffset());
		offset.decrementCurrentXOffset();
		offset.decrementCurrentYOffset();
		assertEquals("x offset", 0.0, offset.getCurrentXOffset());
		assertEquals("y offset", 0.0, offset.getCurrentYOffset());
	}

	public void testOffset_9() {

		offset = new Offset(0, 4.5);
		assertEquals("x offset", 0.0, offset.getXOffset());
		assertEquals("y offset", 4.5, offset.getYOffset());
		offset.incrementCurrentXOffset();
		offset.incrementCurrentYOffset();
		assertEquals("x offset", 0.0, offset.getCurrentXOffset());
		assertEquals("y offset", 4.5, offset.getCurrentYOffset());
		offset.decrementCurrentXOffset();
		offset.decrementCurrentYOffset();
		assertEquals("x offset", 0.0, offset.getCurrentXOffset());
		assertEquals("y offset", 0.0, offset.getCurrentYOffset());
	}

	public void testOffset_10() {

		offset = new Offset(2.1, 4.5);
		assertEquals("x offset", 2.1, offset.getXOffset());
		assertEquals("y offset", 4.5, offset.getYOffset());
		offset.incrementCurrentXOffset();
		offset.incrementCurrentYOffset();
		assertEquals("x offset", 2.1, offset.getCurrentXOffset());
		assertEquals("y offset", 4.5, offset.getCurrentYOffset());
		offset.decrementCurrentXOffset();
		offset.decrementCurrentYOffset();
		assertEquals("x offset", 0.0, offset.getCurrentXOffset());
		assertEquals("y offset", 0.0, offset.getCurrentYOffset());
	}

	public void testOffset_11() {

		offset = new Offset(-2.1, -4.5);
		assertEquals("x offset", -2.1, offset.getXOffset());
		assertEquals("y offset", -4.5, offset.getYOffset());
		offset.incrementCurrentXOffset();
		offset.incrementCurrentYOffset();
		assertEquals("x offset", -2.1, offset.getCurrentXOffset());
		assertEquals("y offset", -4.5, offset.getCurrentYOffset());
		offset.decrementCurrentXOffset();
		offset.decrementCurrentYOffset();
		assertEquals("x offset", 0.0, offset.getCurrentXOffset());
		assertEquals("y offset", 0.0, offset.getCurrentYOffset());
	}

	// ----------------------------
	public void testOffset_12() {

		offset = new Offset(-2.1, -4.5);
		assertEquals("x offset", -2.1, offset.getXOffset());
		assertEquals("y offset", -4.5, offset.getYOffset());
		offset.incrementCurrentXOffset();
		offset.incrementCurrentYOffset();
		assertEquals("x offset", -2.1, offset.getCurrentXOffset());
		assertEquals("y offset", -4.5, offset.getCurrentYOffset());
		offset.reset();
		assertEquals("x offset", 0.0, offset.getCurrentXOffset());
		assertEquals("y offset", 0.0, offset.getCurrentYOffset());
	}
}
