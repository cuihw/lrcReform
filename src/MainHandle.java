import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainHandle {
	
	
	static MainHandle handle;
	
	
	String path = "D:\\tmp\\MP3LRC";
	
	static int MAX_LENGTH = 25;

	public static void main(String[] args) {
		System.out.println(args);

		System.out.println(args.length); 

		for (int i = 0; i < args.length; i++)
			System.out.println(args[i]);


		System.out.println(MAX_LENGTH); 

		handle = new MainHandle();

		handle.HandleFiles();
	}

	private void HandleFiles() {
		// TODO Auto-generated method stub
		File filePath = new File(path);
		
		if (filePath.exists() && filePath.isDirectory()) {
			
			readLrcFiles(filePath);
		}
	}

	private void readLrcFiles(File filePath) {
		// TODO Auto-generated method stub

		System.out.println(filePath.getAbsolutePath() + "is directory ");
		File [] filelist = filePath.listFiles();
		
		for (File file: filelist) {
			
			if (file.isDirectory()) {
				readLrcFiles(file);
			} else if (file.getAbsolutePath().endsWith(".lrc")) {

				System.out.println("handle file: " + file.getAbsolutePath());
				reLineFile(file);
				// break;
			}
			
		}
		
	}
	
	private List<String> lines = new ArrayList<>();

	private void reLineFile(File file) {
		
		// TODO Auto-generated method stub
		lines.clear();
		
		FileReader reader;
		BufferedReader br ;

		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				transfromString(line);
			}
			reader.close();
			br.close();
			
			writeText(file);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeText(File file) {
		FileOutputStream outSTr = null;
        BufferedOutputStream Buff = null;
        try {
			outSTr = new FileOutputStream(file);
	        Buff = new BufferedOutputStream(outSTr);
	        
	        
	        for (int i = 0; i < lines.size(); i++) {
                Buff.write(( lines.get(i) + "\r\n").getBytes());
            }
	        
	        Buff.flush();
            Buff.close();
            outSTr.close();
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void transfromString(String line) {
		if (line.startsWith("[0")) {

			TimeInfo timeinfo = new TimeInfo();

			getTime(timeinfo, line); 

			System.out.println("  min = " + timeinfo.min + " second = " +timeinfo.second +  " mills = " + timeinfo.mills);
			
			String text = getText(line);
			System.out.println("text = " + text);

			if (text.length() > MAX_LENGTH) {
				reformLine(timeinfo, text);	
			} else {
				lines.add(line);
			}
		} else {
			lines.add(line);
		}
	}

	private void reformLine(TimeInfo timeinfo, String text) {
		
		for(int i = MAX_LENGTH; i < text.length(); i-- ) {
			char chars = text.charAt(i);
			if (chars == ' ') {
				String text1 = text.substring(0, i);
				String text2 = text.substring(i).trim();
				
				int min = timeinfo.min;
				
				int second = timeinfo.second;
				int mills = timeinfo.mills;
				
				text1 = "[" +  String.format("%02d", min) + ":" +  String.format("%02d", second) + "." + String.format("%02d", mills) +  "]" + text1;

				lines.add(text1);
				
				
				
				if (text2.length() > MAX_LENGTH) {
					timeinfo.second = timeinfo.second + 2;
					if (timeinfo.second > 59) {
						timeinfo.second = timeinfo.second-59;
						timeinfo.min = timeinfo.min + 1;
					}
					
					reformLine(timeinfo,text2);

				} else {
					second = timeinfo.second + 2;
					
					if (second > 59) {
						second = second-59;
						min = min + 1;
					}

					text2 = "[" +  String.format("%02d", min) + ":" +  
							String.format("%02d", second)  + "." +
							 String.format("%02d", mills) +  "]" + text2;
					lines.add(text2);
				}
				System.out.println("text1 = " + text1 + "; text2 = " + text2);
				break;
			}
		}
	}

	private String getText(String line) {
		// TODO Auto-generated method stub
		int end = line.indexOf("]");
		String text = line.substring(end + 1);
		
		return text;
	}

	private void getTime(TimeInfo timeinfo,  String line) {
		if (line.startsWith("[")) {
			int end = line.indexOf("]");
			String time = line.substring(1, end);
			System.out.print("time = " + time);
			String [] arrays = time.split(":");
			timeinfo.min = Integer.parseInt(arrays[0]);

			String arrays1 = arrays[1];
			end = arrays1.indexOf(".");

			timeinfo.second = Integer.parseInt(arrays1.substring(0,end));
			timeinfo.mills = Integer.parseInt(arrays1.substring(end + 1));

		}
	}
	
	
	static public class TimeInfo {
		public Integer min = 0;
		public Integer second = 0;
		public Integer mills = 0;
	}


}
