/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.quickstart;

import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.IInputWizard;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.PeaksInputWizard;
import org.osgi.service.component.annotations.Component;

@Component(service = TileDefinition.class)
public class PeakTileDefinition extends WizardTile {

	@Override
	public String getTitle() {

		return "Peaks";
	}

	@Override
	protected IInputWizard createWizard() {

		return new PeaksInputWizard();
	}
}