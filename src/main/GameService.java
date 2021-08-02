package main;

import jdk.nashorn.internal.objects.annotations.Getter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/game")
public class GameService {

    @GET
    @Path("/start")
    public Response startGame() {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.setGame(new Game());
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/test")
    public Response testGame() {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.setGame(new Game());
        gameSingleton.getGame().getPlayer1().setCoins(50);
        gameSingleton.getGame().getPlayer2().setCoins(50);
        gameSingleton.getGame().getPlayer3().setCoins(50);
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/rollSingle")
    public Response rollSingle() {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame().rollSingle();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/rollDouble")
    public Response rollDouble() {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame().rollDouble();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/confirm")
    public Response confirm() {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame().confirmRoll();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/steal/{player}")
    public Response confirm(@PathParam("player") Integer playerNumber) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame().steal(playerNumber);
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/purchaseCard/{index}")
    public Response purchaseCard(@PathParam("index") Integer index){
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame().purchaseCard(index);
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path(("/endTurn"))
    public Response endTurn() {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame().endTurn();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame());
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/purchaseLandmark/{id}")
    public Response purchaseLandmark(@PathParam("id") String id) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame().purchaseLandmark(id);
        GameResponse gameResponse = new GameResponse((gameSingleton.getGame()));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

}
