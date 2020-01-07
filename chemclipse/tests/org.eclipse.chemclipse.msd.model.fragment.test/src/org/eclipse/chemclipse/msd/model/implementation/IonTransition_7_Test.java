/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import junit.framework.TestCase;

import org.eclipse.chemclipse.msd.model.core.IIonTransition;

public class IonTransition_7_Test extends TestCase {

	private IIonTransition ionTransition;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test_1() {

		ionTransition = new IonTransition(120.2d, 121.3d, 87.4d, 88.5d, 7.0d, 1.2, 1.3, 1);
		assertEquals(1.09d, ionTransition.getDeltaQ1Ion(), 0.01d);
		assertEquals(1.09d, ionTransition.getDeltaQ3Ion(), 0.01d);
	}

	public void test_2() {

		ionTransition = new IonTransition(120.1d, 120.9d, 87.2d, 87.7d, 7.0d, 1.2, 1.3, 1);
		assertEquals(0.8d, ionTransition.getDeltaQ1Ion(), 0.01d);
		assertEquals(0.5d, ionTransition.getDeltaQ3Ion(), 0.01d);
	}

	public void test_3() {

		ionTransition = new IonTransition(119.9d, 120.9d, 87.9d, 88.2d, 7.0d, 1.2, 1.3, 1);
		assertEquals(1.0d, ionTransition.getDeltaQ1Ion(), 0.01d);
		assertEquals(0.29d, ionTransition.getDeltaQ3Ion(), 0.01d);
	}

	public void test_4() {

		ionTransition = new IonTransition(119.9d, 119.9d, 87.9d, 87.9d, 7.0d, 1.2, 1.3, 1);
		assertEquals(0.0d, ionTransition.getDeltaQ1Ion());
		assertEquals(0.0d, ionTransition.getDeltaQ3Ion());
	}

	public void test_5() {

		ionTransition = new IonTransition(0.0d, 0.0d, 0.0d, 0.0d, 7.0d, 1.2, 1.3, 1);
		assertEquals(0.0d, ionTransition.getDeltaQ1Ion());
		assertEquals(0.0d, ionTransition.getDeltaQ3Ion());
	}
}
