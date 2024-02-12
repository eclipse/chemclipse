/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

public abstract class AbstractProcessEntryContainer implements ProcessEntryContainer {

	/*
	 * The flag supportResume shall be persisted. By default, it is false, due to the following reasons:
	 * - Allow backward compatibility
	 * - Some meta methods must be executed at a whole
	 * So, the user explicitly has to define that a method may support the resume action.
	 * The selected resume index shall be not persisted as the user shall be asked each time
	 * to select the entry point if needed.
	 */
	private boolean supportResume = false;
	private int resumeIndex = ProcessEntryContainer.DEFAULT_RESUME_INDEX; // transient

	@Override
	public boolean isSupportResume() {

		return supportResume;
	}

	@Override
	public void setSupportResume(boolean supportResume) {

		this.supportResume = supportResume;
	}

	@Override
	public int getResumeIndex() {

		return resumeIndex;
	}

	@Override
	public void setResumeIndex(int resumeIndex) {

		this.resumeIndex = resumeIndex;
	}
}
