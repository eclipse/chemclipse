/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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

import junit.framework.TestCase;

public class ProcessingInfo_4_Test extends TestCase {

	private IProcessingInfo processingInfo;
	private IProcessingMessage processingMessage;
	private String processingResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		processingInfo = new ProcessingInfo();
		processingResult = "Hello World!";
		processingInfo.setProcessingResult(processingResult);
		//
		processingMessage = new ProcessingMessage(MessageType.WARN, "Load Peak", "The peak X35P couldn't be loaded completely.");
		processingInfo.addMessage(processingMessage);
		//
		processingMessage = new ProcessingMessage(MessageType.INFO, "Calculate Abundance", "The abundance was calculated correctly.");
		processingInfo.addMessage(processingMessage);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testProcessingInfo_1() {

		assertFalse(processingInfo.hasErrorMessages());
		processingMessage = new ProcessingMessage(MessageType.ERROR, "Load Peak", "The peak X35P couldn't be loaded, cause it seems to have no values.");
		processingInfo.addMessage(processingMessage);
	}

	public void testProcessingInfo_2() {

		processingMessage = new ProcessingMessage(MessageType.ERROR, "Load Peak", "The peak X35P couldn't be loaded, cause it seems to have no values.");
		processingInfo.addMessage(processingMessage);
		assertTrue(processingInfo.hasErrorMessages());
	}
}
