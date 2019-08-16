/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.swt.widgets.Composite;

public class SpinnerFieldEditorBounded extends SpinnerFieldEditor2 {

	private int min;
	private int max;

	public SpinnerFieldEditorBounded(String name, String labelText, int min, int max, int strategy, Composite parent) {
		super(name, labelText, strategy, parent);
		this.min = min;
		this.max = max;
	}

	public SpinnerFieldEditorBounded(String name, String labelText, int min, int max, Composite parent) {
		super(name, labelText, parent);
		this.min = min;
		this.max = max;
	}

	public void setIncrement(int value) {

		spinner.setIncrement(value);
	}

	@Override
	protected boolean doCheckState() {

		int value = getIntValue();
		if(value < min || value > max) {
			setErrorMessage("Invalid min/max values: " + min + ", " + max);
			return false;
		}
		return super.doCheckState();
	}
}
