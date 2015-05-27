package esp.apisearch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import esp.apisearch.model.Field;

/*
 * Class to represent an Schema
 */
public class Schema implements Serializable{

  private static final long serialVersionUID = 5026282433836976464L;

  String name;
	List<Field> fields = new ArrayList<Field>();

	public Schema(){}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}

	public List<String> hasErrors(){
		ArrayList<String> errors = new ArrayList<String>();
		if(this.name == null || this.name.isEmpty())
			errors.add("Schema must have a name.");
		boolean fieldError = false;
		for (Field field : fields) {
			if(!field.hasErrors().isEmpty()){
				fieldError = true;
				break;
			}
    }
		if (fieldError) { 
			errors.add("At least one field has error.");
		}
		return errors;
	}
	
	public ar.com.asanteit.esp.spi.index.schema.Schema generateEspSchema(){
		ar.com.asanteit.esp.spi.index.schema.Schema schema = new ar.com.asanteit.esp.spi.index.schema.Schema();
		for (Field field : fields) {
			schema.add(field.generateEspField());
		}
		return schema;
	}
	
	@Override
	public String toString() {
		String description = "schema:" + name;
		description = description.concat("||fields:");
		for (Field field : fields) {
			description = description.concat(field.getName()).concat(",")
			    .concat(field.getType()).concat("|");
		}
		return description;
	}
}
