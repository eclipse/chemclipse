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

public class ResponseSignals_3_Test extends TestCase {

	IResponseSignals responseSignals = new ResponseSignals();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * 103
		 */
		responseSignals.add(new ResponseSignal(103, 0.5d, 500));
		responseSignals.add(new ResponseSignal(103, 1.0d, 1000));
		responseSignals.add(new ResponseSignal(103, 2.5d, 2500));
		responseSignals.add(new ResponseSignal(103, 5.0d, 5000));
		responseSignals.add(new ResponseSignal(103, 7.5d, 7500));
		responseSignals.add(new ResponseSignal(103, 10.0d, 10000));
		/*
		 * 104
		 */
		responseSignals.add(new ResponseSignal(104, 0.5d, 50));
		responseSignals.add(new ResponseSignal(104, 1.0d, 100));
		responseSignals.add(new ResponseSignal(104, 2.5d, 250));
		responseSignals.add(new ResponseSignal(104, 5.0d, 500));
		responseSignals.add(new ResponseSignal(104, 7.5d, 750));
		responseSignals.add(new ResponseSignal(104, 10.0d, 1000));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(50.0d, responseSignals.getMinResponseValue());
	}

	public void test2() {

		assertEquals(10000.0d, responseSignals.getMaxResponseValue());
	}

	public void test3() {

		assertEquals(500.0d, responseSignals.getMinResponseValue(103));
	}

	public void test4() {

		assertEquals(10000.0d, responseSignals.getMaxResponseValue(103));
	}

	public void test5() {

		assertEquals(50.0d, responseSignals.getMinResponseValue(104));
	}

	public void test6() {

		assertEquals(1000.0d, responseSignals.getMaxResponseValue(104));
	}
}
