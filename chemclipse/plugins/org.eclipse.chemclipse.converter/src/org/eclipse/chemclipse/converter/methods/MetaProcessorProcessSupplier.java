package org.eclipse.chemclipse.converter.methods;

import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

final class MetaProcessorProcessSupplier extends AbstractProcessSupplier<MetaProcessorSettings> implements ProcessExecutionConsumer<Void> {

	private final IProcessMethod method;

	MetaProcessorProcessSupplier(String bundle, IProcessMethod method, MethodProcessTypeSupplier parent) {
		super(bundle + "." + method.getUUID(), method.getName(), method.getDescription(), method.isFinal() ? null : MetaProcessorSettings.class, parent, MethodProcessTypeSupplier.getDataTypes(method));
		this.method = method;
	}

	@Override
	public String getCategory() {

		return method.getCategory();
	}

	public IProcessMethod getMethod() {

		return method;
	}

	@Override
	public <X> void execute(ProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

		ProcessExecutionConsumer<?> callerDelegate = context.getContextObject(ProcessExecutionConsumer.class);
		if(callerDelegate != null) {
			ProcessEntryContainer.applyProcessEntries(method, context, callerDelegate);
		}
	}
}