package esp.apisearch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.asanteit.esp.spi.index.schema.Analyzer;
import ar.com.asanteit.esp.spi.index.schema.CharFilter;
import ar.com.asanteit.esp.spi.index.schema.TokenFilter;
import ar.com.asanteit.esp.spi.index.schema.Tokenizer;

/*
 * Class that represents a field of a Schema.
 * Will contain the name, the type and extra information.
 */
public class Field implements Serializable {

	private static final long serialVersionUID = 3282236164927541984L;

	String name;
	String type;

	public Field() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> hasErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		if (this.name == null || this.name.isEmpty())
			errors.add("Field must have a name.");
		if (this.type == null || this.type.isEmpty() || !validType())
			errors.add("Schema must have a valid type.");
		return errors;
	}

	private boolean validType() {
	  if(type.equals("ID") || type.equals("TEXT") || type.equals("DISPLAY")){
	  	return true;
	  }
	  return false;
  }

	public ar.com.asanteit.esp.spi.index.schema.Field generateEspField() {
		switch (type) {
		case "ID":
			return ar.com.asanteit.esp.spi.index.schema.Field.ID(name);
		case "TEXT":
			return ar.com.asanteit.esp.spi.index.schema.Field.TEXT(name, true,
			    Analyzers.STANDARD(), Analyzers.STANDARD());
		case "DISPLAY":
			return ar.com.asanteit.esp.spi.index.schema.Field.DISPLAY(name);
		default:
			return null;
		}
	}

	private static class Analyzers {
		private static final Analyzer STANDARD() {
			Map<String, String> config = new HashMap<String, String>();
			config.put("minGramSize", "3");
			config.put("maxGramSize", "10");
			return Analyzer.builder()
			    .tokenizer(Tokenizer.STANDARD())
			    .tokenFilters(TokenFilter.STOPWORDS(), TokenFilter.LOWERCASE(),
			        new TokenFilter("edgeNGram", config))
			    .charFilters(CharFilter.MAPPING())
			    .build();
		}
	}
}
