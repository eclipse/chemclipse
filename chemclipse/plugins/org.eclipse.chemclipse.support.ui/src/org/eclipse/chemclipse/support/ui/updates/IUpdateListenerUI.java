/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.updates;

import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.swt.widgets.Display;

/**
 * If an update listener without Display is needed, have a look at:
 * org.eclipse.chemclipse.support.updates.IUpdateListener
 * {@link IUpdateListener}
 */
public interface IUpdateListenerUI {

	void update(Display display);
}