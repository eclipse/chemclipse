/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class ExcludedIons_3_Test extends TestCase {

	private IMarkedIons excludedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.EXCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		excludedIons = null;
		super.tearDown();
	}

	public void testContains_1() {

		excludedIons.add(new MarkedIon((int)IIon.TIC_ION));
		assertTrue("contains", excludedIons.getIonsNominal().contains(0));
	}

	public void testContains_2() {

		excludedIons.add(new MarkedIon((int)IIon.TIC_ION));
		assertTrue("contains", excludedIons.getIonsNominal().contains(0));
	}
}
