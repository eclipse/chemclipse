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

import static org.junit.Assert.assertNotEquals;

import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * Equals and hashCode test.
 * 
 * @author Philip Wenig
 */
public class SupplierIon_4_Test extends TestCase {

	private IIon ion1;
	private IIon ion2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion1 = new Ion(1.0f, 5726.4f);
		ion2 = new Ion(1.0f, 5716.4f);
	}

	@Override
	protected void tearDown() throws Exception {

		ion1 = null;
		ion2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertNotEquals("equals", ion1, ion2);
	}

	public void testEquals_2() {

		assertNotEquals("equals", ion2, ion1);
	}

	public void testHashCode_1() {

		assertNotEquals("hashCode", ion1.hashCode(), ion2.hashCode());
	}

	public void testHashCode_2() {

		assertNotEquals("hashCode", ion2.hashCode(), ion1.hashCode());
	}
}
