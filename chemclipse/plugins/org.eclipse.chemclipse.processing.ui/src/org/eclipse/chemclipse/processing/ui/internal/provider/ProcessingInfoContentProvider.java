/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use {@link MessageProvider} interface
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.internal.provider;

import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ProcessingInfoContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {

		/*
		 * Processing messages
		 */
		if(inputElement instanceof MessageProvider) {
			MessageProvider processingInfo = (MessageProvider)inputElement;
			return processingInfo.getMessages().toArray();
		} else {
			return null;
		}
	}
}
