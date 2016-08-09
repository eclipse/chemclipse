/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.internal.provider;

import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MarkedWavelengthsChooserContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		/*
		 * If the input element is an instance of marked ions, a list
		 * of ions will be returned.<br/> The list is sorted
		 * ascending.
		 */
		if(inputElement != null && inputElement instanceof IMarkedWavelengths) {
			IMarkedWavelengths markedWavelengths = (IMarkedWavelengths)inputElement;
			return markedWavelengths.toArray();
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
