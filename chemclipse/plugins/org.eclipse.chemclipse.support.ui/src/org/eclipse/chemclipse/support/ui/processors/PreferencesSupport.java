/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferencesSupport {

	private final IPreferenceStore preferenceStore;
	private final IProcessSupplierContext context;
	private final Predicate<IProcessSupplier<?>> filter;
	private final String keyDefault;
	//
	private Set<IProcessSupplier<?>> processSuppliers;
	private String keyActive;

	public PreferencesSupport(IPreferenceStore preferenceStore, String key, IProcessSupplierContext context, Predicate<IProcessSupplier<?>> filter) {

		this.preferenceStore = preferenceStore;
		this.context = context;
		this.filter = filter;
		/*
		 * Use the default key as a fallback.
		 * Differentiate between specific editors MSD, CSD, WSD, ... .
		 */
		this.keyDefault = key;
		for(DataCategory dataCategory : DataCategory.values()) {
			preferenceStore.setDefault(getKeyActive(dataCategory), "");
		}
		//
		updateProcessSuppliers();
	}

	public void persist(List<Processor> processors) {

		updateProcessSuppliers();
		preferenceStore.setValue(keyActive, ProcessorSupport.getActiveProcessors(processors));
	}

	public List<Processor> getStoredProcessors() {

		updateProcessSuppliers();
		return ProcessorSupport.getProcessors(processSuppliers, getPreference());
	}

	/**
	 * Use getStoredProcessors() or persist() instead.
	 * 
	 * @return {@link IPreferenceStore}
	 */
	public IPreferenceStore getPreferenceStore() {

		return preferenceStore;
	}

	private String getPreference() {

		return preferenceStore.getString(keyActive);
	}

	private void updateProcessSuppliers() {

		processSuppliers = context.getSupplier(filter);
		this.keyActive = determineKeyActive(processSuppliers);
	}

	private String determineKeyActive(Set<IProcessSupplier<?>> processSuppliers) {

		for(IProcessSupplier<?> processSupplier : processSuppliers) {
			Set<DataCategory> dataCategories = processSupplier.getSupportedDataTypes();
			if(dataCategories.size() == 1) {
				return dataCategories.iterator().next().name();
			}
		}
		//
		return getKeyActive(null);
	}

	private String getKeyActive(DataCategory dataCategory) {

		if(dataCategory != null) {
			return keyDefault + "." + dataCategory.name();
		} else {
			return keyDefault;
		}
	}
}
