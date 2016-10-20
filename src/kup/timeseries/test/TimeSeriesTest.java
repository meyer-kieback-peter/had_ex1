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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;

public class TimeSeriesTest {

	@Test
	public void testAddDataPoint() throws TimeSeriesException {
		TimeSeries ts = new TimeSeries("ts");
		GregorianCalendar cal = DataPointTest.magicDate();
		for (int i= 0; i<10; i++) {
			cal.set(GregorianCalendar.SECOND, i);
			ts.addDataPoint(new DataPoint(cal.getTime(), (float)i));
		}
		assertEquals(10,ts.size());
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test(expected = TimeSeriesException.class)
	public void testAddDuplicateDataPoint() throws TimeSeriesException {
		TimeSeries ts = new TimeSeries("ts1");
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(2016, 3, 10, 14, 8, 0);
		ts.addDataPoint(new DataPoint(cal.getTime(), 42.0f));
		ts.addDataPoint(new DataPoint(cal.getTime(), 43.0f));
		thrown.expect(IndexOutOfBoundsException.class);
	}

	@Test
	public void testEqual() throws TimeSeriesException {
		GregorianCalendar cal = DataPointTest.magicDate();

		TimeSeries ts1 = new TimeSeries("ts");
		TimeSeries ts2 = new TimeSeries("ts");
		TimeSeries ts3 = new TimeSeries("ts");
		TimeSeries ts4 = new TimeSeries("ts");
		
		for (int i= 0; i<10; i++) {
			cal.set(GregorianCalendar.SECOND, i);
			ts1.addDataPoint(new DataPoint(cal.getTime(), (float)i));
			ts2.addDataPoint(new DataPoint(cal.getTime(), (float)i));
			if (i != 4) {
				ts3.addDataPoint(new DataPoint(cal.getTime(), (float)i));
			}
			if (i == 5) {
				ts4.addDataPoint(new DataPoint(cal.getTime(), (float)i+1));
			} else {
				ts4.addDataPoint(new DataPoint(cal.getTime(), (float)i));
			}
		}
		assertEquals(10,ts1.size());
		assertTrue(ts1.equals(ts2));
		assertFalse(ts1.equals(ts3));
		assertFalse(ts1.equals(ts4));
	}
	
}
