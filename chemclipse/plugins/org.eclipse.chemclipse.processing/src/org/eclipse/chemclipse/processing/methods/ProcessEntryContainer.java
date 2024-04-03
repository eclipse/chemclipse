/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support instruments
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.processing.methods.SubProcessExecutionConsumer.SubProcess;
import org.eclipse.chemclipse.processing.supplier.IProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;

/**
 * A {@link ProcessEntryContainer} holds some {@link IProcessEntry}s
 *
 */
public interface ProcessEntryContainer extends Iterable<IProcessEntry> {

	/**
	 * Empty string "" is used for backward compatibility.
	 * Don't change this.
	 */
	String DEFAULT_PROFILE = "Default Profile";
	int DEFAULT_RESUME_INDEX = 0; // Process all items.

	/**
	 * 
	 * @return an informative name describing the container
	 */
	String getName();

	/**
	 * Returns the active profile.
	 * 
	 * @return String
	 */
	String getActiveProfile();

	/**
	 * Sets the active profile.
	 * 
	 * @param activeProfile
	 */
	void setActiveProfile(String activeProfile);

	void addProfile(String profile);

	void deleteProfile(String profile);

	/**
	 * Returns the unmodifiable set of profiles.
	 * 
	 * @return Set<String>
	 */
	Set<String> getProfiles();

	/**
	 * return an informative description of this container
	 */
	default String getDescription() {

		return "";
	}

	/**
	 * This flag defines if the process method supports the resume operation.
	 * Both resume and profile selection will be checked.
	 * 
	 * This option needs to be activated on purpose.
	 * 
	 * @return boolean
	 */
	boolean isSupportResume();

	void setSupportResume(boolean supportResume);

	int getResumeIndex();

	void setResumeIndex(int resumeIndex);

	int getNumberOfEntries();

	/**
	 * Compares that this all contained {@link IProcessEntry}s are equal to the other one given, the default implementation works as follows:
	 * <ol>
	 * <li>if other is null, and this container does not contains any entry, <code>true</code> is returned</li>
	 * <li>if any entry is not contentEquals to the other one <code>false</code> is returned</li>
	 * <li>if any of the iterator return more elements than the other <code>false</code> is returned
	 * </ol>
	 * 
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare different instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1}
	 * 
	 * @param other
	 * @return
	 */
	default boolean entriesEquals(ProcessEntryContainer other) {

		Iterator<IProcessEntry> thisEntries = iterator();
		if(other == null) {
			return !thisEntries.hasNext();
		}
		Iterator<IProcessEntry> otherEntries = other.iterator();
		while(thisEntries.hasNext() && otherEntries.hasNext()) {
			IProcessEntry thisEntry = thisEntries.next();
			IProcessEntry otherEntry = otherEntries.next();
			if(!thisEntry.contentEquals(otherEntry)) {
				return false;
			}
		}
		if(otherEntries.hasNext() || thisEntries.hasNext()) {
			// not all where consumed
			return false;
		}
		return true;
	}

	static <X, T> T applyProcessEntries(ProcessEntryContainer container, ProcessExecutionContext context, IProcessExecutionConsumer<T> consumer) {

		return applyProcessEntries(container, context, new BiFunction<IProcessEntry, IProcessSupplier<X>, IProcessorPreferences<X>>() {

			@Override
			public IProcessorPreferences<X> apply(IProcessEntry processEntry, IProcessSupplier<X> processSupplier) {

				return processEntry.getPreferences(processSupplier);
			}
		}, consumer);
	}

	static <X, T> T applyProcessEntries(ProcessEntryContainer container, ProcessExecutionContext context, BiFunction<IProcessEntry, IProcessSupplier<X>, IProcessorPreferences<X>> preferenceSupplier, IProcessExecutionConsumer<T> consumer) {

		int resumeIndex = container.isSupportResume() ? container.getResumeIndex() : DEFAULT_RESUME_INDEX;
		//
		int index = -1;
		for(IProcessEntry processEntry : container) {
			/*
			 * Resume method at a given position?
			 */
			index++;
			if(index < resumeIndex) {
				continue;
			}
			/*
			 * Validation
			 */
			IProcessSupplier<X> processor = context.getSupplier(processEntry.getProcessorId());
			if(processor == null) {
				context.addWarnMessage(processEntry.getName(), "The processor was not found, the execution wil be skipped.");
				continue;
			}
			/*
			 * Process
			 */
			try {
				IProcessorPreferences<X> processorPreferences = preferenceSupplier.apply(processEntry, processor);
				context.setContextObject(IProcessEntry.class, processEntry);
				context.setContextObject(IProcessSupplier.class, processor);
				context.setContextObject(IProcessExecutionConsumer.class, consumer);
				context.setContextObject(IProcessorPreferences.class, processorPreferences);
				ProcessExecutionContext entryContext = context.split(processor.getContext());
				//
				try {
					if(processEntry.getNumberOfEntries() > 0) {
						/*
						 * Combined method
						 */
						IProcessSupplier.applyProcessor(processorPreferences, new SubProcessExecutionConsumer<T>(consumer, new SubProcess<T>() {

							@Override
							public <SubX> void execute(IProcessorPreferences<SubX> preferences, IProcessExecutionConsumer<T> parent, ProcessExecutionContext subcontext) {

								applyProcessEntries(processEntry, subcontext, preferenceSupplier, parent);
							}
						}), entryContext);
					} else {
						/*
						 * Simple method
						 */
						IProcessSupplier.applyProcessor(processorPreferences, consumer, entryContext);
					}
				} finally {
					context.setContextObject(IProcessSupplier.class, null);
					context.setContextObject(IProcessEntry.class, null);
					context.setContextObject(IProcessExecutionConsumer.class, null);
					context.setContextObject(IProcessorPreferences.class, null);
				}
			} catch(RuntimeException e) {
				context.addErrorMessage(processEntry.getName(), "Internal error when running the process method.", e);
			}
		}
		/*
		 * Result
		 */
		return consumer.getResult();
	}
}