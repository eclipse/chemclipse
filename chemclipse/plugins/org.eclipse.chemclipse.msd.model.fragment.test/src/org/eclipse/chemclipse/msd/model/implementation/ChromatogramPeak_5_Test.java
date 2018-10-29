/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.Set;

import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

/**
 * The chromatogram and peak will be initialized in DefaultPeakTestCase.<br/>
 * The peak has 15 scans, starting at a retention time of 1500 milliseconds (ms)
 * and ends at a retention time of 15500 ms.<br/>
 * The chromatogram has 17 scans, starting at a retention time of 500 ms and
 * ends at a retention time of 16500 ms. It has a background of 1750 units.
 * 
 * @author eselmeister
 */
public class ChromatogramPeak_5_Test extends ChromatogramPeakTestCase {

	private IChromatogramPeakMSD peak;
	private IIdentificationTarget entry1;
	private IIdentificationTarget entry2;
	private ILibraryInformation libraryInformation;
	private IComparisonResult comparisonResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peak = new ChromatogramPeakMSD(getPeakModel(), getChromatogram());
		libraryInformation = new LibraryInformation();
		comparisonResult = new ComparisonResult(0.8f, 0.95f, 0.0f, 0.0f);
		entry1 = new IdentificationTarget(libraryInformation, comparisonResult);
		entry1.setIdentifier("PBM");
		entry2 = new IdentificationTarget(libraryInformation, comparisonResult);
		entry2.setIdentifier("INCOS");
	}

	@Override
	protected void tearDown() throws Exception {

		peak = null;
		super.tearDown();
	}

	public void testGetTargets_1() {

		Set<IIdentificationTarget> targets = peak.getTargets();
		assertNotNull("Targets", targets);
	}

	public void testGetTargets_2() {

		Set<IIdentificationTarget> targets = peak.getTargets();
		assertEquals("Size", 0, targets.size());
	}

	public void testTargets_1() {

		peak.getTargets().add(entry1);
		peak.getTargets().add(entry2);
		Set<IIdentificationTarget> targets = peak.getTargets();
		assertEquals("Size", 2, targets.size());
	}

	public void testTargets_2() {

		peak.getTargets().add(entry1);
		peak.getTargets().add(entry2);
		peak.getTargets().add(entry1);
		Set<IIdentificationTarget> targets = peak.getTargets();
		assertEquals("Size", 2, targets.size());
	}

	public void testTargets_3() {

		peak.getTargets().add(entry1);
		peak.getTargets().add(entry2);
		peak.getTargets().add(entry1);
		peak.getTargets().remove(entry1);
		Set<IIdentificationTarget> targets = peak.getTargets();
		assertEquals("Size", 1, targets.size());
	}

	public void testTargets_4() {

		peak.getTargets().add(entry1);
		peak.getTargets().add(entry2);
		peak.getTargets().clear();
		Set<IIdentificationTarget> targets = peak.getTargets();
		assertEquals("Size", 0, targets.size());
	}
}
