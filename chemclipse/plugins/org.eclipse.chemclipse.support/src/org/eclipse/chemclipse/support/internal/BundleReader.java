/*******************************************************************************
 * Copyright (c) 2020 Christoph Läubrich.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.support.Settings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads system wide installed plugins (e.g. community extensions, extra bundles like jdbc driver and so on) that are not shipped as part of chemclipse
 * 
 */
@Component(service = {Runnable.class}, property = "action=BundleReader", immediate = true)
public class BundleReader implements Runnable {

	private static final String BUNDLE_READER_LOCATION_PREFIX = "BundleReader:";
	private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
	private static final String PLUGIN_EXTENSION = ".jar";
	private static final FileFilter PLUGIN_FILE_FILTER = file -> (file.isFile() && file.getName().endsWith(PLUGIN_EXTENSION));
	private BundleContext bundleContext;

	@Activate
	protected void start(BundleContext bundleContext) {

		this.bundleContext = bundleContext;
		run();
	}

	@Override
	public void run() {

		File directory = Settings.getSystemPluginDirectory();
		if(directory.isDirectory()) {
			String absolutePath = directory.getAbsolutePath();
			LOG.info("Reading global plugins from {}", absolutePath);
			Set<Long> currentBundles = new HashSet<>();
			List<Bundle> startCandidates = new ArrayList<>();
			for(File file : directory.listFiles(PLUGIN_FILE_FILTER)) {
				try {
					try (FileInputStream stream = new FileInputStream(file)) {
						Bundle bundle = bundleContext.installBundle(BUNDLE_READER_LOCATION_PREFIX + file.getAbsolutePath(), stream);
						LOG.info("{} is installed with id {}", file.getName(), bundle.getBundleId());
						startCandidates.add(bundle);
						currentBundles.add(bundle.getBundleId());
					}
				} catch(IOException | BundleException | RuntimeException e) {
					LOG.warn("Installing plugin {} failed: {}", file.getName(), e.toString());
				}
			}
			for(Bundle bundle : bundleContext.getBundles()) {
				if(bundle.getLocation().startsWith(BUNDLE_READER_LOCATION_PREFIX) && !currentBundles.contains(bundle.getBundleId())) {
					try {
						bundle.uninstall();
						LOG.info("Plugin {} was uninstalled", bundle.getSymbolicName());
					} catch(BundleException | RuntimeException e) {
						LOG.warn("Uninstalling plugin {} failed: {}", bundle.getSymbolicName(), e.toString());
					}
				}
			}
			for(Bundle bundle : startCandidates) {
				try {
					bundle.start();
				} catch(BundleException | RuntimeException e) {
					LOG.info("Plugin {} was not started: {}", bundle.getSymbolicName(), e.toString());
				}
			}
		}
	}
}
