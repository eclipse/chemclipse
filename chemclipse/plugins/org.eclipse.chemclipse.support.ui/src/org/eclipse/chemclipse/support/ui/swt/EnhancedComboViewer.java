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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class EnhancedComboViewer extends ComboViewer {

	public EnhancedComboViewer(Composite parent) {

		super(parent);
		initialize();
	}

	public EnhancedComboViewer(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	private void initialize() {

		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=567652
		 */
		if(OperatingSystemUtils.isLinux()) {
			Combo combo = getCombo();
			combo.setBackground(combo.getBackground());
		}
	}
}