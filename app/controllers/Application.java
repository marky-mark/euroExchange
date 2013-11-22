package controllers;

import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
//        AkkaSystem.get().actorOf(new Props(ExchangeRateActor.class));
        render();
    }

}