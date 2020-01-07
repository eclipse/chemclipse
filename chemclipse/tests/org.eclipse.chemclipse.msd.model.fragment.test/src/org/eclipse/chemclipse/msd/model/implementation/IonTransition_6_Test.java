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

public class IonTransition_6_Test extends TestCase {

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
		assertEquals(121, ionTransition.getQ1Ion());
		assertEquals(88.0d, ionTransition.getQ3Ion());
	}

	public void test_2() {

		ionTransition = new IonTransition(120.1d, 120.9d, 87.2d, 87.7d, 7.0d, 1.2, 1.3, 1);
		assertEquals(121, ionTransition.getQ1Ion());
		assertEquals(87.5d, ionTransition.getQ3Ion());
	}

	public void test_3() {

		ionTransition = new IonTransition(119.9d, 120.9d, 87.9d, 88.2d, 7.0d, 1.2, 1.3, 1);
		assertEquals(120, ionTransition.getQ1Ion());
		assertEquals(88.1d, ionTransition.getQ3Ion());
	}
}
