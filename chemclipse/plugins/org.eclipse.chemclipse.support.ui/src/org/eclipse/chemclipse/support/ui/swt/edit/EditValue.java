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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Control;

/**
 * 
 * An {@link EditValue} encapsulates a control and a value that should be edited, in contrast to a plain control, an {@link EditValue} can be accessed even if the control is disposed, it stores the last edit state. It also has ways to detect if an actual edit has happened.
 * Interested parties can also register an {@link Observer} to be notified on each change, the general contract is, that the object passed to the observer is the current edit value
 *
 * @param <T>
 */
public abstract class EditValue<T> extends Observable {

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

	@Override
	public synchronized void addObserver(Observer o) {

		super.addObserver(o);
		o.update(this, getValue());
	}
}
