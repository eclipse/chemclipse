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
package org.eclipse.chemclipse.numeric.equations;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import junit.framework.TestCase;

/**
 * Checks calculation of width between two points.
 * 
 * @author eselmeister
 */
public class Equations_5_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCalculateWidth_1() {

		IPoint p1 = new Point(1, 4);
		IPoint p2 = new Point(8, 14);
		double width = Equations.calculateWidth(p1, p2);
		assertEquals("Width", 12.206555615733702d, width);
	}

	public void testCalculateWidth_2() {

		IPoint p1 = new Point(0, 0);
		IPoint p2 = new Point(0, 0);
		double width = Equations.calculateWidth(p1, p2);
		assertEquals("Width", 0.0d, width);
	}
}
