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
package org.eclipse.chemclipse.model.versioning;

import java.util.Date;

import org.eclipse.chemclipse.model.versioning.TimeHelper;

import junit.framework.TestCase;

/**
 * TimeHelper offers some convenient methods regarding time stamps and so on.
 * This class will test them.
 * 
 * @author eselmeister
 */
public class TimeHelper_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetStoragePath() {

		/*
		 * Errors could occur when there is a too big delta between
		 * "new Date().getTime()" and calling the method
		 * "TimeHelper.getTimeStampId()".<br/> Compare for example:<br/><br/>
		 * now = _12246558967[44]<br/> test = _12246558967[45]<br/>
		 */
		String now = "_" + new Date().getTime();
		String test = TimeHelper.getTimeStampId();
		assertTrue("getTimeStampId", now.regionMatches(0, test, 0, 12));
	}
}
