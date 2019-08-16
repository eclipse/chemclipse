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
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.Compound;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.Compounds;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.Hit;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.ICompound;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.ICompounds;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.IHit;

import junit.framework.TestCase;

public class Compounds_1_Test extends TestCase {

	private ICompounds compounds;
	private ICompound compound;
	private IHit hit;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		compounds = new Compounds();
		compound = new Compound();
		hit = new Hit();
		hit.setName("Styrene");
		compound.add(hit);
		compounds.add(compound);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals(1, compounds.size());
	}

	public void testSize_2() {

		compound = new Compound();
		hit = new Hit();
		hit.setName("Styrene");
		compound.add(hit);
		compounds.remove(compound);
		assertEquals(0, compounds.size());
	}

	public void testSize_3() {

		compound = new Compound();
		hit = new Hit();
		hit.setName("Styrene");
		compound.add(hit);
		compounds.add(compound);
		assertEquals(2, compounds.size());
	}
}
