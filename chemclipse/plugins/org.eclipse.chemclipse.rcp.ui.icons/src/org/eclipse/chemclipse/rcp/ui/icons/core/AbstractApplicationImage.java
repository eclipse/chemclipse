/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.MissingResourceException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public abstract class AbstractApplicationImage implements IApplicationImage {

	private Bundle bundle;

	public AbstractApplicationImage(Bundle bundle) {
		this.bundle = bundle; // Activator.getContext().getBundle()
	}

	@Override
	public Image getImage(String fileName, String size) {

		Image image = JFaceResources.getImageRegistry().get(fileName);
		if(image == null) {
			addIconImageDescriptor(fileName, size);
			image = JFaceResources.getImageRegistry().get(fileName);
		}
		return image;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String fileName, String size) {

		ImageDescriptor imageDescriptor = null;
		imageDescriptor = JFaceResources.getImageRegistry().getDescriptor(fileName);
		if(imageDescriptor == null) {
			addIconImageDescriptor(fileName, size);
			imageDescriptor = JFaceResources.getImageRegistry().getDescriptor(fileName);
		}
		return imageDescriptor;
	}

	@Override
	public InputStream getImageAsInputStream(String fileName, String size) throws IOException {

		InputStream inputStream = null;
		URL url = FileLocator.find(bundle, new Path(ICON_PATH + size + "/" + fileName), null);
		inputStream = url.openConnection().getInputStream();
		return inputStream;
	}

	/**
	 * Stores the image descriptor in the JFace image registry.
	 * Returns whether this operation was successful or not.
	 * 
	 * @param fileName
	 * @param size
	 * @return boolean
	 */
	private boolean addIconImageDescriptor(String fileName, String size) {

		try {
			URL fileLocation = FileLocator.find(bundle, new Path(ICON_PATH + size + "/" + fileName), null);
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(fileLocation);
			JFaceResources.getImageRegistry().put(fileName, imageDescriptor);
		} catch(MissingResourceException e) {
			return false;
		} catch(IllegalArgumentException e) {
			return false;
		}
		return true;
	}
}
