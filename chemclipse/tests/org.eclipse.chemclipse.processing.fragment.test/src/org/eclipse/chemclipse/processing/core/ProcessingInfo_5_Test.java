/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
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

public class ProcessingInfo_5_Test extends TestCase {

	private IProcessingInfo<String> processingInfo;
	private IProcessingMessage processingMessage;
	private String processingResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		processingInfo = new ProcessingInfo<>();
		processingResult = "Hello World!";
		processingInfo.setProcessingResult(processingResult);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testProcessingInfo_1() {

		assertFalse(processingInfo.hasWarnMessages());
	}

	public void testProcessingInfo_2() {

		processingMessage = new ProcessingMessage(MessageType.WARN, "Load Peak", "The peak X35P couldn't be loaded completely.");
		processingInfo.addMessage(processingMessage);
		assertTrue(processingInfo.hasWarnMessages());
	}
}
