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

public class IonUniquenessValues_1_Test extends TestCase {

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

		uniquenessValues.add(43, 0.26f);
		// 1.0 - 0.26
		float uv = uniquenessValues.getUniquenessValue(43);
		assertEquals("Uniqueness Value", 0.74f, uv);
		float pv = uniquenessValues.getPropabilityValue(43);
		assertEquals("Probability Value", 0.26f, pv);
	}

	public void testRemove_1() {

		uniquenessValues.add(43, 0.26f);
		uniquenessValues.remove(43);
		float uv = uniquenessValues.getUniquenessValue(43);
		assertEquals("Uniqueness Value", 1.0f, uv);
		float pv = uniquenessValues.getPropabilityValue(43);
		assertEquals("Probability Value", 0.0f, pv);
	}

	public void testGetUniquenessValue_1() {

		float uv = uniquenessValues.getUniquenessValue(43);
		assertEquals("Uniqueness Value", 1.0f, uv);
	}

	public void testGetPropabilityValue_1() {

		float pv = uniquenessValues.getPropabilityValue(43);
		assertEquals("Probability Value", 0.0f, pv);
	}
}
