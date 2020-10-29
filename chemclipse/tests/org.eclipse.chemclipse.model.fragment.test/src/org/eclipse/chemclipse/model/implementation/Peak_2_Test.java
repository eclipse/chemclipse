/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IPeak;

import junit.framework.TestCase;

public class Peak_2_Test extends TestCase {

	private IPeak peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peak = new Peak();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(false, peak.isMarkedAsDeleted());
	}

	public void test2() {

		peak.setMarkedAsDeleted(true);
		assertEquals(true, peak.isMarkedAsDeleted());
	}

	public void test3() {

		peak.setMarkedAsDeleted(false);
		assertEquals(false, peak.isMarkedAsDeleted());
	}
}
