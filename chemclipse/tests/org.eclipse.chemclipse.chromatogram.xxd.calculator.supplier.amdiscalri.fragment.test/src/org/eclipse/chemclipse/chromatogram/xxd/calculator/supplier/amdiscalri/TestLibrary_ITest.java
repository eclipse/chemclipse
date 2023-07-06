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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri;

import java.io.File;

import junit.framework.TestCase;

public class TestLibrary_ITest extends TestCase {

	public void test1() {

		File file = new File(PathResolver.getAbsolutePath(PathResolver.ALKANES));
		assertTrue(file.exists());
	}
}