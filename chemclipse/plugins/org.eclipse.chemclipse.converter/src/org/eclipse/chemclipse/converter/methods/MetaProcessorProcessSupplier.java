package org.eclipse.chemclipse.converter.methods;

import java.util.function.BiFunction;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutor;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

final class MetaProcessorProcessSupplier extends AbstractProcessSupplier<MetaProcessorSettings> implements ProcessExecutor {

	private final IProcessMethod method;

	MetaProcessorProcessSupplier(String id, IProcessMethod method, MethodProcessTypeSupplier parent) {
		super(id, method.getName(), method.getDescription(), method.isFinal() ? null : MetaProcessorSettings.class, parent, MethodProcessTypeSupplier.getDataTypes(method));
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

		X settings = preferences.getSettings();
		if(settings instanceof MetaProcessorSettings) {
			MetaProcessorSettings processorSettings = (MetaProcessorSettings)settings;
			ProcessExecutionConsumer<?> callerDelegate = context.getContextObject(ProcessExecutionConsumer.class);
			if(callerDelegate != null) {
				ProcessEntryContainer.applyProcessEntries(method, context, new BiFunction<IProcessEntry, IProcessSupplier<X>, ProcessorPreferences<X>>() {

					@Override
					public ProcessorPreferences<X> apply(IProcessEntry entry, IProcessSupplier<X> supplier) {

						return processorSettings.getProcessorPreferences(entry, entry.getPreferences(supplier));
					}
				}, callerDelegate);
			}
		}
	}
}