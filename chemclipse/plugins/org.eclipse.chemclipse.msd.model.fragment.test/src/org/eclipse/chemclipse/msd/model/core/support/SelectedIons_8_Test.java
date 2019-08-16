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

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class SelectedIons_8_Test extends TestCase {

	private IMarkedIons selectedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		selectedIons = new MarkedIons();
		selectedIons.add(new MarkedIon(28.82849943f));
		selectedIons.add(new MarkedIon(28.787f));
		selectedIons.add(new MarkedIon(29));
		selectedIons.add(new MarkedIon(29.267849f));
		selectedIons.add(new MarkedIon(30.96f));
		selectedIons.add(new MarkedIon(31));
	}

	@Override
	protected void tearDown() throws Exception {

		selectedIons = null;
		super.tearDown();
	}

	public void testSize_1() {

		assertEquals(6, selectedIons.size());
	}
}
