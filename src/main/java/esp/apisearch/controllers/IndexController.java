package esp.apisearch.controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

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
	    String json) throws ApplicationException {
		if (!indexManagerService().isIndexCreated(index)) {
			return Response.status(408).build();
		}
		log.debug("About to add to add to index: " + index);
		Document document = new Document();

		JsonObject jsonObject = JsonObject.readFrom(json);
		for (Member member : jsonObject) {
			if (member.getValue().isString()) {
				document.setField(member.getName(), member.getValue().asString());
			} else if (member.getName().equalsIgnoreCase("facets")) {
				for (JsonValue value : member.getValue().asArray()) {
					if (value.isString()) {
						String facetPath = value.asString();
						if(isFacetPathValid(facetPath)) document.addFacet(facetPath);
					}
				}
			}
		}

		indexManagerService().getWriteTransaction(index).addDocument(document);
		return Response.ok().build();
	}

	@GET
	@Path("/commit")
	public void commitIndex() throws ApplicationException {
		indexManagerService().commitAllIndexes();
	}

	private IndexManagerService indexManagerService() {
		return (IndexManagerService) context.getAttribute("indexManagerService");
	}
	
	private boolean isFacetPathValid(String facetPath){
		String pattern = "(/[a-zA-Z0-9_-]+)+?";
		return Pattern.matches(pattern, facetPath);
	}

}
