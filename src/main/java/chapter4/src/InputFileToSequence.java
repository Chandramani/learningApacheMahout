package chapter4.src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;


public class InputFileToSequence {
	
	public static void main(String args[]) throws IOException
	{
		if (args.length != 2) {
			System.out.println("Arguments: [input tsv file] [output sequence file]");
			return;
		}
		Configuration conf = new Configuration();
        conf.addResource(new Path("/usr/local/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("/usr/local/hadoop/conf/hdfs-site.xml"));
		FileSystem fs = FileSystem.get(conf);
		
		Path inFileDir = new Path(args[0]);
		Path outFileDir = new Path(args[1]);
		
		if (!fs.exists(inFileDir))
		{
			  System.out.println("Input file not found");
			  return;
		}
		if (!fs.isFile(inFileDir))
		{
			System.out.println("Input should be a file");
		}
		
			if (fs.exists(outFileDir))
			{
				System.out.println("Output already exists");
				return;
			}
			FSDataInputStream in = fs.open(inFileDir);
			FSDataOutputStream out = fs.create(outFileDir);
			
			int bytesRead;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
				}
			in.close();
			out.close();
	}

}
