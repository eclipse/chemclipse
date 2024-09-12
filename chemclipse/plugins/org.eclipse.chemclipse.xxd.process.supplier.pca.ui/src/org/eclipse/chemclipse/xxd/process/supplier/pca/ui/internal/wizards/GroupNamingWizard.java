/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

public class GroupNamingWizard extends Wizard implements IWizard {

	private GroupNamingWizardPage groupNamingWizardPage;
	private ISamplesPCA<IVariable, ISample> samples;

	public GroupNamingWizard(ISamplesPCA<IVariable, ISample> samples) {

		this.groupNamingWizardPage = new GroupNamingWizardPage(samples);
		this.samples = samples;
	}

	@Override
	public void addPages() {

		addPage(groupNamingWizardPage);
	}

	@Override
	public boolean performFinish() {

		List<ISample> groupSamples = this.groupNamingWizardPage.getGroupSamples();
		for(int i = 0; i < groupSamples.size(); i++) {
			samples.getSamples().get(i).setGroupName(groupSamples.get(i).getGroupName());
		}
		return true;
	}
}
