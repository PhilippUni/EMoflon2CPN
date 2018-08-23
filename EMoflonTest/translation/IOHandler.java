package translation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;




public class IOHandler {
	private IOHandler() {}
	
	public static void write(File file, String content) throws IOException {
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), content.getBytes(StandardCharsets.ISO_8859_1));
	}
	
	public static String read(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}
	
}
