/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.evaluation;

import java.util.List;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;

public interface IEvaluationWizardElements extends IChromatogramWizardElements {

	String getDescription();

	void setDescription(String evaluationDescription);

	/**
	 * This method may return null.
	 * 
	 * @param category
	 * @return {@link IChromatogramProcessEntry}
	 */
	IChromatogramProcessEntry getProcessEntry(String category);

	List<IChromatogramProcessEntry> getProcessingEntries();

	String getNotes();

	void setNotes(String notes);
}
