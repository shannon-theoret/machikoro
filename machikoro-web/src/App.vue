<script setup>
import Header from './components/Header.vue'
import Game from './components/Game.vue'
import api from '@/api/axiosInstance';
import { ref, computed, onMounted } from 'vue'
import NewGame from './components/NewGame.vue';

const game = ref({});
const toStart = ref(false);
const randomMessage = ref("Welcome to the game!");
const error = ref("");
const gameCode = ref("");

const startGame = () => {
  game.value = {};
  toStart.value = true;
}

const beginGame = async (players) => {
  if (players.length < 2) {
    error.value = "At least two players are required to start the game.";
    return;
  }
  error.value = "";
  const playerData = players.map((player, index) => ({
    playerNumber: index + 1,
    playerName: player.name,
    isNPC: player.isNPC
  }));
  await makeApiCall('/newGame', playerData, null);
  gameCode.value = game.value.code;
  toStart.value = false;
}

const openGame = async (code) => {
  if (code) {
    try {
      const response = await api.get('', {
        params: { gameCode: code }
      });
      game.value = response.data;
      gameCode.value = code;
      sessionStorage.setItem("gameCode", gameCode.value);
      toStart.value = false;
      error.value = "";
    } catch (err) {
      error.value = err.response?.data?.message || "An unknown error occurred.";
    }
  } else {
    error.value = "No game code provided!";
  }
}

const makeApiCall = async (url, data, params) => {
    try {
        const response = await api.post(url, data, { params });
        game.value = response.data;
        error.value = "";
    } catch (err) {
        error.value = err.response?.data?.message || "An unknown error occurred.";
    }
};

onMounted(() => {
  const storedGameCode = sessionStorage.getItem("gameCode");
  console.log("Stored game code:", storedGameCode);
  if (storedGameCode) {
    gameCode.value = storedGameCode;
    openGame(storedGameCode); // Automatically open game if gameCode is stored
  }
});

</script>

<template>
  <Header :gameCode="gameCode" @start-game="startGame" @open-game="openGame"/>
  {{ error }}
  <NewGame v-if="toStart" @begin-game="beginGame"></NewGame>
  <Game v-if="game.step && game.step !== 'SETUP'" :game="game"></Game>
</template>
