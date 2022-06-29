package exports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonInterface {
	
	public static void main(String[] args) throws IOException {
		String path = "F:/research/software/code/eclipse/formats-representations/py/";
		String outp = getScriptOutput(new String[]{"python", path + "beam.py", path + "notes.txt"});
		System.out.println("OPEN");
		System.out.println(outp);
		System.out.println("CLOSE");
	}


	/**
	 * Returns the output of the invoked script as a String.
	 * 
	 * @param cmd A String[] containing 
	 *            <ul>
	 *            <li>as element 0: the interpreter, e.g., "python"</li>
	 *            <li>as element 1: the path to the script to invoke</li>
	 *            <li>as element(s) 2-...: the arguments that the script to invoke takes</li>
	 *            </ul>
	 *            
	 * @return The output of the invoked script.
	 * @throws IOException
	 */
	public static String getScriptOutput(String[] cmd) throws IOException {
		String outp = "";
		try {
			// Create runtime to execute external command
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(cmd);

			// Retrieve output from Python script
			BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line = bfr.readLine()) != null) {
				outp += line + "\r\n";
//				System.out.println(line);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return outp;
	}
}
