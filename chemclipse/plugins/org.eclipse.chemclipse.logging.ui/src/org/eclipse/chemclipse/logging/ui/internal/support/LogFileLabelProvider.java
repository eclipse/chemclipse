/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.internal.support;

import java.io.File;

import org.eclipse.chemclipse.logging.ui.Activator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LogFileLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof File) {
			File file = (File)element;
			return file.getName();
		}
		return text;
	}

	/**
	 * Return the image.
	 */
	public Image getImage(Object element) {

		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		if(imageRegistry != null) {
			return imageRegistry.get(Activator.ICON_LOG);
		} else {
			return null;
		}
	}
}
