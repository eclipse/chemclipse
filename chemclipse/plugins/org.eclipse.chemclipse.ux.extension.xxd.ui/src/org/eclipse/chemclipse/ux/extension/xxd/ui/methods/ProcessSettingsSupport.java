/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - move method to open wizard, refactor for new settings
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.NodeProcessorPreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ProcessSettingsSupport {

	private static IEclipsePreferences preferences = null;

	public static <T> IProcessorPreferences<T> getWorkspacePreferences(IProcessSupplier<T> supplier) {

		return new NodeProcessorPreferences<>(supplier, getEclipsePreferences().node(supplier.getId()));
	}

	public static Collection<IProcessorPreferences<?>> getPreferences(IProcessSupplierContext context) {

		return getPreferences(context, false);
	}

	public static Collection<IProcessorPreferences<?>> getPreferences(IProcessSupplierContext context, boolean dynamicPreferencesOnly) {

		List<IProcessorPreferences<?>> processorPreferences = new ArrayList<>();
		try {
			IEclipsePreferences eclipsePreferences = getEclipsePreferences();
			String[] childrenNames = eclipsePreferences.childrenNames();
			for(String childrenName : childrenNames) {
				/*
				 * Preferences
				 * Skip empty nodes.
				 */
				Preferences preferences = eclipsePreferences.node(childrenName);
				if(preferences.keys().length == 0) {
					continue;
				}
				//
				IProcessSupplier<?> processSupplier = context.getSupplier(childrenName);
				if(processSupplier != null) {
					if(!dynamicPreferencesOnly) {
						processorPreferences.add(new NodeProcessorPreferences<>(processSupplier, preferences));
					}
				} else {
					String id = preferences.name();
					processSupplier = new DynamicProcessSupplier(id, id, "This is a dynamic process method.");
					processorPreferences.add(new NodeProcessorPreferences<>(processSupplier, preferences));
				}
			}
		} catch(BackingStoreException e) {
		}
		//
		return processorPreferences;
	}

	private static IEclipsePreferences getEclipsePreferences() {

		if(preferences == null) {
			preferences = InstanceScope.INSTANCE.getNode(IProcessSupplier.class.getName());
		}
		//
		return preferences;
	}
}