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

import junit.framework.TestCase;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;

/**
 * @author eselmeister
 */
public class Chromatogram_26_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private List<IIntegrationEntry> chromatogramIntegrationEntries;
	private IIntegrationEntryMSD chromatogramIntegrationEntry;
	private List<IIntegrationEntry> backgroundIntegrationEntries;
	private IIntegrationEntryMSD backgroundIntegrationEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		/*
		 * Chromatogram Integration Entries
		 */
		chromatogramIntegrationEntries = new ArrayList<IIntegrationEntry>();
		chromatogramIntegrationEntry = new IntegrationEntryMSD(25.3f, 26778.7d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		chromatogramIntegrationEntry = new IntegrationEntryMSD(28.1f, 3446.2d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		chromatogramIntegrationEntry = new IntegrationEntryMSD(29.2f, 84598.5d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		chromatogramIntegrationEntry = new IntegrationEntryMSD(34.7f, 4885.1d);
		chromatogramIntegrationEntries.add(chromatogramIntegrationEntry);
		chromatogram.setChromatogramIntegratedArea(chromatogramIntegrationEntries, "ChromatogramIntegrator");
		/*
		 * Background Integration Entries
		 */
		backgroundIntegrationEntries = new ArrayList<IIntegrationEntry>();
		backgroundIntegrationEntry = new IntegrationEntryMSD(28.2f, 378374.78d);
		backgroundIntegrationEntries.add(backgroundIntegrationEntry);
		backgroundIntegrationEntry = new IntegrationEntryMSD(56.1f, 92043074.78d);
		backgroundIntegrationEntries.add(backgroundIntegrationEntry);
		chromatogram.setBackgroundIntegratedArea(backgroundIntegrationEntries, "BackgroundIntegrator");
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

		assertEquals("ChromatogramIntegrator", chromatogram.getChromatogramIntegratorDescription());
	}

	public void testGetBackgroundIntegratedArea_1() {

		assertEquals(92421449.56d, chromatogram.getBackgroundIntegratedArea());
	}

	public void testGetBackgroundIntegratorDescription_1() {

		assertEquals("BackgroundIntegrator", chromatogram.getBackgroundIntegratorDescription());
	}
}
