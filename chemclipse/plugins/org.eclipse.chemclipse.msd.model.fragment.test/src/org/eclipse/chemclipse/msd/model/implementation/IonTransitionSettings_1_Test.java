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

import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;

import junit.framework.TestCase;

public class IonTransitionSettings_1_Test extends TestCase {

	private IIonTransitionSettings ionTransitionSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ionTransitionSettings = new IonTransitionSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGet_1() {

		assertEquals(0, ionTransitionSettings.size());
	}

	public void testGet_2() {

		assertNull(ionTransitionSettings.get(0));
	}

	public void testGet_3() {

		assertNotNull(ionTransitionSettings.getIonTransitionGroups());
	}
}
