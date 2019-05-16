/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import junit.framework.TestCase;

public class ResponseSignals_1_Test extends TestCase {

	IResponseSignals responseSignals = new ResponseSignals();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(0.0d, responseSignals.getMinResponseValue());
	}

	public void test2() {

		assertEquals(0.0d, responseSignals.getMaxResponseValue());
	}

	public void test3() {

		assertEquals(0.0d, responseSignals.getMinResponseValue(103));
	}

	public void test4() {

		assertEquals(0.0d, responseSignals.getMaxResponseValue(103));
	}

	public void test5() {

		assertEquals(0.0d, responseSignals.getMinResponseValue(104));
	}

	public void test6() {

		assertEquals(0.0d, responseSignals.getMaxResponseValue(104));
	}
}
