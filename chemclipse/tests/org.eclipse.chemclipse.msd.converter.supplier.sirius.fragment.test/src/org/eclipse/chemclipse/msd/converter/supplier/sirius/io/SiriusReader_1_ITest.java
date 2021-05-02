/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.sirius.io;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.sirius.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class SiriusReader_1_ITest extends TestCase {

	private SiriusReader reader;
	private File file;
	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new SiriusReader();
		file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_KAEMPFEROL));
		massSpectra = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize() {

		assertEquals(1, massSpectra.size());
	}
}
