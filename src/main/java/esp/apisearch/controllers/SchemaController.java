package esp.apisearch.controllers;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import ar.com.asanteit.esp.spi.ApplicationException;
import ar.com.asanteit.esp.spi.Platform;
import ar.com.asanteit.esp.spi.index.IndexException;
import esp.apisearch.model.Schema;

@Path("/api/schema/")
public class SchemaController {
	private final Logger log = Logger.getLogger(SchemaController.class);
	
	@Context
	private ServletContext context;

	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(Schema schema) throws ApplicationException {
		if(schema.hasErrors().isEmpty()){
			try {
				platform().getIndexService().createIndex(schema.getName(), false, false);
				platform().getIndexService().openIndex(schema.getName(), schema.generateEspSchema());
			} catch (IndexException e) {
				log.info("Index is already open (" + schema.getName() + ")");
				return Response.serverError().build();
			}
			return Response.ok().build();
		}else{
			return Response.serverError().build();
		}
	}
	
	@DELETE
	@Path("")
	public Response destroy(@QueryParam("index")String index){
		try {
			platform().getIndexService().destroyIndex(index);	    
    } catch (IndexException e) {
    	e.printStackTrace();
    	return Response.serverError().build();
    }
		return Response.ok().build();
	}
	
	private Platform platform() {
		return (Platform) context.getAttribute("platform");
	}
}
