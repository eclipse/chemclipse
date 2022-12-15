/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

import junit.framework.TestCase;

public class SeparationColumnFactory_1_Test extends TestCase {

	public void test1() {

		assertEquals("DEFAULT", SeparationColumnFactory.getSeparationColumnType(null).name());
	}

	public void test2() {

		assertEquals("DEFAULT", SeparationColumnFactory.getSeparationColumnType("").name());
	}

	public void test3() {

		assertEquals("POLAR", SeparationColumnFactory.getSeparationColumnType("POLAR").name());
	}

	public void test4() {

		assertEquals("SEMI_POLAR", SeparationColumnFactory.getSeparationColumnType("SEMI_POLAR").name());
	}

	public void test5() {

		assertEquals("SEMI_POLAR", SeparationColumnFactory.getSeparationColumnType("SEMIPOLAR").name());
	}

	public void test6() {

		assertEquals("NON_POLAR", SeparationColumnFactory.getSeparationColumnType("NON_POLAR").name());
	}

	public void test7() {

		assertEquals("NON_POLAR", SeparationColumnFactory.getSeparationColumnType("APOLAR").name());
	}
}