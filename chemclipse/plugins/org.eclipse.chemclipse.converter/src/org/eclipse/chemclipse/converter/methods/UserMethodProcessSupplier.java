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
 * Philip Wenig - add profile support
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessExecutor;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;

public final class UserMethodProcessSupplier extends AbstractProcessSupplier<Void> implements ProcessEntryContainer, IProcessExecutor {

	private static final String SKIP_MESSAGE = "SKIP CHECK: Is this method used?";
	//
	private final IProcessMethod processMethod;

	public UserMethodProcessSupplier(IProcessMethod processMethod, MethodProcessTypeSupplier processTypeSupplier) {

		super(getUserMethodID(processMethod), processMethod.getName(), processMethod.getDescription(), null, processTypeSupplier, MethodProcessSupport.getDataTypes(processMethod));
		this.processMethod = processMethod;
	}

	@Override
	public Iterator<IProcessEntry> iterator() {

		return processMethod.iterator();
	}

	@Override
	public int getNumberOfEntries() {

		return processMethod.getNumberOfEntries();
	}

	@Override
	public <X> void execute(IProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

		IProcessExecutionConsumer<?> consumer = context.getContextObject(IProcessExecutionConsumer.class);
		ProcessEntryContainer.applyProcessEntries(processMethod, context, consumer);
	}

	@Override
	public String getActiveProfile() {

		System.out.println(SKIP_MESSAGE);
		return "";
	}

	@Override
	public void setActiveProfile(String activeProfile) {

		System.out.println(SKIP_MESSAGE);
	}

	@Override
	public void addProfile(String profile) {

		System.out.println(SKIP_MESSAGE);
	}

	@Override
	public void deleteProfile(String profile) {

		System.out.println(SKIP_MESSAGE);
	}

	@Override
	public Set<String> getProfiles() {

		System.out.println(SKIP_MESSAGE);
		return Collections.emptySet();
	}

	@Override
	public boolean isSupportResume() {

		return processMethod.isSupportResume();
	}

	@Override
	public void setSupportResume(boolean supportResume) {

		System.out.println(SKIP_MESSAGE);
	}

	@Override
	public int getResumeIndex() {

		return processMethod.getResumeIndex();
	}

	@Override
	public void setResumeIndex(int resumeIndex) {

		System.out.println(SKIP_MESSAGE);
	}

	private static String getUserMethodID(IProcessMethod method) {

		File sourceFile = method.getSourceFile();
		if(sourceFile != null) {
			return MethodProcessSupport.getID(method, "user:" + sourceFile.getName());
		}
		//
		return MethodProcessSupport.getID(method, "user");
	}
}