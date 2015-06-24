/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class PrincipleComponentProcessor_1_ITest extends TestCase {

	private PrincipleComponentProcessor processor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		processor = new PrincipleComponentProcessor();
	}

	@Override
	protected void tearDown() throws Exception {

		processor = null;
		super.tearDown();
	}

	public void testProcess_1() {

		List<File> chromatograms = new ArrayList<File>();
		File dir = new File("/home/ocbfiles/");
		for(File file : dir.listFiles()) {
			chromatograms.add(file);
		}
		processor.process(chromatograms, 200, 3, new NullProgressMonitor());
		assertTrue(true);
	}
}
