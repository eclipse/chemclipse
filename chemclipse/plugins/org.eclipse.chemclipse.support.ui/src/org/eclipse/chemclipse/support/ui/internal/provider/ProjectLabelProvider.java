/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;

public class ProjectLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	@Override
	public String getDescription(Object anElement) {

		return "";
	}

	@Override
	public String getText(Object element) {

		if(element instanceof IContainer) {
			return ((IContainer)element).getName();
		}
		return "";
	}

	public Image getImage(Object element) {

		Image image;
		if(element instanceof IFolder) {
			image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16);
		} else if(element instanceof IFile) {
			image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16);
		} else {
			image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_CLOSED, IApplicationImage.SIZE_16x16);
		}
		//
		return image;
	}
}
