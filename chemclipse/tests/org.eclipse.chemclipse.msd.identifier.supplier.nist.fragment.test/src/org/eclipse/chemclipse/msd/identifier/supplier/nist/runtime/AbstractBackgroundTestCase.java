/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class AbstractBackgroundTestCase extends TestCase {

	List<String> parameterBackground = new ArrayList<>();

	@Override
	protected void setUp() throws Exception {

		parameterBackground.add(INistSupport.INSTRUMENT);
		parameterBackground.add(INistSupport.PAR2);
	}
}
