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
 * Philip Wenig - access member variable, probably remove this context
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui;

import java.util.function.Consumer;

import javax.inject.Inject;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierFactory;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
public class E4ProcessSupplierContext implements ProcessSupplierContext {

	@Inject
	private ProcessSupplierContext processSupplierContext;
	@Inject
	private IEclipseContext eclipseContext;

	@SuppressWarnings("unchecked")
	@Override
	public <T> IProcessSupplier<T> getSupplier(String id) {

		IProcessSupplier<T> supplier = processSupplierContext.getSupplier(id);
		if(supplier != null) {
			Object factorySupplier = ContextInjectionFactory.invoke(supplier, ProcessSupplierFactory.class, eclipseContext, null);
			if(factorySupplier instanceof IProcessSupplier<?>) {
				ContextInjectionFactory.inject(factorySupplier, eclipseContext);
				return supplier.getClass().cast(factorySupplier);
			}
		}
		return supplier;
	}

	@Override
	public void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer) {

		processSupplierContext.visitSupplier(new Consumer<IProcessSupplier<?>>() {

			@Override
			public void accept(IProcessSupplier<?> supplier) {

				Object factorySupplier = ContextInjectionFactory.invoke(supplier, ProcessSupplierFactory.class, eclipseContext, null);
				if(factorySupplier instanceof IProcessSupplier<?>) {
					ContextInjectionFactory.inject(factorySupplier, eclipseContext);
					consumer.accept((IProcessSupplier<?>)factorySupplier);
				} else {
					consumer.accept(supplier);
				}
			}
		});
	}
}
