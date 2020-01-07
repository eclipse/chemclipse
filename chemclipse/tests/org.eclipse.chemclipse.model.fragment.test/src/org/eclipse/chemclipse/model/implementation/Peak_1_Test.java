/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IPeak;

import junit.framework.TestCase;

public class Peak_1_Test extends TestCase {

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

		assertEquals(true, peak.isActiveForAnalysis());
	}

	public void test2() {

		peak.setActiveForAnalysis(false);
		assertEquals(false, peak.isActiveForAnalysis());
	}

	public void test3() {

		peak.setActiveForAnalysis(true);
		assertEquals(true, peak.isActiveForAnalysis());
	}
}
