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
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

public class Chromatogram_19_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IIdentificationTarget entry1;
	private IIdentificationTarget entry2;
	private ILibraryInformation libraryInformation;
	private IComparisonResult comparisonResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		libraryInformation = new LibraryInformation();
		comparisonResult = new ComparisonResult(0.8f, 0.95f, 0.0f, 0.0f);
		entry1 = new IdentificationTarget(libraryInformation, comparisonResult);
		entry1.setIdentifier("Test-Tools");
		entry2 = new IdentificationTarget(libraryInformation, comparisonResult);
		entry2.setIdentifier("DB-Tools");
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		entry1 = null;
		entry2 = null;
		super.tearDown();
	}

	public void testGetTargets_1() {

		Set<IIdentificationTarget> targets = chromatogram.getTargets();
		assertNotNull("Targets", targets);
	}

	public void testGetTargets_2() {

		Set<IIdentificationTarget> targets = chromatogram.getTargets();
		assertEquals("Size", 0, targets.size());
	}

	public void testTargets_1() {

		chromatogram.getTargets().add(entry1);
		chromatogram.getTargets().add(entry2);
		Set<IIdentificationTarget> targets = chromatogram.getTargets();
		assertEquals("Size", 2, targets.size());
	}

	public void testTargets_2() {

		chromatogram.getTargets().add(entry1);
		chromatogram.getTargets().add(entry2);
		chromatogram.getTargets().add(entry1);
		Set<IIdentificationTarget> targets = chromatogram.getTargets();
		assertEquals("Size", 2, targets.size());
	}

	public void testTargets_3() {

		chromatogram.getTargets().add(entry1);
		chromatogram.getTargets().add(entry2);
		chromatogram.getTargets().add(entry1);
		chromatogram.getTargets().remove(entry1);
		Set<IIdentificationTarget> targets = chromatogram.getTargets();
		assertEquals("Size", 1, targets.size());
	}

	public void testTargets_4() {

		chromatogram.getTargets().add(entry1);
		chromatogram.getTargets().add(entry2);
		chromatogram.getTargets().clear();
		Set<IIdentificationTarget> targets = chromatogram.getTargets();
		assertEquals("Size", 0, targets.size());
	}
}
