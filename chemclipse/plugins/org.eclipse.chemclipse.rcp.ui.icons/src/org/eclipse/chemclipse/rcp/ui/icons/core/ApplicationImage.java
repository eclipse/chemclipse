/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - reimplementation to support discovering of bundles
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ApplicationImage extends AbstractApplicationImage implements IApplicationImage {

	private ImageDecorator imageDecorator = new ImageDecorator();
	private Map<String, Image> decoratedImageCache = new HashMap<>();

	public ApplicationImage(Bundle bundle) {

		super(bundle);
	}

	@Override
	public Image getImage(String fileName, String size, boolean active) {

		Image image = super.getImage(fileName, size);
		if(active && image != null) {
			String path = getPath(fileName, size) + "_decorated";
			Image decoratedImage = decoratedImageCache.get(path);
			if(decoratedImage == null) {
				imageDecorator.setSize(size);
				imageDecorator.setImage(image);
				decoratedImage = imageDecorator.createImage();
				if(decoratedImage != null) {
					decoratedImageCache.put(path, decoratedImage);
					image = decoratedImage;
				}
			}
		}
		//
		return image;
	}
}