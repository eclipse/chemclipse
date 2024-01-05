/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.literature;

import junit.framework.TestCase;

public class LiteratureSupport_3_Test extends TestCase {

	public void test1a() {

		assertEquals("https://doi.org/10.1186/1471-2105-11-405", LiteratureSupport.getContainedLink("UR  - https://doi.org/10.1186/1471-2105-11-405\n"));
	}

	public void test1b() {

		assertEquals("https://doi.org/10.1186/1471-2105-11-405", LiteratureSupport.getContainedLink("UR  - https://doi.org/10.1186/1471-2105-11-405"));
	}

	public void test2a() {

		assertEquals("http://doi.org/10.1186/1471-2105-11-405", LiteratureSupport.getContainedLink("UR  - http://doi.org/10.1186/1471-2105-11-405\n"));
	}

	public void test2b() {

		assertEquals("http://doi.org/10.1186/1471-2105-11-405", LiteratureSupport.getContainedLink("UR  - http://doi.org/10.1186/1471-2105-11-405"));
	}

	public void test3a() {

		assertEquals("https://dx.doi.org/10.1002/ffj.3311", LiteratureSupport.getContainedLink("UR  - https://dx.doi.org/10.1002/ffj.3311\n"));
	}

	public void test3b() {

		assertEquals("https://dx.doi.org/10.1002/ffj.3311", LiteratureSupport.getContainedLink("UR  - https://dx.doi.org/10.1002/ffj.3311"));
	}

	public void test4a() {

		assertEquals("https://doi.org/10.1002/rcm.9294", LiteratureSupport.getContainedLink("UR  - https://doi.org/10.1002/rcm.9294\n"));
	}

	public void test4b() {

		assertEquals("https://doi.org/10.1002/rcm.9294", LiteratureSupport.getContainedLink("UR  - https://doi.org/10.1002/rcm.9294"));
	}

	public void test5a() {

		assertEquals("https://www.eclipse.org", LiteratureSupport.getContainedLink("https://www.eclipse.org\n"));
	}

	public void test5b() {

		assertEquals("https://www.eclipse.org", LiteratureSupport.getContainedLink("https://www.eclipse.org"));
	}
}