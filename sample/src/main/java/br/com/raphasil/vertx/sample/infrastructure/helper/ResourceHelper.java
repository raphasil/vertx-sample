package br.com.raphasil.vertx.sample.infrastructure.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import io.vertx.core.json.JsonObject;

public class ResourceHelper {

	public Optional<JsonObject> getConfig() {		
		return readFile("/config.json").map( file ->  { 
			return new JsonObject(file); 
		});
	}

	private Optional<String> readFile(String file) {

		Optional<String> result = Optional.empty();

		try (InputStream in = String.class.getResourceAsStream(file)) {
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			String str = "";
			while ((bytesRead = in.read(contents)) != -1) {
				str += new String(contents, 0, bytesRead);
			}
			
			result = Optional.of(str);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
