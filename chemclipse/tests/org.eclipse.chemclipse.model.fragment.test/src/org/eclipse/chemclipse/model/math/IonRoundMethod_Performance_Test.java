/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.math;

import junit.framework.TestCase;

public class IonRoundMethod_Performance_Test extends TestCase {

	public void testActive() {

		executeActive();
		assertTrue(true);
	}

	public void testDefault() {

		execute(IonRoundMethod.DEFAULT);
		assertTrue(true);
	}

	public void testMinus00() {

		execute(IonRoundMethod.MINUS_00);
		assertTrue(true);
	}

	public void testMinus01() {

		execute(IonRoundMethod.MINUS_01);
		assertTrue(true);
	}

	private void execute(IonRoundMethod method) {

		long start = System.currentTimeMillis();
		for(int i = 0; i < 5000000; i++) {
			method.round(Math.random());
		}
		long stop = System.currentTimeMillis();
		System.out.println(method.label() + ": " + (stop - start) + " ms");
	}

	private void executeActive() {

		long start = System.currentTimeMillis();
		for(int i = 0; i < 5000000; i++) {
			IonRoundMethod.getActive().round(Math.random());
		}
		long stop = System.currentTimeMillis();
		System.out.println(IonRoundMethod.getActive().label() + ": " + (stop - start) + " ms");
	}
}
