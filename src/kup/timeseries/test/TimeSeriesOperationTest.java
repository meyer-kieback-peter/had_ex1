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

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import kup.timeseries.CrossSampleException;
import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;
import kup.timeseries.function.Sub;

public class TimeSeriesOperationTest {

	@Test
	public void testSub() throws TimeSeriesException, CrossSampleException {
		Calendar cal = DataPointTest.magicDate();
		int[] secVal = {1,3,42};
		TimeSeries ts1 = new TimeSeries("ts1");
		TimeSeries ts2 = new TimeSeries("ts2");
		TimeSeries expectedSub = new TimeSeries("sub");
		for (int i : secVal) {
			cal.set(GregorianCalendar.SECOND, i);
			ts1.addDataPoint(new DataPoint(cal.getTime(), 3.0f*i));
			ts2.addDataPoint(new DataPoint(cal.getTime(), 2.0f*i));
			expectedSub.addDataPoint(new DataPoint(cal.getTime(), i));
		}

		Sub sub = new Sub(ts1, ts2);
		TimeSeries tsSub = sub.compute();
		assertEquals(tsSub, expectedSub);
	}
}
