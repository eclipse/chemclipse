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
package org.eclipse.chemclipse.support.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

/**
 * A {@link Wizard} that only shows a single page
 *
 */
public class SinglePageWizard extends Wizard {

	public SinglePageWizard(String title, WizardPage page) {
		this(title, false, page);
	}

	public SinglePageWizard(String title, boolean needsProgressMonitor, WizardPage page) {
		addPage(page);
		setWindowTitle(title);
		setNeedsProgressMonitor(needsProgressMonitor);
	}

	@Override
	public boolean performFinish() {

		return true;
	}
}
