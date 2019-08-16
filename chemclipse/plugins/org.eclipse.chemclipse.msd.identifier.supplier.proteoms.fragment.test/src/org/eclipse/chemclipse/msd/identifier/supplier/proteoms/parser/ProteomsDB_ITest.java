/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.parser;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.TestPathHelper;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.db.ProteomsDB;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.AbstractSpectrum;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.Peak;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMSMS;
import org.junit.Test;

import junit.framework.TestCase;

public class ProteomsDB_ITest extends TestCase {

	private static final Logger log = Logger.getLogger(ProteomsDB_ITest.class);
	private List<SpectrumMS> msList;
	private String projectDir;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		msList = getSampleSpectrums();
		projectDir = TestPathHelper.getAbsolutePath(TestPathHelper.TEST_DIR_PROJECT_DB) + "test_project";
		File file = new File(projectDir);
		projectDir = file.getAbsolutePath();
		log.debug("Project dir " + projectDir);
		// projectDir = "C:/tmp/projectTest";
		file.mkdirs();
	}

	private static List<SpectrumMS> getSampleSpectrums() {

		ArrayList<SpectrumMS> msList = new ArrayList<>();
		{ // MS 1
			SpectrumMS ms1 = new SpectrumMS(1, "ms name 1");
			ms1.addPeak(new Peak(1.1, 2.2));
			ms1.addPeak(new Peak(2.1, 3.2));
			SpectrumMSMS msms1 = new SpectrumMSMS();
			msms1.setId(11);
			msms1.setName("msms11");
			ms1.addMSMSchild(msms1);
			msList.add(ms1);
		}
		{ // MS 2
			SpectrumMS ms2 = new SpectrumMS(2, "ms name 2");
			ms2.addPeak(new Peak(1.1, 2.2));
			ms2.addPeak(new Peak(2.1, 3.2));
			msList.add(ms2);
		}
		return msList;
	}

	@Test
	public void testSaveRead() throws Exception {

		ProteomsDB db = new ProteomsDB(Paths.get(projectDir));
		db.saveAllSpectrumsMS(msList);
		List<SpectrumMS> msListReaded = db.getSpectrumsMS(true, true, true);
		assertEquals(msList.size(), msListReaded.size());
		sortSpectrumsAndPeaks(msList);
		sortSpectrumsAndPeaks(msListReaded);
		for(int i = 0; i < msList.size(); i++) {
			SpectrumMS ms1 = msList.get(i);
			SpectrumMS ms2 = msListReaded.get(i);
			log.debug("Test " + ms1.getId());
			assertNotNull(ms2);
			assertEquals(ms1.getId(), ms2.getId());
			assertEquals(ms1.getName(), ms2.getName());
			assertEquals(ms1.getNumberOfPeak(), ms2.getNumberOfPeak());
			assertEquals(ms1.getNumberOfMSMS(), ms2.getNumberOfMSMS());
			List<SpectrumMSMS> msms1list = ms1.getMsmsSpectrumsChildren();
			List<SpectrumMSMS> msms2list = ms2.getMsmsSpectrumsChildren();
			{
				if(msms1list != null)
					for(int j = 0; j < msms1list.size(); j++) {
						SpectrumMSMS msms1 = msms1list.get(j);
						SpectrumMSMS msms2 = msms2list.get(j);
						assertNotNull(msms1);
						assertNotNull(msms2);
						assertEquals(msms1.getId(), msms2.getId());
						assertEquals(msms1.getName(), msms2.getName());
						assertEquals(msms1.getNumberOfPeak(), msms2.getNumberOfPeak());
						assertEquals(msms1.hashCode(), msms2.hashCode());
						assertEquals(msms1.getPrecursorPeak(), msms2.getPrecursorPeak());
						assertEquals(msms1.getPeaks(), msms2.getPeaks());
					}
			}
		}
	}

	private void sortSpectrumsAndPeaks(List<SpectrumMS> msList) {

		Comparator<AbstractSpectrum> spectrumComparator = new Comparator<AbstractSpectrum>() {

			@Override
			public int compare(AbstractSpectrum o1, AbstractSpectrum o2) {

				return Long.compare(o1.getId(), o2.getId());
			}
		};
		Collections.sort(msList, spectrumComparator);
		for(SpectrumMS ms : msList) {
			List<Peak> peaks = ms.getPeaks();
			if(peaks != null) {
				Collections.sort(peaks, new Comparator<Peak>() {

					@Override
					public int compare(Peak o1, Peak o2) {

						return Double.compare(o1.getMz(), o2.getMz());
					}
				});
			}
			List<SpectrumMSMS> msmsList = ms.getMsmsSpectrumsChildren();
			if(msList != null) {
				Collections.sort(msmsList, spectrumComparator);
			}
		}
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		FileUtils.deleteDirectory(new File(projectDir));
	}
}
