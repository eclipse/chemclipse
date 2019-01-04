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
package org.eclipse.chemclipse.xxd.model.quantitation;

import org.eclipse.chemclipse.model.quantitation.ResponseSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;

import junit.framework.TestCase;

public class ConcentrationResponseEntryMSD_3_Test extends TestCase {

	private IResponseSignal concentrationResponseEntry1;
	private IResponseSignal concentrationResponseEntry2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		concentrationResponseEntry1 = new ResponseSignal(76.2d, 0.7d, 47875);
		concentrationResponseEntry2 = new ResponseSignal(75.2d, 0.7d, 47875);
	}

	@Override
	protected void tearDown() throws Exception {

		concentrationResponseEntry1 = null;
		concentrationResponseEntry2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse(concentrationResponseEntry1.equals(concentrationResponseEntry2));
	}

	public void testEquals_2() {

		assertFalse(concentrationResponseEntry2.equals(concentrationResponseEntry1));
	}

	public void testHashCode_1() {

		assertTrue(concentrationResponseEntry1.hashCode() != concentrationResponseEntry2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue(concentrationResponseEntry2.hashCode() != concentrationResponseEntry1.hashCode());
	}

	public void testToString_1() {

		assertTrue(concentrationResponseEntry1.toString() != concentrationResponseEntry2.toString());
	}

	public void testToString_2() {

		assertTrue(concentrationResponseEntry2.toString() != concentrationResponseEntry1.toString());
	}
}
