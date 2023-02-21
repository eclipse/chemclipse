/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
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

import java.net.URL;

import org.eclipse.chemclipse.support.ui.Activator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.osgi.framework.Bundle;

public class ProjectLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	@Override
	public String getDescription(Object anElement) {

		return "";
	}

	@Override
	public String getText(Object element) {

		if(element instanceof IContainer container) {
			return container.getName();
		}
		return "";
	}

	@Override
	public Image getImage(Object element) {

		Image image = null;
		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		if(imageRegistry != null) {
			if(element instanceof IFolder) {
				image = imageRegistry.get(Activator.ICON_FOLDER_OPENED);
				if(image == null) {
					imageRegistry.put(Activator.ICON_FOLDER_OPENED, createImageDescriptor(Activator.getDefault().getBundle(), "icons/16x16/folder_opened.gif"));
					image = imageRegistry.get(Activator.ICON_FOLDER_OPENED);
				}
			} else if(element instanceof IFile) {
				image = imageRegistry.get(Activator.ICON_FILE);
				if(image == null) {
					imageRegistry.put(Activator.ICON_FILE, createImageDescriptor(Activator.getDefault().getBundle(), "icons/16x16/file.gif"));
					image = imageRegistry.get(Activator.ICON_FILE);
				}
			} else {
				image = imageRegistry.get(Activator.ICON_FOLDER_CLOSED);
				if(image == null) {
					imageRegistry.put(Activator.ICON_FOLDER_CLOSED, createImageDescriptor(Activator.getDefault().getBundle(), "icons/16x16/folder_closed.gif"));
					image = imageRegistry.get(Activator.ICON_FOLDER_CLOSED);
				}
			}
			//
		}
		//
		return image;
	}

	private ImageDescriptor createImageDescriptor(Bundle bundle, String string) {

		ImageDescriptor imageDescriptor = null;
		IPath path = new Path(string);
		URL url = FileLocator.find(bundle, path, null);
		imageDescriptor = ImageDescriptor.createFromURL(url);
		return imageDescriptor;
	}
}
