/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add icon shortcut
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface IApplicationImageProvider {

	/*
	 * Protocols and paths
	 */
	String ICON = "icon";
	String ICON_PROTOCOL = ICON + "://";
	String ICON_PATH = "icons/";
	/*
	 * Sizes of icons, overlays ...
	 */
	String SIZE_7x7 = "7x7";
	String SIZE_7x8 = "7x8";
	String SIZE_8x8 = "8x8";
	String SIZE_7x16 = "7x16";
	String SIZE_16x16 = "16x16";
	String SIZE_32x32 = "32x32";
	String SIZE_48x48 = "48x48";
	String SIZE_64x64 = "64x64";
	String SIZE_75x66 = "75x66";
	String SIZE_128x68 = "128x68";
	String SIZE_128x128 = "128x128";
	String SIZE_250x300 = "250x330"; // About

	/**
	 * Returns an image with the given icons size.
	 * 
	 * @param fileName
	 * @param size
	 * @return Image
	 */
	Image getImage(String fileName, String size);

	/**
	 * Returns an image with the given icons size.
	 * 
	 * @param fileName
	 * @param size
	 * @param active
	 * @return Image
	 */
	Image getImage(String fileName, String size, boolean active);

	/**
	 * Returns an image descriptor with the given icons size.
	 * 
	 * @param fileName
	 * @param size
	 * @return {@link ImageDescriptor}
	 */
	ImageDescriptor getImageDescriptor(String fileName, String size);

	default ImageDescriptor getIcon(String fileName) {

		return getImageDescriptor(fileName, SIZE_16x16);
	}

	/**
	 * List all icons known to this provider
	 * 
	 * @param size
	 *            the desired size
	 * @return
	 */
	Collection<String> listImages(String size);

	/**
	 * Returns the images as an input stream.
	 * 
	 * @param fileName
	 * @param size
	 * @return InputStream
	 * @throws IOException
	 */
	InputStream getImageAsInputStream(String fileName, String size) throws IOException;
}
