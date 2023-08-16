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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.support.ui.processors.Processor;
import org.eclipse.chemclipse.support.ui.processors.ProcessorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferencesProcessSupport {

	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IProcessSupplierContext processSupplierContext = new ProcessTypeSupport();
	private Set<IProcessSupplier<?>> processSuppliers = new HashSet<>();
	//
	private Predicate<IProcessSupplier<?>> predicateProcessSupplier;
	private DataCategory dataCategory = DataCategory.AUTO_DETECT;

	public PreferencesProcessSupport(DataCategory dataCategory) {

		this.dataCategory = dataCategory;
		this.predicateProcessSupplier = new Predicate<IProcessSupplier<?>>() {

			@Override
			public boolean test(IProcessSupplier<?> processSupplier) {

				return processSupplier.getSupportedDataTypes().contains(getDataCategory());
			}
		};
		//
		updateProcessSuppliers();
	}

	public DataCategory getDataCategory() {

		return dataCategory;
	}

	public void setDataCategory(DataCategory dataCategory) {

		this.dataCategory = dataCategory;
	}

	public String persist(List<Processor> processors) {

		updateProcessSuppliers();
		return ProcessorSupport.getActiveProcessors(processors);
	}

	public List<Processor> getActiveProcessors() {

		updateProcessSuppliers();
		String settings = preferenceStore.getString(PreferenceConstants.P_QUICK_ACCESS_PROCESSORS + dataCategory.name());
		return ProcessorSupport.getActiveProcessors(processSuppliers, settings);
	}

	private void updateProcessSuppliers() {

		processSuppliers.clear();
		processSuppliers.addAll(processSupplierContext.getSupplier(predicateProcessSupplier));
	}
}