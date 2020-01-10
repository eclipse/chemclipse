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
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;
import org.eclipse.chemclipse.model.support.AnalysisSupport;
import org.eclipse.chemclipse.model.support.IAnalysisSupport;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class AnalysisSupport_5_Test extends TestCase {

	@SuppressWarnings("unused")
	private IAnalysisSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		support = null;
	}

	public void testConstruct_1() {

		try {
			support = new AnalysisSupport(null, 10);
		} catch(AnalysisSupportException e) {
			assertTrue("AnalysisSupportException", true);
		}
	}
}
