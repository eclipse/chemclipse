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

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class SelectedIons_3_Test extends TestCase {

	private IMarkedIons selectedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		selectedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		selectedIons = null;
		super.tearDown();
	}

	public void testContains_1() {

		selectedIons.add(new MarkedIon((int)IIon.TIC_ION));
		assertTrue("contains", selectedIons.getIonsNominal().contains(0));
	}

	public void testContains_2() {

		selectedIons.add(new MarkedIon((int)IIon.TIC_ION));
		assertTrue("contains", selectedIons.getIonsNominal().contains(AbstractIon.getIon(0.0f)));
	}
}
