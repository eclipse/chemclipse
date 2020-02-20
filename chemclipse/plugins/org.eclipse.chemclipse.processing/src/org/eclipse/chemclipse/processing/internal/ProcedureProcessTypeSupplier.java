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
 *******************************************************************************/
package org.eclipse.chemclipse.processing.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.procedures.Procedure;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ExecutionResultTransformer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
// import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

// @Component(service = {IProcessTypeSupplier.class})
public class ProcedureProcessTypeSupplier implements IProcessTypeSupplier {

	private ProcessSupplierContext context;
	private final List<Procedure<?>> procedures = new CopyOnWriteArrayList<Procedure<?>>();

	@Override
	public String getCategory() {

		return "Procedures";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		for(Procedure<?> procedure : procedures) {
			list.add(new ProcedureProcessSupplier<>(procedure));
		}
		return list;
	}

	@Reference(unbind = "-")
	public void setProcessSupplierContext(ProcessSupplierContext context) {

		this.context = context;
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addProcedure(Procedure<?> procedure) {

		procedures.add(procedure);
	}

	public void removeProcedure(Procedure<?> procedure) {

		procedures.remove(procedure);
	}

	private final class ProcedureProcessSupplier<ConfigType> extends AbstractProcessSupplier<ConfigType> implements ProcessSupplierContext, ExecutionResultTransformer<ConfigType> {

		private final Procedure<ConfigType> procedure;

		public ProcedureProcessSupplier(Procedure<ConfigType> procedure) {
			super(procedure.getID(), procedure.getName(), procedure.getDescription(), procedure.getConfigClass(), ProcedureProcessTypeSupplier.this, procedure.getDataCategories());
			this.procedure = procedure;
		}

		@Override
		public SupplierType getType() {

			return SupplierType.STRUCTURAL;
		}

		@Override
		public <T> IProcessSupplier<T> getSupplier(String id) {

			IProcessSupplier<T> supplier = context.getSupplier(id);
			if(supplier != null && accepts(supplier)) {
				return supplier;
			}
			return null;
		}

		@Override
		public void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer) {

			context.getSupplier(this::accepts).forEach(consumer);
		}

		private boolean accepts(IProcessSupplier<?> supplier) {

			Set<DataCategory> supportedDataTypes = supplier.getSupportedDataTypes();
			for(DataCategory category : procedure.getDataCategories()) {
				if(supportedDataTypes.contains(category)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public <T> ProcessExecutionConsumer<T> transform(ProcessExecutionConsumer<T> consumer, ProcessorPreferences<ConfigType> processorPreferences, ProcessExecutionContext context) throws IOException {

			ProcessExecutionConsumer<T> executionConsumer = procedure.createConsumer(processorPreferences.getSettings(), consumer, context);
			if(executionConsumer == null) {
				return consumer;
			}
			return executionConsumer;
		}
	}
}
