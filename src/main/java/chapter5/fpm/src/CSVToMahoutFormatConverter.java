package chapter5.fpm.src;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class CSVToMahoutFormatConverter {
	public static void main(String args[]) throws Exception {
		
		String data_dir="data/chapter5/";
		String csvFilename = data_dir+"marketbasket.csv";

		BufferedReader csvReader = new BufferedReader(new FileReader(csvFilename));

		String line = csvReader.readLine();
		String[] tokens = line.split(",");
		FileWriter mappingWriter = new FileWriter(data_dir+"item_mapping.csv");
		int itemID = 0;
		for(int idx=1;idx<tokens.length;idx++) { 
			// loops starts from 1 to ignore the first column element
			mappingWriter.write(tokens[idx].trim() + "," + itemID + "\n");
			itemID++;
		}
		mappingWriter.close();
		csvReader.close();
		
		FileWriter datWriter = new FileWriter(data_dir+"marketbasket_converted.csv");
		csvReader = new BufferedReader(new FileReader(csvFilename));
		int transactionCount = 0;
		boolean isfirstLine=true;
		while(true) {
			line = csvReader.readLine();
			if (line == null) {
				break;
			}
			if(isfirstLine)
			{
				isfirstLine=false;
				continue;
			}
			tokens = line.split(",");
			itemID = 0;
			boolean isFirstElement = true;
			for(int idx=1;idx<tokens.length;idx++) { 
				if (tokens[idx].trim().equals("true")) {
					if (isFirstElement) {
						isFirstElement = false;
					} else {
						datWriter.append(",");
					}
					datWriter.append(Integer.toString(itemID));
				}
				itemID++;
			}
			datWriter.append("\n");
			transactionCount++;
		}
		datWriter.flush();
		datWriter.close();
		csvReader.close();
		System.out.println("Wrote " + transactionCount + " transactions.");
	}
}