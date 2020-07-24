/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import org.eclipse.chemclipse.logging.support.Settings;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This component reads static configuration files from the config folder
 * 
 *
 */
@Component(service = {Runnable.class}, property = "action=ConfigReader", immediate = true)
public class ConfigReader implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
	private static final String FILE_PROPERTY = ".configreader.file";
	private static final String CFG_EXTENSION = ".cfg";
	private static final FileFilter CFG_FILE_FILTER = file -> file.isDirectory() || (file.isFile() && file.getName().endsWith(CFG_EXTENSION));
	private ConfigurationAdmin configurationAdmin;

	private void updateConfiguration() {

		File directory = Settings.getSystemConfigDirectory();
		if(directory.isDirectory()) {
			LOG.info("Reading static config files from {}", directory.getAbsolutePath());
			Set<String> pids = new HashSet<>();
			for(File file : directory.listFiles(CFG_FILE_FILTER)) {
				if(file.isFile()) {
					String pid = readConfigFile(file, null);
					if(pid != null) {
						pids.add(pid);
					}
				} else if(file.isDirectory()) {
					String fpid = file.getName();
					for(File factoryFile : file.listFiles(CFG_FILE_FILTER)) {
						String pid = readConfigFile(factoryFile, fpid);
						if(pid != null) {
							pids.add(pid);
						}
					}
				}
			}
			try {
				Configuration[] configurations = configurationAdmin.listConfigurations(null);
				if(configurations != null) {
					for(Configuration configuration : configurations) {
						Dictionary<String, Object> properties = configuration.getProperties();
						String pid = configuration.getPid();
						if(properties != null && properties.get(FILE_PROPERTY) != null && !pids.contains(pid)) {
							LOG.info("remove vanished configuration for pid {}", pid);
							configuration.delete();
						}
					}
				}
			} catch(IOException | InvalidSyntaxException e) {
				LOG.error("Delete obsolete configurations failed!", e);
			}
		} else {
			LOG.debug("Directory {} does not exits, no static configuration is read", directory);
		}
	}

	private String readConfigFile(File file, String fpid) {

		String name = file.getName();
		try {
			Configuration configuration;
			if(fpid == null) {
				String pid = name.substring(0, name.length() - CFG_EXTENSION.length());
				configuration = configurationAdmin.getConfiguration(pid, null);
			} else {
				Configuration[] configurations = configurationAdmin.listConfigurations("(&(.configreader.file=" + file.getName() + ")(service.factoryPid=" + fpid + "))");
				if(configurations == null || configurations.length == 0) {
					configuration = configurationAdmin.createFactoryConfiguration(fpid, null);
				} else {
					configuration = configurations[0];
				}
			}
			Dictionary<String, Object> oldProperties = configuration.getProperties();
			Hashtable<String, Object> properties = new Hashtable<>();
			properties.put(FILE_PROPERTY, file.getName());
			Properties cfg = new Properties();
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				cfg.load(fileInputStream);
				boolean changed;
				if(oldProperties == null) {
					changed = true;
				} else {
					changed = oldProperties.size() != cfg.size() + 2;
				}
				for(String key : cfg.stringPropertyNames()) {
					String property = cfg.getProperty(key);
					properties.put(key, property);
					if(oldProperties != null) {
						Object oldValue = oldProperties.get(key);
						if(!property.equals(oldValue)) {
							changed = true;
						}
					}
				}
				if(changed) {
					configuration.update(properties);
					LOG.info("Updated configuration " + configuration.getPid() + " from file " + name);
				}
			}
			return configuration.getPid();
		} catch(IOException e) {
			LOG.error("Reading configfile " + file.getAbsolutePath() + " failed!", e);
		} catch(InvalidSyntaxException e) {
			LOG.error("Reading configfile " + file.getAbsolutePath() + " failed!", e);
		}
		return null;
	}

	@Reference(unbind = "-")
	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {

		this.configurationAdmin = configurationAdmin;
	}

	@Activate
	@Override
	public void run() {

		updateConfiguration();
	}
}
