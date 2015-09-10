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

import org.eclipse.chemclipse.support.ui.Activator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

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
			image = Activator.getDefault().getImage(Activator.ICON_FOLDER_OPENED);
		} else if(element instanceof IFile) {
			image = Activator.getDefault().getImage(Activator.ICON_FILE);
		} else {
			image = Activator.getDefault().getImage(Activator.ICON_FOLDER_CLOSED);
		}
		//
		return image;
	}
}
