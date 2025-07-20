package com.shannontheoret.machikoro.controller;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.Landmark;
import com.shannontheoret.machikoro.dto.PlayerDTO;
import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.exception.GameException;
import com.shannontheoret.machikoro.service.GameService;
import com.shannontheoret.machikoro.utilities.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
        origins = {"https://machikoro.shannontheoret.com", "http://localhost:5173"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true"
)
@RestController
public class GameController {
    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<Object> getGame(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.findByCode(gameCode));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/newGame")
    public ResponseEntity<Object> newGame(@RequestBody List<PlayerDTO> players) {
        try {
            return ResponseEntity.ok(gameService.beginGame(players));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/roll")
    public ResponseEntity<Object> roll(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.roll(gameCode, false));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/rollTwoDice")
    public ResponseEntity<Object> rollTwoDice(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.roll(gameCode, true));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/confirmRoll")
    public ResponseEntity<Object> confirmRoll(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.confirmRoll(gameCode));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/steal")
    public ResponseEntity<Object> steal(@RequestParam String gameCode, @RequestParam Integer playerNumber) {
        try {
            return ResponseEntity.ok(gameService.steal(gameCode, playerNumber));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/purchaseCard")
    public ResponseEntity<Object> purchaseCard(@RequestParam String gameCode, @RequestParam Card card) {
        try {
            return ResponseEntity.ok(gameService.purchaseCard(gameCode, card));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/purchaseLandmark")
    public ResponseEntity<Object> purchaseLandmark(@RequestParam String gameCode, @RequestParam Landmark landmark) {
        try {
            return ResponseEntity.ok(gameService.purchaseLandmark(gameCode, landmark));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/completeTurn")
    public ResponseEntity<Object> completeTurn(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.completeTurn(gameCode));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/makeNPCMove")
    public ResponseEntity<Object> makeNPCMove(@RequestParam String gameCode) {
        try {
            return ResponseEntity.ok(gameService.makeNPCMove(gameCode));
        } catch (GameException e) {
            return ResponseUtil.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
