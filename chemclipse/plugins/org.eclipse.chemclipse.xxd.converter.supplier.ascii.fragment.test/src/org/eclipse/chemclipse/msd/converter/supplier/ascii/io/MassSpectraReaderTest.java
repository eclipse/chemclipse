/*******************************************************************************
 * Copyright (c) 2016, 2018 Trig Chen.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Trig Chen - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.ascii.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.junit.Test;

public class MassSpectraReaderTest {

	private static final Logger logger = Logger.getLogger(MassSpectraReaderTest.class);

	@Test
	public void testRead_01() {

		File file = new File("testdata/files/ms01.ascii");
		MassSpectraReader reader = new MassSpectraReader();
		try {
			IMassSpectra ms = reader.read(file, null);
			assertEquals(1, ms.size());
			IScanMSD spec = ms.getMassSpectrum(1);
			assertEquals(1250, spec.getRetentionTime());
			assertEquals(32, spec.getRetentionIndex(), 1e-6);
			assertEquals(27, spec.getIons().size());
			assertEquals(4.7814, spec.getIons().get(0).getIon(), 1e-6);
			assertEquals(8.0, spec.getIons().get(0).getAbundance(), 1e-6);
			assertEquals(4.8659, spec.getIons().get(26).getIon(), 1e-6);
			assertEquals(3.0, spec.getIons().get(26).getAbundance(), 1e-6);
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	@Test
	public void testRead_02() {

		File file = new File("testdata/files/ms02.ascii");
		MassSpectraReader reader = new MassSpectraReader();
		try {
			IMassSpectra ms = reader.read(file, null);
			assertEquals(1, ms.size());
			IScanMSD spec = ms.getMassSpectrum(1);
			assertEquals(0, spec.getRetentionTime());
			assertEquals(2.5, spec.getRetentionIndex(), 1e-6);
			assertEquals(3, spec.getIons().size());
			assertEquals(4.7814, spec.getIons().get(0).getIon(), 1e-6);
			assertEquals(8.0, spec.getIons().get(0).getAbundance(), 1e-6);
			assertEquals(4.7944, spec.getIons().get(2).getIon(), 1e-6);
			assertEquals(0.0, spec.getIons().get(2).getAbundance(), 1e-6);
		} catch(IOException e) {
			logger.warn(e);
		}
	}
}
