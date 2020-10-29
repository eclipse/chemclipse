/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
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

import java.util.List;

import junit.framework.TestCase;

public class ProcessingInfo_3_Test extends TestCase {

	private IProcessingInfo<String> processingInfo;
	private IProcessingInfo<String> processingInfo2;
	private IProcessingMessage processingMessage;
	private String processingResult;
	private String processingResult2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		processingInfo = new ProcessingInfo<>();
		processingResult = "Hello World!";
		processingInfo.setProcessingResult(processingResult);
		//
		processingMessage = new ProcessingMessage(MessageType.ERROR, "Load Peak", "The peak X35P couldn't be loaded, cause it seems to have no values.");
		processingInfo.addMessage(processingMessage);
		//
		processingMessage = new ProcessingMessage(MessageType.WARN, "Load Peak", "The peak X35P couldn't be loaded completely.");
		processingInfo.addMessage(processingMessage);
		//
		processingMessage = new ProcessingMessage(MessageType.INFO, "Calculate Abundance", "The abundance was calculated correctly.");
		processingInfo.addMessage(processingMessage);
		/*
		 * ProcessingInfo2
		 */
		processingInfo2 = new ProcessingInfo<>(processingInfo);
		processingResult2 = "Hello World 2!";
		processingInfo2.setProcessingResult(processingResult2);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testProcessingInfo_1() {

		assertNotNull(processingInfo.getProcessingResult());
	}

	public void testProcessingInfo_2() {

		Object result = processingInfo2.getProcessingResult();
		assertTrue(result instanceof String);
		assertEquals("Hello World 2!", (String)result);
	}

	public void testProcessingInfo_3() {

		List<IProcessingMessage> messages = processingInfo2.getMessages();
		assertEquals(3, messages.size());
	}

	public void testProcessingInfo_4() {

		List<IProcessingMessage> messages = processingInfo2.getMessages();
		IProcessingMessage message = messages.get(0);
		assertEquals(MessageType.ERROR, message.getMessageType());
		assertEquals("Load Peak", message.getDescription());
		assertEquals("The peak X35P couldn't be loaded, cause it seems to have no values.", message.getMessage());
	}

	public void testProcessingInfo_5() {

		List<IProcessingMessage> messages = processingInfo2.getMessages();
		IProcessingMessage message = messages.get(1);
		assertEquals(MessageType.WARN, message.getMessageType());
		assertEquals("Load Peak", message.getDescription());
		assertEquals("The peak X35P couldn't be loaded completely.", message.getMessage());
	}

	public void testProcessingInfo_6() {

		List<IProcessingMessage> messages = processingInfo2.getMessages();
		IProcessingMessage message = messages.get(2);
		assertEquals(MessageType.INFO, message.getMessageType());
		assertEquals("Calculate Abundance", message.getDescription());
		assertEquals("The abundance was calculated correctly.", message.getMessage());
	}
}
