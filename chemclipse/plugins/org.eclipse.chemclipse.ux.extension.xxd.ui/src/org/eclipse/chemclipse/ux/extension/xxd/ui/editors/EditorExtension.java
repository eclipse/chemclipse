/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.util.Observable;

import org.eclipse.swt.widgets.Composite;

/**
 * An editor extension allows the dynamic creation of a component that might be embedded inside another editor or component
 * 
 * @author Christoph Läubrich
 *
 */
public interface EditorExtension {

	/**
	 * Creates a new extension
	 * 
	 * @param parent
	 * @return an Observable that can be used to monitor for changes in this extension
	 */
	public Observable createExtension(Composite parent);
}
