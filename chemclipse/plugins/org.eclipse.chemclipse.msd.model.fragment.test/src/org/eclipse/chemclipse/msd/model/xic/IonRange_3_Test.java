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

import junit.framework.TestCase;

/**
 * Tests the class IonRange concerning equals, hashCode and toString.
 * 
 * @author eselmeister
 */
public class IonRange_3_Test extends TestCase {

	private IIonRange ionRange1;
	private IIonRange ionRange2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ionRange1 = new IonRange(3, 5);
		ionRange2 = new IonRange(1, 5);
	}

	@Override
	protected void tearDown() throws Exception {

		ionRange1 = null;
		ionRange2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse(ionRange1.equals(ionRange2));
	}

	public void testEquals_2() {

		assertFalse(ionRange2.equals(ionRange1));
	}

	public void testHashCode_1() {

		assertFalse(ionRange1.hashCode() == ionRange2.hashCode());
	}

	public void testHashCode_2() {

		assertFalse(ionRange2.hashCode() == ionRange1.hashCode());
	}

	public void testToString_1() {

		assertFalse(ionRange1.toString().equals(ionRange2.toString()));
	}

	public void testToString_2() {

		assertFalse(ionRange2.toString().equals(ionRange1.toString()));
	}
}
