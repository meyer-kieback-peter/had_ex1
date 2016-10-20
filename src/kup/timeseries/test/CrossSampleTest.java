/**
 * BaaS heating anomaly detection example
 * 
 * copyright Kieback&Peter GmbH & Co KG (2016) http://www.kieback-peter.de/
 * 
 * Sie können diese Datei unter folgenden Bedingungen weiterverwenden:
 * 
 * Die Datei wurde unter der Lizenz
 * „Creative Commons Namensnennung-Weitergabe unter gleichen Bedingungen Deutschland“
 * in Version 3.0 (abgekürzt „CC-by-sa 3.0/de“) veröffentlicht.
 * 
 * Den rechtsverbindlichen Lizenzvertrag finden Sie unter http://creativecommons.org/licenses/by-sa/3.0/de/legalcode.
 * 
 * Es folgt eine vereinfachte Zusammenfassung des Vertrags in allgemeinverständlicher Sprache ohne juristische Wirkung.
 * 
 * Es ist Ihnen gestattet,
 * - das Werk zu vervielfältigen, zu verbreiten und öffentlich zugänglich zu machen sowie
 * - Abwandlungen und Bearbeitungen des Werkes anzufertigen,
 * 
 * sofern Sie folgende Bedingungen einhalten:
 * Namensnennung: Sie müssen den Urheber bzw. den Rechteinhaber in der von ihm festgelegten Weise, die URI (z. B. die Internetadresse dieser Seite) sowie den Titel des Werkes und bei einer Abwandlung einen Hinweis darauf angeben.
 *
 * Weitergabe unter gleichen Bedingungen: Wenn Sie das lizenzierte Werk bearbeiten, abwandeln oder als Vorlage für ein neues Werk verwenden, dürfen Sie die neu entstandenen Werke nur unter dieser oder einer zu dieser kompatiblen Lizenz nutzen und weiterverbreiten.
 * 
 * Lizenzangabe: Sie müssen anderen alle Lizenzbedingungen mitteilen, die für dieses Werk gelten. Am einfachsten ist es, wenn Sie dazu einen Link auf den Lizenzvertrag (siehe oben) einbinden.
 *
 * Bitte beachten Sie, dass andere Rechte die Weiterverwendung einschränken können.
 * 
 */

package kup.timeseries.test;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.Vector;

import org.junit.Test;

import kup.timeseries.CrossSample;
import kup.timeseries.CrossSampleException;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;

public class CrossSampleTest {

	@Test
	public void testCrossSample() throws TimeSeriesException, CrossSampleException {
		GregorianCalendar cal = DataPointTest.magicDate();

		Vector<TimeSeries> tsin = new Vector<TimeSeries>(); 
		int[] ts1Val = {2, 3, 5, 10};
		tsin.add(TimeSeriesInterpolationTest.createTestTimeSeries("ts1", cal, ts1Val));
		int[] ts2Val = {1, 2, 3, 9, 11};
		tsin.add(TimeSeriesInterpolationTest.createTestTimeSeries("ts2", cal, ts2Val));
		int[] ts3Val = {0, 2, 3, 7, 8, 12};
		tsin.add(TimeSeriesInterpolationTest.createTestTimeSeries("ts3", cal, ts3Val));

		int[] tsComp = {0, 1, 2, 3, 5, 7, 8, 9, 10, 11, 12};
		Vector<TimeSeries> tscomp = new Vector<TimeSeries>(); 
		tscomp.add(TimeSeriesInterpolationTest.createTestTimeSeries("ts1_cs", cal, tsComp));
		tscomp.add(TimeSeriesInterpolationTest.createTestTimeSeries("ts2_cs", cal, tsComp));
		tscomp.add(TimeSeriesInterpolationTest.createTestTimeSeries("ts3_cs", cal, tsComp));
		
		CrossSample cs = new CrossSample(tsin, "_cs"); 
		Vector<TimeSeries> tsout = cs.compute();
		assertEquals(tsout.size(), tsin.size());
		for (int i = 0; i<tsin.size(); i++) {
			assertEquals(tsout.elementAt(i).name(),tsin.elementAt(i).name()+"_cs");
			assertTrue(tsout.elementAt(i).equals(tscomp.elementAt(i)));
		}
	}

}
