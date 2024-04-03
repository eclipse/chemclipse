/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - improvements
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

import org.eclipse.chemclipse.logging.core.Logger;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public final class NodeProcessorPreferences<T> implements IProcessorPreferences<T> {

	private static final Logger logger = Logger.getLogger(NodeProcessorPreferences.class);
	//
	public static final String KEY_USE_SYSTEM_DEFAULTS = "useSystemDefaults";
	public static final String KEY_USER_SETTINGS = "userSettings";
	public static final String KEY_ASK_FOR_SETTINGS = "askForSettings";
	//
	private IProcessSupplier<T> supplier;
	private Preferences preferences;

	public NodeProcessorPreferences(IProcessSupplier<T> supplier, Preferences preferences) {

		this.supplier = supplier;
		this.preferences = preferences;
	}

	public Preferences getPreferences() {

		return preferences;
	}

	@Override
	public DialogBehavior getDialogBehaviour() {

		if(supplier.getSettingsClass() == null) {
			return DialogBehavior.NONE;
		}
		//
		trySync();
		boolean askForSettings = preferences.getBoolean(KEY_ASK_FOR_SETTINGS, true);
		if(askForSettings) {
			return DialogBehavior.SHOW;
		} else {
			return DialogBehavior.SAVED_DEFAULTS;
		}
	}

	@Override
	public void setAskForSettings(boolean askForSettings) {

		preferences.putBoolean(KEY_ASK_FOR_SETTINGS, askForSettings);
		tryFlush();
	}

	@Override
	public void setUserSettings(String settings) {

		preferences.put(KEY_USER_SETTINGS, settings);
		tryFlush();
	}

	@Override
	public void reset() {

		try {
			preferences.clear();
			tryFlush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}

	@Override
	public boolean isUseSystemDefaults() {

		if(supplier.getSettingsClass() == null) {
			return true;
		}
		//
		trySync();
		return preferences.getBoolean(KEY_USE_SYSTEM_DEFAULTS, true);
	}

	@Override
	public void setUseSystemDefaults(boolean useSystemDefaults) {

		preferences.putBoolean(KEY_USE_SYSTEM_DEFAULTS, useSystemDefaults);
		tryFlush();
	}

	@Override
	public String getUserSettingsAsString() {

		trySync();
		return preferences.get(KEY_USER_SETTINGS, "");
	}

	@Override
	public IProcessSupplier<T> getSupplier() {

		return supplier;
	}

	public void trySync() {

		try {
			preferences.sync();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}

	private void tryFlush() {

		try {
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}