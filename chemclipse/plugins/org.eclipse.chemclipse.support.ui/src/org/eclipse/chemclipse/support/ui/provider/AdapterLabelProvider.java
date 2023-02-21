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
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.provider;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class AdapterLabelProvider extends LabelProvider implements IToolTipProvider {

	@Override
	public Image getImage(Object element) {

		ILabelProvider labelProvider = Adapters.adapt(element, ILabelProvider.class);
		if(labelProvider != null) {
			return labelProvider.getImage(element);
		}
		return null;
	}

	@Override
	public String getText(Object element) {

		ILabelProvider labelProvider = Adapters.adapt(element, ILabelProvider.class);
		if(labelProvider != null) {
			return labelProvider.getText(element);
		}
		return String.valueOf(element);
	}

	@Override
	public String getToolTipText(Object element) {

		ILabelProvider labelProvider = Adapters.adapt(element, ILabelProvider.class);
		if(labelProvider instanceof IToolTipProvider toolTipProvider) {
			return toolTipProvider.getToolTipText(element);
		}
		IToolTipProvider toolTipProvider = Adapters.adapt(element, IToolTipProvider.class);
		if(toolTipProvider != null) {
			return toolTipProvider.getToolTipText(element);
		}
		return null;
	}
}
