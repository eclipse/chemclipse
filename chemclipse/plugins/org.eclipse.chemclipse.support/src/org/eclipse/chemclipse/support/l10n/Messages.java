/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.l10n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses OSGI-INF/l10n/bundle%.properties
 */
public class Messages {

	public static final String NO_ENTRY = "n.a."; // $NON-NLS-1$
	//
	private static final Logger logger = LoggerFactory.getLogger(Messages.class);
	/*
	 * Model: bundle.properties -> ISO-8859-1 : To enable a wide range of languages.
	 * UI: bundle.properties -> ISO-8859-1 : Otherwise the *.e4xmi doesn't show the entries correctly.
	 */
	private Map<String, String> messageMap;
	private static final String COMMENT = "#"; // $NON-NLS-1$
	private static final String DELIMITER = "="; // $NON-NLS-1$
	private static final String CHARSET = "UTF-8"; // "ISO-8859-1", "UTF-8" -> determine automatically

	/**
	 * Use it bundle specific in the following way:
	 * Messages messages = new Messages(Activator.getContext().getBundle());
	 * 
	 * @param bundle
	 */
	public Messages(Bundle bundle) {

		initializeMessageMap(bundle);
	}

	/**
	 * The placeholders {0}, {1} ... will be replaced by the string arguments
	 * if needed.
	 * 
	 * @param key
	 * @param args
	 * @return String
	 */
	public String getMessage(String key, String... args) {

		String message = messageMap.get(key);
		if(message == null) {
			message = NO_ENTRY;
			logger.warn(NO_ENTRY + " > " + key); // $NON-NLS-1$
		} else if(args != null && args.length > 0) {
			/*
			 * Replace all placeholders.
			 * It's a regex, so don't remove the escape sequence: \\
			 */
			for(int i = 0; i < args.length; i++) {
				message = message.replaceAll("\\{" + i + "\\}", args[i]); // $NON-NLS-1$
			}
		}
		return message;
	}

	/**
	 * Initializes the message map.
	 * 
	 * @param bundle
	 */
	private void initializeMessageMap(Bundle bundle) {

		/*
		 * Initialize the message map.
		 * The user can manually select a language that shall be used.
		 */
		Locale locale = Locale.getDefault();
		String language = locale.getLanguage();
		String country = locale.getCountry();
		//
		messageMap = new HashMap<>();
		/*
		 * Parse the language files.
		 * They are parsed in this order to fill the gap of missing translations.
		 */
		logger.info(language + "_" + country);
		/*
		 * bundle_de_DE.properties
		 */
		String path = "OSGI-INF/l10n/bundle_" + language + "_" + country + ".properties"; // $NON-NLS-1$
		parseFile(path, bundle, false);
		/*
		 * bundle_de.properties
		 */
		path = "OSGI-INF/l10n/bundle_" + language + ".properties"; // $NON-NLS-1$
		parseFile(path, bundle, true); // true - do not override existing keys
		/*
		 * bundle.properties (default)
		 */
		path = "OSGI-INF/l10n/bundle.properties"; // $NON-NLS-1$
		parseFile(path, bundle, true); // true - do not override existing keys
	}

	/**
	 * Tries to get and parse the files. Messages will be added if the file is available.
	 * 
	 * @param path
	 * @param bundle
	 * @param checkKey
	 */
	private void parseFile(String path, Bundle bundle, boolean checkKey) {

		try {
			/*
			 * See:
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=145096
			 * Local installation, hence file: can be removed.
			 */
			URL url = FileLocator.toFileURL(bundle.getEntry(path));
			if(url != null) {
				String pathname = url.toString().replace("file:", "");
				File file = new File(pathname);
				if(file.exists()) {
					logger.trace("Parsing messages from file {}", file);
					addMessages(file, checkKey);
				} else {
					logger.warn("File doesn't exists: {}", file);
				}
			}
		} catch(Exception e) {
			/*
			 * Don't print the exception.
			 */
		}
	}

	/**
	 * Adds the messages from the given file to the message map.
	 * If checkKey is true, the message will be added only if there is no message stored yet.
	 * 
	 * @param file
	 * @param checkKey
	 */
	private void addMessages(File file, boolean checkKey) {

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET))) {
			;
			/*
			 * Import each entry.
			 */
			String line;
			while((line = bufferedReader.readLine()) != null) {
				/*
				 * Continue if the line is a comment.
				 */
				if(line.startsWith(COMMENT)) {
					continue;
				} else {
					String[] message = line.split(DELIMITER, 2);
					if(message.length == 2) {
						String key = message[0].trim();
						String value = message[1].trim();
						/*
						 * If checkKey = true
						 * add the value only of the key doesn't exists.
						 */
						boolean addKey = true;
						if(checkKey) {
							if(messageMap.containsKey(key)) {
								addKey = false;
							}
						}
						/*
						 * It could be that the key should be not added.
						 */
						if(addKey) {
							messageMap.put(key, value);
						}
					}
				}
			}
		} catch(IOException e) {
			logger.warn("reading file {} failed", file, e);
		}
	}
}
