/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class SelectedIons_5_Test extends TestCase {

	private IMarkedIons selectedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		selectedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		selectedIons.add(new MarkedIon(28.78749204f));
	}

	@Override
	protected void tearDown() throws Exception {

		selectedIons = null;
		super.tearDown();
	}

	public void testContains_1() {

		assertFalse("contains", selectedIons.contains(28.8d));
	}
}
