/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class MassSpectrumListContentProviderLazy implements ILazyContentProvider {

	private TableViewer tableViewer;
	private IScanMSD[] massSpectra;

	public MassSpectrumListContentProviderLazy(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		this.massSpectra = (IScanMSD[])newInput;
	}

	@Override
	public void updateElement(int index) {

		tableViewer.replace(massSpectra[index], index);
	}
}
