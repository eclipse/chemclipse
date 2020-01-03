/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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
import java.util.Hashtable;
import java.util.Properties;

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
@Component(service = {})
public class ConfigReader {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
	private static final String CFG_EXTENSION = ".cfg";
	private ConfigurationAdmin configurationAdmin;
	private static final FileFilter CFG_FILE_FILTER = new FileFilter() {

		@Override
		public boolean accept(File file) {

			return file.isDirectory() || (file.isFile() && file.getName().endsWith(CFG_EXTENSION));
		}
	};

	@Activate
	public void start() {

		File directory = Settings.getSystemConfigDirectory();
		if(directory.isDirectory()) {
			LOG.info("Reading static config files from " + directory.getAbsolutePath());
			for(File file : directory.listFiles(CFG_FILE_FILTER)) {
				if(file.isFile()) {
					readConfigFile(file, null);
				} else if(file.isDirectory()) {
					String fpid = file.getName();
					for(File factoryFile : file.listFiles(CFG_FILE_FILTER)) {
						readConfigFile(factoryFile, fpid);
					}
				}
			}
		} else {
			LOG.debug("Directory {} does not exits, no static configuration is read", directory);
		}
	}

	public void readConfigFile(File file, String fpid) {

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
			properties.put(".configreader.file", file.getName());
			Properties cfg = new Properties();
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				cfg.load(fileInputStream);
				boolean changed;
				if(oldProperties == null) {
					changed = true;
				} else {
					changed = oldProperties.size() != cfg.size();
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
		} catch(IOException e) {
			LOG.error("Reading configfile " + file.getAbsolutePath() + " failed!", e);
		} catch(InvalidSyntaxException e) {
			LOG.error("Reading configfile " + file.getAbsolutePath() + " failed!", e);
		}
	}

	@Reference(unbind = "-")
	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {

		this.configurationAdmin = configurationAdmin;
	}
}
