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

import junit.framework.TestCase;

public class MagicNumberMatcher_12_ITest extends TestCase {

	private class MagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

		@Override
		public boolean checkFileFormat(File file) {

			return checkFileName(file, "(.*\\.)(R|r)([0-9][0-9])");
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

		File file = new File("test.R01");
		file.createNewFile();
		assertTrue(magicNumberMatcher.checkFileFormat(file));
		file.delete();
	}

	public void test2() throws IOException {

		File file = new File("test.R02");
		file.createNewFile();
		assertTrue(magicNumberMatcher.checkFileFormat(file));
		file.delete();
	}

	public void test3() throws IOException {

		File file = new File("test.r01");
		file.createNewFile();
		assertTrue(magicNumberMatcher.checkFileFormat(file));
		file.delete();
	}

	public void test4() throws IOException {

		File file = new File("test.r02");
		file.createNewFile();
		assertTrue(magicNumberMatcher.checkFileFormat(file));
		file.delete();
	}

	public void test5() throws IOException {

		File file = new File("test.R0");
		file.createNewFile();
		assertFalse(magicNumberMatcher.checkFileFormat(file));
		file.delete();
	}

	public void test6() throws IOException {

		File file = new File("test.r0");
		file.createNewFile();
		assertFalse(magicNumberMatcher.checkFileFormat(file));
		file.delete();
	}
}
