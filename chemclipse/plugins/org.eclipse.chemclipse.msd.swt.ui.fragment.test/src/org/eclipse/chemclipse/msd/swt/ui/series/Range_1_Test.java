/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.series;

import org.eclipse.swtchart.Range;

import junit.framework.TestCase;

public class Range_1_Test extends TestCase {

	private Range range;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testRange_1() {

		range = new Range(0, 0);
		assertEquals("lower", 0.0d, range.lower);
	}

	public void testRange_2() {

		range = new Range(0, 0);
		assertEquals("upper", 0.0d, range.upper);
	}
}
