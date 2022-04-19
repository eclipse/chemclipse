/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.graphics.Color;

import junit.framework.TestCase;

public class Colors_Test extends TestCase {

	public void test1() {

		/*
		 * Use this test to check the colors for the preference store.
		 */
		print("DARK_GRAY", Colors.DARK_GRAY);
		print("DARK_RED", Colors.DARK_RED);
		print("GRAY", Colors.GRAY);
		print("RED", Colors.RED);
		assertTrue(true);
	}

	private void print(String name, Color color) {

		System.out.println(name + " -> " + color.toString());
	}
}