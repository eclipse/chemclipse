/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.support.history.EditHistorySortOrder;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.EditInformationComparator;
import org.eclipse.chemclipse.support.history.IEditInformation;

import junit.framework.TestCase;

public class DefaultChromatogram_1_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private IEditInformation editInformation;
	private final String entry1 = "I have modified the chromatogram.";
	private final String entry2 = "Me too.";
	private final String entry3 = "What are we doing now?";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		/*
		 * Why do we need a Thread.sleep() here? If the EditInformation objects
		 * are created too fast, the getDate().getTime() will be the same in all
		 * the three cases. If the date is the same, the objects can't be sorted
		 * by date.
		 */
		editInformation = new EditInformation(entry1);
		chromatogram.getEditHistory().add(editInformation);
		Thread.sleep(10);
		editInformation = new EditInformation(entry2);
		chromatogram.getEditHistory().add(editInformation);
		Thread.sleep(10);
		editInformation = new EditInformation(entry3);
		chromatogram.getEditHistory().add(editInformation);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		editInformation = null;
		super.tearDown();
	}

	public void testGetEditHistory_1() {

		List<IEditInformation> history = new ArrayList<>(chromatogram.getEditHistory());
		assertEquals("1st entry", entry1, history.get(0).getDescription());
		assertEquals("2nd entry", entry2, history.get(1).getDescription());
		assertEquals("3rd entry", entry3, history.get(2).getDescription());
	}

	public void testGetEditHistory_2() {

		List<IEditInformation> history = new ArrayList<>(chromatogram.getEditHistory());
		Collections.sort(history, new EditInformationComparator(EditHistorySortOrder.DATE_ASC));
		assertEquals("1st entry", entry1, history.get(0).getDescription());
		assertEquals("2nd entry", entry2, history.get(1).getDescription());
		assertEquals("3rd entry", entry3, history.get(2).getDescription());
	}

	public void testGetEditHistory_3() {

		List<IEditInformation> history = new ArrayList<>(chromatogram.getEditHistory());
		Collections.sort(history, new EditInformationComparator(EditHistorySortOrder.DATE_DESC));
		assertEquals("1st entry", entry3, history.get(0).getDescription());
		assertEquals("2nd entry", entry2, history.get(1).getDescription());
		assertEquals("3rd entry", entry1, history.get(2).getDescription());
	}
}
