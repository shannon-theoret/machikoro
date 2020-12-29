import { Component } from '@angular/core';
import { HttpClient, HttpResponse, HttpErrorResponse } from "@angular/common/http";
import {Observable, of, throwError} from 'rxjs';
import {catchError, tap} from "rxjs/operators";
import {CARDS} from "./cards";
import {Landmark, LANDMARKS} from "./landmarks";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'machi-koro-app';
  ROOT_URL = 'http://localhost:8080/MachiKoro3_war_exploded/machikoroapp'

  game: Game = new Game();
  prevGame: Game = new Game();
  started: Boolean = false;
  landmarks = LANDMARKS;

  constructor(private http: HttpClient) {
  }


  startGame() {
    this.http.get<Game>(this.ROOT_URL + "/game/start")
      .pipe(tap(game => {
        this.game = game
        this.prevGame = game}))
      .subscribe();
    this.started = true;
  }

  testGame() {
    this.http.get<Game>(this.ROOT_URL + "/game/test")
      .pipe(tap(game => {
        this.game = game
        this.prevGame = game}))
      .subscribe();
    this.started = true;
  }

  rollSingleDie() {
    this.prevGame = this.game;
    this.http.get<Game>(this.ROOT_URL + "/game/rollSingle")
      .pipe(tap(game => {
        this.game = game}))
      .subscribe();
  }

  rollTwoDice() {
    this.prevGame = this.game;
    this.http.get<Game>(this.ROOT_URL + "/game/rollDouble")
      .pipe(tap(game => {
        this.game = game}))
      .subscribe();
  }

  confirmRoll() {
    this.http.get<Game>(this.ROOT_URL + "/game/confirm")
      .pipe(tap(game => {
        this.game = game}))
      .subscribe();
  }

  steal(playerNumber) {
    this.http.get<Game>(this.ROOT_URL + "/game/steal/" + playerNumber)
      .pipe(tap(game => {
        this.game = game}))
      .subscribe();
  }

  purchseCard(card) {
    if (this.game.step !== "buy") {
      alert("You are not currently in the buy phase of your turn.")
      return;
    }
    if (this.game.currentPlayer.coins < card.cost) {
      alert("You cannot afford this purchase, please select another card or click 'Next'.");
      return;
    }
    if (card.type === "purple" && this.game.currentPlayer.stock[card["index"]] === 1) {
      alert("You may only have 1 copy of a given purple card, please select another card or click 'Next'.");
      return;
    }
    this.prevGame = this.game;
    this.http.get<Game>(this.ROOT_URL + "/game/purchaseCard/" + card.index)
      .pipe(tap(game => {
        this.game = game}))
      .subscribe();
  }

  purchaseLandmark(landmark: Landmark) {
    if (this.game.step !== "buy") {
      alert("You are not currently in the buy phase of your turn.")
      return;
    }
    if (this.game.currentPlayer.coins < landmark.cost) {
      alert("You cannot afford this purchase, please select another card or click 'Next'.");
      return;
    }
    this.prevGame = this.game;
    this.http.get<Game>(this.ROOT_URL + "/game/purchaseLandmark/" + landmark.id)
      .pipe(tap(game => {
        this.game = game}))
      .subscribe();
  }

  endTurn() {
    this.prevGame = this.game;
    this.http.get<Game>(this.ROOT_URL + "/game/endTurn")
      .pipe(tap(game => {
        this.game = game}))
      .subscribe();
  }
}

export class Player implements Player{

  hasTrainStation : boolean;
  hasShoppingMall : boolean;
  hasAmusementPark : boolean;
  hasRadioTower : boolean;
  stock : Stock;
  coins : number;

}


export interface Player {
  hasTrainStation : boolean;
  hasShoppingMall : boolean;
  hasAmusementPark : boolean;
  hasRadioTower : boolean;
  stock : Stock;
  coins : number;
}

export interface Game {
  player1: Player;
  player2: Player;
  player3: Player;
  stock: Stock;
  currentPlayer: Player;
  currentPlayerNumber: Number;
  step: String;
  recentRoll: Dice;
}

export class Game implements Game {
  player1: Player;
  player2: Player;
  player3: Player;
  stock: Stock;
  currentPlayer: Player;
  currentPlayerNumber: Number;
  step: String;
  recentRoll: Dice;
}

export interface Stock {
  0: number;
  1: number;
  2: number;
  3: number;
  4: number;
  5: number;
  6: number;
  7: number;
  8: number;
  9: number;
  10: number;
  11: number;
  12: number;
  13: number;
  14: number;
}

export class Stock implements Stock {
  0: number;
  1: number;
  2: number;
  3: number;
  4: number;
  5: number;
  6: number;
  7: number;
  8: number;
  9: number;
  10: number;
  11: number;
  12: number;
  13: number;
  14: number;
}

export interface Dice {
  dieOne: number;
  dieTwo: number;
}

export class Dice implements Dice {
  dieOne: number;
  dieTwo: number;
}

