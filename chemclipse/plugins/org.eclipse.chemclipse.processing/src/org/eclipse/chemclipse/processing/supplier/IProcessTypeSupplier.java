/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - add getProcessorSupplier method, relax return type of getProcessorSuppliers
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

import java.util.Collection;
import java.util.function.Consumer;

public interface IProcessTypeSupplier extends IProcessSupplierContext {

	String getCategory();

	Collection<IProcessSupplier<?>> getProcessorSuppliers();

	@SuppressWarnings("unchecked")
	@Override
	default <T> IProcessSupplier<T> getSupplier(String id) {

		for(IProcessSupplier<?> supplier : getProcessorSuppliers()) {
			if(supplier.matchesId(id)) {
				return (IProcessSupplier<T>)supplier;
			}
		}
		//
		return null;
	}

	@Override
	default void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer) {

		getProcessorSuppliers().forEach(consumer);
	}
}