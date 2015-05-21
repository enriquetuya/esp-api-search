package esp.apisearch.controllers;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
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
import ar.com.asanteit.esp.spi.index.Document;
import ar.com.asanteit.esp.spi.index.IndexService;
import ar.com.asanteit.esp.spi.index.WriteTransaction;

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
	    HashMap<String, String> values)
	    throws ApplicationException {
		log.debug("About to add to add to index: " + index);
		try (WriteTransaction tx = indexService().writeTransaction(index)) {
			Document document = new Document();
			for (String key : values.keySet()) {
	      document.setField(key, values.get(key));
      }
			tx.addDocument(document);
			tx.success();
		}
		return Response.ok().build();
	}

	private Platform platform() {
		return (Platform) context.getAttribute("platform");
	}

	private IndexService indexService() {
		return platform().getIndexService();
	}

}
