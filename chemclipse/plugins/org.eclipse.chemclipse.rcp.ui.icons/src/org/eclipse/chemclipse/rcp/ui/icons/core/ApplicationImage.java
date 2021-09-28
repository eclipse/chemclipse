/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

public class ApplicationImage implements IApplicationImage, BundleTrackerCustomizer<IconBundle> {

	private static final Logger logger = Logger.getLogger(ApplicationImage.class);
	private static final String BUNDLE_SEPARATOR = "/";
	//
	private final BundleTracker<IconBundle> bundleTracker;
	private final Map<String, ImageDescriptor> descriptorCache = new ConcurrentHashMap<>();
	private final Map<String, Image> imageCache = new ConcurrentHashMap<>();
	private final Map<String, Collection<String>> listCache = new ConcurrentHashMap<>();
	private final ImageDecorator imageDecorator = new ImageDecorator();

	public ApplicationImage(BundleContext bundleContext) {

		bundleTracker = new BundleTracker<>(bundleContext, Bundle.RESOLVED | Bundle.STARTING | Bundle.ACTIVE, this);
	}

	@Override
	public Image getImage(String fileName, String size) {

		return getImage(fileName, size, false);
	}

	@Override
	public Image getImage(String fileName, String size, boolean active) {

		return imageCache.computeIfAbsent(size + "/" + fileName + "/" + active, t -> {
			String[] parts = fileName.split(BUNDLE_SEPARATOR, 2);
			if(parts.length < 2) {
				return EmptyApplicationImageProvider.getInstance().getImage(fileName, size);
			}
			//
			Image image = getProvider(parts[0]).getImage(parts[1], size, active);
			if(active) {
				try {
					imageDecorator.setSize(size);
					imageDecorator.setImage(image);
					image = CompletableFuture.supplyAsync(() -> imageDecorator.createImage()).get(50, TimeUnit.MILLISECONDS);
				} catch(Exception e) {
					/*
					 * TimeOutException
					 */
					logger.warn(e);
					logger.warn("The decoration for the following icon fails: " + fileName);
					logger.warn("Please check the icon. Is it a png? Does it contain an embedded RGB profile?");
					logger.warn("Solution: Probably try to convert the icon to *.gif format.");
				}
			}
			//
			return image;
		});
	}

	@Override
	public ImageDescriptor getImageDescriptor(String fileName, String size) {

		return descriptorCache.computeIfAbsent(size + "/" + fileName, t -> {
			String[] parts = fileName.split(BUNDLE_SEPARATOR, 2);
			if(parts.length < 2) {
				return EmptyApplicationImageProvider.getInstance().getImageDescriptor(fileName, size);
			}
			return getProvider(parts[0]).getImageDescriptor(parts[1], size);
		});
	}

	@Override
	public InputStream getImageAsInputStream(String fileName, String size) throws IOException {

		String[] parts = fileName.split(BUNDLE_SEPARATOR, 2);
		if(parts.length < 2) {
			return EmptyApplicationImageProvider.getInstance().getImageAsInputStream(fileName, size);
		}
		return getProvider(parts[0]).getImageAsInputStream(parts[1], size);
	}

	@Override
	public IconBundle addingBundle(Bundle bundle, BundleEvent event) {

		URL entry = bundle.getEntry(ICON_PATH);
		if(entry != null) {
			clear();
			return new IconBundle(bundle);
		}
		return null;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, IconBundle object) {

		// don't mind
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, IconBundle object) {

		clear();
		object.dispose();
	}

	private void clear() {

		listCache.clear();
		descriptorCache.clear();
		imageCache.clear();
	}

	@Override
	public Collection<String> listImages(String size) {

		return listCache.computeIfAbsent(size, this::listAllImages);
	}

	private Collection<String> listAllImages(String size) {

		List<String> images = new ArrayList<>();
		Map<Bundle, IconBundle> tracked = bundleTracker.getTracked();
		for(Entry<Bundle, IconBundle> entry : tracked.entrySet()) {
			Collection<String> listImages = entry.getValue().listImages(size);
			listImages.forEach(bundleKey -> images.add(entry.getKey().getSymbolicName() + BUNDLE_SEPARATOR + bundleKey));
		}
		return images;
	}

	private IApplicationImageProvider getProvider(String bundleName) {

		Map<Bundle, IconBundle> tracked = bundleTracker.getTracked();
		for(Entry<Bundle, IconBundle> entry : tracked.entrySet()) {
			if(entry.getKey().getSymbolicName().equals(bundleName)) {
				return entry.getValue();
			}
		}
		return EmptyApplicationImageProvider.getInstance();
	}

	public void start() {

		bundleTracker.open();
	}

	public void stop() {

		bundleTracker.close();
	}
}
