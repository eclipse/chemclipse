/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.components.ions;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author eselmeister
 */
public class MarkedIonsChooserContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		/*
		 * If the input element is an instance of marked ions, a list
		 * of ions will be returned.<br/> The list is sorted
		 * ascending.
		 */
		if(inputElement != null && inputElement instanceof IMarkedIons) {
			IMarkedIons markedIons = (IMarkedIons)inputElement;
			return markedIons.toArray();
		} else {
			return null;
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}
}
