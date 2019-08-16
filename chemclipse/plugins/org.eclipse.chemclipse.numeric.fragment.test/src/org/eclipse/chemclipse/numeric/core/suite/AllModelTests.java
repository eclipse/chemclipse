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
package org.eclipse.chemclipse.numeric.core.suite;

import org.eclipse.chemclipse.numeric.geometry.Slope_1_Test;
import junit.framework.TestSuite;

public class AllModelTests {

	public static TestSuite suite() {

		TestSuite suite = new TestSuite();
		suite.addTestSuite(Slope_1_Test.class);
		// add more TestCases here
		return suite;
	}
}
