/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - active decorator option has been added
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class EmptyApplicationImageProvider implements IApplicationImageProvider {

	private static EmptyApplicationImageProvider instance;
	private Image image;

	@Override
	public Image getImage(String fileName, String size) {

		return getImage(fileName, size, false);
	}

	@Override
	public Image getImage(String fileName, String size, boolean active) {

		if(image == null) {
			image = ImageDescriptor.getMissingImageDescriptor().createImage();
		}
		return image;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String fileName, String size) {

		return ImageDescriptor.getMissingImageDescriptor();
	}

	@Override
	public Collection<String> listImages(String size) {

		return Collections.emptyList();
	}

	@Override
	public InputStream getImageAsInputStream(String fileName, String size) throws IOException {

		throw new FileNotFoundException(size + "/" + fileName);
	}

	public static IApplicationImageProvider getInstance() {

		if(instance == null) {
			instance = new EmptyApplicationImageProvider();
		}
		return instance;
	}
}
