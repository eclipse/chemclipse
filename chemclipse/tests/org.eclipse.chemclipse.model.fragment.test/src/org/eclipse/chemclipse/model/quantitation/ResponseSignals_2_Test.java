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

import org.eclipse.chemclipse.model.core.ISignal;

import junit.framework.TestCase;

public class ResponseSignals_2_Test extends TestCase {

	IResponseSignals responseSignals = new ResponseSignals();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		responseSignals.add(new ResponseSignal(ISignal.TOTAL_INTENSITY, 0.5d, 500));
		responseSignals.add(new ResponseSignal(ISignal.TOTAL_INTENSITY, 1.0d, 1000));
		responseSignals.add(new ResponseSignal(ISignal.TOTAL_INTENSITY, 2.5d, 2500));
		responseSignals.add(new ResponseSignal(ISignal.TOTAL_INTENSITY, 5.0d, 5000));
		responseSignals.add(new ResponseSignal(ISignal.TOTAL_INTENSITY, 7.5d, 7500));
		responseSignals.add(new ResponseSignal(ISignal.TOTAL_INTENSITY, 10.0d, 10000));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(500.0d, responseSignals.getMinResponseValue());
	}

	public void test2() {

		assertEquals(10000.0d, responseSignals.getMaxResponseValue());
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
