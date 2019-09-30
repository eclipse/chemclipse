/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.supplier.IMeasurementProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class MethodProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "User Methods";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
		for(IProcessMethod processMethod : userMethods) {
			list.add(new MethodProcessSupplier(processMethod, this));
		}
		return list;
	}

	private static final class MethodProcessSupplier extends AbstractProcessSupplier<Void> implements IMeasurementProcessSupplier<Void>, IChromatogramSelectionProcessSupplier<Void> {

		private IProcessMethod method;

		public MethodProcessSupplier(IProcessMethod method, IProcessTypeSupplier parent) {
			super("ProcessMethod." + method.getUUID(), method.getName(), method.getDescription(), null, parent, getDataTypes(method));
			this.method = method;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, Void processSettings, ProcessExecutionContext context) {

			return IChromatogramSelectionProcessSupplier.applyProcessMethod(chromatogramSelection, method, context);
		}

		@Override
		public Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, Void processSettings, ProcessExecutionContext context) {

			return IMeasurementProcessSupplier.applyProcessMethod(measurements, method, context);
		}
	}

	private static DataCategory[] getDataTypes(IProcessMethod method) {

		Set<DataCategory> categories = new HashSet<>();
		for(IProcessEntry entry : method) {
			List<DataType> dataTypes = entry.getSupportedDataTypes();
			for(DataType dataType : dataTypes) {
				categories.add(dataType.toDataCategory());
			}
		}
		return categories.toArray(new DataCategory[0]);
	}
}
