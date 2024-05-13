/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;

public class PeakQuantifierSupport_1_Test extends PeakQuantifierSupportTestCase {

	public void test1() {

		IPeak peak = getPeak();
		assertEquals("", PeakQuantifierSupport.getInternalStandardConcentrations(peak));
	}

	public void test2() {

		IPeak peak = getPeak();
		assertEquals("", PeakQuantifierSupport.getPeakConcentrations(peak));
	}

	public void test3() {

		IInternalStandard internalStandard = PeakQuantifierSupport.getInternalStandard(null, null);
		assertNull(internalStandard);
	}

	public void test4() {

		IInternalStandard internalStandard = PeakQuantifierSupport.getInternalStandard("", "");
		assertNull(internalStandard);
	}

	public void test5() {

		IInternalStandard internalStandard = PeakQuantifierSupport.getInternalStandard("Test", "");
		assertNull(internalStandard);
	}
}