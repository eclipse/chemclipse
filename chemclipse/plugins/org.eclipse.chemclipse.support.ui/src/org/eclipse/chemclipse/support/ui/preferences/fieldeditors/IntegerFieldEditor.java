/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.swt.widgets.Composite;

public class IntegerFieldEditor extends org.eclipse.jface.preference.IntegerFieldEditor {

	public IntegerFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	public IntegerFieldEditor(String name, String labelText, int min, int max, Composite parent) {
		super(name, labelText, parent);
		setValidRange(min, max);
	}
}
