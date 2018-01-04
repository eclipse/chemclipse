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
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.model.support.IIntegrationConstraints;
import org.eclipse.chemclipse.model.support.IntegrationConstraint;
import org.eclipse.chemclipse.model.support.IntegrationConstraints;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class IntegrationConstraints_1_Test extends TestCase {

	private IIntegrationConstraints integrationConstraints;
	private IntegrationConstraint constraint;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		integrationConstraints = new IntegrationConstraints();
		constraint = IntegrationConstraint.LEAVE_PEAK_AS_IT_IS;
	}

	@Override
	protected void tearDown() throws Exception {

		integrationConstraints = null;
		constraint = null;
		super.tearDown();
	}

	public void testHasIntegrationConstraint_1() {

		assertFalse("hasIntegrationConstraint", integrationConstraints.hasIntegrationConstraint(constraint));
	}

	public void testHasIntegrationConstraint_2() {

		integrationConstraints.add(constraint);
		assertTrue("hasIntegrationConstraint", integrationConstraints.hasIntegrationConstraint(constraint));
	}

	public void testHasIntegrationConstraint_3() {

		integrationConstraints.add(constraint);
		integrationConstraints.add(constraint);
		assertTrue("hasIntegrationConstraint", integrationConstraints.hasIntegrationConstraint(constraint));
	}

	public void testHasIntegrationConstraint_4() {

		integrationConstraints.add(constraint);
		integrationConstraints.remove(constraint);
		assertFalse("hasIntegrationConstraint", integrationConstraints.hasIntegrationConstraint(constraint));
	}

	public void testHasIntegrationConstraint_5() {

		integrationConstraints.add(constraint);
		integrationConstraints.add(constraint);
		integrationConstraints.remove(constraint);
		assertFalse("hasIntegrationConstraint", integrationConstraints.hasIntegrationConstraint(constraint));
	}

	public void testHasIntegrationConstraint_6() {

		integrationConstraints.add(constraint);
		integrationConstraints.add(constraint);
		integrationConstraints.removeAll();
		assertFalse("hasIntegrationConstraint", integrationConstraints.hasIntegrationConstraint(constraint));
	}
}
