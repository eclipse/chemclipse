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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class IconBundle implements IApplicationImageProvider {

	private final Bundle bundle;
	private final ImageRegistry registry = new ImageRegistry(Display.getDefault());

	public IconBundle(Bundle bundle) {

		this.bundle = bundle;
	}

	@Override
	public Image getImage(String fileName, String size) {

		return getImage(fileName, size, false);
	}

	@Override
	public Image getImage(String fileName, String size, boolean active) {

		String path = getPath(fileName, size);
		Image image = registry.get(path);
		//
		if(image == null) {
			addIconImageDescriptor(path);
			return registry.get(path);
		}
		//
		if(image.isDisposed()) {
			registry.remove(path);
			return getImage(fileName, size, active);
		}
		//
		return image;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String fileName, String size) {

		String key = getPath(fileName, size);
		ImageDescriptor imageDescriptor = registry.getDescriptor(key);
		if(imageDescriptor == null) {
			return addIconImageDescriptor(key);
		}
		return imageDescriptor;
	}

	private ImageDescriptor addIconImageDescriptor(String path) {

		URL entry = bundle.getEntry(path);
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(entry);
		registry.put(path, imageDescriptor);
		return imageDescriptor;
	}

	@Override
	public InputStream getImageAsInputStream(String fileName, String size) throws IOException {

		URL entry = bundle.getEntry(getPath(fileName, size));
		if(entry == null) {
			return EmptyApplicationImageProvider.getInstance().getImageAsInputStream(fileName, size);
		}
		return entry.openStream();
	}

	public void dispose() {

		registry.dispose();
	}

	@Override
	public Collection<String> listImages(String size) {

		List<String> icons = new ArrayList<String>();
		String path = ICON_PATH + size + "/";
		Enumeration<String> findEntries = bundle.getEntryPaths(path);
		if(findEntries != null) {
			while(findEntries.hasMoreElements()) {
				icons.add(findEntries.nextElement().substring(path.length()));
			}
		}
		return icons;
	}

	private static String getPath(String fileName, String size) {

		return ICON_PATH + size + "/" + fileName;
	}
}
