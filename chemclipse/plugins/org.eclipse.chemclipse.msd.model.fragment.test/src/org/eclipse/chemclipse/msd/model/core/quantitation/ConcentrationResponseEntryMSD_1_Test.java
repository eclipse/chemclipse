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
package org.eclipse.chemclipse.msd.model.core.quantitation;

import org.eclipse.chemclipse.model.quantitation.ResponseSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;

import junit.framework.TestCase;

public class ConcentrationResponseEntryMSD_1_Test extends TestCase {

	private IResponseSignal concentrationResponseEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		concentrationResponseEntry = new ResponseSignal(76.2d, 0.7d, 47875);
	}

	@Override
	protected void tearDown() throws Exception {

		concentrationResponseEntry = null;
		super.tearDown();
	}

	public void testGetIon_1() {

		assertEquals(76.2d, concentrationResponseEntry.getSignal());
	}

	public void testGetConcenctration_1() {

		assertEquals(0.7d, concentrationResponseEntry.getConcentration());
	}

	public void testGetResponse_1() {

		assertEquals(47875.0d, concentrationResponseEntry.getResponse());
	}
}
