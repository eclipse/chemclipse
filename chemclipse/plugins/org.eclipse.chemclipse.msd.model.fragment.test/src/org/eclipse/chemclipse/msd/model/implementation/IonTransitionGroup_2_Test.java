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
import org.eclipse.chemclipse.msd.model.core.IIonTransitionGroup;

import junit.framework.TestCase;

public class IonTransitionGroup_2_Test extends TestCase {

	private IIonTransition ionTransition1;
	private IIonTransition ionTransition2;
	private IIonTransition ionTransition3;
	private IIonTransitionGroup ionTransitionGroup;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ionTransition1 = new IonTransition(120.2d, 121.3d, 88.5d, 87.4d, 7.0d, 1.2, 1.3, 1);
		ionTransition2 = new IonTransition(121.2d, 122.3d, 88.5d, 87.4d, 7.0d, 1.2, 1.3, 1);
		ionTransition3 = new IonTransition(122.2d, 123.3d, 88.5d, 87.4d, 7.0d, 1.2, 1.3, 1);
		//
		ionTransitionGroup = new IonTransitionGroup();
		ionTransitionGroup.add(ionTransition1);
		ionTransitionGroup.add(ionTransition2);
		ionTransitionGroup.add(ionTransition3);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGet_1() {

		assertEquals(3, ionTransitionGroup.size());
	}

	public void testGet_2() {

		assertNotNull(ionTransitionGroup.get(ionTransition1));
	}

	public void testGet_3() {

		assertNotNull(ionTransitionGroup.getIonTransitions());
	}

	public void testGet_4() {

		assertTrue(ionTransition1.equals(ionTransitionGroup.get(ionTransition1)));
	}

	public void testGet_5() {

		assertTrue(ionTransition2.equals(ionTransitionGroup.get(ionTransition2)));
	}

	public void testGet_6() {

		assertTrue(ionTransition3.equals(ionTransitionGroup.get(ionTransition3)));
	}

	public void testGet_7() {

		assertTrue(ionTransition1.equals(ionTransitionGroup.get(0)));
	}

	public void testGet_8() {

		assertTrue(ionTransition2.equals(ionTransitionGroup.get(1)));
	}

	public void testGet_9() {

		assertTrue(ionTransition3.equals(ionTransitionGroup.get(2)));
	}
}
