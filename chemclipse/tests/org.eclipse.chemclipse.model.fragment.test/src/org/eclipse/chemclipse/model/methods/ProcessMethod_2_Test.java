/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;

import junit.framework.TestCase;

public class ProcessMethod_2_Test extends TestCase {

	private ProcessMethod processMethod;
	private IProcessEntry processEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		Set<DataCategory> dataCategories = new HashSet<>();
		dataCategories.add(DataCategory.MSD);
		processMethod = new ProcessMethod(dataCategories);
		processEntry = new ProcessEntry(processMethod);
		processMethod.getEntries().add(processEntry);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		String profile = "Test";
		processMethod.setActiveProfile(profile);
		assertEquals(profile, processMethod.getActiveProfile());
		assertEquals(profile, processEntry.getActiveProfile());
		assertEquals(2, processMethod.getProfiles().size());
	}

	public void test2() {

		String profile = "Test";
		processMethod.setActiveProfile(profile);
		assertEquals(profile, processMethod.getActiveProfile());
		assertEquals(profile, processEntry.getActiveProfile());
		assertEquals(2, processMethod.getProfiles().size());
		//
		processMethod.deleteProfile(profile);
		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processMethod.getActiveProfile());
		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processEntry.getActiveProfile());
		assertEquals(1, processMethod.getProfiles().size());
	}
}
