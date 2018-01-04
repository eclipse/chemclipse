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

import junit.framework.TestCase;

public class RectangularTrapezium_1_Test extends TestCase {

	private RectangularTrapezium rectangularTrapezium;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		rectangularTrapezium = new RectangularTrapezium(10.0d, 15.0d, 10.0d);
	}

	@Override
	protected void tearDown() throws Exception {

		rectangularTrapezium = null;
		super.tearDown();
	}

	public void testGetArea() {

		assertEquals("Area", 125.0d, rectangularTrapezium.getArea());
	}
}
