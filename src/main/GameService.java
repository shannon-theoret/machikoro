package main;

import jdk.nashorn.internal.objects.annotations.Getter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/game")
public class GameService {

    @GET
    @Path("/start/{code}")
    public Response startGame(@PathParam("code") String code) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code);
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame(code));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

/*    @GET
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
*/

    @GET
    @Path("/{code}/rollSingle")
    public Response rollSingle(@PathParam("code") String code) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code).rollSingle();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame(code));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/{code}/rollDouble")
    public Response rollDouble(@PathParam("code") String code) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code).rollDouble();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame(code));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/{code}/confirm")
    public Response confirm(@PathParam("code") String code) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code).confirmRoll();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame(code));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/{code}/steal/{player}")
    public Response confirm(@PathParam("code") String code, @PathParam("player") Integer playerNumber) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code).steal(playerNumber);
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame(code));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/{code}/purchaseCard/{index}")
    public Response purchaseCard(@PathParam("code") String code, @PathParam("index") Integer index){
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code).purchaseCard(index);
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame(code));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/{code}/endTurn")
    public Response endTurn(@PathParam("code") String code) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code).endTurn();
        GameResponse gameResponse = new GameResponse(gameSingleton.getGame(code));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

    @GET
    @Path("/{code}/purchaseLandmark/{id}")
    public Response purchaseLandmark(@PathParam("code") String code, @PathParam("id") String id) {
        GameSingleton gameSingleton = GameSingleton.getInstance();
        gameSingleton.getGame(code).purchaseLandmark(id);
        GameResponse gameResponse = new GameResponse((gameSingleton.getGame(code)));
        return Response.status(Response.Status.OK)
                .entity(gameResponse).build();
    }

}
