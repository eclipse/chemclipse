/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IIonTransition;

import junit.framework.TestCase;

public class IonTransition_1_Test extends TestCase {

	private IIonTransition ionTransition;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ionTransition = new IonTransition(120.2d, 121.3d, 88.5d, 87.4d, 7.0d, 1.2, 1.3, 1);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetFilter1FirstIon_1() {

		assertEquals(120.2d, ionTransition.getQ1StartIon());
	}

	public void testGetFilter1LastIon_1() {

		assertEquals(121.3d, ionTransition.getQ1StopIon());
	}

	public void testGetFilter3FirstIon_1() {

		assertEquals(88.5d, ionTransition.getQ3StartIon());
	}

	public void testGetFilter3LastIon_1() {

		assertEquals(87.4d, ionTransition.getQ3StopIon());
	}

	public void testGetCollisionEnergy_1() {

		assertEquals(7.0d, ionTransition.getCollisionEnergy());
	}

	public void testGetTransitionGroup_1() {

		assertEquals(1, ionTransition.getTransitionGroup());
	}
}
