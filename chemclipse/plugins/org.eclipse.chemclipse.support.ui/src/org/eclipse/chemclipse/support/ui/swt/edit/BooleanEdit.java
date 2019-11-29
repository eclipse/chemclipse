/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.edit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BooleanEdit extends EditValue<Boolean> {

	private final Button button;
	private final boolean initialValue;
	private boolean currentValue;

	public BooleanEdit(Composite parent, String label, boolean initialValue) {
		this.initialValue = initialValue;
		currentValue = initialValue;
		button = new Button(parent, SWT.CHECK);
		if(label != null) {
			button.setText(label);
		}
		button.setSelection(initialValue);
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				currentValue = button.getSelection();
				setChanged();
				notifyObservers(currentValue);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
	}

	@Override
	public boolean isEdited() {

		return currentValue != initialValue;
	}

	@Override
	public Boolean getValue() {

		return currentValue;
	}

	@Override
	public Control getControl() {

		return button;
	}
}
