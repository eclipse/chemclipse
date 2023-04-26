/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class EnhancedCombo {

	public static Combo create(Composite parent, int style) {

		Combo combo = new Combo(parent, style);
		initialize(combo);
		//
		return combo;
	}

	private static void initialize(Combo combo) {

		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=567652
		 */
		if(OperatingSystemUtils.isLinux()) {
			combo.setBackground(combo.getBackground());
		}
	}
}