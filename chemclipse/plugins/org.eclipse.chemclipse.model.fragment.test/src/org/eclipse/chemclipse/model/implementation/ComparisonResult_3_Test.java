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

import org.eclipse.chemclipse.model.identifier.ComparisonResult;

import junit.framework.TestCase;

public class ComparisonResult_3_Test extends TestCase {

	private static final float DEFAULT_VALUE = 100.0f;
	private ComparisonResult comparisonResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		comparisonResult = new ComparisonResult(DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE);
		comparisonResult.setPenalty(22.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(78.0f, comparisonResult.getMatchFactor());
	}

	public void test2() {

		assertEquals(78.0f, comparisonResult.getReverseMatchFactor());
	}

	public void test3() {

		assertEquals(78.0f, comparisonResult.getMatchFactorDirect());
	}

	public void test4() {

		assertEquals(78.0f, comparisonResult.getReverseMatchFactorDirect());
	}

	public void test5() {

		assertEquals(DEFAULT_VALUE, comparisonResult.getProbability());
	}

	public void test6() {

		assertEquals(DEFAULT_VALUE, comparisonResult.getMatchFactorNotAdjusted());
	}

	public void test7() {

		assertEquals(DEFAULT_VALUE, comparisonResult.getReverseMatchFactorNotAdjusted());
	}

	public void test8() {

		assertEquals(DEFAULT_VALUE, comparisonResult.getMatchFactorDirectNotAdjusted());
	}

	public void test9() {

		assertEquals(DEFAULT_VALUE, comparisonResult.getReverseMatchFactorDirectNotAdjusted());
	}
}
