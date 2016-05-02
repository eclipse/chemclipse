/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.List;

import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;

import junit.framework.TestCase;

public class Chromatogram_19_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramTargetMSD entry1;
	private IChromatogramTargetMSD entry2;
	private ILibraryInformation libraryInformation;
	private IComparisonResult comparisonResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		libraryInformation = new LibraryInformation();
		comparisonResult = new ComparisonResult(0.8f, 0.95f, 0.0f, 0.0f);
		entry1 = new ChromatogramTarget(libraryInformation, comparisonResult);
		entry1.setIdentifier("Test-Tools");
		entry2 = new ChromatogramTarget(libraryInformation, comparisonResult);
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

		List<IChromatogramTargetMSD> targets = chromatogram.getTargets();
		assertNotNull("Targets", targets);
	}

	public void testGetTargets_2() {

		List<IChromatogramTargetMSD> targets = chromatogram.getTargets();
		assertEquals("Size", 0, targets.size());
	}

	public void testTargets_1() {

		chromatogram.addTarget(entry1);
		chromatogram.addTarget(entry2);
		List<IChromatogramTargetMSD> targets = chromatogram.getTargets();
		assertEquals("Size", 2, targets.size());
	}

	public void testTargets_2() {

		chromatogram.addTarget(entry1);
		chromatogram.addTarget(entry2);
		chromatogram.addTarget(entry1);
		List<IChromatogramTargetMSD> targets = chromatogram.getTargets();
		assertEquals("Size", 2, targets.size());
	}

	public void testTargets_3() {

		chromatogram.addTarget(entry1);
		chromatogram.addTarget(entry2);
		chromatogram.addTarget(entry1);
		chromatogram.removeTarget(entry1);
		List<IChromatogramTargetMSD> targets = chromatogram.getTargets();
		assertEquals("Size", 1, targets.size());
	}

	public void testTargets_4() {

		chromatogram.addTarget(entry1);
		chromatogram.addTarget(entry2);
		chromatogram.removeAllTargets();
		List<IChromatogramTargetMSD> targets = chromatogram.getTargets();
		assertEquals("Size", 0, targets.size());
	}
}
