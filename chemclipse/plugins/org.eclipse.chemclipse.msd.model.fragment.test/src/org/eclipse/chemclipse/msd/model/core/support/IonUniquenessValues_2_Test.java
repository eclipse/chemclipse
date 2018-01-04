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
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.msd.model.core.support.IIonUniquenessValues;
import org.eclipse.chemclipse.msd.model.core.support.IonUniquenessValues;

import junit.framework.TestCase;

public class IonUniquenessValues_2_Test extends TestCase {

	private IIonUniquenessValues uniquenessValues;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		uniquenessValues = new IonUniquenessValues();
	}

	@Override
	protected void tearDown() throws Exception {

		uniquenessValues = null;
		super.tearDown();
	}

	public void testAdd_1() {

		uniquenessValues.add(43, -0.1f);
		float uv = uniquenessValues.getUniquenessValue(43);
		assertEquals("Uniqueness Value", 1.0f, uv);
		float pv = uniquenessValues.getPropabilityValue(43);
		assertEquals("Probability Value", 0.0f, pv);
	}

	public void testAdd_2() {

		uniquenessValues.add(43, 1.1f);
		float uv = uniquenessValues.getUniquenessValue(43);
		assertEquals("Uniqueness Value", 1.0f, uv);
		float pv = uniquenessValues.getPropabilityValue(43);
		assertEquals("Probability Value", 0.0f, pv);
	}

	public void testAdd_3() {

		uniquenessValues.add(150, 0.1f);
		float uv = uniquenessValues.getUniquenessValue(150);
		assertEquals("Uniqueness Value", 0.9f, uv);
		float pv = uniquenessValues.getPropabilityValue(150);
		assertEquals("Probability Value", 0.1f, pv);
	}

	public void testRemove_1() {

		uniquenessValues.remove(56);
	}
}
