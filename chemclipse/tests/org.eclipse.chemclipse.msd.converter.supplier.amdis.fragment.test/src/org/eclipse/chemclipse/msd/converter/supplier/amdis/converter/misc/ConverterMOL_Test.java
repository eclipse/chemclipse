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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.misc;

import junit.framework.TestCase;

public class ConverterMOL_Test extends TestCase {

	public void test1() {

		assertEquals("19906720", ConverterMOL.extractCASNumber(" CAS rn = 19906720, "));
	}

	public void test2() {

		assertEquals("19906-72-0", ConverterMOL.extractCASNumber(" CAS rn = 19906-72-0, "));
	}

	public void test3() {

		assertEquals("", ConverterMOL.extractCASNumber(""));
	}

	public void test4() {

		assertEquals("", ConverterMOL.extractCASNumber(null));
	}

	public void test5() {

		assertEquals("", ConverterMOL.extractCASNumber(" CAS rn = ABC, "));
	}
}