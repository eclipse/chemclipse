/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring Observable
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Control;

/**
 * 
 * An {@link EditValue} encapsulates a control and a value that should be edited, in contrast to a plain control, an {@link EditValue} can be accessed even if the control is disposed, it stores the last edit state. It also has ways to detect if an actual edit has happened.
 * Interested parties can also register an {@link PropertyChangeListener} to be notified on each change, the general contract is, that the object passed to the observer is the current edit value
 *
 * @param <T>
 */
public abstract class EditValue<T> extends PropertyChangeSupport {

	private static final long serialVersionUID = -5844012602717155272L;

	public EditValue() {

		super("EditValue");
	}

	/**
	 * 
	 * @return <code>true</code> if the value was edited, false if it already has its initial state
	 */
	public abstract boolean isEdited();

	/**
	 * 
	 * @return the last edited value
	 */
	public abstract T getValue();

	public abstract Control getControl();

	public void updateChange(Object source, String propertyName, Object oldValue, Object newValue) {

		firePropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

		super.addPropertyChangeListener(listener);
		firePropertyChange(new PropertyChangeEvent(this, "EditValue", getValue(), getValue()));
	}
}