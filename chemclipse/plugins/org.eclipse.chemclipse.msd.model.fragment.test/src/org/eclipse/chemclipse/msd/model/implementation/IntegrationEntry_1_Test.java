/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;

import junit.framework.TestCase;

public class IntegrationEntry_1_Test extends TestCase {

	private IIntegrationEntryMSD integrationEntry;

	@Override
	protected void setUp() throws Exception {

		integrationEntry = new IntegrationEntryMSD(AbstractIon.TIC_ION, 2308934.78d);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIon_1() {

		assertEquals(AbstractIon.TIC_ION, integrationEntry.getIon());
	}

	public void testGetIntegratedArea_1() {

		assertEquals(2308934.78d, integrationEntry.getIntegratedArea());
	}
}
