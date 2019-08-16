/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class Chromatogram_26_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private List<IIntegrationEntry> chromatogramIntegrationEntries;
	private IIntegrationEntry chromatogramIntegrationEntry;
	private List<IIntegrationEntry> backgroundIntegrationEntries;
	private IIntegrationEntry backgroundIntegrationEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		/*
		 * Chromatogram Integration Entries
		 */
		chromatogramIntegrationEntries = new ArrayList<IIntegrationEntry>();
		chromatogramIntegrationEntry = new IntegrationEntry(25.3f, 26778.7d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		chromatogramIntegrationEntry = new IntegrationEntry(28.1f, 3446.2d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		chromatogramIntegrationEntry = new IntegrationEntry(29.2f, 84598.5d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		chromatogramIntegrationEntry = new IntegrationEntry(34.7f, 4885.1d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		/*
		 * Background Integration Entries
		 */
		backgroundIntegrationEntries = new ArrayList<IIntegrationEntry>();
		backgroundIntegrationEntry = new IntegrationEntry(28.2f, 378374.78d);
		backgroundIntegrationEntries.add(backgroundIntegrationEntry);
		backgroundIntegrationEntry = new IntegrationEntry(56.1f, 92043074.78d);
		backgroundIntegrationEntries.add(backgroundIntegrationEntry);
		//
		chromatogram.setIntegratedArea(chromatogramIntegrationEntries, backgroundIntegrationEntries, "Test Integrator");
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testGetChromatogramIntegratedArea_1() {

		assertEquals(119708.5d, chromatogram.getChromatogramIntegratedArea());
	}

	public void testGetChromatogramIntegratorDescription_1() {

		assertEquals("Test Integrator", chromatogram.getIntegratorDescription());
	}

	public void testGetBackgroundIntegratedArea_1() {

		assertEquals(92421449.56d, chromatogram.getBackgroundIntegratedArea());
	}

	public void testGetBackgroundIntegratorDescription_1() {

		assertEquals("Test Integrator", chromatogram.getIntegratorDescription());
	}
}
