/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class IonListContentProviderLazy implements ILazyContentProvider {

	private TableViewer tableViewer;
	private List<IIon> ions;

	public IonListContentProviderLazy(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public void dispose() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		this.ions = (List<IIon>)newInput;
	}

	@Override
	public void updateElement(int index) {

		if(ions != null && index < ions.size()) {
			tableViewer.replace(ions.get(index), index);
		}
	}
}
