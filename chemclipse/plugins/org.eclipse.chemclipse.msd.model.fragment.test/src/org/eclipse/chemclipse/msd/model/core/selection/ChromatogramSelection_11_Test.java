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
package org.eclipse.chemclipse.msd.model.core.selection;

import java.util.Set;

import org.easymock.EasyMock;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;

import junit.framework.TestCase;

public class ChromatogramSelection_11_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD chromatogramSelection;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Use createNiceMock if you use void methods that are not important to
		 * test.
		 */
		chromatogram = EasyMock.createNiceMock(IChromatogramMSD.class);
		EasyMock.expect(chromatogram.getStartRetentionTime()).andStubReturn(1);
		EasyMock.expect(chromatogram.getStopRetentionTime()).andStubReturn(100);
		EasyMock.expect(chromatogram.getMaxSignal()).andStubReturn(127500.0f);
		EasyMock.expect(chromatogram.getNumberOfPeaks()).andStubReturn(1);
		EasyMock.replay(chromatogram);
		/*
		 * Default values from IChromatogram will be chosen.
		 */
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		chromatogramSelection = null;
		super.tearDown();
	}

	public void testGetSelectedIons_1() {

		IMarkedIons selectedIons = chromatogramSelection.getSelectedIons();
		assertNotNull(selectedIons);
	}

	public void testGetSelectedIons_2() {

		IMarkedIons selectedIons;
		selectedIons = chromatogramSelection.getSelectedIons();
		selectedIons.add(new MarkedIon(43));
		selectedIons.add(new MarkedIon(104));
		selectedIons = chromatogramSelection.getSelectedIons();
		assertNotNull(selectedIons);
		Set<Integer> selectedIonsNominal = selectedIons.getIonsNominal();
		assertEquals("Contains", true, selectedIonsNominal.contains(43));
		assertEquals("Contains", true, selectedIonsNominal.contains(104));
		assertEquals("Contains", false, selectedIonsNominal.contains(42));
		assertEquals("Contains", false, selectedIonsNominal.contains(105));
	}

	public void testGetExcludedIons_1() {

		IMarkedIons excludedIons = chromatogramSelection.getExcludedIons();
		assertNotNull(excludedIons);
	}

	public void testGetExcludedIons_2() {

		IMarkedIons excludedIons;
		excludedIons = chromatogramSelection.getExcludedIons();
		excludedIons.add(new MarkedIon(18));
		excludedIons.add(new MarkedIon(28));
		excludedIons = chromatogramSelection.getExcludedIons();
		assertNotNull(excludedIons);
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		assertEquals("Contains", false, excludedIonsNominal.contains(43));
		assertEquals("Contains", false, excludedIonsNominal.contains(104));
		assertEquals("Contains", true, excludedIonsNominal.contains(18));
		assertEquals("Contains", true, excludedIonsNominal.contains(28));
	}
}
