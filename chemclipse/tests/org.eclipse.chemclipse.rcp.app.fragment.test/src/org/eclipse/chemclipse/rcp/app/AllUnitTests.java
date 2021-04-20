/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.rcp.app.test.TestAssembler;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUnitTests {

	public static Test suite() {

		TestAssembler testAssembler = new TestAssembler(Activator.getContext().getBundles());
		TestSuite suite = new TestSuite("Run all unit tests.");
		testAssembler.assembleTests(suite, "org.eclipse.chemclipse.", "org.eclipse.chemclipse.", "*_Test");
		testAssembler.assembleTests(suite, "net.openchrom.", "net.openchrom.", "*_Test");
		return suite;
	}
}