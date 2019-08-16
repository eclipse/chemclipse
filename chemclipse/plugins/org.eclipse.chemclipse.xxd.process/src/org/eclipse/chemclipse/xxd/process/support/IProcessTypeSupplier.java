/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - add getProcessorSupplier method
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.util.List;

public interface IProcessTypeSupplier {

	String getCategory();

	List<IProcessSupplier> getProcessorSuppliers();

	default IProcessSupplier getProcessorSupplier(String id) {

		for(IProcessSupplier supplier : getProcessorSuppliers()) {
			if(supplier.getId().equals(id)) {
				return supplier;
			}
		}
		return null;
	}
}
