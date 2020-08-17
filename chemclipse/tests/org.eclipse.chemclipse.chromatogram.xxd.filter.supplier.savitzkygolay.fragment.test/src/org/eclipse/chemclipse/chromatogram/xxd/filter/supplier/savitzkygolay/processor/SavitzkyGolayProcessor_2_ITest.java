/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class SavitzkyGolayProcessor_2_ITest extends TestCase {

	private ITotalScanSignals totalScanSignals;
	private ChromatogramFilterSettings supplierFilterSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Signals
		 */
		totalScanSignals = new TotalScanSignals(5726);
		BufferedReader reader = new BufferedReader(new FileReader(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1))));
		String line;
		while((line = reader.readLine()) != null) {
			String[] values = line.split(", ");
			ITotalScanSignal totalScanSignal = new TotalScanSignal(Integer.valueOf(values[0]), Float.valueOf(values[1]), Float.valueOf(values[2]));
			totalScanSignals.add(totalScanSignal);
		}
		reader.close();
		/*
		 * Processor and settings
		 */
		supplierFilterSettings = new ChromatogramFilterSettings();
		supplierFilterSettings.setWidth(9);
		supplierFilterSettings.setOrder(5);
		supplierFilterSettings.setDerivative(2);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(5726, sgTic.length);
	}

	public void test2() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(5293.248834498874d, sgTic[0]);
	}

	public void test3() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(2291.7514568764136d, sgTic[1]);
	}

	public void test4() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(752.2296037295941d, sgTic[2]);
	}

	public void test5() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(261.02942890442205d, sgTic[3]);
	}

	public void test6() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-380.4685314685271d, sgTic[289]);
	}

	public void test7() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(1808.3257575757625d, sgTic[892]);
	}

	public void test8() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(37.69871794872506d, sgTic[1293]);
	}

	public void test9() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(1410.2395104895122d, sgTic[1474]);
	}

	public void test10() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(5961.75641025642d, sgTic[2970]);
	}

	public void test11() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(2217.6532634032847d, sgTic[3766]);
	}

	public void test12() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(1234.2377622377862d, sgTic[4180]);
	}

	public void test13() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-494.48135198133605d, sgTic[4993]);
	}

	public void test14() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-772.391899766888d, sgTic[5722]);
	}

	public void test15() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-1393.1410256409508d, sgTic[5723]);
	}

	public void test16() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-1429.792249417107d, sgTic[5724]);
	}

	public void test17() {

		double[] sgTic = SavitzkyGolayProcessor.smooth(totalScanSignals, supplierFilterSettings, new NullProgressMonitor());
		assertEquals(-489.6917249416001d, sgTic[5725]);
	}
}
