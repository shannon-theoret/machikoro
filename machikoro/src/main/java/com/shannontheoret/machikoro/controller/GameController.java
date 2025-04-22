package com.shannontheoret.machikoro.controller;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.Landmark;
import com.shannontheoret.machikoro.dto.PlayerDTO;
import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.exception.GameException;
import com.shannontheoret.machikoro.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {
    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/testStuff")
    public ResponseEntity<Object> testStuff(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.testStuff(gameCode));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getGame(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.findByCode(gameCode));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/newGame")
    public ResponseEntity<Object> newGame(@RequestBody List<PlayerDTO> players) {
        try {
            Game game = gameService.newGame(players.size());
            for (PlayerDTO player : players) {
                gameService.setupPlayer(game.getCode(), player.getPlayerNumber(), player.getPlayerName(), player.getIsNPC());
            }
            return ResponseEntity.ok(gameService.beginGame(game.getCode()));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/roll")
    public ResponseEntity<Object> roll(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.roll(gameCode, false));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/rollTwoDice")
    public ResponseEntity<Object> rollTwoDice(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.roll(gameCode, true));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/confirmRoll")
    public ResponseEntity<Object> confirmRoll(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.confirmRoll(gameCode));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/steal")
    public ResponseEntity<Object> steal(@RequestParam String gameCode, @RequestParam Integer playerNumber) {
        try {
            return ResponseEntity.ok(gameService.steal(gameCode, playerNumber));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/purchaseCard")
    public ResponseEntity<Object> purchaseCard(@RequestParam String gameCode, @RequestParam Card card) {
        try {
            return ResponseEntity.ok(gameService.purchaseCard(gameCode, card));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/purchaseLandmark")
    public ResponseEntity<Object> purchaseLandmark(@RequestParam String gameCode, @RequestParam Landmark landmark) {
        try {
            return ResponseEntity.ok(gameService.purchaseLandmark(gameCode, landmark));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/completeTurn")
    public ResponseEntity<Object> completeTurn(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.completeTurn(gameCode));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/makeNPCMove")
    public ResponseEntity<Object> makeNPCMove(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.makeNPCMove(gameCode));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

/*
    @PostMapping("/beginGame")
    public ResponseEntity<Object> beginGame(@RequestParam String gameCode, Map<Integer, String> playerNames) {
        try {
            return ResponseEntity.ok(gameService.beginGame(gameCode, playerNames));
        } catch (GameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

 */
}
