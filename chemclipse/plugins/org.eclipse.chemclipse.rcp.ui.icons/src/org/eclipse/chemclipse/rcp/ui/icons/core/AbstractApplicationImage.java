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
 * Christoph Läubrich - fix loading of images with different size
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public abstract class AbstractApplicationImage implements IApplicationImage {

	private static final String PATH_SEPARATOR = "/";
	private static final String PLATFORM_PREFIX = "platform:/plugin/";
	private static final String FOLDER_ICONS = "icons";
	private static final String EXTENSION_PNG = ".png";
	private static final String EXTENSION_GIF = ".gif";
	//
	private Bundle bundle;
	private Map<String, ImageDescriptor> imageDescriptorCache = new HashMap<>();
	private Map<String, Image> imageCache = new HashMap<>();

	public AbstractApplicationImage(Bundle bundle) {

		this.bundle = bundle;
	}

	@Override
	public Image getImage(String fileName, String size) {

		String path = getPath(fileName, size);
		Image image = imageCache.get(path);
		if(image == null) {
			ImageDescriptor imageDescriptor = getImageDescriptor(path);
			if(imageDescriptor != null) {
				image = imageDescriptor.createImage();
				if(image != null) {
					imageCache.put(path, image);
				}
			}
		}
		//
		return image;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String fileName, String size) {

		String path = getPath(fileName, size);
		return getImageDescriptor(path);
	}

	@Override
	public InputStream getImageAsInputStream(String fileName, String size) throws IOException {

		InputStream inputStream = null;
		URL url = FileLocator.find(bundle, new Path(getPath(fileName, size)), null);
		inputStream = url.openConnection().getInputStream();
		//
		return inputStream;
	}

	@Override
	public Collection<String> listImages(String size) {

		List<String> images = new ArrayList<>();
		//
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(PATH_SEPARATOR);
			builder.append(FOLDER_ICONS);
			builder.append(PATH_SEPARATOR);
			builder.append(size);
			builder.append(PATH_SEPARATOR);
			//
			IPath path = new Path(builder.toString());
			URL url = FileLocator.find(bundle, path, null);
			File directory = new File(FileLocator.resolve(url).getPath().toString());
			if(directory.isDirectory()) {
				for(File file : directory.listFiles()) {
					String name = file.getName().toLowerCase();
					if(name.endsWith(EXTENSION_PNG) || name.endsWith(EXTENSION_GIF)) {
						images.add(file.getName());
					}
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		//
		return images;
	}

	@Override
	public String getURI(String fileName, String size) {

		StringBuilder builder = new StringBuilder();
		//
		builder.append(PLATFORM_PREFIX);
		builder.append(bundle.getSymbolicName());
		builder.append(PATH_SEPARATOR);
		builder.append(getPath(fileName, size));
		//
		return builder.toString();
	}

	/**
	 * Calculates the path, given by the file name and size.
	 * 
	 * @param fileName
	 * @param size
	 * @return String
	 */
	protected String getPath(String fileName, String size) {

		StringBuilder builder = new StringBuilder();
		//
		builder.append(FOLDER_ICONS);
		builder.append(PATH_SEPARATOR);
		builder.append(size);
		builder.append(PATH_SEPARATOR);
		builder.append(fileName);
		//
		return builder.toString();
	}

	/**
	 * Return the image descriptor given by the path.
	 * 
	 * @param path
	 * @return {@link ImageDescriptor}
	 */
	private ImageDescriptor getImageDescriptor(String path) {

		ImageDescriptor imageDescriptor = imageDescriptorCache.get(path);
		if(imageDescriptor == null) {
			try {
				URL fileLocation = FileLocator.find(bundle, new Path(path), null);
				imageDescriptor = ImageDescriptor.createFromURL(fileLocation);
				if(imageDescriptor != null) {
					imageDescriptorCache.put(path, imageDescriptor);
				}
			} catch(Exception e) {
			}
		}
		//
		return imageDescriptor;
	}
}