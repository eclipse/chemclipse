/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.provider;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public abstract class AbstractLabelProvider implements ILabelProvider {

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {

		return false;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public String getText(Object element) {

		return null;
	}

	@Override
	public Image getImage(Object element) {

		return null;
	}
}
