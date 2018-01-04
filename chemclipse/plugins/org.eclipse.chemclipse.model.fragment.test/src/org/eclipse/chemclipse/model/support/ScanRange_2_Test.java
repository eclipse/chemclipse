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

import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;

import junit.framework.TestCase;

/**
 * Tests the class IonRange concerning equals, hashCode and toString.
 * 
 * @author eselmeister
 */
public class ScanRange_2_Test extends TestCase {

	private IScanRange scanRange1;
	private IScanRange scanRange2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		scanRange1 = new ScanRange(1, 5);
		scanRange2 = new ScanRange(1, 5);
	}

	@Override
	protected void tearDown() throws Exception {

		scanRange1 = null;
		scanRange2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertTrue(scanRange1.equals(scanRange2));
	}

	public void testEquals_2() {

		assertTrue(scanRange2.equals(scanRange1));
	}

	public void testHashCode_1() {

		assertTrue(scanRange1.hashCode() == scanRange2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue(scanRange2.hashCode() == scanRange1.hashCode());
	}

	public void testToString_1() {

		assertTrue(scanRange1.toString().equals(scanRange2.toString()));
	}

	public void testToString_2() {

		assertTrue(scanRange2.toString().equals(scanRange1.toString()));
	}
}
