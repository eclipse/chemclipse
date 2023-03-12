/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

/**
 * Constructor test.
 * 
 * @author eselmeister
 */
public class Ion_7_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstructor_1() {

		try {
			new Ion(5.5f);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_2() {

		try {
			new Ion(-0.1f, 2593.5f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(false);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_3() {

		try {
			new Ion(-0.1f);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_4() {

		try {
			new Ion(1.0f, -0.1f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(true);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}
}
