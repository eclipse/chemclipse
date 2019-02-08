/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.converter.TestPathHelper;

import junit.framework.TestCase;

public class MagicNumberMatcher_11_ITest extends TestCase {

	private static final Map<Integer, Byte> INDEX_MAP = new HashMap<>();
	static {
		INDEX_MAP.put(0, (byte)0xF0);
		INDEX_MAP.put(5, (byte)0x9F);
		INDEX_MAP.put(7, (byte)0x3B);
	}

	private class MagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

		@Override
		public boolean checkFileFormat(File file) {

			return checkPattern(file, 7, INDEX_MAP);
		}
	}

	private IMagicNumberMatcher magicNumberMatcher;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		magicNumberMatcher = new MagicNumberMatcher();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() throws IOException {

		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_BIN_TEST));
		assertFalse(magicNumberMatcher.checkFileFormat(file));
	}
}
