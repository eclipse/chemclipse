/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms;

import java.util.ArrayList;
import java.util.List;

public class ProteomsUtil {

	public static final List<?> EMPTY_LIST = new ArrayList<>(0);
	public static final Object[] EMPTY_ARRAY = new Object[0];
	private static long lastUniqueID = -1;

	/**
	 * Unique ID based on time.
	 * 
	 * @return
	 */
	public static synchronized long nextUniqueID() {

		long t = System.nanoTime();
		while(t == lastUniqueID) {
			try {
				Thread.sleep(1);
			} catch(InterruptedException ignore) {
			}
			t = System.currentTimeMillis();
		}
		lastUniqueID = t;
		return t;
	}
}
