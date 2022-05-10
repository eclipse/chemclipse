/*******************************************************************************
 * Copyright (c) 2010, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support;

import java.util.List;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.core.runtime.NullProgressMonitor;

public class Denoising_1_ITest extends ChromatogramImporterTestCase {

	private IMarkedIons ionsToRemove;
	private IMarkedIons ionsToPreserve;
	private List<ICombinedMassSpectrum> noiseMassSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ionsToRemove = new MarkedIons(MarkedTraceModus.INCLUDE);
		ionsToRemove.add(new MarkedIon(18));
		ionsToRemove.add(new MarkedIon(28));
		ionsToRemove.add(new MarkedIon(32));
		ionsToRemove.add(new MarkedIon(84));
		ionsToRemove.add(new MarkedIon(207));
		ionsToPreserve = new MarkedIons(MarkedTraceModus.INCLUDE);
		ionsToPreserve.add(new MarkedIon(103));
		ionsToPreserve.add(new MarkedIon(103));
		noiseMassSpectra = Denoising.applyDenoisingFilter(chromatogramSelection, ionsToRemove, ionsToPreserve, true, 1, 13, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetSize_1() {

		assertEquals("Size", 202, noiseMassSpectra.size());
	}
}
