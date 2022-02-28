/*******************************************************************************
 * Copyright (c) 2014, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class MassSpectrumListContentProviderLazy implements ILazyContentProvider {

	private TableViewer tableViewer;
	private IMassSpectra massSpectra;

	public MassSpectrumListContentProviderLazy(TableViewer tableViewer) {

		this.tableViewer = tableViewer;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		this.massSpectra = (IMassSpectra)newInput;
	}

	@Override
	public void updateElement(int index) {

		tableViewer.replace(massSpectra.getList().get(index), index);
	}
}
