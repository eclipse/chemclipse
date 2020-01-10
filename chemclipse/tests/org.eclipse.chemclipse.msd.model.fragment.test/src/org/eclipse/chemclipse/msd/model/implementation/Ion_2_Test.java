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
package org.eclipse.chemclipse.msd.model.implementation;

import junit.framework.TestCase;

/**
 * Abundance and ion test.
 * 
 * @author eselmeister
 */
public class Ion_2_Test extends TestCase {

	private Ion ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion = new Ion(2.2f, 4527.3f);
	}

	@Override
	protected void tearDown() throws Exception {

		ion = null;
		super.tearDown();
	}

	public void testGetAbundance() {

		assertEquals("getAbundance", 4527.3f, ion.getAbundance());
	}

	public void testGetIon() {

		assertEquals("getIon", 2.200000047683716d, ion.getIon());
	}
}
