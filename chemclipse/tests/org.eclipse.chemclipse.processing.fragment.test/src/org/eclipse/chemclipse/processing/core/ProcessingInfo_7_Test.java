/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

import junit.framework.TestCase;

public class ProcessingInfo_7_Test extends TestCase {

	private IProcessingInfo<Object> processingInfo;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		processingInfo = new ProcessingInfo<>();
		processingInfo.setProcessingResult(null);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	@SuppressWarnings("deprecation")
	public void testProcessingInfo_1() {

		try {
			processingInfo.getProcessingResult(String.class);
		} catch(TypeCastException e) {
			assertTrue(true);
		}
	}

	@SuppressWarnings("deprecation")
	public void testProcessingInfo_2() {

		try {
			processingInfo.getProcessingResult(Integer.class);
		} catch(TypeCastException e) {
			assertTrue(true);
		}
	}
}
