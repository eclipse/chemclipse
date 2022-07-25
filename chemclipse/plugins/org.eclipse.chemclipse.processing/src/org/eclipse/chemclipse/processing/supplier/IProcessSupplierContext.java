/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactorings
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.eclipse.chemclipse.processing.DataCategory;

public interface IProcessSupplierContext {

	/**
	 * Gets the {@link IProcessSupplier} for the given id from this context or <code>null</code> if no supplier exits for the id
	 * 
	 * @param id
	 * @return
	 */
	<T> IProcessSupplier<T> getSupplier(String id);

	/**
	 * 
	 * iterates all available {@link IProcessSupplier}
	 */
	void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer);

	default Set<IProcessSupplier<?>> getSupplier(Predicate<IProcessSupplier<?>> predicate) {

		Set<IProcessSupplier<?>> supplier = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
		visitSupplier(processSupplier -> {
			if(predicate.test(processSupplier)) {
				supplier.add(processSupplier);
			}
		});
		return supplier;
	}

	static Predicate<IProcessSupplier<?>> forDataTypes(Iterable<DataCategory> dataTypes) {

		if(dataTypes == null) {
			return test -> true;
		}
		//
		return new Predicate<IProcessSupplier<?>>() {

			@Override
			public boolean test(IProcessSupplier<?> processSupplier) {

				for(DataCategory category : dataTypes) {
					if(processSupplier.getSupportedDataTypes().contains(category)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	static Predicate<IProcessSupplier<?>> createDataCategoryPredicate(DataCategory... categories) {

		return supplier -> {
			if(supplier == null) {
				return false;
			}
			//
			Set<DataCategory> supportedDataTypes = supplier.getSupportedDataTypes();
			for(DataCategory dataCategory : categories) {
				if(supportedDataTypes.contains(dataCategory)) {
					return true;
				}
			}
			return false;
		};
	}
}
