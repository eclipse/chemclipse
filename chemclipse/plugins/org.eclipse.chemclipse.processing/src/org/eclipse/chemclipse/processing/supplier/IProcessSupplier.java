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
package org.eclipse.chemclipse.processing.supplier;

import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.support.settings.parser.SettingsParser;

public interface IProcessSupplier<SettingType> {

	/**
	 * 
	 * @return the ID of this processor
	 */
	String getId();

	/**
	 * 
	 * @return the name of this processor
	 */
	String getName();

	default String getCategory() {

		return getTypeSupplier().getCategory();
	}

	/**
	 * 
	 * @return a brief description of this processor
	 */
	String getDescription();

	/**
	 * 
	 * @return the settingsclass
	 */
	Class<SettingType> getSettingsClass();

	/**
	 * 
	 * @return a set of supported datatypes
	 */
	Set<DataCategory> getSupportedDataTypes();

	/**
	 * 
	 * @return the settingsparser for this supplier
	 */
	SettingsParser<SettingType> getSettingsParser();

	/**
	 * 
	 * @return the {@link IProcessTypeSupplier} this {@link IProcessSupplier} belongs to
	 */
	IProcessTypeSupplier getTypeSupplier();

	/**
	 * This method should be used when trying to find a processor that matches a stored id in favor of comparing the id directly because a processor might want to support old id names for backward compatibility
	 * 
	 * @param id
	 * @return <code>true</code> if the id matches this processor false otherwise
	 */
	default boolean matchesId(String id) {

		return getId().equals(id);
	}

	/**
	 * 
	 * @return the context that belongs to this supplier, this could either be the supplier itself or the corresponding {@link IProcessTypeSupplier}
	 */
	default ProcessSupplierContext getContext() {

		if(this instanceof ProcessSupplierContext) {
			return (ProcessSupplierContext)this;
		} else {
			return getTypeSupplier();
		}
	}

	public static <X, T> T applyProcessor(ProcessorPreferences<X> processorPreferences, ProcessExecutionConsumer<T> consumer, ProcessExecutionContext context) {

		IProcessSupplier<X> supplier = processorPreferences.getSupplier();
		try {
			context.setContextObject(IProcessSupplier.class, supplier);
			context.setContextObject(ProcessorPreferences.class, processorPreferences);
			context.setContextObject(ProcessExecutionConsumer.class, consumer);
			int numberOfCalls = 0;
			boolean canDirectExecute = consumer.canExecute(processorPreferences);
			ProcessEntryContainer container = null;
			ProcessExecutionConsumer<?> supplierExecutionConsumer = null;
			if(canDirectExecute) {
				numberOfCalls++;
			}
			if(supplier instanceof ProcessEntryContainer) {
				container = (ProcessEntryContainer)supplier;
				numberOfCalls++;
			}
			if(supplier instanceof ProcessExecutionConsumer) {
				ProcessExecutionConsumer<?> processExecutionConsumer = (ProcessExecutionConsumer<?>)supplier;
				if(processExecutionConsumer.canExecute(processorPreferences)) {
					numberOfCalls++;
					supplierExecutionConsumer = processExecutionConsumer;
				}
			}
			boolean mustSplit = numberOfCalls > 1;
			if(mustSplit) {
				context.setWorkRemaining(numberOfCalls);
			}
			if(canDirectExecute) {
				consumer.execute(processorPreferences, mustSplit ? context.split() : context);
			}
			if(container != null) {
				// ProcessEntry Containers are always executed as is, with the default Entry Preferences
				ProcessEntryContainer.applyProcessEntries(container, mustSplit ? context.split() : context, consumer);
			}
			if(supplierExecutionConsumer != null) {
				// execution consumers might behave different, they get full control of further execution flow
				supplierExecutionConsumer.execute(processorPreferences, mustSplit ? context.split() : context);
			}
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch(Exception e) {
			context.addErrorMessage(supplier.getName(), "execution throws an error, processor is skipped", e);
		} finally {
			context.setContextObject(IProcessSupplier.class, null);
			context.setContextObject(ProcessorPreferences.class, null);
			context.setContextObject(ProcessExecutionConsumer.class, null);
		}
		return consumer.getResult();
	}
}