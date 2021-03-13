/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

import junit.framework.TestCase;

public class ProcessMethod_4_Test extends TestCase {

	private ProcessMethod processMethod;
	private IProcessEntry processEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		Set<DataCategory> dataCategories = new HashSet<>();
		dataCategories.add(DataCategory.MSD);
		processMethod = new ProcessMethod(dataCategories);
		processMethod.addProfile("Test");
		processMethod.setActiveProfile("Test");
		//
		processEntry = new ProcessEntry(processMethod);
		processEntry.setActiveProfile(ProcessEntryContainer.DEFAULT_PROFILE);
		processEntry.setSettings("Hello World");
		processEntry.setActiveProfile("Test");
		processEntry.setSettings("This is another setting");
		processEntry.setActiveProfile(ProcessEntryContainer.DEFAULT_PROFILE);
		processMethod.getEntries().add(processEntry);
		//
		processMethod.setActiveProfile("Test");
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("Test", processMethod.getActiveProfile());
		assertEquals("This is another setting", processEntry.getSettings());
	}

	public void test2() {

		ProcessMethod processMethodNew = new ProcessMethod(processMethod);
		IProcessEntry processEntryNew = processMethodNew.getEntries().get(0);
		assertEquals("Test", processMethodNew.getActiveProfile());
		assertEquals("This is another setting", processEntryNew.getSettings());
	}
}
