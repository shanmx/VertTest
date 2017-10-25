package com.shanmx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
    /**
     * Created by chengen on 26/04/2017.
     */
public class MyFirstVerticle extends AbstractVerticle {
	@Override
	public void start(Future<Void> fut) {
		//HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);

		router.route().handler(routingContext -> {

		  // This handler will be called for every request
		  HttpServerResponse response = routingContext.response();
		  response.putHeader("content-type", "text/plain");

		  // Write to the response and end it
		  response.end("<HTML><body><H1>Hello World from Vert.x-Web!</H1></body><HTML>");
		});

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
