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
package org.eclipse.chemclipse.model.instruments;

import junit.framework.TestCase;

public class Instrument_5_Test extends TestCase {

	private Instrument instrument;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		instrument = new Instrument("", "", "");
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("", instrument.getIdentifier());
	}

	public void test2() {

		assertEquals("", instrument.getName());
	}

	public void test3() {

		assertEquals("", instrument.getDescription());
	}
}
