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
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IBackgroundAbundanceRange;

import junit.framework.TestCase;

/**
 * Tests the class BackgroundAbundanceRange concerning equals, hashCode and
 * toString.
 * 
 * @author eselmeister
 */
public class BackgroundAbundanceRange_2_Test extends TestCase {

	private IBackgroundAbundanceRange backgroundAbundanceRange1;
	private IBackgroundAbundanceRange backgroundAbundanceRange2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		backgroundAbundanceRange1 = new BackgroundAbundanceRange(0, 5);
		backgroundAbundanceRange2 = new BackgroundAbundanceRange(0, 5);
	}

	@Override
	protected void tearDown() throws Exception {

		backgroundAbundanceRange1 = null;
		backgroundAbundanceRange2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertTrue(backgroundAbundanceRange1.equals(backgroundAbundanceRange2));
	}

	public void testEquals_2() {

		assertTrue(backgroundAbundanceRange2.equals(backgroundAbundanceRange1));
	}

	public void testHashCode_1() {

		assertTrue(backgroundAbundanceRange1.hashCode() == backgroundAbundanceRange2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue(backgroundAbundanceRange2.hashCode() == backgroundAbundanceRange1.hashCode());
	}

	public void testToString_1() {

		assertTrue(backgroundAbundanceRange1.toString().equals(backgroundAbundanceRange2.toString()));
	}

	public void testToString_2() {

		assertTrue(backgroundAbundanceRange2.toString().equals(backgroundAbundanceRange1.toString()));
	}
}
