/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class MethodProcessSupportTestCase extends TestCase {

	protected IProcessMethod getProcessMethod(DataCategory[] methodCategories, List<DataCategory[]> processCategories) {

		ProcessMethod processMethod = getProcessMethod(methodCategories);
		for(DataCategory[] categories : processCategories) {
			processMethod.addProcessEntry(getProcessEntry(processMethod, categories));
		}
		//
		return processMethod;
	}

	private ProcessMethod getProcessMethod(DataCategory[] dataCategories) {

		return new ProcessMethod(new HashSet<>(Arrays.asList(dataCategories)));
	}

	private ProcessEntry getProcessEntry(IProcessMethod processMethod, DataCategory[] dataCategories) {

		ProcessEntry processEntry = new ProcessEntry(processMethod);
		for(DataCategory dataCategory : dataCategories) {
			processEntry.addDataCategory(dataCategory);
		}
		//
		return processEntry;
	}
}