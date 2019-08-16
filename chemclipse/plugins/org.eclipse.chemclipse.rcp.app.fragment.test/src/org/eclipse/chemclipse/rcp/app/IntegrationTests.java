/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.chemclipse.rcp.app.test.TestAssembler;

public class IntegrationTests {

	public static Test suite() {

		/*
		 * Currently
		 * Runs: 2177
		 * Errors: 103
		 * Failures: 38
		 * TODO clean
		 */
		TestAssembler testAssembler = new TestAssembler(Activator.getContext().getBundles());
		TestSuite suite = new TestSuite("Run all integration tests.");
		testAssembler.assembleTests(suite, "org.eclipse.chemclipse.", "org.eclipse.chemclipse.", "*_ITest");
		testAssembler.assembleTests(suite, "net.openchrom.", "net.openchrom.", "*_ITest");
		return suite;
	}
}
