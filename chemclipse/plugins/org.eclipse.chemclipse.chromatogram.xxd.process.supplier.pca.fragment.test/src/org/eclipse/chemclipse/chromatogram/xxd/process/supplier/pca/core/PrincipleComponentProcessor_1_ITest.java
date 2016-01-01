/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.core.runtime.NullProgressMonitor;

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

		List<IDataInputEntry> chromatograms = new ArrayList<IDataInputEntry>();
		File dir = new File("/home/ocbfiles/");
		for(File file : dir.listFiles()) {
			chromatograms.add(new DataInputEntry(file.toString()));
		}
		// Note: Added argument for extraction type
		processor.process(chromatograms, 200, 3, new NullProgressMonitor(), 0);
		assertTrue(true);
	}
}
