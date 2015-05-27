package esp.apisearch.controllers;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import ar.com.asanteit.esp.spi.ApplicationException;
import ar.com.asanteit.esp.spi.index.Document;
import esp.apisearch.services.IndexManagerService;

@Path("/api/index/")
public class IndexController {
	private final Logger log = Logger.getLogger(IndexController.class);

	@Context
	private ServletContext context;
	
	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@QueryParam("index") String index,
	    HashMap<String, String> values) throws ApplicationException {
		log.debug("About to add to add to index: " + index);
		Document document = new Document();
		for (String key : values.keySet()) {
			document.setField(key, values.get(key));
		}
		indexManagerService().getWriteTransaction(index).addDocument(document); 
		return Response.ok().build();
	}

//	@POST
//	@Path("/async")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response async_add(@QueryParam("index") String index, String json)
//	    throws ApplicationException {
//		Request indexRequest = Request.builder().requestType("/api/index")
//		    //
//		    .param("index", index)
//		    //
//		    .stream(new ByteArrayInputStream(json.getBytes()))
//		    .requestHost("localhost").requestPort(9090)//
//		    .requestMethod("POST")//
//		    .requestScheme("HTTP")//
//		    .contentType(MediaType.APPLICATION_JSON).build();
//		platform().getSchedulerService().async(indexRequest);
//		return Response.ok().build();
//	}

	@GET
	@Path("/commit")
	public void commitIndex() throws ApplicationException {
		indexManagerService().commitAllIndexes();
	}

	private IndexManagerService indexManagerService() {
		return (IndexManagerService)context.getAttribute("indexManagerService");
	}

}
