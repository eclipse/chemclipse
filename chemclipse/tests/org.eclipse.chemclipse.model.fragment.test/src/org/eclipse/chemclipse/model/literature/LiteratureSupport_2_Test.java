/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.literature;

import junit.framework.TestCase;

public class LiteratureSupport_2_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("https://doi.org/10.1186/1471-2105-11-405", LiteratureSupport.getContainedDOI("UR  - https://doi.org/10.1186/1471-2105-11-405\n"));
	}

	public void test2() {

		assertEquals("http://doi.org/10.1186/1471-2105-11-405", LiteratureSupport.getContainedDOI("UR  - http://doi.org/10.1186/1471-2105-11-405\n"));
	}

	public void test3() {

		assertEquals("https://dx.doi.org/10.1002/ffj.3311", LiteratureSupport.getContainedDOI("UR  - https://dx.doi.org/10.1002/ffj.3311\n"));
	}

	public void test4() {

		assertEquals("http://dx.doi.org/10.1002/ffj.3311", LiteratureSupport.getContainedDOI("UR  - http://dx.doi.org/10.1002/ffj.3311\n"));
	}
}