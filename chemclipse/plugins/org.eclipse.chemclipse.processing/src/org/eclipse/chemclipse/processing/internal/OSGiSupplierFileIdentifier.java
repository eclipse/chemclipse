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
 * Philip Wenig - fixed remove operation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.chemclipse.processing.converter.AbstractSupplierFileIdentifier;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.processing.converter.SupplierContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = {ISupplierFileIdentifier.class})
public class OSGiSupplierFileIdentifier extends AbstractSupplierFileIdentifier implements ISupplierFileIdentifier {

	private final List<SupplierContext> supplierContexts = new CopyOnWriteArrayList<>();

	public OSGiSupplierFileIdentifier() {

		super(new CopyOnWriteArrayList<>());
	}

	@Override
	public String getType() {

		return "Chemclipse";
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addSupplierContext(SupplierContext supplierContext) {

		supplierContexts.add(supplierContext);
	}

	public void removeSupplierContext(SupplierContext supplierContext) {

		supplierContexts.remove(supplierContext);
	}

	@Override
	public Collection<ISupplier> getSupplier() {

		List<ISupplier> list = new ArrayList<>();
		for(SupplierContext context : supplierContexts) {
			list.addAll(context.getSupplier());
		}
		return list;
	}
}
