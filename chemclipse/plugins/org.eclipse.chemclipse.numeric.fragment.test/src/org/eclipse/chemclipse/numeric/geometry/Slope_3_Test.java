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
package org.eclipse.chemclipse.numeric.geometry;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import junit.framework.TestCase;

public class Slope_3_Test extends TestCase {

	private ISlope slope1, slope2;
	private IPoint p1, p2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		p1 = new Point(5.0d, 10.0d);
		p2 = new Point(7.0d, 3.0d);
		slope1 = new Slope(p1, p2);
		p1 = new Point(5.0d, 10.0d);
		p2 = new Point(8.0d, 3.0d);
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

		assertFalse("equals", slope1.equals(slope2));
	}

	public void testEquals_2() {

		assertFalse("equals", slope2.equals(slope1));
	}

	public void testEquals_3() {

		assertFalse("equals", slope1.equals(null));
	}

	public void testEquals_4() {

		assertFalse("equals", slope1.equals("test"));
	}
}
