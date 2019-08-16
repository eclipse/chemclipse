/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.Activator;
import org.eclipse.chemclipse.rcp.app.test.TestAssembler;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		// Properties log4jProperties = PropertiesUtil.getLog4jProperties();
		// log4jProperties.setProperty("log4j.appender.ChemClipseConsoleAppender.layout.ConversionPattern", "%-5p %C.%M (%F:%L) %m%n");
		// PropertyConfigurator.configure(log4jProperties);

		TestAssembler testAssembler = new TestAssembler(Activator.getContext().getBundle().getBundleContext().getBundles());
		TestSuite suite = new TestSuite("Run all tests.");
		String bundleAndPackageName = "org.eclipse.chemclipse.msd.identifier.supplier.proteoms";
		testAssembler.assembleTests(suite, bundleAndPackageName, bundleAndPackageName, "Test*"); // Unit
		// testAssembler.assembleTests(suite, bundleAndPackageName, bundleAndPackageName, "*_ITest"); // Integration
		return suite;
	}
}
