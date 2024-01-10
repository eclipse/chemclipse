/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.internal.preferences.PreferenceSupplierFallback;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public abstract class AbstractPreferenceSupplier implements IPreferenceSupplier {

	private static final Logger logger = Logger.getLogger(AbstractPreferenceSupplier.class);
	private static final Map<String, IPreferenceSupplier> INSTANCES = new HashMap<>();
	//
	private Map<String, String> defaultValues = new HashMap<String, String>();

	public AbstractPreferenceSupplier() {

		initialize();
		INSTANCES.put(getClass().getName(), this);
	}

	public static IPreferenceSupplier INSTANCE(Class<? extends IPreferenceSupplier> clazz) {

		String key = clazz.getName();
		IPreferenceSupplier preferenceSupplier = INSTANCES.get(key);
		if(preferenceSupplier == null) {
			/*
			 * The instance should have been added already via the constructor.
			 * If it has not been added for some reasons, create a new instance on-the-fly.
			 * The fallback case should never happen.
			 */
			try {
				preferenceSupplier = clazz.getDeclaredConstructor().newInstance();
				INSTANCES.put(key, preferenceSupplier);
			} catch(Exception e) {
				logger.warn("PreferenceSupplierFallback: " + key);
				preferenceSupplier = new PreferenceSupplierFallback();
			}
		}
		//
		return preferenceSupplier;
	}

	/**
	 * Initializes and persists the default values.
	 */
	public void initialize() {

		initializeDefaults();
		persistDefaults();
	}

	@Override
	public String getPostfix() {

		return "";
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public void putDefault(String key, Object def) {

		String value;
		if(def instanceof Boolean v) {
			value = Boolean.toString(v);
		} else if(def instanceof Byte v) {
			value = Byte.toString(v);
		} else if(def instanceof Short v) {
			value = Short.toString(v);
		} else if(def instanceof Integer v) {
			value = Integer.toString(v);
		} else if(def instanceof Long v) {
			value = Long.toString(v);
		} else if(def instanceof Float v) {
			value = Float.toString(v);
		} else if(def instanceof Double v) {
			value = Double.toString(v);
		} else if(def instanceof String v) {
			value = v;
		} else {
			value = def.toString();
		}
		/*
		 * Put
		 */
		defaultValues.put(key, value);
	}

	@Override
	public Map<String, String> getDefaultValues() {

		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}
}