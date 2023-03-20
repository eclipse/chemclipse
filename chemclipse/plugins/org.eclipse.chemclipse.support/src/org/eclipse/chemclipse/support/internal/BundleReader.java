/*******************************************************************************
 * Copyright (c) 2020, 2023 Christoph Läubrich.
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.logging.support.Settings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * Reads system wide installed plugins (e.g. community extensions, extra bundles like jdbc driver and so on) that are not shipped as part of chemclipse
 * 
 */
@Component(service = {Runnable.class}, property = "action=BundleReader", immediate = true)
public class BundleReader implements Runnable {

	private static final String BUNDLE_READER_LOCATION_PREFIX = "BundleReader:";
	private static final Logger LOG = Logger.getLogger(BundleReader.class);
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
			LOG.info(MessageFormat.format("Reading global plugins from {0}", absolutePath));
			Set<Long> currentBundles = new HashSet<>();
			List<Bundle> startCandidates = new ArrayList<>();
			for(File file : directory.listFiles(PLUGIN_FILE_FILTER)) {
				try {
					try (FileInputStream stream = new FileInputStream(file)) {
						Bundle bundle = bundleContext.installBundle(BUNDLE_READER_LOCATION_PREFIX + file.getAbsolutePath(), stream);
						LOG.info(MessageFormat.format("{0} is installed with id {1}", file.getName(), bundle.getBundleId()));
						startCandidates.add(bundle);
						currentBundles.add(bundle.getBundleId());
					}
				} catch(IOException | BundleException | RuntimeException e) {
					LOG.warn(MessageFormat.format("Installing plugin {0} failed.", file.getName()), e);
				}
			}
			for(Bundle bundle : bundleContext.getBundles()) {
				if(bundle.getLocation().startsWith(BUNDLE_READER_LOCATION_PREFIX) && !currentBundles.contains(bundle.getBundleId())) {
					try {
						bundle.uninstall();
						LOG.info(MessageFormat.format("Plugin {0} was uninstalled", bundle.getSymbolicName()));
					} catch(BundleException | RuntimeException e) {
						LOG.warn(MessageFormat.format("Uninstalling plugin {0} failed.", bundle.getSymbolicName()), e);
					}
				}
			}
			for(Bundle bundle : startCandidates) {
				try {
					bundle.start();
				} catch(BundleException | RuntimeException e) {
					LOG.info(MessageFormat.format("Plugin {0} was not started.", bundle.getSymbolicName()), e);
				}
			}
		}
	}
}
