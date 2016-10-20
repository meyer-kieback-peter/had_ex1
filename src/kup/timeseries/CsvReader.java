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

package kup.timeseries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CsvReader {
	private final File source;
	private final String name;
	private String delimiter = ";";
	private String dateFormat = "dd.MM.yyyy HH:mm:ss";
	private int dateCol = 0;
	private int valueCol = 1;
	private int startRow = 1;
	
	public CsvReader(File source, String name) {
		this.source = source;
		this.name = name;
	}
	
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public int getDateCol() {
		return dateCol;
	}

	public void setDateCol(int dateCol) {
		this.dateCol = dateCol;
	}

	public int getValueCol() {
		return valueCol;
	}

	public void setValueCol(int valueCol) {
		this.valueCol = valueCol;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public TimeSeries read() throws IOException, ParseException, TimeSeriesException {
		TimeSeries ret = new TimeSeries(name);
		BufferedReader br = new BufferedReader(new FileReader(source));
		String line = "";
		int rowCnt = 0;
		while ((line = br.readLine()) != null) {
			if (rowCnt >= startRow) {
				String[] s = line.split(delimiter);
				if (dateCol >= s.length || valueCol >= s.length) {
					br.close();
					throw new ParseException("too few columns", rowCnt);
				}
				SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			    Date date = formatter.parse(s[dateCol]);
			    float value = Float.parseFloat(s[valueCol]);
			    DataPoint dp = new DataPoint(date, value);
			    ret.addDataPoint(dp);
			}
			rowCnt++;
		}
		br.close();
		return ret;
	}
	
}
