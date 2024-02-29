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
package org.eclipse.chemclipse.msd.model.support;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.IonTransition;

import junit.framework.TestCase;

public class ScanSupport_1_Test extends TestCase {

	public void test1() {

		IIon ion = null;
		assertEquals("", ScanSupport.getLabelTandemMS(ion));
	}

	public void test2() {

		assertEquals("58.0", ScanSupport.getLabelTandemMS(getIon(58.0d, null)));
	}

	public void test3() {

		assertEquals("58.05", ScanSupport.getLabelTandemMS(getIon(58.05d, null)));
	}

	public void test4() {

		IIon ion = getIon(58.1d, new IonTransition(168.7, 58.1, 15, 1.0d, 1.0d, 0));
		assertEquals("169 > 58.1 @15", ScanSupport.getLabelTandemMS(ion));
	}

	public void test5() {

		IIonTransition ionTransition = null;
		assertEquals("", ScanSupport.getLabelTandemMS(ionTransition));
	}

	public void test6() {

		IIonTransition ionTransition = new IonTransition(168.7, 58.1, 15, 1.0d, 1.0d, 0);
		assertEquals("169 > 58.1 @15", ScanSupport.getLabelTandemMS(ionTransition));
	}

	public void test7() {

		IIonTransition ionTransition = new IonTransition(168.7, 169.7, 56.3, 57.3, 15, 1.0d, 1.0d, 0);
		assertEquals("169 > 56.8 @15", ScanSupport.getLabelTandemMS(ionTransition));
	}

	private IIon getIon(double mz, IIonTransition ionTransition) {

		try {
			if(ionTransition == null) {
				return new Ion(mz);
			} else {
				IIon ion = getIon(mz, null);
				return new Ion(ion, ionTransition);
			}
		} catch(Exception e) {
			return null;
		}
	}
}