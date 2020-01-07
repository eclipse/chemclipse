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
package org.eclipse.chemclipse.rcp.app.test;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import junit.framework.TestSuite;

public class TestAssembler {

	public static final String FRAGMENT_HOST = "Fragment-Host";
	private Bundle[] bundles;

	public TestAssembler(Bundle[] bundles) {
		this.bundles = bundles;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void assembleTests(TestSuite testSuite, String bundleName, String packageName, String filter) {

		for(Bundle bundle : bundles) {
			if(!isFragment(bundle) && bundle.getSymbolicName().startsWith(bundleName)) {
				List<Class> testClasses = getTestClasses(bundle, packageName, filter);
				for(Class clazz : testClasses) {
					testSuite.addTestSuite(clazz);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private List<Class> getTestClasses(Bundle bundle, String packageName, String filter) {

		List<Class> testClasses = new ArrayList<Class>();
		Enumeration testClassNames = bundle.findEntries("/", filter + ".class", true);
		if(testClassNames != null) {
			while(testClassNames.hasMoreElements()) {
				String testClassPath = ((URL)testClassNames.nextElement()).getPath();
				testClassPath = testClassPath.replace('/', '.');
				int packageNameStart = testClassPath.indexOf(packageName);
				/*
				 * If the class is not starting with root, skip it.
				 */
				if(packageNameStart == -1) {
					continue;
				}
				/*
				 * The string ".class" has a length of 6 characters.
				 */
				String testClassName = testClassPath.substring(packageNameStart);
				testClassName = testClassName.substring(0, testClassName.length() - 6);
				/*
				 * Try to load the bundles class loader.
				 */
				Class testClass = null;
				try {
					testClass = bundle.loadClass(testClassName);
				} catch(ClassNotFoundException e) {
					throw new RuntimeException("Class was not loadable: " + testClassName, e);
				}
				/*
				 * Skip abstract classes.
				 */
				if(!Modifier.isAbstract(testClass.getModifiers())) {
					testClasses.add(testClass);
				}
			}
		}
		return testClasses;
	}

	/**
	 * Returns whether the bundle is a fragment or not.
	 * 
	 * @param bundle
	 * @return boolean
	 */
	@SuppressWarnings("rawtypes")
	private boolean isFragment(Bundle bundle) {

		Enumeration headerKeys = bundle.getHeaders().keys();
		/*
		 * Search in all elements the String Fragment-Host.
		 */
		while(headerKeys.hasMoreElements()) {
			if(headerKeys.nextElement().toString().equals(FRAGMENT_HOST)) {
				return true;
			}
		}
		return false;
	}
}
