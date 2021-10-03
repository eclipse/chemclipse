/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.geometry;

import static org.junit.Assert.assertNotEquals;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

import junit.framework.TestCase;

public class Slope_2_Test extends TestCase {

	private ISlope slope1, slope2;
	private IPoint p1, p2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		p1 = new Point(5.0d, 10.0d);
		p2 = new Point(7.0d, 3.0d);
		slope1 = new Slope(p1, p2);
		slope2 = new Slope(p1, p2);
	}

	@Override
	protected void tearDown() throws Exception {

		p1 = null;
		p2 = null;
		slope1 = null;
		slope2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertEquals("equals", slope1, slope2);
	}

	public void testEquals_2() {

		assertEquals("equals", slope2, slope1);
	}

	public void testEquals_3() {

		assertNotNull("equals", slope1);
	}

	public void testEquals_4() {

		assertNotEquals("equals", slope1, new Object());
	}

	public void hashCode_1() {

		assertEquals("hashCode", slope1.hashCode(), slope2.hashCode());
	}
}
