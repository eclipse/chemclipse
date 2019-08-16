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
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.support.ITwoPoints;
import org.eclipse.chemclipse.model.support.TwoPoints;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.exceptions.PointIsNullException;
import junit.framework.TestCase;

public class TwoPoints_2_Test extends TestCase {

	private IPoint p1;
	private IPoint p2;
	private ITwoPoints points;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstructor_1() {

		p1 = new Point(5.0d, 10.0d);
		p2 = new Point(7.0d, 3.0d);
		try {
			points = new TwoPoints(p1, p2);
		} catch(PointIsNullException e) {
			assertTrue("Constructor", false);
		}
		assertNotNull(points);
	}

	public void testConstructor_2() {

		p1 = null;
		p2 = new Point(7.0d, 3.0d);
		try {
			points = new TwoPoints(p1, p2);
		} catch(PointIsNullException e) {
			assertTrue("Constructor", true);
		}
	}

	public void testConstructor_3() {

		p1 = new Point(5.0d, 10.0d);
		p2 = null;
		try {
			points = new TwoPoints(p1, p2);
		} catch(PointIsNullException e) {
			assertTrue("Constructor", true);
		}
	}

	public void testConstructor_4() {

		p1 = null;
		p2 = null;
		try {
			points = new TwoPoints(p1, p2);
		} catch(PointIsNullException e) {
			assertTrue("Constructor", true);
		}
	}
}
