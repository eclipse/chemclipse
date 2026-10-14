/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * Routes SLF4J to the ChemClipse logging. This bundle is a fragment of slf4j.api,
 * so the service loader of the host finds it without an OSGi service loader mediator.
 */
public class ChemClipseServiceProvider implements SLF4JServiceProvider {

	/*
	 * Declare the version of the SLF4J API this implementation is compiled against.
	 */
	private static final String REQUESTED_API_VERSION = "2.0.99";
	//
	private ILoggerFactory loggerFactory;
	private IMarkerFactory markerFactory;
	private MDCAdapter mdcAdapter;

	@Override
	public ILoggerFactory getLoggerFactory() {

		return loggerFactory;
	}

	@Override
	public IMarkerFactory getMarkerFactory() {

		return markerFactory;
	}

	@Override
	public MDCAdapter getMDCAdapter() {

		return mdcAdapter;
	}

	@Override
	public String getRequestedApiVersion() {

		return REQUESTED_API_VERSION;
	}

	@Override
	public void initialize() {

		loggerFactory = new ChemClipseLoggerFactory();
		markerFactory = new BasicMarkerFactory();
		mdcAdapter = new NOPMDCAdapter();
	}
}
