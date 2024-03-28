/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import static org.junit.Assert.assertThrows;

import junit.framework.TestCase;

/**
 * HashCode test.
 * 
 * @author eselmeister
 */
public class Ion_10_Test extends TestCase {

	private Ion ion1;
	private Ion ion2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion1 = new Ion(5.2f, 4746.3f);
	}

	@Override
	protected void tearDown() throws Exception {

		ion1 = null;
		ion2 = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		ion2 = new Ion(ion1);
		assertTrue("hashCode", ion1.equals(ion2));
	}

	public void testConstructor_2() {

		assertThrows(IllegalArgumentException.class, () -> ion2 = new Ion(null));
	}
}
