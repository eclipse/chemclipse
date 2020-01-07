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
 * HashCode test.
 * 
 * @author eselmeister
 */
public class Ion_8_Test extends TestCase {

	private Ion ion1;
	private Ion ion2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion1 = new Ion(5.2f, 4756.3f);
		ion2 = new Ion(5.2f, 4756.3f);
	}

	@Override
	protected void tearDown() throws Exception {

		ion1 = null;
		ion2 = null;
		super.tearDown();
	}

	public void testHashCode_1() {

		assertEquals("hashCode", ion1.hashCode(), ion2.hashCode());
	}

	public void testHashCode_2() {

		assertEquals("hashCode", ion2.hashCode(), ion1.hashCode());
	}
}
