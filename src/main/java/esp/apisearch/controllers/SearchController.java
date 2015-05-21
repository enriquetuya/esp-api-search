package esp.apisearch.controllers;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import ar.com.asanteit.esp.spi.ApplicationException;
import ar.com.asanteit.esp.spi.Platform;
import ar.com.asanteit.esp.spi.index.IndexException;
import ar.com.asanteit.esp.spi.index.IndexService;
import ar.com.asanteit.esp.spi.index.ReadTransaction;
import ar.com.asanteit.esp.spi.index.query.Query;
import ar.com.asanteit.esp.spi.index.results.Results;

@Path("/api/search/")
public class SearchController {
	private final Logger log = Logger.getLogger(SearchController.class);

	@Context
	private ServletContext context;

	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Results search(@QueryParam("index") String index,
	    Query query)
	    throws ApplicationException {
		try (ReadTransaction rt = indexService().readTransaction(index)) {
			return rt.search(query);
		} catch (IndexException e) {
			throw new ApplicationException("Unable to execute query.", e);
		}
	}

	private Platform platform() {
		return (Platform) context.getAttribute("platform");
	}

	private IndexService indexService() {
		return platform().getIndexService();
	}

}
