/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class ProxySelectionChangedListener implements ISelectionChangedListener {

	private ISelectionChangedListener proxy;

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		if(proxy != null) {
			proxy.selectionChanged(event);
		}
	}

	public void setProxy(ISelectionChangedListener proxy) {

		this.proxy = proxy;
	}
}