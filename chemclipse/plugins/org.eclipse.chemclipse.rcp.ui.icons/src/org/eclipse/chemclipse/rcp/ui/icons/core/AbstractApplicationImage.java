/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - fix loading of images with different size
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

	private final Bundle bundle;

	public AbstractApplicationImage(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public Image getImage(String fileName, String size) {

		String path = getPath(fileName, size);
		Image image = JFaceResources.getImageRegistry().get(path);
		if(image == null) {
			addIconImageDescriptor(path);
			return JFaceResources.getImageRegistry().get(path);
		}
		return image;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String fileName, String size) {

		String key = getPath(fileName, size);
		ImageDescriptor imageDescriptor = JFaceResources.getImageRegistry().getDescriptor(key);
		if(imageDescriptor == null) {
			return addIconImageDescriptor(key);
		}
		return imageDescriptor;
	}

	@Override
	public InputStream getImageAsInputStream(String fileName, String size) throws IOException {

		InputStream inputStream = null;
		URL url = FileLocator.find(bundle, new Path(getPath(fileName, size)), null);
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
	private ImageDescriptor addIconImageDescriptor(String path) {

		try {
			URL fileLocation = FileLocator.find(bundle, new Path(path), null);
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(fileLocation);
			JFaceResources.getImageRegistry().put(path, imageDescriptor);
			return imageDescriptor;
		} catch(MissingResourceException e) {
			return null;
		} catch(IllegalArgumentException e) {
			return null;
		}
	}

	private static String getPath(String fileName, String size) {

		return ICON_PATH + size + "/" + fileName;
	}
}
