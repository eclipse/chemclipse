/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

public class FilterHandlerShiftFastBackward extends AbstractFilterHandler implements IMillisecondsToShift {

	@Override
	public int getMillisecondsToShift() {

		return PreferenceSupplier.getMillisecondsToShiftFastBackward();
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		super.execute(part);
	}
}
